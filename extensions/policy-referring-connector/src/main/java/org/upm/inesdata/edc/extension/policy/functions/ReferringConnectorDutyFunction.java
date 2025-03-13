package org.upm.inesdata.edc.extension.policy.functions;

import org.eclipse.edc.participant.spi.ParticipantAgentPolicyContext;
import org.eclipse.edc.policy.engine.spi.AtomicConstraintRuleFunction;
import org.eclipse.edc.policy.model.Duty;
import org.eclipse.edc.policy.model.Operator;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.result.Result;

/** AtomicConstraintFunction to validate the referring connector claim for edc duties. */
public class ReferringConnectorDutyFunction extends AbstractReferringConnectorValidation
        implements AtomicConstraintRuleFunction<Duty, ParticipantAgentPolicyContext> {

    public ReferringConnectorDutyFunction(Monitor monitor) {
        super(monitor);
    }

    @Override
    public boolean evaluate(Operator operator, Object rightValue, Duty rule, ParticipantAgentPolicyContext context) {
        return evaluate(operator, rightValue, context);
    }

    @Override
    public Result<Void> validate(Operator operator, Object rightValue, Duty rule) {
        return AtomicConstraintRuleFunction.super.validate(operator, rightValue, rule);
    }

    @Override
    public String name() {
        return AtomicConstraintRuleFunction.super.name();
    }
}