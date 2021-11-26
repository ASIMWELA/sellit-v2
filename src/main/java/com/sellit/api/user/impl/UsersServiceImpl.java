package com.sellit.api.user.impl;

import com.sellit.api.dto.user.UserDetailsDto;
import com.sellit.api.exception.EntityAlreadyExistException;
import com.sellit.api.exception.EntityNotFoundException;
import com.sellit.api.payload.ApiResponse;
import com.sellit.api.payload.PageMetadata;
import com.sellit.api.payload.PagedResponse;
import com.sellit.api.payload.user.UpdateUserAddressRequest;
import com.sellit.api.payload.user.UserUpdateRequest;
import com.sellit.api.user.UserService;
import com.sellit.api.user.repository.UserRepository;
import com.sellit.api.user.entity.UserAddressEntity;
import com.sellit.api.user.entity.UserEntity;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UsersServiceImpl implements UserService {
    UserRepository userRepository;

    public UsersServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public ResponseEntity<PagedResponse> getUsers(int pageNo, int pageSize) {
        log.info("Return users Request");
        Pageable pageRequest = PageRequest.of(pageNo, pageSize);
        Slice<UserEntity> users = userRepository.findAll(pageRequest);
        List<UserEntity> totalNum = userRepository.findAll();
        PageMetadata pageMetadata = PageMetadata.builder()
                .firstPage(users.isFirst())
                .lastPage(users.isLast())
                .pageNumber(users.getNumber())
                .pageSize(users.getSize())
                .numberOfRecordsOnPage(users.getNumberOfElements())
                .totalNumberOfRecords(totalNum.size())
                .hasNext(users.hasNext())
                .hasPrevious(users.hasPrevious())
                .build();
        List<UserDetailsDto> userList = new ArrayList<>();
        users.getContent().forEach(user -> {
            UserDetailsDto userDetailsDto = UserDetailsDto.builder().userName(user.getUserName())
                    .mobileNumber(user.getMobileNumber())
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .isEnabled(user.isEnabled())
                    .lastName(user.getLastName()).build();
            userList.add(userDetailsDto);
        });
        log.info("Returned Users");
        return new ResponseEntity<>(PagedResponse.builder().data(userList).pageMetadata(pageMetadata).build(), HttpStatus.OK);

    }

    @Override
    public ResponseEntity<ApiResponse> updateUserDetails(String userUuid, UserUpdateRequest userUpdateRequest) {
        UserEntity user = userRepository.findByUuid(userUuid).orElseThrow(
                () -> new EntityNotFoundException("No user with the provided identifier")
        );
        if (userUpdateRequest.getEmail() != null) {
            if (userRepository.existsByEmail(userUpdateRequest.getEmail())) {
                throw new EntityAlreadyExistException("Email already taken");
            }
            user.setEmail(userUpdateRequest.getEmail());
        }
        if (userUpdateRequest.getUserName() != null) {
            if (userRepository.existsByUserName(userUpdateRequest.getUserName())) {
                throw new EntityAlreadyExistException("UserEntity name already taken");
            }
            user.setUserName(userUpdateRequest.getUserName());
        }
        if (userUpdateRequest.getMobileNumber() != null) {
            if (userRepository.existsByMobileNumber(userUpdateRequest.getMobileNumber())) {
                throw new EntityAlreadyExistException("mobile number already taken");
            }
            user.setMobileNumber(userUpdateRequest.getMobileNumber());
        }
        if (userUpdateRequest.getPassword() != null) {
            user.setPassword(userUpdateRequest.getPassword());
        }
        if (userUpdateRequest.getFirstName() != null) {
            user.setFirstName(userUpdateRequest.getFirstName());
        }
        if (userUpdateRequest.getLastName() != null) {
            user.setLastName(userUpdateRequest.getLastName());
        }
        userRepository.save(user);
        log.info("Updated user {}", user.getUuid());
        return new ResponseEntity<>(new ApiResponse(true, "Details updated"), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ApiResponse> updateUserAddress(String userUuid, UpdateUserAddressRequest updateUserAddressRequest) {
        UserEntity user = userRepository.findByUuid(userUuid).orElseThrow(
                () -> new EntityNotFoundException("No user with the provided identifier")
        );
        if (user.getAddress() != null) {
            UserAddressEntity address = user.getAddress();
            if (updateUserAddressRequest.getCity() != null) {
                address.setCity(updateUserAddressRequest.getCity());
            }
            if (updateUserAddressRequest.getCountry() != null) {
                address.setCountry(updateUserAddressRequest.getCountry());
            }
            if (updateUserAddressRequest.getRegion() != null) {
                address.setRegion(updateUserAddressRequest.getRegion());
            }
            if (updateUserAddressRequest.getLocationDescription() != null) {
                address.setLocationDescription(updateUserAddressRequest.getLocationDescription());
            }
            if (updateUserAddressRequest.getStreet() != null) {
                address.setStreet(updateUserAddressRequest.getStreet());
            }
            user.setAddress(address);
            address.setUser(user);
            userRepository.save(user);
            log.info("Updated the address of {}", user.getUuid());
            return new ResponseEntity<>(new ApiResponse(true, "Address updated"), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiResponse(false, "you dont have address to update"), HttpStatus.OK);
        }
    }
}
