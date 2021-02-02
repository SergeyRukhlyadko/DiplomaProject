package integration.auth;

import org.diploma.app.Main;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static util.TestUtil.getResource;

@SpringBootTest(classes = Main.class)
@AutoConfigureMockMvc
public class LogoutTestSuite {

    @Autowired
    MockMvc mvc;

    @Test
    @WithMockUser
    void LogoutIsDone() throws Exception {
        mvc.perform(get("/api/auth/logout"))
            .andExpect(status().isOk())
            .andExpect(content().json(new String(getResource("json/response/DefaultBody_True.json"))));
    }

    @Test
    void LogoutFailedWithoutAuthentication() throws Exception {
        mvc.perform(get("/api/auth/logout"))
            .andExpect(status().isOk())
            .andExpect(content().json(new String(getResource("json/response/DefaultBody_False.json"))));
    }
}
