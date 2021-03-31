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

import static integration.ResponseResultMatcher.invalidEmailFormat;
import static org.hamcrest.Matchers.blankString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
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

    static String requestPath;

    @BeforeAll
    static void setUp() {
        requestPath = RequestPath.Post.LOGIN.value();
    }

    @Test
    @Transactional
    @Sql("/sql/User.sql")
    void LoginCompleted() throws Exception {
        mvc.perform(
            post(requestPath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getResource("json/request/auth/LoginBody_Ok.json"))
        ).andExpect(status().isOk())
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
    void LoginUnregisteredUser() throws Exception {
        mvc.perform(
            post(requestPath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getResource("json/request/auth/LoginBody_Ok.json"))
        ).andExpect(status().isOk())
            .andExpect(content().json(new String(getResource("json/response/DefaultBody_False.json"))));
    }

    @Test
    void InvalidEmailFormat() throws Exception {
        mvc.perform(
            post(requestPath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getResource("json/request/auth/LoginBody_InvalidEmailFormat.json"))
        ).andExpect(invalidEmailFormat("e_mail"));
    }

    @Test
    @Transactional
    @Sql("/sql/User.sql")
    void WrongEmail() throws Exception {
        mvc.perform(
            post(requestPath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getResource("json/request/auth/LoginBody_WrongEmail.json"))
        ).andExpect(status().isOk())
            .andExpect(content().json(new String(getResource("json/response/DefaultBody_False.json"))));
    }

    @Test
    @Transactional
    @Sql("/sql/User.sql")
    void WrongPassword() throws Exception {
        mvc.perform(
            post(requestPath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getResource("json/request/auth/LoginBody_WrongPassword.json"))
        ).andExpect(status().isOk())
            .andExpect(content().json(new String(getResource("json/response/DefaultBody_False.json"))));
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
            .andExpect(jsonPath("$.errors.password").value(is(not(blankString()))));

    }
}
