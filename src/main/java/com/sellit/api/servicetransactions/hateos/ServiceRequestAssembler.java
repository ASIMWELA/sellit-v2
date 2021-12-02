package com.sellit.api.servicetransactions.hateos;

import com.sellit.api.servicetransactions.entity.ServiceRequestEntity;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class ServiceRequestAssembler implements RepresentationModelAssembler<ServiceRequestEntity, ServiceRequestDto> {
    @Override
    public ServiceRequestDto toModel(ServiceRequestEntity entity) {
        return ServiceRequestDto.buildServiceRequestDto(entity);
    }
    @Override
    public CollectionModel<ServiceRequestDto> toCollectionModel(Iterable<? extends ServiceRequestEntity> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities);
    }
}
