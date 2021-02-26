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

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static util.TestUtil.getResource;

@SpringBootTest(classes = Main.class)
@AutoConfigureMockMvc
public class ChangePasswordTestSuite {

    @Autowired
    MockMvc mvc;

    @Test
    @Transactional
    @Sql({"/sql/captcha.sql", "/sql/TestUser.sql"})
    void PasswordChanged() throws Exception {
        mvc.perform(
            post("/api/auth/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getResource("json/request/auth/ChangePasswordBody_Ok.json"))
        ).andExpect(status().isOk())
            .andExpect(content().json(new String(getResource("json/response/DefaultBody_True.json"))));
    }

    @Test
    void FailedWithEmptyJSON() throws Exception {
        mvc.perform(
            post("/api/auth/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getResource("json/EmptyJson.json"))
        ).andExpect(status().isOk())
            .andExpect(
                content().json(
                    new String(
                        getResource("json/response/auth/ChangePassword_ResponseErrorBody_EmptyAllValues.json"))));
    }

    @Test
    void FailedWithAllEmptyValues() throws Exception {
        mvc.perform(
            post("/api/auth/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getResource("json/request/auth/ChangePasswordBody_EmptyAllValues.json"))
        ).andExpect(status().isOk())
            .andExpect(
                content().json(
                    new String(
                        getResource("json/response/auth/ChangePassword_ResponseErrorBody_EmptyAllValues.json"))));
    }

    @Test
    void FailedWithPasswordLengthLessThanSix() throws Exception {
        mvc.perform(
            post("/api/auth/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getResource("json/request/auth/ChangePasswordBody_PasswordLessThanSix.json"))
        ).andExpect(status().isOk())
            .andExpect(
                content().json(new String(getResource("json/response/BadRequestBody_ShortPassword.json"))));
    }

    @Test
    void FailedWithInvalidContentType() throws Exception {
        mvc.perform(post("/api/auth/password"))
            .andExpect(status().isBadRequest())
            .andExpect(
                content().json(new String(getResource("json/response/BadRequestBody_InvalidContentType.json"))));
    }

    @Test
    void FailedWithNullBody() throws Exception {
        mvc.perform(post("/api/auth/password").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(
                content().json(new String(getResource("json/response/BadRequestBody_InvalidRequest.json"))));
    }
}
