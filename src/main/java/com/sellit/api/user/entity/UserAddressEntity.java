package com.sellit.api.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sellit.api.commons.BaseEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Entity
@Table(name = "user_addresses")
@Builder
public class UserAddressEntity extends BaseEntity {
    @Column(length = 100, nullable = false)
    String city;
    @Column(length = 60, nullable = false)
    String country;
    @Column(length = 60, nullable = false)
    String region;
    @Column(length = 60, nullable = false)
    String street;
    @Column(length = 500, name = "location_description", nullable = false)
    String locationDescription;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="user_id", referencedColumnName = "id", unique = true)
    @JsonIgnore
    UserEntity user;
}
