package buildingblocks.keycloak;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringJUnitWebConfig(KeycloakConfigurationTests.TestConfig.class)
@TestPropertySource(properties = {
  "spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://localhost:1/certs",
  "app.security.public-get-paths=/public/**"
})
public class KeycloakConfigurationTests {

  @Configuration
  @EnableWebMvc
  @EnableWebSecurity
  @Import(KeycloakConfiguration.class)
  static class TestConfig {
    @Bean
    Logger logger() {
      return LoggerFactory.getLogger("test");
    }

    @Bean
    TestController testController() {
      return new TestController();
    }
  }

  @RestController
  static class TestController {
    @GetMapping("/public/ping")
    String ping() {
      return "pong";
    }

    @PostMapping("/public/ping")
    String postPing() {
      return "pong";
    }

    @GetMapping("/private/user")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    String forUsers() {
      return "user";
    }

    @GetMapping("/private/admin")
    @PreAuthorize("hasAuthority('ADMIN')")
    String forAdmins() {
      return "admin";
    }
  }

  private MockMvc mockMvc;

  @BeforeEach
  void setUp(WebApplicationContext context) {
    mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
  }

  @Test
  void should_allow_anonymous_get_on_configured_public_path() throws Exception {
    mockMvc.perform(get("/public/ping")).andExpect(status().isOk());
  }

  @Test
  void should_not_make_other_methods_on_a_public_path_public() throws Exception {
    mockMvc.perform(post("/public/ping")).andExpect(status().is4xxClientError());
  }

  @Test
  void should_reject_anonymous_on_private_path() throws Exception {
    mockMvc.perform(get("/private/user")).andExpect(status().is4xxClientError());
  }

  @Test
  void should_let_user_in_user_endpoint_but_not_admin_endpoint() throws Exception {
    var user = jwt().authorities(new SimpleGrantedAuthority("USER"));

    mockMvc.perform(get("/private/user").with(user)).andExpect(status().isOk());
    mockMvc.perform(get("/private/admin").with(user)).andExpect(status().isForbidden());
  }

  @Test
  void should_let_admin_in_both_endpoints() throws Exception {
    var admin = jwt().authorities(new SimpleGrantedAuthority("ADMIN"));

    mockMvc.perform(get("/private/user").with(admin)).andExpect(status().isOk());
    mockMvc.perform(get("/private/admin").with(admin)).andExpect(status().isOk());
  }
}
