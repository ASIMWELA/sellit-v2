package com.sellit.api.servicetransactions.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sellit.api.commons.BaseEntity;
import com.sellit.api.provider.entity.ProviderReviewLogEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Entity
@Table(name = "service_appointments")
public class ServiceAppointmentEntity extends BaseEntity {
    @Column(name="service_delivered_on", nullable = false)
    Date serviceDeliveredOn;
    @NonNull
    @Column(name="service_start_time", nullable = false)
    Date serviceStartTime;
    @Column(name="service_end_time", nullable = false)
    @NonNull
    Date serviceEndTime;
    @Column(name="appointment_desc", nullable = false, length = 1000)
    @NotEmpty(message = "appointmentDescription cannot be empty")
    @Size(min=15, message = "appointmentDescription should at least have 15 characters")
    String appointmentDescription;
    @OneToOne
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JoinColumn(name="service_delivery_offer_id", referencedColumnName = "id", unique = true)
    ServiceDeliveryOfferEntity serviceDeliveryOffer;
    @OneToMany(mappedBy = "serviceAppointment")
    @LazyCollection(LazyCollectionOption.FALSE)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonIgnore
    List<ProviderReviewLogEntity> providerReviewLogs = new ArrayList<>();
}
