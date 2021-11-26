package com.sellit.api.customer.impl;

import com.sellit.api.role.RoleEntity;
import com.sellit.api.customer.CustomerService;
import com.sellit.api.user.entity.UserEntity;
import com.sellit.api.user.entity.UserAddressEntity;
import com.sellit.api.Enum.ERole;
import com.sellit.api.exception.EntityAlreadyExistException;
import com.sellit.api.exception.EntityNotFoundException;
import com.sellit.api.payload.ApiResponse;
import com.sellit.api.payload.customer.CustomerSignupRequest;
import com.sellit.api.role.RoleRepository;
import com.sellit.api.user.repository.UserAddressRepository;
import com.sellit.api.user.repository.UserRepository;
import com.sellit.api.utils.UuidGenerator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomerServiceImpl implements CustomerService {

    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;

    public CustomerServiceImpl(UserRepository userRepository,
                               PasswordEncoder passwordEncoder,
                               RoleRepository roleRepository,
                               UserAddressRepository addressRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @Override
    public ResponseEntity<ApiResponse> signupCustomer(CustomerSignupRequest customerSignupRequest){
        if(userRepository.existsByEmail(customerSignupRequest.getCustomer().getEmail())){
            throw new EntityAlreadyExistException("Email already taken");
        }
        if(userRepository.existsByUserName(customerSignupRequest.getCustomer().getUserName())){
            throw new EntityAlreadyExistException("UserEntity name already taken");
        }
        if(userRepository.existsByMobileNumber(customerSignupRequest.getCustomer().getMobileNumber())){
            throw new EntityAlreadyExistException("mobile number already taken");
        }

        log.info("Signup customer request ");
        UserEntity customer = UserEntity.builder()
                        .firstName(customerSignupRequest.getCustomer().getFirstName())
                        .email(customerSignupRequest.getCustomer().getEmail().toLowerCase())
                        .userName(customerSignupRequest.getCustomer().getUserName().toLowerCase())
                        .isEnabled(true)
                        .lastName(customerSignupRequest.getCustomer().getLastName())
                        .mobileNumber(customerSignupRequest.getCustomer().getMobileNumber())
                        .password(customerSignupRequest.getCustomer().getPassword())
                        .build();
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        customer.setUuid(UuidGenerator.generateRandomString(12));
        RoleEntity roleCustomer = roleRepository.findByName(ERole.ROLE_CUSTOMER).orElseThrow(
                ()->new EntityNotFoundException("RoleEntity not set")
        );
        customer.setRoles(Collections.singletonList(roleCustomer));

        UserAddressEntity customerAddress = UserAddressEntity
                .builder()
                .city(customerSignupRequest.getAddress().getCity())
                .country(customerSignupRequest.getAddress().getCountry())
                .region(customerSignupRequest.getAddress().getRegion())
                .locationDescription(customerSignupRequest.getAddress().getLocationDescription())
                .street(customerSignupRequest.getAddress().getStreet())
                .build();
        customerAddress.setUuid(UuidGenerator.generateRandomString(12));

        customer.setAddress(customerAddress);
        customerAddress.setUser(customer);
        log.info("Saving the customer");
        userRepository.save(customer);
        return new ResponseEntity<>(new ApiResponse(true, "customer saved"), HttpStatus.CREATED);
    }

}
