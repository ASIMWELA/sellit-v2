package com.sellit.api.user.hateos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.Column;
import java.util.Date;


@Getter
@Setter

public class UserDto extends RepresentationModel<UserDto> {

    String userName;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    Date lastLogin;

    String firstName;

    String lastName;

    String email;

    String mobileNumber;

    boolean isAProvider;

    boolean isEnabled;
}
