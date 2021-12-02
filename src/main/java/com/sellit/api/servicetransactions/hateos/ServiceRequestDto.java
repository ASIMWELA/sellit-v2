package com.sellit.api.servicetransactions.hateos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.sellit.api.servicetransactions.entity.ServiceRequestEntity;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Relation(itemRelation = "itemRequest", collectionRelation = "itemRequests")
@JsonPropertyOrder({"expectedHours", "requiredOn", "expectedStartTime", "requirementDescription"})
public class ServiceRequestDto extends RepresentationModel<ServiceRequestDto> {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String requirementDescription;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    LocalDate requiredOn;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    LocalDate expectedStartTime;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    Long expectedHours;
    public static ServiceRequestDto buildServiceRequestDto(ServiceRequestEntity entity){
        return ServiceRequestDto.builder()
                .requiredOn(entity.getRequiredOn())
                .expectedHours(entity.getExpectedTentativeEffortRequiredInHours())
                .expectedStartTime(entity.getExpectedStartTime())
                .requirementDescription(entity.getRequirementDescription())
                .build();
    }

}
