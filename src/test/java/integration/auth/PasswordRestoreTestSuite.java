package integration.auth;

import integration.RequestPath;
import org.diploma.app.Main;
import org.diploma.app.service.EmailService;
import org.junit.jupiter.api.BeforeAll;
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
public class PasswordRestoreTestSuite {

    @Autowired
    MockMvc mvc;

    @MockBean
    EmailService emailService;

    private static String requestPath;

    @BeforeAll
    static void setUp() {
        requestPath = RequestPath.PASSWORD_RESTORE.value();
    }

    @Test
    @Transactional
    @Sql("/sql/User.sql")
    void RestoreCodeUpdated() throws Exception {
        mvc.perform(
            post(requestPath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getResource("json/request/auth/PasswordRestoreBody_Ok.json"))
        ).andExpect(status().isOk())
            .andExpect(content().json(new String(getResource("json/response/DefaultBody_True.json"))));
    }

    @Test
    void EmailNotExist() throws Exception {
        mvc.perform(
            post(requestPath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getResource("json/request/auth/PasswordRestoreBody_Ok.json"))
        ).andExpect(status().isOk())
            .andExpect(content().json(new String(getResource("json/response/DefaultBody_False.json"))));
    }

    @Test
    @Transactional
    @Sql("/sql/SameUsers.sql")
    void MoreThanOneRowUpdated() throws Exception {
        mvc.perform(
            post(requestPath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getResource("json/request/auth/PasswordRestoreBody_Ok.json"))
        ).andExpect(status().is5xxServerError());
    }
}
