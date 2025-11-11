package com.fijalkowskim.authenid.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthorizationCodeFlowIntegrationTest {

    private static final String CLIENT_ID = "demo-client";
    private static final String CLIENT_SECRET = "secret";
    private static final String REDIRECT_URI = "http://localhost:9000/callback";
    private static final String SCOPE_OPENID = "openid";
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "nimda";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void authorizationCodeFlowWithPkce_shouldReturnAccessAndIdToken() throws Exception {
        String codeVerifier = "SmdFvNKTr6ygJyEy-e-VPpqHDt6LoSLgwf5wch9K_7Y";
        String codeChallenge = pkceS256(codeVerifier);

        MvcResult initialAuthorizeResult = mockMvc.perform(
                        get("/oauth2/authorize")
                                .queryParam("response_type", "code")
                                .queryParam("client_id", CLIENT_ID)
                                .queryParam("redirect_uri", REDIRECT_URI)
                                .queryParam("scope", SCOPE_OPENID)
                                .queryParam("code_challenge", codeChallenge)
                                .queryParam("code_challenge_method", "S256")
                )
                .andExpect(status().is3xxRedirection())
                .andReturn();

        MockHttpSession session = (MockHttpSession) initialAuthorizeResult.getRequest().getSession(false);
        assertThat(session).isNotNull();

        mockMvc.perform(
                        post("/login")
                                .session(session)
                                .param("username", ADMIN_USERNAME)
                                .param("password", ADMIN_PASSWORD)
                                .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andReturn();

        MvcResult authorizeResult = mockMvc.perform(
                        get("/oauth2/authorize")
                                .session(session)
                                .queryParam("response_type", "code")
                                .queryParam("client_id", CLIENT_ID)
                                .queryParam("redirect_uri", REDIRECT_URI)
                                .queryParam("scope", SCOPE_OPENID)
                                .queryParam("code_challenge", codeChallenge)
                                .queryParam("code_challenge_method", "S256")
                )
                .andExpect(status().is3xxRedirection())
                .andReturn();

        String location = authorizeResult.getResponse().getHeader("Location");
        assertThat(location).isNotBlank();

        UriComponents uriComponents = UriComponentsBuilder.fromUriString(location).build();
        String code = uriComponents.getQueryParams().getFirst("code");
        assertThat(code).isNotBlank();

        MvcResult tokenResult = mockMvc.perform(
                        post("/oauth2/token")
                                .with(httpBasic(CLIENT_ID, CLIENT_SECRET))
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("grant_type", "authorization_code")
                                .param("code", code)
                                .param("redirect_uri", REDIRECT_URI)
                                .param("code_verifier", codeVerifier)
                )
                .andExpect(status().isOk())
                .andReturn();

        String body = tokenResult.getResponse().getContentAsString();
        JsonNode json = objectMapper.readTree(body);

        String accessToken = json.path("access_token").asText();
        String idToken = json.path("id_token").asText();
        String tokenType = json.path("token_type").asText();
        String scope = json.path("scope").asText();

        assertThat(accessToken).isNotBlank();
        assertThat(idToken).isNotBlank();
        assertThat(tokenType).isEqualToIgnoringCase("bearer");
        assertThat(scope).contains(SCOPE_OPENID);

        JsonNode idTokenPayload = decodeJwtPayload(idToken);
        assertThat(idTokenPayload.path("preferred_username").asText()).isEqualTo(ADMIN_USERNAME);
        assertThat(idTokenPayload.path("roles").isArray()).isTrue();
        assertThat(idTokenPayload.path("roles").toString()).contains("SYSTEM_ADMIN");
    }

    private static String pkceS256(String verifier) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(verifier.getBytes(StandardCharsets.US_ASCII));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
    }

    private JsonNode decodeJwtPayload(String jwt) throws Exception {
        String[] parts = jwt.split("\\.");
        String payload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
        return objectMapper.readTree(payload);
    }
}
