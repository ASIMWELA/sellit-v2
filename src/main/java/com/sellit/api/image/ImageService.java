package com.sellit.api.image;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
public interface ImageService {
    ResponseEntity<?> uploadServiceImage(String serviceUuid, MultipartFile image);
    ResponseEntity<?> uploadProviderImage(String providerUuid, MultipartFile image);
    ResponseEntity<?> uploadCustomerImage(String customerUuid, MultipartFile image);
    ResponseEntity<?> getServiceImages(String serviceUuid);
    ResponseEntity<?> getCustomerImage(String customerUuid);
    ResponseEntity<?> getProviderImage(String providerUuid);
    ResponseEntity<?> deleteServiceImage(String serviceUuid, String imageName);
}
