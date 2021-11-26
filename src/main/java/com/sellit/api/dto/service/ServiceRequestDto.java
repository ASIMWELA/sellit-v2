package com.sellit.api.dto.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceRequestDto {
    String uuid,
            requestDescription,
            requiredDate,
            expectedStartTime,
            requestBy,
            email,
            locationCity,
            country;
    Long expectedHours;
}
