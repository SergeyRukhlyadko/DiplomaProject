package integration.auth;

import integration.RequestPath;
import org.diploma.app.Main;
import org.junit.jupiter.api.BeforeAll;
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
public class RegistrationTestSuite {

    @Autowired
    MockMvc mvc;

    static String requestPath;

    @BeforeAll
    static void setUp() {
        requestPath = RequestPath.Post.REGISTRATION.value();
    }

    @Test
    @Transactional
    @Sql("/sql/Captcha.sql")
    void RegistrationCompleted() throws Exception {
        mvc.perform(
            post(requestPath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getResource("json/request/auth/RegisterBody_Ok.json"))
        ).andExpect(status().isOk())
            .andExpect(content().json(new String(getResource("json/response/DefaultBody_True.json"))));
    }

    @Test
    @Transactional
    @Sql("/sql/GlobalSetting_MultiUserMode_Disable.sql")
    void RegistrationIsClosed() throws Exception {
        mvc.perform(post(requestPath).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    @Sql({"/sql/Captcha.sql", "/sql/User.sql"})
    void EmailAlreadyExists() throws Exception {
        mvc.perform(
            post(requestPath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getResource("json/request/auth/RegisterBody_Ok.json"))
        ).andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value(false))
            .andExpect(jsonPath("$.errors.e_mail").value(is(not(blankString()))));
    }

    @Test
    void InvalidEmailFormat() throws Exception {
        mvc.perform(
            post(requestPath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getResource("json/request/auth/RegisterBody_InvalidEmail.json"))
        ).andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value(false))
            .andExpect(jsonPath("$.errors.e_mail").value(is(not(blankString()))));
    }

    @Test
    void PasswordLengthLessThanSix() throws Exception {
        mvc.perform(
            post(requestPath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getResource("json/request/auth/RegisterBody_ShortPassword.json"))
        ).andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value(false))
            .andExpect(jsonPath("$.errors.password").value(is(not(blankString()))));
    }

    @Test
    void WrongCaptcha() throws Exception {
        mvc.perform(
            post(requestPath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getResource("json/request/auth/RegisterBody_Ok.json"))
        ).andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value(false))
            .andExpect(jsonPath("$.errors.captcha").value(is(not(blankString()))));
    }

    @Test
    void EmptyJSON() throws Exception {
        mvc.perform(
            post(requestPath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getResource("json/EmptyJSON.json"))
        ).andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value(false))
            .andExpect(jsonPath("$.errors.e_mail").value(is(not(blankString()))))
            .andExpect(jsonPath("$.errors.name").value(is(not(blankString()))))
            .andExpect(jsonPath("$.errors.password").value(is(not(blankString()))))
            .andExpect(jsonPath("$.errors.captcha").value(is(not(blankString()))))
            .andExpect(jsonPath("$.errors.captcha_secret").value(is(not(blankString()))));
    }
}
