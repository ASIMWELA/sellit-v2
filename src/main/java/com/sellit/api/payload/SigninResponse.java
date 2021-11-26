package com.sellit.api.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sellit.api.user.entity.UserEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class SigninResponse {
    TokenPayload tokenPayload;
    UserEntity userData;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String serviceProviderUuid;
}
