package org.dotspace.oofp.controller;

import org.dotspace.oofp.model.dto.behaviorstep.Violations;
import org.dotspace.oofp.service.formreview.FormReviewService;
import org.dotspace.oofp.utils.eip.auth.EntitlementsResolver;
import org.dotspace.oofp.utils.functional.monad.validation.Validation;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.annotation.Resource;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@AutoConfigureMockMvc(addFilters = false)
@Import({FormReviewControllerTest.TestConfig.class, org.dotspace.oofp.controller.FormReviewController.class})
class FormReviewControllerTest {

    @Resource
    MockMvc mockMvc;

    @Test
    void search_items_read_only() throws Exception {
        mockMvc.perform(
                        post("/api/forms/1/groups/2/items/search")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                      "keyword": "abc",
                                      "filters": {
                                        "signupNo": 12345,
                                        "ssType": "TYPE_A"
                                      },
                                      "sort": [
                                        {
                                          "field": "createdDate",
                                          "direction": "desc"
                                        }
                                      ],
                                      "paging": {
                                        "page": 1,
                                        "size": 10
                                      }
                                    }
                                """)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }

    @Configuration
    static class TestConfig {

        @Bean
        FormReviewService formReviewService() {
            return new FormReviewService();
        }

        @Bean
        Supplier<Validation<Violations, Authentication>> authenticationSupplier() {
            return () -> Validation.valid(
                    new UsernamePasswordAuthenticationToken(
                            "testUser", "N/A", List.of(new SimpleGrantedAuthority("ROLE_user"))
                    )
            );
        }

        @Bean
        EntitlementsResolver entitlementsResolver() {
            return principalId -> Validation.valid(new EntitlementsResolver.Entitlements(
                    Set.of("admin", "user"),
                    Set.of("adminGroup", "userGroup"),
                    Set.of("FORM_REVIEW_READ", "FORM_REVIEW_WRITE")
            ));
        }

    }
}
