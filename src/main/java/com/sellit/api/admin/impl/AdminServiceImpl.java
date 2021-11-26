package com.sellit.api.admin.impl;

import com.sellit.api.admin.AdminService;
import com.sellit.api.payload.ApiResponse;
import com.sellit.api.user.entity.UserEntity;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;

public class AdminServiceImpl implements AdminService {
    @Override
    public ResponseEntity<ApiResponse> blockUserAccount(String userUuid) {
        //TODO: IMPLEMENT
       return null;
    }

    @Override
    public ResponseEntity<PagedModel<UserEntity>> getAllUsers(int page, int size, PagedResourcesAssembler<UserEntity> pagedResourcesAssembler) {
        return null;
    }

}
