package com.sellit.api.provider;

import com.sellit.api.payload.ApiResponse;
import com.sellit.api.payload.provider.ProviderSignupRequest;
import com.sellit.api.payload.provider.UpdateProviderDetailsRequest;
import com.sellit.api.payload.provider.UpdateServiceProviderRequest;
import com.sellit.api.provider.entity.ProviderReviewLogEntity;
import com.sellit.api.provider.entity.ServiceProviderEntity;
import org.springframework.http.ResponseEntity;

import java.security.Principal;

public interface ProviderService {
    ResponseEntity<ApiResponse> signupProvider(ProviderSignupRequest providerSignupRequest);
    ResponseEntity<ApiResponse> assignServiceToProvider(String serviceUuid, String providerUuid, ServiceProviderEntity serviceProviderEntity);
    ResponseEntity<ApiResponse> submitProviderReview(String serviceAppointmentUuid, Principal principal, ProviderReviewLogEntity providerReviewLogEntity);
    ResponseEntity<ApiResponse> updateProvideDetails(String providerUuid, UpdateProviderDetailsRequest request);
    ResponseEntity<ApiResponse> updateServiceProviderDetails(String serviceProviderUuid, UpdateServiceProviderRequest request);

}
