package com.sellit.api.customer;

import com.sellit.api.payload.ApiResponse;
import com.sellit.api.payload.customer.CustomerSignupRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

public interface CustomerService {
    ResponseEntity<ApiResponse> signupCustomer(@RequestBody @Valid CustomerSignupRequest customerSignupRequest);
}
