package com.sellit.api.image.impl;

import com.sellit.api.image.ImageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public class ImageServiceImpl implements ImageService {
    @Override
    public ResponseEntity<?> uploadServiceImage(String serviceUuid, MultipartFile image) {
        return null;
    }

    @Override
    public ResponseEntity<?> uploadProviderImage(String providerUuid, MultipartFile image) {
        return null;
    }

    @Override
    public ResponseEntity<?> uploadCustomerImage(String customerUuid, MultipartFile image) {
        return null;
    }

    @Override
    public ResponseEntity<?> getServiceImages(String serviceUuid) {
        return null;
    }

    @Override
    public ResponseEntity<?> getCustomerImage(String customerUuid) {
        return null;
    }

    @Override
    public ResponseEntity<?> getProviderImage(String providerUuid) {
        return null;
    }

    @Override
    public ResponseEntity<?> deleteServiceImage(String serviceUuid, String imageName) {
        return null;
    }
}
