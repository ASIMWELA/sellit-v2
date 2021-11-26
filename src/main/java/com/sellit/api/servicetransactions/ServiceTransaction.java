package com.sellit.api.servicetransactions;

import com.sellit.api.payload.ApiResponse;
import com.sellit.api.payload.JsonResponse;
import com.sellit.api.payload.PagedResponse;
import com.sellit.api.servicetransactions.entity.*;
import org.springframework.http.ResponseEntity;

public interface ServiceTransaction {
    ResponseEntity<ApiResponse> saveServiceCategory(ServiceCategoryEntity serviceCategoryRequest);
    ResponseEntity<ApiResponse> saveService(ServiceEntity serviceEntity, String serviceCategoryUuid);
    ResponseEntity<PagedResponse> getServices(int pageNo, int pageSize);
    ResponseEntity<ApiResponse> requestService(String customerUuid, String serviceUuid, ServiceRequestEntity serviceRequest);
    ResponseEntity<ApiResponse> serviceDeliveryOffer(String serviceRequestUuid, String serviceProviderUuid, ServiceDeliveryOfferEntity serviceDeliveryOfferEntity);
    ResponseEntity<ApiResponse> acceptServiceOffer(String serviceDeliveryOfferUuid, ServiceAppointmentEntity serviceAppointmentEntity);
    ResponseEntity<JsonResponse> getServiceProviders(String serviceUuid);
    ResponseEntity<PagedResponse> getServiceRequests(int pageNo, int pageSize);
    ResponseEntity<JsonResponse> getOffersForARequest(String serviceRequestUuid);
    ResponseEntity<JsonResponse> getUserAppointments(String userUuid);
    ResponseEntity<JsonResponse> getProviderReviewLogs(String serviceProviderUuid);
    ResponseEntity<PagedResponse> getCategories(Integer pageNo, Integer pageSize);
    ResponseEntity<JsonResponse> getServiceRequests(String customerUuid);
    ResponseEntity<JsonResponse> getProviderServices(String providerUuid);
    ResponseEntity<JsonResponse> getProviderAppointments(String providerUuid);
}
