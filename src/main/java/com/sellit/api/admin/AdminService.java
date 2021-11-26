package com.sellit.api.admin;

import com.sellit.api.payload.ApiResponse;
import com.sellit.api.user.entity.UserEntity;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;

public interface AdminService {
    ResponseEntity<ApiResponse> blockUserAccount(String userUuid);
    ResponseEntity<PagedModel<UserEntity>> getAllUsers(int page, int size, PagedResourcesAssembler<UserEntity> pagedResourcesAssembler);
}
