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

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static util.TestUtil.getResource;

@SpringBootTest(classes = Main.class)
@AutoConfigureMockMvc
public class LoginTestSuite {

    @Autowired
    MockMvc mvc;

    @Test
    @Transactional
    @Sql("/sql/TestUser.sql")
    void LoginCompleted() throws Exception {
        mvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getResource("json/request/auth/LoginBody_Ok.json"))
        ).andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value(true))
            .andExpect(jsonPath("$.user.id").value(greaterThan(0)))
            .andExpect(jsonPath("$.user.name").value("TestName"))
            .andExpect(jsonPath("$.user.photo").hasJsonPath())
            .andExpect(jsonPath("$.user.email").value("test@mail.com"))
            .andExpect(jsonPath("$.user.moderation").value(false))
            .andExpect(jsonPath("$.user.moderationCount").value(greaterThanOrEqualTo(0)))
            .andExpect(jsonPath("$.user.settings").value(false));
    }

    @Test
    void LoginFailedWithEmptyJSON() throws Exception {
        mvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getResource("json/EmptyJSON.json"))
        ).andExpect(status().isOk())
            .andExpect(content().json(
                new String(getResource("json/response/auth/Login_ResponseErrorBody_EmptyAllValues.json"))
            ));
    }

    @Test
    void LoginFailedWithAllEmptyValues() throws Exception {
        mvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getResource("json/request/auth/LoginBody_EmptyAllValues.json"))
        ).andExpect(status().isOk())
            .andExpect(content().json(
                new String(getResource("json/response/auth/Login_ResponseErrorBody_EmptyAllValues.json"))
            ));
    }

    @Test
    void LoginFailedWithInvalidEmail() throws Exception {
        mvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getResource("json/request/auth/LoginBody_InvalidEmail.json"))
        ).andExpect(status().isOk())
            .andExpect(content().json(
                new String(getResource("json/response/BadRequestBody_InvalidEmail.json"))
            ));
    }

    @Test
    void LoginFailedWithPasswordLengthLessThanSix() throws Exception {
        mvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getResource("json/request/auth/LoginBody_ShortPassword.json"))
        ).andExpect(status().isOk())
            .andExpect(content().json(
                new String(getResource("json/response/BadRequestBody_ShortPassword.json"))
            ));
    }

    @Test
    void LoginFailedWithInvalidContentType() throws Exception {
        mvc.perform(
            post("/api/auth/login").content(getResource("json/request/auth/LoginBody_Ok.json"))
        ).andExpect(status().isBadRequest())
            .andExpect(content().json(
                new String(getResource("json/response/BadRequestBody_InvalidContentType.json"))
            ));
    }

    @Test
    void LoginFailedWithNullBody() throws Exception {
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(content().json(new String(getResource("json/response/BadRequestBody_InvalidRequest.json"))));
    }

    @Test
    void LoginFailedWithUnregisteredUser() throws Exception {
        mvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getResource("json/request/auth/LoginBody_Ok.json"))
        ).andExpect(status().isOk())
            .andExpect(content().json(new String(getResource("json/response/DefaultBody_False.json"))));
    }

    @Test
    @Transactional
    @Sql("/sql/TestUser.sql")
    void LoginFailedWithWrongPassword() throws Exception {
        mvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getResource("json/request/auth/LoginBody_WrongPassword.json"))
        ).andExpect(status().isOk())
            .andExpect(content().json(new String(getResource("json/response/DefaultBody_False.json"))));
    }
}
