package integration.auth;

import org.diploma.app.Main;
import org.diploma.app.service.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static util.TestUtil.getResource;

@SpringBootTest(classes = Main.class)
@AutoConfigureMockMvc
public class RestorePasswordTestSuite {

    @Autowired
    MockMvc mvc;

    @MockBean
    EmailService emailService;

    @Test
    @Transactional
    @Sql("/sql/TestUser.sql")
    void RestoreCodeUpdated() throws Exception {
        mvc.perform(
            post("/api/auth/restore")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getResource("json/request/auth/RestorePasswordBody_Ok.json"))
        ).andExpect(status().isOk())
            .andExpect(content().json(new String(getResource("json/response/DefaultBody_True.json"))));
    }

    @Test
    void FailedWithNotExistEmail() throws Exception {
        mvc.perform(
            post("/api/auth/restore")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getResource("json/request/auth/RestorePasswordBody_Ok.json"))
        ).andExpect(status().isOk())
            .andExpect(content().json(new String(getResource("json/response/DefaultBody_False.json"))));
    }

    @Test
    @Transactional
    @Sql("/sql/SameUsers.sql")
    void FailedMoreThanOneRowUpdated() throws Exception {
        mvc.perform(
            post("/api/auth/restore")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getResource("json/request/auth/RestorePasswordBody_Ok.json"))
        ).andExpect(status().is5xxServerError());
    }

    @Test
    void FailedWithEmptyValue() throws Exception {
        mvc.perform(
            post("/api/auth/restore")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getResource("json/request/auth/RestorePassword_EmptyValue.json"))
        ).andExpect(status().isOk())
            .andExpect(
                content().json(
                    new String(
                        getResource("json/response/auth/RestorePassword_EmptyValue.json"))));
    }

    @Test
    void FailedWithEmptyBody() throws Exception {
        mvc.perform(post("/api/auth/restore").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(
                content().json(new String(getResource("json/response/BadRequestBody_InvalidRequest.json"))));
    }

    @Test
    void FailedWithInvalidContentType() throws Exception {
        mvc.perform(post("/api/auth/restore"))
            .andExpect(status().isBadRequest())
            .andExpect(
                content().json(new String(getResource("json/response/BadRequestBody_InvalidContentType.json"))));
    }
}
