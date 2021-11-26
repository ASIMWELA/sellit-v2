package com.sellit.api.servicetransactions;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;


@WebMvcTest(ServiceTransactionsController.class)
class ServiceTransactionsControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ServiceTransaction serviceTransaction;

    @Test
    void saveServiceCategory() {
    }

    @Test
    void getServiceCategories() {
    }

    @Test
    void saveService() {
    }

    @Test
    void getServices() {
    }

    @Test
    void requestService() {
    }

    @Test
    void serviceDeliveryOffer() {
    }

    @Test
    void acceptServiceOffer() {
    }

    @Test
    void getServiceProviders() {
    }

    @Test
    void getServiceRequests() {
    }

    @Test
    void testGetServiceRequests() {
    }

    @Test
    void getRequestOffers() {
    }

    @Test
    void getUserAppointments() {
    }

    @Test
    void getProviderReviewLogs() {
    }

    @Test
    void getProviderServices() {
    }

    @Test
    void getProviderAppointments() {
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}