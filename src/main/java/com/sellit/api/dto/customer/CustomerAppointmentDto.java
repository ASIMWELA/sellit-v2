package com.sellit.api.dto.customer;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerAppointmentDto {
 String uuid,
         appointmentDate,
         appointmentStartTime,
         appointmentEndTime,
         appointmentDesc,
         appointmentWith,
         providerPhone,
         providerEmail;
}
