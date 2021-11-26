package com.sellit.api.dto.provider;

import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProviderAppointmentDto {
    String uuid,
            appointmentDate,
            appointmentStartTime,
            appointmentEndTime,
            appointmentDesc,
            appointmentWith,
            customerPhone,
            customerEmail;
}
