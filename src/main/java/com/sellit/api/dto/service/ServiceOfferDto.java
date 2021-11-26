package com.sellit.api.dto.service;


import com.sellit.api.dto.OfferPackage;
import com.sellit.api.dto.provider.ProviderDetails;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ServiceOfferDto {
    OfferPackage offerPackage;
    ProviderDetails providerDetails;
}
