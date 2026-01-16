package com.rewine.backend.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rewine.backend.dto.request.CreateEventRequest;
import com.rewine.backend.dto.request.LoginRequest;
import com.rewine.backend.dto.request.UpdateEventRequest;
import com.rewine.backend.dto.response.AuthResponse;
import com.rewine.backend.dto.response.EventDetailsResponse;
import com.rewine.backend.model.enums.EventStatus;
import com.rewine.backend.model.enums.EventType;
import com.rewine.backend.model.enums.OrganizerType;
import com.rewine.backend.integration.BaseIntegrationTest;
import com.rewine.backend.model.entity.EventEntity;
import com.rewine.backend.model.entity.RoleEntity;
import com.rewine.backend.model.entity.UserEntity;
import com.rewine.backend.repository.IEventRepository;
import com.rewine.backend.repository.IRoleRepository;
import com.rewine.backend.repository.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for event ownership and authorization.
 * Tests verify that partners can only modify their own events,
 * while admins can modify any event.
 */
@AutoConfigureMockMvc
@Transactional
@SuppressWarnings("checkstyle:MagicNumber")
public class EventOwnershipIntegrationTest extends BaseIntegrationTest {

    private static final String TEST_PASSWORD = "TestPassword123!";
    private static final int HTTP_FORBIDDEN = 403;
    private static final String ACCESS_DENIED_CODE = "E2006";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IRoleRepository roleRepository;

    @Autowired
    private IEventRepository eventRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private RoleEntity roleUser;
    private RoleEntity rolePartner;
    private RoleEntity roleAdmin;

    private UserEntity partnerA;
    private UserEntity partnerB;
    private UserEntity adminUser;

    private EventEntity eventByPartnerA;

    @BeforeEach
    void setUp() {
        // Create or get roles
        roleUser = getOrCreateRole("ROLE_USER", "Standard user role");
        rolePartner = getOrCreateRole("ROLE_PARTNER", "Partner role");
        roleAdmin = getOrCreateRole("ROLE_ADMIN", "Administrator role");

        // Create Partner A
        partnerA = createUser("partnerA", "partnerA@test.com", Set.of(roleUser, rolePartner));

        // Create Partner B
        partnerB = createUser("partnerB", "partnerB@test.com", Set.of(roleUser, rolePartner));

        // Create Admin
        adminUser = createUser("adminUser", "admin@test.com", Set.of(roleUser, roleAdmin));

        // Create an event owned by Partner A
        eventByPartnerA = createEvent(partnerA, "Partner A's Event");
    }

    private RoleEntity getOrCreateRole(String name, String description) {
        return roleRepository.findByName(name)
                .orElseGet(() -> {
                    RoleEntity role = new RoleEntity();
                    role.setName(name);
                    role.setDescription(description);
                    return roleRepository.save(role);
                });
    }

    private UserEntity createUser(String username, String email, Set<RoleEntity> roles) {
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(TEST_PASSWORD));
        user.setEnabled(true);
        user.setEmailVerified(true);
        user.setLocked(false);
        user.setRoles(new HashSet<>(roles));
        return userRepository.save(user);
    }

    private EventEntity createEvent(UserEntity organizer, String title) {
        EventEntity event = new EventEntity();
        event.setTitle(title);
        event.setOrganizer(organizer);
        event.setType(EventType.TASTING);
        event.setStatus(EventStatus.PUBLISHED);
        event.setStartDate(Instant.now().plus(7, ChronoUnit.DAYS));
        event.setEndDate(Instant.now().plus(7, ChronoUnit.DAYS).plus(4, ChronoUnit.HOURS));
        event.setLocationName("Test Location");
        event.setLocationAddress("123 Test St");
        event.setLocationCity("Test City");
        event.setLocationRegion("Test Region");
        event.setLatitude(BigDecimal.valueOf(-32.8895));
        event.setLongitude(BigDecimal.valueOf(-68.8458));
        event.setDescription("Test event description");
        return eventRepository.save(event);
    }

    private String loginAndGetToken(String usernameOrEmail) throws Exception {
        LoginRequest loginRequest = new LoginRequest(usernameOrEmail, TEST_PASSWORD);

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        AuthResponse authResponse = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                AuthResponse.class
        );
        return authResponse.accessToken();
    }

    @Nested
    @DisplayName("Partner Update Own Event Tests")
    class PartnerUpdateOwnEventTests {

        @Test
        @DisplayName("Partner should be able to update their own event")
        void partnerShouldUpdateOwnEvent() throws Exception {
            String token = loginAndGetToken("partnerA");

            UpdateEventRequest request = UpdateEventRequest.builder()
                    .title("Updated Event Title")
                    .description("Updated description")
                    .type(EventType.TOUR)
                    .status(EventStatus.PUBLISHED)
                    .startDate(eventByPartnerA.getStartDate().plus(1, ChronoUnit.DAYS))
                    .endDate(eventByPartnerA.getEndDate().plus(1, ChronoUnit.DAYS))
                    .locationName("Updated Location")
                    .locationAddress("456 Updated St")
                    .locationCity("Updated City")
                    .locationRegion("Updated Region")
                    .latitude(BigDecimal.valueOf(-33.0))
                    .longitude(BigDecimal.valueOf(-68.5))
                    .build();

            mockMvc.perform(put("/events/" + eventByPartnerA.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title").value("Updated Event Title"))
                    .andExpect(jsonPath("$.description").value("Updated description"));
        }

        @Test
        @DisplayName("Partner should be able to delete their own event")
        void partnerShouldDeleteOwnEvent() throws Exception {
            String token = loginAndGetToken("partnerA");

            mockMvc.perform(delete("/events/" + eventByPartnerA.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isNoContent());

            // Verify event is deleted or marked as such
            assertThat(eventRepository.findById(eventByPartnerA.getId())).isEmpty();
        }
    }

    @Nested
    @DisplayName("Partner Cannot Modify Other's Event Tests")
    class PartnerCannotModifyOthersEventTests {

        @Test
        @DisplayName("Partner should NOT be able to update another partner's event")
        void partnerShouldNotUpdateOthersEvent() throws Exception {
            String tokenPartnerB = loginAndGetToken("partnerB");

            UpdateEventRequest request = UpdateEventRequest.builder()
                    .title("Malicious Update")
                    .description("Trying to update someone else's event")
                    .type(EventType.TOUR)
                    .status(EventStatus.PUBLISHED)
                    .startDate(eventByPartnerA.getStartDate())
                    .endDate(eventByPartnerA.getEndDate())
                    .build();

            mockMvc.perform(put("/events/" + eventByPartnerA.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenPartnerB)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.status").value(HTTP_FORBIDDEN))
                    .andExpect(jsonPath("$.code").value(ACCESS_DENIED_CODE));

            // Verify event was not modified
            EventEntity event = eventRepository.findById(eventByPartnerA.getId()).orElseThrow();
            assertThat(event.getTitle()).isEqualTo("Partner A's Event");
        }

        @Test
        @DisplayName("Partner should NOT be able to delete another partner's event")
        void partnerShouldNotDeleteOthersEvent() throws Exception {
            String tokenPartnerB = loginAndGetToken("partnerB");

            mockMvc.perform(delete("/events/" + eventByPartnerA.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenPartnerB))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.status").value(HTTP_FORBIDDEN))
                    .andExpect(jsonPath("$.code").value(ACCESS_DENIED_CODE));

            // Verify event still exists
            assertThat(eventRepository.findById(eventByPartnerA.getId())).isPresent();
        }
    }

    @Nested
    @DisplayName("Admin Can Modify Any Event Tests")
    class AdminCanModifyAnyEventTests {

        @Test
        @DisplayName("Admin should be able to update any event")
        void adminShouldUpdateAnyEvent() throws Exception {
            String adminToken = loginAndGetToken("adminUser");

            UpdateEventRequest request = UpdateEventRequest.builder()
                    .title("Admin Updated Title")
                    .description("Admin updated this event")
                    .type(EventType.FESTIVAL)
                    .status(EventStatus.PUBLISHED)
                    .startDate(eventByPartnerA.getStartDate())
                    .endDate(eventByPartnerA.getEndDate())
                    .build();

            mockMvc.perform(put("/events/" + eventByPartnerA.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title").value("Admin Updated Title"));
        }

        @Test
        @DisplayName("Admin should be able to delete any event")
        void adminShouldDeleteAnyEvent() throws Exception {
            // Create another event for deletion
            EventEntity anotherEvent = createEvent(partnerA, "Another Event");
            String adminToken = loginAndGetToken("adminUser");

            mockMvc.perform(delete("/events/" + anotherEvent.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken))
                    .andExpect(status().isNoContent());

            assertThat(eventRepository.findById(anotherEvent.getId())).isEmpty();
        }
    }

    @Nested
    @DisplayName("Partner Create Event Tests")
    class PartnerCreateEventTests {

        @Test
        @DisplayName("Partner should be able to create a new event")
        void partnerShouldCreateEvent() throws Exception {
            String token = loginAndGetToken("partnerA");

            CreateEventRequest request = CreateEventRequest.builder()
                    .title("New Partner Event")
                    .description("A new event created by partner")
                    .type(EventType.TASTING)
                    .startDate(Instant.now().plus(14, ChronoUnit.DAYS))
                    .endDate(Instant.now().plus(14, ChronoUnit.DAYS).plus(3, ChronoUnit.HOURS))
                    .locationName("Winery Location")
                    .locationAddress("789 Wine St")
                    .locationCity("Wine City")
                    .locationRegion("Wine Region")
                    .latitude(BigDecimal.valueOf(-32.5))
                    .longitude(BigDecimal.valueOf(-68.8))
                    .maxAttendees(50)
                    .price(BigDecimal.valueOf(25.00))
                    .organizerType(OrganizerType.PARTNER)
                    .build();

            MvcResult result = mockMvc.perform(post("/events")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.title").value("New Partner Event"))
                    .andExpect(jsonPath("$.organizerId").value(partnerA.getId().toString()))
                    .andReturn();

            EventDetailsResponse response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    EventDetailsResponse.class
            );

            // Verify event was created and associated with the partner
            assertThat(eventRepository.findById(response.getId())).isPresent();
        }
    }

    @Nested
    @DisplayName("Non-Partner Access Denied Tests")
    class NonPartnerAccessDeniedTests {

        @Test
        @DisplayName("Regular user without PARTNER role should not create events")
        void regularUserShouldNotCreateEvent() throws Exception {
            UserEntity regularUser = createUser("regularUser", "regular@test.com", Set.of(roleUser));
            String token = loginAndGetToken("regularUser");

            CreateEventRequest request = CreateEventRequest.builder()
                    .title("Unauthorized Event")
                    .description("Should not be created")
                    .type(EventType.TASTING)
                    .startDate(Instant.now().plus(14, ChronoUnit.DAYS))
                    .endDate(Instant.now().plus(14, ChronoUnit.DAYS).plus(3, ChronoUnit.HOURS))
                    .locationName("Location")
                    .locationAddress("Address")
                    .locationCity("City")
                    .locationRegion("Region")
                    .latitude(BigDecimal.valueOf(-32.5))
                    .longitude(BigDecimal.valueOf(-68.8))
                    .maxAttendees(50)
                    .price(BigDecimal.valueOf(25.00))
                    .build();

            mockMvc.perform(post("/events")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Regular user without PARTNER role should not update events")
        void regularUserShouldNotUpdateEvent() throws Exception {
            UserEntity regularUser = createUser("regularUser2", "regular2@test.com", Set.of(roleUser));
            String token = loginAndGetToken("regularUser2");

            UpdateEventRequest request = UpdateEventRequest.builder()
                    .title("Hacked Title")
                    .build();

            mockMvc.perform(put("/events/" + eventByPartnerA.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden());
        }
    }
}

