package org.upm.inesdata.edc.extension.policy.functions;

import org.eclipse.edc.participant.spi.ParticipantAgentPolicyContext;
import org.eclipse.edc.policy.engine.spi.AtomicConstraintRuleFunction;
import org.eclipse.edc.policy.model.Operator;
import org.eclipse.edc.policy.model.Prohibition;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.result.Result;

/** AtomicConstraintFunction to validate the referring connector claim  edc prohibitions. */
public class ReferringConnectorProhibitionFunction extends AbstractReferringConnectorValidation
        implements AtomicConstraintRuleFunction<Prohibition, ParticipantAgentPolicyContext> {

    public ReferringConnectorProhibitionFunction(Monitor monitor) {
        super(monitor);
    }

    @Override
    public boolean evaluate(Operator operator, Object rightValue, Prohibition rule, ParticipantAgentPolicyContext context) {
        return evaluate(operator, rightValue, context);
    }

    @Override
    public Result<Void> validate(Operator operator, Object rightValue, Prohibition rule) {
        return AtomicConstraintRuleFunction.super.validate(operator, rightValue, rule);
    }

    @Override
    public String name() {
        return AtomicConstraintRuleFunction.super.name();
    }
}