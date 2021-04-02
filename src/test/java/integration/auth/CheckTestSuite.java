package integration.auth;

import org.diploma.app.Main;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static util.TestUtil.getResource;

@SpringBootTest(classes = Main.class)
@AutoConfigureMockMvc
public class CheckTestSuite {

    @Autowired
    MockMvc mvc;

    @Test
    @Transactional
    @Sql("/sql/User.sql")
    @WithMockUser("user@mail.com")
    void AuthenticatedUser() throws Exception {
        mvc.perform(get("/api/auth/check"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value(true))
            .andExpect(jsonPath("$.user.id").value(greaterThan(0)))
            .andExpect(jsonPath("$.user.name").value("UserName"))
            .andExpect(jsonPath("$.user.photo").hasJsonPath())
            .andExpect(jsonPath("$.user.email").value("user@mail.com"))
            .andExpect(jsonPath("$.user.moderation").value(false))
            .andExpect(jsonPath("$.user.moderationCount").value(greaterThanOrEqualTo(0)))
            .andExpect(jsonPath("$.user.settings").value(false));
    }

    @Test
    void NotAuthenticatedUser() throws Exception {
        mvc.perform(get("/api/auth/check"))
            .andExpect(status().isOk())
            .andExpect(content().json(new String(getResource("json/response/DefaultBody_False.json"))));
    }
}
