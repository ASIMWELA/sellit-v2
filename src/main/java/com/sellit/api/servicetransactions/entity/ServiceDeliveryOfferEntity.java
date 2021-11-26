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
import javax.validation.constraints.PositiveOrZero;
import java.util.Date;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Entity
@Table(name = "service_delivery_offers")
public class ServiceDeliveryOfferEntity extends BaseEntity {
    @Column(name="discount_in_percent", length = 50, nullable = false)
    @PositiveOrZero
    double discountInPercent;
    @Column(name="estimated_cost", length = 50, nullable = false)
    @PositiveOrZero
    double estimatedCost;
    @Column(name="offer_submission_date")
    Date offerSubmissionDate;
    @Column(name="is_offer_accepted", length = 5)
    boolean isOfferAccepted;
    @OneToOne(mappedBy = "serviceDeliveryOffer")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnore
    ServiceAppointmentEntity serviceAppointment;
    @ManyToOne
    @JoinColumn(name = "service_request_id")
    @LazyCollection(LazyCollectionOption.FALSE)
    @JsonIgnore
    ServiceRequestEntity serviceRequest;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "service_provider_id")
    @LazyCollection(LazyCollectionOption.FALSE)
    ServiceProviderEntity serviceProvider;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceDeliveryOfferEntity that = (ServiceDeliveryOfferEntity) o;
        return Double.compare(that.discountInPercent,
                discountInPercent) == 0 && Double.compare(that.estimatedCost, estimatedCost) == 0
                && isOfferAccepted == that.isOfferAccepted
                && offerSubmissionDate.equals(that.offerSubmissionDate)
                && serviceAppointment.equals(that.serviceAppointment)
                && serviceRequest.equals(that.serviceRequest)
                && serviceProvider.equals(that.serviceProvider);
    }

    @Override
    public int hashCode() {
        return Objects.hash(discountInPercent, estimatedCost,
                offerSubmissionDate, isOfferAccepted,
                serviceAppointment, serviceRequest,
                serviceProvider);
    }
}
