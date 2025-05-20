package org.upm.inesdata.complexpolicy.model;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Represents a type-safe ODRL Policy Operator.
 * <p>
 * This enum defines various operators that can be used in ODRL policies.
 * </p>
 * 

 * @author tim.dahlmanns@isst.fraunhofer.de
 */
@Schema(description = "Type-Safe ODRL Policy Operator", enumAsRef = true)
public enum OperatorDto {
    EQ,
    NEQ,
    GT,
    GEQ,
    LT,
    LEQ,
    IN,
    HAS_PART,
    IS_A,
    IS_ALL_OF,
    IS_ANY_OF,
    IS_NONE_OF
}
