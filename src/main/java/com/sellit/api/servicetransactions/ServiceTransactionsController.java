package com.sellit.api.servicetransactions;


import com.sellit.api.payload.ApiResponse;
import com.sellit.api.payload.JsonResponse;
import com.sellit.api.payload.PagedResponse;
import com.sellit.api.servicetransactions.entity.*;
import com.sellit.api.servicetransactions.impl.ServiceTransactionsImpl;
import lombok.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/api/v1/services")
@Validated
public class ServiceTransactionsController {

    private final ServiceTransactionsImpl serviceTransactions;
    public ServiceTransactionsController(ServiceTransactionsImpl serviceTransactions) {
        this.serviceTransactions = serviceTransactions;
    }
    @Secured({"ROLE_PROVIDER", "ROLE_ADMIN"})
    @PostMapping("/categories")
    public ResponseEntity<ApiResponse> saveServiceCategory(@RequestBody @Valid ServiceCategoryEntity serviceCategoryEntity){
        return serviceTransactions.saveServiceCategory(serviceCategoryEntity);
    }

    @GetMapping("/categories")
    public ResponseEntity<PagedResponse> getServiceCategories(@PositiveOrZero(message = "page number cannot be negative") @RequestParam(defaultValue = "0") Integer pageNo, @Positive @RequestParam(defaultValue = "10") Integer pageSize){
        return serviceTransactions.getCategories(pageNo, pageSize);
    }
    @Secured({"ROLE_PROVIDER", "ROLE_ADMIN"})
    @PostMapping("/{categoryUuid}/save-service")
    @Transactional
    public ResponseEntity<ApiResponse> saveService(@RequestBody @Valid ServiceEntity serviceEntity, @PathVariable("categoryUuid") String categoryUuid){
        return serviceTransactions.saveService(serviceEntity, categoryUuid);
    }
    @GetMapping
    public ResponseEntity<PagedResponse> getServices(@PositiveOrZero(message = "page number cannot be negative") @RequestParam(defaultValue = "0") Integer pageNo, @Positive @RequestParam(defaultValue = "10") Integer pageSize){
        return serviceTransactions.getServices(pageNo, pageSize);
    }
    @Secured("ROLE_CUSTOMER")
    @PostMapping("/{customerUuid}/{serviceUuid}/request-service")
    @Transactional
    public ResponseEntity<ApiResponse> requestService(@PathVariable @NonNull String customerUuid, @PathVariable @NonNull String serviceUuid, @RequestBody @Valid ServiceRequestEntity serviceRequest){
        return serviceTransactions.requestService(customerUuid, serviceUuid, serviceRequest);
    }
    @Secured("ROLE_PROVIDER")
    @PostMapping("/{serviceRequestUuid}/{serviceProviderUuid}/make-offer")
    @Transactional
    public ResponseEntity<ApiResponse> serviceDeliveryOffer(@NonNull @PathVariable String serviceRequestUuid, @NonNull @PathVariable String serviceProviderUuid, @RequestBody @Valid ServiceDeliveryOfferEntity serviceDeliveryOfferEntity){
        return serviceTransactions.serviceDeliveryOffer(serviceRequestUuid, serviceProviderUuid, serviceDeliveryOfferEntity);
    }
    @Secured("ROLE_CUSTOMER")
    @PostMapping("/{serviceDeliveryOfferUuid}/complete-offer")
    @Transactional
    public ResponseEntity<ApiResponse> acceptServiceOffer(@NonNull @PathVariable String serviceDeliveryOfferUuid, @RequestBody @Valid ServiceAppointmentEntity serviceAppointmentEntity){
        return serviceTransactions.acceptServiceOffer(serviceDeliveryOfferUuid, serviceAppointmentEntity);
    }

    @GetMapping("/{serviceUuid}/providers")
    public ResponseEntity<JsonResponse> getServiceProviders(@NonNull @PathVariable String serviceUuid){
        return serviceTransactions.getServiceProviders(serviceUuid);
    }
    @GetMapping("/requests")
    public ResponseEntity<PagedResponse> getServiceRequests(@PositiveOrZero(message = "page number cannot be negative") @RequestParam(defaultValue = "0") Integer pageNo, @Positive @RequestParam(defaultValue = "10") Integer pageSize){
        return serviceTransactions.getServiceRequests(pageNo, pageSize);
    }
    @GetMapping("/requests/{customerUuid}")
    public ResponseEntity<JsonResponse> getServiceRequests(@PathVariable String customerUuid){
        return serviceTransactions.getServiceRequests(customerUuid);
    }
    @GetMapping("/requests/{requestUuid}/offers")
    public ResponseEntity<JsonResponse> getRequestOffers(@PathVariable @NonNull String requestUuid){
        return serviceTransactions.getOffersForARequest(requestUuid);
    }

    @GetMapping("/appointments/{userUuid}")
    public ResponseEntity<JsonResponse> getUserAppointments(@PathVariable @NonNull String userUuid){
        return serviceTransactions.getUserAppointments(userUuid);
    }
    @GetMapping("/service-provider/{serviceProviderUuid}/review-logs")
    @Transactional
    public ResponseEntity<JsonResponse> getProviderReviewLogs(@PathVariable String serviceProviderUuid){
        return serviceTransactions.getProviderReviewLogs(serviceProviderUuid);
    }

    @GetMapping("/{providerUuid}")
    @Transactional
    public ResponseEntity<JsonResponse> getProviderServices(@PathVariable @NonNull String providerUuid){
        return serviceTransactions.getProviderServices(providerUuid);
    }

    @GetMapping("/{providerUuid}/appointments")
    @Transactional
    public ResponseEntity<JsonResponse> getProviderAppointments(@PathVariable @NonNull String providerUuid){
        return serviceTransactions.getProviderAppointments(providerUuid);
    }
}
