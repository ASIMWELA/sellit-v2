package com.sellit.api.servicetransactions.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sellit.api.commons.BaseEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Entity
@Table(name = "service_categories")
@Builder
public class ServiceCategoryEntity extends BaseEntity {
    @Column(name="service_category_name", unique = true, length = 50, nullable = false)
    @NotEmpty(message = "serviceCategoryName cannot be empty")
    @Size(min=3, message = "serviceCategoryName should at least have 3 characters")
    String serviceCategoryName;
    @OneToMany(mappedBy = "serviceCategory", cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonIgnore
    List<ServiceEntity> serviceEntities = new ArrayList<>();
}
