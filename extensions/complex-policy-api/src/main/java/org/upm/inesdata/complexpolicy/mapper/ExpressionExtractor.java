package org.upm.inesdata.complexpolicy.mapper;

import lombok.RequiredArgsConstructor;
import org.eclipse.edc.policy.model.Permission;
import org.eclipse.edc.policy.model.Policy;
import org.upm.inesdata.complexpolicy.model.UiPolicyExpression;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ExpressionExtractor {
    private final PolicyValidator policyValidator;
    private final ExpressionMapper expressionMapper;

    /**
     * Build {@link UiPolicyExpression} from an ODRL {@link Policy}.
     * <p>
     * This operation is lossy which is why we document warnings / errors in {@link MappingErrors}.
     *
     * @param policy ODRL policy
     * @param errors mapping errors
     * @return ui policy expression
     */
    public UiPolicyExpression getPermissionExpression(Policy policy, MappingErrors errors) {
        var expressions = getPermissionExpressions(policy, errors);
        if (expressions.isEmpty()) {
            return UiPolicyExpression.empty();
        } else if (expressions.size() == 1) {
            return expressions.get(0);
        } else {
            return UiPolicyExpression.and(expressions);
        }
    }

    /**
     * Build {@link UiPolicyExpression}s from an ODRL {@link Policy}.
     * <p>
     * This operation is lossy which is why we document warnings / errors in {@link MappingErrors}.
     *
     * @param policy ODRL policy
     * @param errors mapping errors
     * @return ui policy expressions
     */
    private List<UiPolicyExpression> getPermissionExpressions(Policy policy, MappingErrors errors) {
        policyValidator.validateOtherPolicyFieldsUnset(policy, errors);

        var permissions = policy.getPermissions();
        if (permissions == null) {
            return List.of();
        }

        if (permissions.size() > 1) {
            errors.add("Multiple permissions were present. Prefer using a conjunction using AND.");
        }

        List<UiPolicyExpression> expressions = new ArrayList<>();
        for (int itPermission = 0; itPermission < permissions.size(); itPermission++) {
            var permissionErrors = errors.forChildObject("permissions").forChildArrayElement(itPermission);
            var permission = permissions.get(itPermission);
            expressions.addAll(getPermissionExpressions(permission, permissionErrors));
        }
        return expressions;
    }

    private List<UiPolicyExpression> getPermissionExpressions(Permission permission, MappingErrors errors) {
        policyValidator.validateOtherPermissionFieldsUnset(permission, errors);

        if (permission == null) {
            return List.of();
        }

        var constraints = permission.getConstraints();
        if (constraints != null && constraints.size() > 1) {
            errors.forChildObject("constraints")
                .add("Multiple constraints were present. Prefer using a conjunction using AND.");
        }

        return expressionMapper.buildUiPolicyExpressions(
            constraints,
            errors.forChildObject("constraints")
        );
    }
}
