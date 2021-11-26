package com.sellit.api.servicetransactions.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sellit.api.commons.BaseEntity;
import com.sellit.api.provider.entity.ServiceProviderEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Entity
@Table(name = "services")
public class ServiceEntity extends BaseEntity {
    @Column(name="service_name", unique = true, nullable = false)
    @NotEmpty(message = "serviceName cannot be empty")
    @Size(min=2, message = "serviceName should have at least 2 characters")
    String serviceName;
    @ManyToOne(targetEntity = ServiceCategoryEntity.class, cascade = CascadeType.ALL)
    @JoinColumn(name="service_category_id", nullable = false)
    ServiceCategoryEntity serviceCategory;
    @OneToMany(mappedBy = "service")
    @LazyCollection(LazyCollectionOption.FALSE)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonIgnore
    List<ServiceProviderEntity> serviceProviderEntities;
    @OneToMany(mappedBy = "service")
    @LazyCollection(LazyCollectionOption.TRUE)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    List<ServiceRequestEntity> serviceRequests;
}
