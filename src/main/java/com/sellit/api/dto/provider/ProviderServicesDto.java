package com.sellit.api.dto.provider;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProviderServicesDto {
    String serviceName,
            serviceUuid,
            serviceCategoryName,
            serviceCategoryUuid;
    double billingRatePerHour;
    int experienceInMonths;
}
