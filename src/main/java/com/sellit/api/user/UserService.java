package com.sellit.api.user;

import com.sellit.api.payload.ApiResponse;
import com.sellit.api.payload.PagedResponse;
import com.sellit.api.payload.user.UpdateUserAddressRequest;
import com.sellit.api.payload.user.UserUpdateRequest;
import org.springframework.http.ResponseEntity;

public interface UserService {
    ResponseEntity<PagedResponse> getUsers(int pageNo, int pageSize);
    ResponseEntity<ApiResponse> updateUserDetails(String userUuid, UserUpdateRequest userUpdateRequest);
    ResponseEntity<ApiResponse> updateUserAddress(String userUuid, UpdateUserAddressRequest updateUserAddressRequest);
}
