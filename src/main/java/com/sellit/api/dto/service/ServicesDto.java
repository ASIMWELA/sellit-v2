package com.sellit.api.dto.service;

import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServicesDto {
    String uuid;
    String serviceName;
    String categoryName;
}
