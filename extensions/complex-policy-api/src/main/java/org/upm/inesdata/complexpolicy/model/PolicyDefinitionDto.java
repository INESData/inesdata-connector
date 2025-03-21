package org.upm.inesdata.complexpolicy.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Policy Definition as required for the Policy Definition Page")
public class PolicyDefinitionDto {
        @Schema(description = "Policy Definition ID", requiredMode = Schema.RequiredMode.REQUIRED)
        private String policyDefinitionId;

        @Schema(description = "Policy Contents", requiredMode = Schema.RequiredMode.REQUIRED)
        private UiPolicy policy;
}
