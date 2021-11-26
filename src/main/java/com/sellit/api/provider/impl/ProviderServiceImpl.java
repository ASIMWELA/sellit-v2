package com.sellit.api.provider.impl;

import com.sellit.api.Enum.ERole;
import com.sellit.api.event.NewProviderReviewEvent;
import com.sellit.api.exception.EntityAlreadyExistException;
import com.sellit.api.exception.EntityNotFoundException;
import com.sellit.api.exception.OperationNotAllowedException;
import com.sellit.api.payload.ApiResponse;
import com.sellit.api.payload.provider.ProviderSignupRequest;
import com.sellit.api.payload.provider.UpdateProviderDetailsRequest;
import com.sellit.api.payload.provider.UpdateServiceProviderRequest;
import com.sellit.api.provider.entity.ProviderEntity;
import com.sellit.api.provider.entity.ProviderReviewLogEntity;
import com.sellit.api.provider.ProviderService;
import com.sellit.api.provider.entity.ServiceProviderEntity;
import com.sellit.api.provider.repository.ProviderRepository;
import com.sellit.api.provider.repository.ProviderReviewLogRepository;
import com.sellit.api.provider.repository.ServiceProviderRepository;
import com.sellit.api.role.RoleEntity;
import com.sellit.api.role.RoleRepository;
import com.sellit.api.servicetransactions.repository.ServiceAppointmentRepository;
import com.sellit.api.servicetransactions.repository.ServiceRepository;
import com.sellit.api.servicetransactions.entity.ServiceAppointmentEntity;
import com.sellit.api.servicetransactions.entity.ServiceEntity;
import com.sellit.api.user.entity.UserEntity;
import com.sellit.api.user.repository.UserRepository;
import com.sellit.api.utils.UuidGenerator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Collections;
import java.util.Date;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProviderServiceImpl implements ProviderService {
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;
    ProviderRepository providerRepository;
    ServiceProviderRepository serviceProviderRepository;
    ServiceRepository serviceRepository;
    ServiceAppointmentRepository serviceAppointmentRepository;
    ProviderReviewLogRepository providerReviewLogRepository;
    ApplicationEventPublisher applicationEventPublisher;

    public ProviderServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository, ProviderRepository providerRepository, ServiceProviderRepository serviceProviderRepository, ServiceRepository serviceRepository, ServiceAppointmentRepository serviceAppointmentRepository, ProviderReviewLogRepository providerReviewLogRepository, ApplicationEventPublisher applicationEventPublisher) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.providerRepository = providerRepository;
        this.serviceProviderRepository = serviceProviderRepository;
        this.serviceRepository = serviceRepository;
        this.serviceAppointmentRepository = serviceAppointmentRepository;
        this.providerReviewLogRepository = providerReviewLogRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public ResponseEntity<ApiResponse> signupProvider(ProviderSignupRequest providerSignupRequest){
        if(userRepository.existsByUserName(providerSignupRequest.getUserName())){
            throw new EntityAlreadyExistException("user name already taken");
        }

        if(userRepository.existsByEmail(providerSignupRequest.getEmail())){
            throw new EntityAlreadyExistException("email already taken");
        }
        if(userRepository.existsByMobileNumber(providerSignupRequest.getMobileNumber())){
            throw new EntityAlreadyExistException("mobile number already taken");
        }

        log.info("Customer signup request");
        UserEntity provider = UserEntity.builder()
                .firstName(providerSignupRequest.getFirstName())
                .email(providerSignupRequest.getEmail().toLowerCase())
                .userName(providerSignupRequest.getUserName().toLowerCase())
                .isEnabled(true)
                .lastName(providerSignupRequest.getLastName())
                .mobileNumber(providerSignupRequest.getMobileNumber())
                .isAProvider(true)
                .password(providerSignupRequest.getPassword())
                .build();
        provider.setPassword(passwordEncoder.encode(provider.getPassword()));
        provider.setUuid(UuidGenerator.generateRandomString(12));
        RoleEntity roleCustomer = roleRepository.findByName(ERole.ROLE_PROVIDER).orElseThrow(
                ()->new EntityNotFoundException("RoleEntity not set")
        );
        provider.setRoles(Collections.singletonList(roleCustomer));
        log.info("Building providerEntity details");
        ProviderEntity providerEntityDetails = ProviderEntity.builder()
                .providerDescription(providerSignupRequest.getProviderDescription())
                .isIndividual(providerSignupRequest.isIndividual())
                .isRegisteredOffice(providerSignupRequest.isRegisteredOffice())
                .officeAddress(providerSignupRequest.getOfficeAddress())
                .build();
        if(!providerEntityDetails.isRegisteredOffice()){
            providerEntityDetails.setOfficeAddress(null);
        }
        providerEntityDetails.setUuid(UuidGenerator.generateRandomString(12));
        provider.setProviderEntityDetails(providerEntityDetails);
        providerEntityDetails.setUser(provider);

        log.info("Saving providerEntity");
        userRepository.save(provider);
        return new ResponseEntity(new ApiResponse(true, "Login to add serviceEntities to be complete the process"), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<ApiResponse> assignServiceToProvider(String serviceUuid, String providerUuid, ServiceProviderEntity serviceProviderEntityRequest){
        log.info("Adding a serviceEntity to a providerEntity");
        ProviderEntity providerEntity = providerRepository.findByUuid(providerUuid).orElseThrow(
                ()-> new EntityNotFoundException("No providerEntity with the identifier provided")
        );


        ServiceEntity serviceEntity = serviceRepository.findByUuid(serviceUuid).orElseThrow(
                ()->new EntityNotFoundException("No serviceEntity with the provided identifier")
        );

        if(!providerEntity.getUser().isAProvider()){
            throw new OperationNotAllowedException("You are not a providerEntity");
        }

        providerEntity.getServices().forEach(serv->{
            if(serv.getService().getServiceName().equalsIgnoreCase(serviceEntity.getServiceName())){
                throw new EntityAlreadyExistException("ServiceEntity already exist on your list");
            }
        });
        log.info("Building serviceEntity to a providerEntity relationship");
        ServiceProviderEntity serviceProviderEntity = ServiceProviderEntity.builder()
                .experienceInMonths(serviceProviderEntityRequest.getExperienceInMonths())
                .serviceOfferingDescription(serviceProviderEntityRequest.getServiceOfferingDescription())
                .billingRatePerHour(serviceProviderEntityRequest.getBillingRatePerHour())
                .provider(providerEntity)
                .service(serviceEntity)
                .build();
        serviceProviderEntity.setUuid(UuidGenerator.generateRandomString(12));
        log.info("Saving a serviceEntity-providerEntity relationship");
        serviceProviderRepository.save(serviceProviderEntity);

        return ResponseEntity.ok(new ApiResponse(true, "serviceEntity added to your list"));
    }

    @Override
    public ResponseEntity<ApiResponse> submitProviderReview(String serviceAppointmentUuid, Principal principal, ProviderReviewLogEntity providerReviewLogEntity){
       log.info("Request to review a providerEntity");
        ServiceAppointmentEntity serviceAppointmentEntity = serviceAppointmentRepository.findByUuid(serviceAppointmentUuid).orElseThrow(
                ()->new EntityNotFoundException("No serviceEntity appointment with the provided identifier")
        );

        String authenticatedUserName = principal.getName();

        if(!serviceAppointmentEntity.getServiceDeliveryOffer().isOfferAccepted()){
            log.error("Unaccepted review rejected");
            throw new OperationNotAllowedException("You cannot review unaccepted offer");
        }

        if(!serviceAppointmentEntity.getServiceDeliveryOffer().getServiceRequest().getUser().getUserName().equals(authenticatedUserName)){
           log.error("Unaccepted review rejected");
            throw new OperationNotAllowedException("You cannot review a providerEntity whom you dont have a completed appointment with");
        }

        providerReviewLogEntity.setReviewDate(new Date());
        providerReviewLogEntity.setUuid(UuidGenerator.generateRandomString(12));
        double avgReview = (providerReviewLogEntity.getAvgPunctualityRating()
                + providerReviewLogEntity.getAvgProficiencyRating()
                + providerReviewLogEntity.getAvgPriceRating()
                + providerReviewLogEntity.getAvgProfessionalismRating()
                + providerReviewLogEntity.getAvgCommunicationRating())/5.0;
        providerReviewLogEntity.setOverallRating(avgReview);
        providerReviewLogEntity.setServiceAppointment(serviceAppointmentEntity);
        providerReviewLogRepository.save(providerReviewLogEntity);
        NewProviderReviewEvent event = new NewProviderReviewEvent(providerReviewLogEntity.getUuid());
        applicationEventPublisher.publishEvent(event);
        return new ResponseEntity<>(new ApiResponse(true, "Review success"), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ApiResponse> updateProvideDetails(String providerUuid, UpdateProviderDetailsRequest request){
        ServiceProviderEntity serviceProviderEntity = serviceProviderRepository.findByUuid(providerUuid).orElseThrow(
                ()->new EntityNotFoundException("No ProviderEntity found with the identifier provided")
        );

        ProviderEntity providerEntity = serviceProviderEntity.getProvider();

        if(request.isIndividual() != providerEntity.isIndividual()){
            providerEntity.setIndividual(request.isIndividual());
        }
        if(request.isRegisteredOffice() != providerEntity.isRegisteredOffice()){
            providerEntity.setRegisteredOffice(request.isRegisteredOffice());
        }
        if (request.getProviderDescription() != null) {
            providerEntity.setProviderDescription(request.getProviderDescription());
        }
        if(request.getOfficeAddress() != null){
            providerEntity.setOfficeAddress(request.getOfficeAddress());
        }
        providerRepository.save(providerEntity);

        return new ResponseEntity<>(new ApiResponse(true, "Details updated"), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ApiResponse> updateServiceProviderDetails(String serviceProviderUuid, UpdateServiceProviderRequest request){

        ServiceProviderEntity serviceProviderEntity = serviceProviderRepository.findByUuid(serviceProviderUuid).orElseThrow(
                ()-> new EntityNotFoundException("No providerEntity with the provided identifier")
        );

        if(request.getServiceOfferingDescription() != null){
            serviceProviderEntity.setServiceOfferingDescription(request.getServiceOfferingDescription());
        }
        if(request.getBillingRatePerHour() != 0.0){

            serviceProviderEntity.setBillingRatePerHour(request.getBillingRatePerHour());
        }
        if(request.getExperienceInMonths() != 0){
            serviceProviderEntity.setExperienceInMonths(request.getExperienceInMonths());
        }
        serviceProviderRepository.save(serviceProviderEntity);
        return new ResponseEntity<>(new ApiResponse(true, "Updated successfully"), HttpStatus.OK);
    }

}
