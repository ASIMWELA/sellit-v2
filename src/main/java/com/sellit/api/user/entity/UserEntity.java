package com.sellit.api.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sellit.api.commons.BaseEntity;
import com.sellit.api.role.RoleEntity;
import com.sellit.api.provider.entity.ProviderEntity;
import com.sellit.api.servicetransactions.entity.ServiceRequestEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Entity
@Table(name = "users")
@Builder
public class UserEntity extends BaseEntity {
    @Column(name="password", length = 70, nullable = false)
    @JsonIgnore
    String password;
    @Column(name="user_name", length = 90, nullable = false, unique = true)
    String userName;
    @Column(name="last_login")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Date lastLogin;
    @Column(name="first_name", length = 70, nullable = false)
    String firstName;
    @Column(name="last_name", length = 70, nullable = false)
    String lastName;
    @Column(name="email", length = 100, unique = true, nullable = false)
    String email;
    @Column(name="mobile_number", length = 30, unique = true, nullable = false)
    String mobileNumber;
    @Column(name="is_provider",length = 3, nullable = false)
    boolean isAProvider;
    @Column(name="is_enabled", length = 3, nullable = false)
    boolean isEnabled;
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    ProviderEntity providerEntityDetails;
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    UserAddressEntity address;
    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JoinTable(
            name="user_roles",
            joinColumns = @JoinColumn(name="user_id"),
            inverseJoinColumns = @JoinColumn(name="role_id"))
    List<RoleEntity> roles ;
    @OneToMany(mappedBy = "user")
    @LazyCollection(LazyCollectionOption.FALSE)
    @JsonIgnore 
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    List<ServiceRequestEntity> serviceRequests;
}
