package com.demonstration.toolrental;

import com.demonstration.toolrental.model.response.RentalAgreement;
import com.demonstration.toolrental.repository.ToolRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.demonstration.toolrental.util.ApplicationConstants.TOOL_CODE_EMPTY_OR_NULL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ToolRentalApplicationTest {

    @LocalServerPort
    private int port;

    @Autowired
    ToolRepository repository;

    @Test
    void getChainsawSuccessful() {
        String urlWithRequest = "http://localhost:" + port +
                "/rental/tool?ToolCode=CHNS&CheckoutDate=07/02/2020&RentalDays=5&Discount=0";
        ResponseEntity<RentalAgreement> response = new RestTemplate().
                getForEntity(urlWithRequest,RentalAgreement.class);

        assertEquals(200,response.getStatusCode().value());
        assertEquals(3, response.getBody().getChargeDays());
        assertEquals(BigDecimal.valueOf(4.47).setScale(2, RoundingMode.UNNECESSARY),response.getBody().getFinalCharge());
    }


    @Test
    void getChainsawFailValidation() {
        String requestWithoutToolCode = "http://localhost:" + port +
                "/rental/tool?CheckoutDate=07/02/2020&RentalDays=5&Discount=0";
        try {
            ResponseEntity<RentalAgreement> response = new RestTemplate().
                    getForEntity(requestWithoutToolCode, RentalAgreement.class);
            fail();
        } catch (Exception ex) {
            assertTrue(ex.getMessage().contains("400"));
        }
        String requestWithoutCheckoutDate = "http://localhost:" + port +
                "/rental/tool?ToolCode=JAKD&RentalDays=5&Discount=0";
        try {
            ResponseEntity<RentalAgreement> response = new RestTemplate().
                    getForEntity(requestWithoutCheckoutDate, RentalAgreement.class);
            fail();
        } catch (Exception ex) {
            assertTrue(ex.getMessage().contains("400"));
        }
        String requestWithoutRentalDays = "http://localhost:" + port +
                "/rental/tool?CheckoutDate=07/02/2020&RentalDays=5&Discount=0";
        try {
            ResponseEntity<RentalAgreement> response = new RestTemplate().
                    getForEntity(requestWithoutRentalDays, RentalAgreement.class);
            fail();
        } catch (Exception ex) {
            assertTrue(ex.getMessage().contains("400"));
        }
        String requestWithoutDiscount = "http://localhost:" + port +
                "/rental/tool?CheckoutDate=07/02/2020&RentalDays=5&Discount=0";
        try {
            ResponseEntity<RentalAgreement> response = new RestTemplate().
                    getForEntity(requestWithoutDiscount, RentalAgreement.class);
            fail();
        } catch (Exception ex) {
            assertTrue(ex.getMessage().contains("400"));
        }
    }

    @Test
    @DisplayName("Check Controller Advice for invalid request")
    void nullToolCodeProvided() {
        String urlWithRequest = "http://localhost:" + port +
                "/rental/tool?ToolCode=&CheckoutDate=07/02/2020&RentalDays=5&Discount=0";
        try {
            ResponseEntity<RentalAgreement> response = new RestTemplate().
                    getForEntity(urlWithRequest, RentalAgreement.class);
            fail();
        } catch (Exception ex) {
            assertTrue(ex.getMessage().contains(TOOL_CODE_EMPTY_OR_NULL));
            assertTrue(ex.getMessage().contains("400"));
        }
    }

    @Test
    @DisplayName("Check Controller Advice for empty result")
    void toolNotFound() {
        String urlWithRequest = "http://localhost:" + port +
                "/rental/tool?ToolCode=NOTFOUND&CheckoutDate=07/02/2020&RentalDays=5&Discount=0";
        try {
            ResponseEntity<?> response = new RestTemplate().getForEntity(urlWithRequest, RentalAgreement.class);
        } catch (Exception ex) {
            assertTrue(ex.getMessage().contains("Failed to find tool with Tool Code: NOTFOUND"));
            assertTrue(ex.getMessage().contains("400"));
        }
    }
}