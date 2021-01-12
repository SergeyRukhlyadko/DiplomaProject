package integration.auth;

import org.diploma.app.Main;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Main.class)
@AutoConfigureMockMvc
public class CaptchaTestSuite {

    @Autowired
    MockMvc mvc;

    @Test
    @Transactional
    void captcha_Get_200() throws Exception {
        mvc.perform(get("/api/auth/captcha"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.secret").exists())
            .andExpect(jsonPath("$.secret")
                .value(matchesPattern("[a-f\\d]{8}-([a-f\\d]{4}-){3}[a-f\\d]{12}")))
            .andExpect(jsonPath("$.image").exists())
            .andExpect(jsonPath("$.image")
                .value(matchesPattern("^data:image\\/jpeg;base64, [A-Za-z\\d\\+\\/=]+$")));
    }
}
