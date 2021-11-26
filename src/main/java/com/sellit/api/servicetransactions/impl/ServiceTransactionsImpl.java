package com.sellit.api.servicetransactions.impl;

import com.sellit.api.Enum.ERole;
import com.sellit.api.dto.*;
import com.sellit.api.dto.customer.CustomerAppointmentDto;
import com.sellit.api.dto.customer.CustomerRequestDto;
import com.sellit.api.dto.provider.ProviderAppointmentDto;
import com.sellit.api.dto.provider.ProviderServicesDto;
import com.sellit.api.dto.provider.ServiceProviderDto;
import com.sellit.api.dto.service.ServiceRequestDto;
import com.sellit.api.dto.service.ServicesDto;
import com.sellit.api.event.AppointmentEvent;
import com.sellit.api.exception.EntityAlreadyExistException;
import com.sellit.api.exception.EntityNotFoundException;
import com.sellit.api.exception.OperationNotAllowedException;
import com.sellit.api.payload.ApiResponse;
import com.sellit.api.payload.JsonResponse;
import com.sellit.api.payload.PageMetadata;
import com.sellit.api.payload.PagedResponse;
import com.sellit.api.provider.entity.ProviderEntity;
import com.sellit.api.provider.entity.ProviderRatingEntity;
import com.sellit.api.provider.entity.ProviderReviewLogEntity;
import com.sellit.api.provider.entity.ServiceProviderEntity;
import com.sellit.api.provider.repository.ProviderRepository;
import com.sellit.api.provider.repository.ServiceProviderRepository;
import com.sellit.api.servicetransactions.*;
import com.sellit.api.servicetransactions.entity.*;
import com.sellit.api.servicetransactions.repository.*;
import com.sellit.api.user.entity.UserEntity;
import com.sellit.api.user.repository.UserRepository;
import com.sellit.api.utils.UuidGenerator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ServiceTransactionsImpl implements ServiceTransaction {

    ServiceCategoryRepository serviceCategoryRepository;
    ServiceRepository serviceRepository;
    UserRepository userRepository;
    ServiceRequestRepository serviceRequestRepository;
    ServiceProviderRepository serviceProviderRepository;
    ServiceDeliveryOfferRepository serviceDeliveryOfferRepository;
    ServiceAppointmentRepository serviceAppointmentRepository;
    ProviderRepository providerRepository;
    ApplicationEventPublisher eventPublisher;

    public ServiceTransactionsImpl(ServiceCategoryRepository serviceCategoryRepository, ServiceRepository serviceRepository, UserRepository userRepository, ServiceRequestRepository serviceRequestRepository, ServiceProviderRepository serviceProviderRepository, ServiceDeliveryOfferRepository serviceDeliveryOfferRepository, ServiceAppointmentRepository serviceAppointmentRepository, ProviderRepository providerRepository, ApplicationEventPublisher eventPublisher) {
        this.serviceCategoryRepository = serviceCategoryRepository;
        this.serviceRepository = serviceRepository;
        this.userRepository = userRepository;
        this.serviceRequestRepository = serviceRequestRepository;
        this.serviceProviderRepository = serviceProviderRepository;
        this.serviceDeliveryOfferRepository = serviceDeliveryOfferRepository;
        this.serviceAppointmentRepository = serviceAppointmentRepository;
        this.providerRepository = providerRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public ResponseEntity<ApiResponse> saveServiceCategory(ServiceCategoryEntity serviceCategoryRequest) {
        log.info("Save serviceEntity category request");
        if (serviceCategoryRepository.existsByServiceCategoryName(serviceCategoryRequest.getServiceCategoryName())) {
            log.error("There is an entity with the same category name : " + serviceCategoryRequest.getServiceCategoryName());
            throw new EntityAlreadyExistException("Category name already exists");
        }

        log.info("Building serviceEntity category information");
        ServiceCategoryEntity serviceCategoryEntity = ServiceCategoryEntity.builder().serviceCategoryName(serviceCategoryRequest.getServiceCategoryName()).build();
        serviceCategoryEntity.setUuid(UuidGenerator.generateRandomString(12));
        serviceCategoryRepository.save(serviceCategoryEntity);
        log.info("Saved serviceEntity category");
        return new ResponseEntity<>(new ApiResponse(true, "Category saved"), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<ApiResponse> saveService(ServiceEntity serviceEntity, String serviceCategoryUuid) {
        log.info("Save serviceEntity request");
        if (serviceRepository.existsByServiceName(serviceEntity.getServiceName())) {
            log.error("There is an entity with the same serviceEntity name : " + serviceEntity.getServiceName());

            throw new EntityAlreadyExistException("ServiceEntity name already exists");
        }
        log.info("Building serviceEntity information");
        ServiceCategoryEntity serviceCategoryEntity = serviceCategoryRepository.findByUuid(serviceCategoryUuid).orElseThrow(
                () -> new EntityNotFoundException("No category with the provided identifier")
        );
        serviceEntity.setUuid(UuidGenerator.generateRandomString(12));
        serviceEntity.setServiceCategory(serviceCategoryEntity);
        serviceRepository.save(serviceEntity);
        log.info("Saved serviceEntity information");
        return new ResponseEntity<>(new ApiResponse(true, "serviceEntity saved"), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<PagedResponse> getServices(int pageNo, int pageSize) {
        log.info("Get serviceEntity information request");
        Pageable pageRequest = PageRequest.of(pageNo, pageSize);
        Slice<ServiceEntity> services = serviceRepository.findAll(pageRequest);
        List<ServiceEntity> totalNumberOfServiceEntities = serviceRepository.findAll();
        PageMetadata pageMetadata = new PageMetadata();
        pageMetadata.setFirstPage(services.isFirst());
        pageMetadata.setLastPage(services.isLast());
        pageMetadata.setHasNext(services.hasNext());
        pageMetadata.setTotalNumberOfRecords(totalNumberOfServiceEntities.size());
        pageMetadata.setHasPrevious(services.hasPrevious());
        pageMetadata.setPageNumber(services.getNumber());
        pageMetadata.setPageSize(services.getSize());
        pageMetadata.setNumberOfRecordsOnPage(services.getNumberOfElements());
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        if (services.hasNext()) {
            pageMetadata.setNextPage(baseUrl + "/api/v1/serviceEntities?pageNo=" + (services.getNumber() + 1) + "&pageSize=" + services.getSize());
        }
        if (services.hasPrevious()) {
            pageMetadata.setNextPage(baseUrl + "/api/v1/serviceEntities?pageNo=" + (services.getNumber() - 1) + "&pageSize=" + services.getSize());
        }
        PagedResponse response = new PagedResponse();
        List<ServicesDto> servicesDtoList = new ArrayList<>();
        services.getContent().forEach(service -> {
            ServicesDto servicesDto = ServicesDto.builder()
                    .serviceName(service.getServiceName())
                    .uuid(service.getUuid())
                    .categoryName(service.getServiceCategory().getServiceCategoryName())
                    .build();
            servicesDtoList.add(servicesDto);
        });
        response.setData(servicesDtoList);
        response.setPageMetadata(pageMetadata);
        log.info("Returned serviceEntities information");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ApiResponse> requestService(String customerUuid, String serviceUuid, ServiceRequestEntity serviceRequest) {
        log.info("Requesting for a " + serviceUuid + " by " + customerUuid);
        UserEntity customer = userRepository.findByUuid(customerUuid).orElseThrow(
                () -> new EntityNotFoundException("No customer with the specified identifier")
        );

        ServiceEntity serviceEntity = serviceRepository.findByUuid(serviceUuid).orElseThrow(
                () -> new EntityNotFoundException("No serviceEntity with the specified identifier")
        );
        if (!(customer.getRoles().get(0).getName().name().equals(ERole.ROLE_CUSTOMER.name()))) {
            throw new OperationNotAllowedException("You not allowed to create a request");
        }
        serviceRequest.setUuid(UuidGenerator.generateRandomString(12));
        serviceRequest.setUser(customer);
        serviceRequest.setService(serviceEntity);
        serviceRequestRepository.save(serviceRequest);
        log.info("Saved the request");
        return new ResponseEntity<>(new ApiResponse(true, "serviceEntity request placed"), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ApiResponse> serviceDeliveryOffer(String serviceRequestUuid, String serviceProviderUuid, ServiceDeliveryOfferEntity serviceDeliveryOfferEntity) {
        log.info("ServiceEntity offer on " + serviceProviderUuid + " by " + serviceProviderUuid);
        ServiceRequestEntity serviceRequest = serviceRequestRepository.findByUuid(serviceRequestUuid).orElseThrow(
                () -> new EntityNotFoundException("No request was made for the identifier provided")
        );

        ServiceProviderEntity serviceProviderEntity = serviceProviderRepository.findByUuid(serviceProviderUuid).orElseThrow(
                () -> new EntityNotFoundException("No service providerEntity found with the given identifier")
        );

        log.info("Building serviceEntity delivery offer information");
        serviceDeliveryOfferEntity.setUuid(UuidGenerator.generateRandomString(12));
        serviceDeliveryOfferEntity.setServiceRequest(serviceRequest);
        serviceDeliveryOfferEntity.setServiceProvider(serviceProviderEntity);
        serviceDeliveryOfferEntity.setOfferAccepted(false);
        serviceDeliveryOfferEntity.setOfferSubmissionDate(new Date());
        serviceDeliveryOfferRepository.save(serviceDeliveryOfferEntity);
        log.info("Saved the offer details");
        return new ResponseEntity<>(new ApiResponse(true, "offer placed"), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ApiResponse> acceptServiceOffer(String serviceDeliveryOfferUuid, ServiceAppointmentEntity serviceAppointmentEntity) {
        log.info("Accepting " + serviceDeliveryOfferUuid + " offer");
        ServiceDeliveryOfferEntity serviceDeliveryOfferEntity = serviceDeliveryOfferRepository.findByUuid(serviceDeliveryOfferUuid).orElseThrow(
                () -> new EntityNotFoundException("No serviceEntity delivery offer with the given identifier"));

        serviceDeliveryOfferEntity.setOfferAccepted(true);
        serviceAppointmentEntity.setServiceDeliveredOn(new Date());
        serviceAppointmentEntity.setServiceDeliveryOffer(serviceDeliveryOfferEntity);
        serviceAppointmentEntity.setUuid(UuidGenerator.generateRandomString(12));
        ServiceAppointmentEntity serviceAppointmentEntity1 = serviceAppointmentRepository.save(serviceAppointmentEntity);
        serviceDeliveryOfferRepository.save(serviceDeliveryOfferEntity);
        AppointmentEvent appointmentEvent = new AppointmentEvent(serviceAppointmentEntity1.getUuid());
        eventPublisher.publishEvent(appointmentEvent);
        log.info("Accepted Offer " + serviceDeliveryOfferUuid);
        return new ResponseEntity<>(new ApiResponse(true, "Appointment created successfully"), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<JsonResponse> getServiceProviders(String serviceUuid) {
        log.info("Get providers for ServiceEntity : {}", serviceUuid);
        List<ServiceProviderDto> serviceProviderDtos = new ArrayList<>();
        ServiceEntity serviceEntity = serviceRepository.findByUuid(serviceUuid).orElseThrow(() -> new EntityNotFoundException("No serviceEntity with the given identifier"));
        serviceEntity.getServiceProviderEntities().forEach(provider->{
            UserEntity user = provider.getProvider().getUser();
            ProviderRatingEntity providerRatingEntity =  provider.getProvider().getProviderRating();
            ServiceProviderDto dto = ServiceProviderDto.builder()
                    .uuid(provider.getUuid())
                    .avgCommunicationRating(providerRatingEntity != null?String.valueOf(providerRatingEntity.getAvgCommunicationRating()):"0.0")
                    .avgPriceRating(providerRatingEntity != null?String.valueOf(providerRatingEntity.getAvgPriceRating()):"0.0")
                    .avgPunctualityRating(providerRatingEntity != null?String.valueOf(providerRatingEntity.getAvgPunctualityRating()):"0.0")
                    .overallRating(providerRatingEntity != null?String.valueOf(providerRatingEntity.getOverallRating()):"0.0")
                    .billingRatePerHour(String.valueOf(provider.getBillingRatePerHour()))
                    .experienceInMonths(String.valueOf(provider.getExperienceInMonths()))
                    .providerEmail(user.getEmail())
                    .providerName(user.getFirstName()+" "+user.getLastName())
                    .build();
            serviceProviderDtos.add(dto);
        });
        JsonResponse res = JsonResponse.builder().data(serviceProviderDtos).build();
        log.info("Returned Providers for serviceEntity : {}", serviceUuid);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<PagedResponse> getServiceRequests(int pageNo, int pageSize) {
        log.info("Get serviceEntity requests");
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Slice<ServiceRequestEntity> requests = serviceRequestRepository.findAll(pageable);
        List<ServiceRequestEntity> totalNum = serviceRequestRepository.findAll();
        PageMetadata pageMetadata = PageMetadata.builder()
                .firstPage(requests.isFirst())
                .lastPage(requests.isLast())
                .pageNumber(requests.getNumber())
                .pageSize(requests.getSize())
                .numberOfRecordsOnPage(requests.getNumberOfElements())
                .totalNumberOfRecords(totalNum.size())
                .hasNext(requests.hasNext())
                .hasPrevious(requests.hasPrevious())
                .build();
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        if (requests.hasNext()) {
            pageMetadata.setNextPage(baseUrl + "/api/v1/serviceEntities/requests?pageNo=" + (requests.getNumber() + 1) + "&pageSize=" + requests.getSize());
        }
        if (requests.hasPrevious()) {
            pageMetadata.setNextPage(baseUrl + "/api/v1/serviceEntities/requests?pageNo=" + (requests.getNumber() - 1) + "&pageSize=" + requests.getSize());
        }

        List<ServiceRequestDto> requestDtos = new ArrayList<>();

        requests.getContent().forEach(request -> {
            SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");
            ServiceRequestDto requestDto =
                    ServiceRequestDto.builder()
                            .uuid(request.getUuid())
                            .requestDescription(request.getRequirementDescription())
                            .expectedHours(request.getExpectedTentativeEffortRequiredInHours())
                            .expectedStartTime(timeFormatter.format(request.getExpectedStartTime()))
                            .requiredDate(dateFormatter.format(request.getRequiredOn()))
                            .requestBy(request.getUser().getFirstName() + " " + request.getUser().getLastName())
                            .country(request.getUser().getAddress().getCountry())
                            .email(request.getUser().getEmail())
                            .locationCity(request.getUser().getAddress().getCity())
                            .build();
            requestDtos.add(requestDto);

        });
        PagedResponse pagedResponse = PagedResponse.builder().pageMetadata(pageMetadata)
                .data(requestDtos).build();
        log.info("Returned serviceEntity requests");
        return new ResponseEntity<>(pagedResponse, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<JsonResponse> getOffersForARequest(String serviceRequestUuid) {
        log.info("Get offers for serviceEntity : {}", serviceRequestUuid);
        ServiceRequestEntity request = serviceRequestRepository.findByUuid(serviceRequestUuid).orElseThrow(() -> new EntityNotFoundException("No request with the given identifier"));
        List<ServiceDeliveryOfferEntity> offers = request.getServiceDeliveryOfferEntities();

        List<OfferPackage> offerDtos = new ArrayList<>();

        offers.forEach(offer -> {
            if (!offer.isOfferAccepted()) {
                UserEntity user = offer.getServiceProvider().getProvider().getUser();
                ProviderEntity providerEntity = offer.getServiceProvider().getProvider();
                ServiceProviderEntity serviceProviderEntity = offer.getServiceProvider();
                ProviderRatingEntity providerRatingEntity = offer.getServiceProvider().getProvider().getProviderRating();

                Calendar c = Calendar.getInstance();
                c.setTime(offer.getOfferSubmissionDate());

                String submissionDate = c.get(Calendar.DAY_OF_MONTH) + "-" + c.get(Calendar.MONTH) + "-" + c.get(Calendar.YEAR);
                String overRating;

                if (providerRatingEntity != null) {
                    overRating = String.valueOf(providerRatingEntity.getOverallRating());
                } else {
                    overRating = "0.0";
                }
                OfferPackage offerPackage =
                        OfferPackage.builder()
                                .estimatedCost(offer.getEstimatedCost())
                                .offerSubmissionDate(offer.getOfferSubmissionDate())
                                .discountInPercent(offer.getDiscountInPercent())
                                .offerBy(user.getFirstName() + " " + user.getLastName())
                                .experienceInMonths(String.valueOf(serviceProviderEntity.getExperienceInMonths()))
                                .email(user.getEmail())
                                .mobileNumber(user.getMobileNumber())
                                .location(providerEntity.getOfficeAddress())
                                .overallRating(overRating)
                                .submissionDate(submissionDate)
                                .uuid(offer.getUuid()).build();
                offerDtos.add(offerPackage);
            }

        });

        log.info("Returned Offers for serviceEntity : {}", serviceRequestUuid);
        return ResponseEntity.ok(new JsonResponse(offerDtos));
    }

    @Override
    public ResponseEntity<JsonResponse> getUserAppointments(String userUuid) {
        UserEntity user = userRepository.findByUuid(userUuid).orElseThrow(
                () -> new EntityNotFoundException("No user with the identifier provided")
        );
        List<ServiceRequestEntity> serviceRequest = user.getServiceRequests();
        List<CustomerAppointmentDto> serviceAppointments = new ArrayList<>();
        if (serviceRequest.size() > 0) {
            serviceRequest.forEach(request -> {
                request.getServiceDeliveryOfferEntities().forEach(offer -> {
                    if (offer.getServiceAppointment() != null) {
                        ServiceAppointmentEntity appointment = offer.getServiceAppointment();
                        UserEntity u = offer.getServiceProvider().getProvider().getUser();
                        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
                        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");

                        CustomerAppointmentDto appointmentDto =
                                CustomerAppointmentDto.builder()
                                        .uuid(appointment.getUuid())
                                        .appointmentDate(dateFormatter.format(appointment.getServiceDeliveredOn()))
                                        .appointmentDesc(appointment.getAppointmentDescription())
                                        .appointmentWith(u.getFirstName() + " " + u.getLastName())
                                        .appointmentStartTime(timeFormatter.format(appointment.getServiceStartTime()))
                                        .providerEmail(u.getEmail())
                                        .appointmentEndTime(timeFormatter.format(appointment.getServiceEndTime()))
                                        .providerPhone(u.getMobileNumber())
                                        .build();
                        serviceAppointments.add(appointmentDto);


                    }
                });
            });
        }
        return ResponseEntity.ok(JsonResponse.builder().data(serviceAppointments).build());
    }

    @Override
    public ResponseEntity<JsonResponse> getProviderReviewLogs(String serviceProviderUuid) {
        log.info("Get providerEntity review logs requests");
        ServiceProviderEntity serviceProviderEntity = serviceProviderRepository.findByUuid(serviceProviderUuid).orElseThrow(
                () -> new EntityNotFoundException("No providerEntity with the identifier provided")
        );
        List<ProviderReviewLogEntity> logs = new ArrayList<>();
        serviceProviderEntity.getServiceDeliveryOffers().forEach(offer -> {
            if (offer.getServiceAppointment() != null) {
                if (offer.getServiceAppointment().getProviderReviewLogs().size() > 0) {
                    logs.addAll(offer.getServiceAppointment().getProviderReviewLogs());
                }
            }

        });

        return ResponseEntity.ok(JsonResponse.builder().data(logs).build());
    }

    @Override
    public ResponseEntity<PagedResponse> getCategories(Integer pageNo, Integer pageSize) {
        log.info("Get serviceEntity requests");
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Slice<ServiceCategoryEntity> categories = serviceCategoryRepository.findAll(pageable);
        List<ServiceCategoryEntity> totalNum = serviceCategoryRepository.findAll();
        PageMetadata pageMetadata = PageMetadata.builder()
                .firstPage(categories.isFirst())
                .lastPage(categories.isLast())
                .pageNumber(categories.getNumber())
                .pageSize(categories.getSize())
                .numberOfRecordsOnPage(categories.getNumberOfElements())
                .totalNumberOfRecords(totalNum.size())
                .hasNext(categories.hasNext())
                .hasPrevious(categories.hasPrevious())
                .build();
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        if (categories.hasNext()) {
            pageMetadata.setNextPage(baseUrl + "/api/v1/serviceEntities/categories?pageNo=" + (categories.getNumber() + 1) + "&pageSize=" + categories.getSize());
        }
        if (categories.hasPrevious()) {
            pageMetadata.setNextPage(baseUrl + "/api/v1/serviceEntities/categories?pageNo=" + (categories.getNumber() - 1) + "&pageSize=" + categories.getSize());
        }
        PagedResponse pagedResponse = PagedResponse.builder().pageMetadata(pageMetadata)
                .data(categories.getContent()).build();
        log.info("Returned serviceEntity categories requests");
        return ResponseEntity.ok(pagedResponse);
    }

    @Override
    public ResponseEntity<JsonResponse> getServiceRequests(String customerUuid) {
        log.info("Get serviceEntity requests for {}", customerUuid);
        UserEntity customer = userRepository.findByUuid(customerUuid).orElseThrow(
                () -> new EntityNotFoundException("No customer with the provided identifier")
        );
        List<ServiceRequestEntity> requests = customer.getServiceRequests();

        List<CustomerRequestDto> customerRequestDtos = new ArrayList<>();
        requests.forEach(request -> {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            CustomerRequestDto requestDto = CustomerRequestDto.builder()
                    .uuid(request.getUuid())
                    .expectedHours(String.valueOf(request.getExpectedTentativeEffortRequiredInHours()))
                    .expectedStartTime(timeFormat.format(request.getExpectedStartTime()))
                    .requirementDescription(request.getRequirementDescription())
                    .requiredOn(simpleDateFormat.format(request.getRequiredOn()))
                    .build();
            customerRequestDtos.add(requestDto);
        });
        log.info("Returned serviceEntity requests for customer {}", customer.getUuid());
        return ResponseEntity.ok(JsonResponse.builder().data(customerRequestDtos).build());
    }

    @Override
    public ResponseEntity<JsonResponse> getProviderServices(String providerUuid) {

        ProviderEntity providerEntity = providerRepository.findByUuid(providerUuid).orElseThrow(
                () -> new EntityNotFoundException("No providerEntity with the providerEntity identifier")
        );
        List<ProviderServicesDto> providerServices = new ArrayList<>();
        if (providerEntity.getServices().size() > 0) {
            providerEntity.getServices().forEach(serviceProvider -> {
                ServiceEntity s = serviceProvider.getService();
                ServiceCategoryEntity category = s.getServiceCategory();
                ProviderServicesDto providerServicesDto = ProviderServicesDto.builder()
                        .serviceName(s.getServiceName())
                        .serviceCategoryName(category.getServiceCategoryName())
                        .serviceCategoryUuid(category.getUuid())
                        .serviceUuid(s.getUuid())
                        .billingRatePerHour(serviceProvider.getBillingRatePerHour())
                        .experienceInMonths(serviceProvider.getExperienceInMonths())
                        .build();
                providerServices.add(providerServicesDto);
            });
        }
        JsonResponse jsonResponse = JsonResponse.builder().data(providerServices).build();
        return ResponseEntity.ok(jsonResponse);
    }

    @Override
    public ResponseEntity<JsonResponse> getProviderAppointments(String providerUuid) {
        UserEntity user = userRepository.findByUuid(providerUuid).orElseThrow(
                () -> new EntityNotFoundException("No user with the provided identifier")
        );
        if (!user.isAProvider()) {
            throw new OperationNotAllowedException("You are not a providerEntity");
        }
        List<ProviderAppointmentDto> providerAppointments = new ArrayList<>();
        ProviderEntity providerEntityDetails = user.getProviderEntityDetails();
        providerEntityDetails.getServices().forEach(service -> {
            service.getServiceDeliveryOffers().forEach(offer -> {
                if (offer.getServiceAppointment() != null) {
                    ServiceAppointmentEntity appointment = offer.getServiceAppointment();
                    UserEntity customer = appointment.getServiceDeliveryOffer().getServiceRequest().getUser();
                    SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
                    SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");
                    ProviderAppointmentDto providerAppointmentDto =
                            ProviderAppointmentDto.builder()
                                    .uuid(appointment.getUuid())
                                    .appointmentDate(dateFormatter.format(appointment.getServiceDeliveredOn()))
                                    .appointmentDesc(appointment.getAppointmentDescription())
                                    .appointmentEndTime(timeFormatter.format(appointment.getServiceEndTime()))
                                    .appointmentStartTime(timeFormatter.format(appointment.getServiceStartTime()))
                                    .appointmentWith(customer.getFirstName() + " " + customer.getLastName())
                                    .customerEmail(customer.getEmail())
                                    .customerPhone(customer.getMobileNumber())
                                    .build();

                    providerAppointments.add(providerAppointmentDto);
                }
            });
        });
        return ResponseEntity.ok(JsonResponse.builder().data(providerAppointments).build());
    }
}
