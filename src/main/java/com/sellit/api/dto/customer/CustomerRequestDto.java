package com.sellit.api.dto.customer;

import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerRequestDto {
    String uuid,
            requirementDescription,
            requiredOn,
            expectedStartTime,
            expectedHours;
}
