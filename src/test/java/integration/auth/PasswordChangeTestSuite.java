package integration.auth;

import org.diploma.app.Main;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.blankString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static util.TestUtil.getResource;

@SpringBootTest(classes = Main.class)
@AutoConfigureMockMvc
public class PasswordChangeTestSuite {

    @Autowired
    MockMvc mvc;

    @Test
    @Transactional
    @Sql({"/sql/Captcha.sql", "/sql/User.sql"})
    void PasswordChanged() throws Exception {
        mvc.perform(
            post("/api/auth/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getResource("json/request/auth/ChangePasswordBody_Ok.json"))
        ).andExpect(status().isOk())
            .andExpect(content().json(new String(getResource("json/response/DefaultBody_True.json"))));
    }

    @Test
    @Transactional
    @Sql({"/sql/Captcha.sql", "/sql/SameUsers.sql"})
    void ChangedMoreThanOnePassword() throws Exception {
        mvc.perform(
            post("/api/auth/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getResource("json/request/auth/ChangePasswordBody_Ok.json"))
        ).andExpect(status().is5xxServerError());
    }

    @Test
    void PasswordLengthLessThanSix() throws Exception {
        mvc.perform(
            post("/api/auth/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getResource("json/request/auth/ChangePasswordBody_PasswordLessThanSix.json"))
        ).andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value(false))
            .andExpect(jsonPath("$.errors.password").value(is(not(blankString()))));
    }

    @Test
    void EmptyJSON() throws Exception {
        mvc.perform(
            post("/api/auth/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getResource("json/EmptyJson.json"))
        ).andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value(false))
            .andExpect(jsonPath("$.errors.code").value(is(not(blankString()))))
            .andExpect(jsonPath("$.errors.password").value(is(not(blankString()))))
            .andExpect(jsonPath("$.errors.captcha").value(is(not(blankString()))))
            .andExpect(jsonPath("$.errors.captcha_secret").value(is(not(blankString()))));
    }

    @Test
    void NotSupportedContentType() throws Exception {
        mvc.perform(post("/api/auth/password"))
            .andExpect(status().isBadRequest())
            .andExpect(
                content().json(new String(getResource("json/response/BadRequestBody_NotSupportedContentType.json"))));
    }

    @Test
    void EmptyBody() throws Exception {
        mvc.perform(post("/api/auth/password").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(content().json(new String(getResource("json/response/BadRequestBody_InvalidRequestBody.json"))));
    }

    @Test
    void InvalidJSON() throws Exception {
        mvc.perform(
            post("/api/auth/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getResource("json/InvalidJSON.json"))
        ).andExpect(status().isBadRequest())
            .andExpect(content().json(new String(getResource("json/response/BadRequestBody_InvalidRequestBody.json"))));
    }
}
