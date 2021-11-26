package com.sellit.api.provider.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sellit.api.commons.BaseEntity;
import com.sellit.api.servicetransactions.entity.ServiceEntity;
import com.sellit.api.servicetransactions.entity.ServiceDeliveryOfferEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Entity
@Table(name = "service_providers")
@Builder
public class ServiceProviderEntity extends BaseEntity {
    @Column(name="billing_rate_per_Hour", length = 20, nullable = false)
    double billingRatePerHour;
    @Column(name="experience_in_months", nullable = false)
    int experienceInMonths;
    @Column(name="service_offering_description", length = 800)
    @NotEmpty(message = "serviceOfferingDescription cannot be empty")
    String serviceOfferingDescription;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "service_id")
    @LazyCollection(LazyCollectionOption.FALSE)
    @JsonIgnore
    ServiceEntity service;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "provider_id")
    @LazyCollection(LazyCollectionOption.FALSE)
    ProviderEntity provider;
    @OneToMany(mappedBy = "serviceProvider", fetch = FetchType.LAZY)
    @LazyCollection(LazyCollectionOption.TRUE)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonIgnore
    List<ServiceDeliveryOfferEntity> serviceDeliveryOffers;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceProviderEntity that = (ServiceProviderEntity) o;
        return Double.compare(that.billingRatePerHour, billingRatePerHour) == 0 && experienceInMonths == that.experienceInMonths && serviceOfferingDescription.equals(that.serviceOfferingDescription) && service.equals(that.service) && provider.equals(that.provider);
    }

    @Override
    public int hashCode() {
        return Objects.hash(billingRatePerHour,
                experienceInMonths,
                serviceOfferingDescription,
                service, provider);
    }
}
