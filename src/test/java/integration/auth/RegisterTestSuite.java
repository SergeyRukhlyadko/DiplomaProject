package integration.auth;

import org.diploma.app.Main;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static util.TestUtil.getResource;

@SpringBootTest(classes = Main.class)
@AutoConfigureMockMvc
public class RegisterTestSuite {

    @Autowired
    MockMvc mvc;

    @Test
    @Transactional
    @Sql("/sql/captcha.sql")
    void register_Post_200() throws Exception {
        mvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getResource("json/request/auth/RegisterBody_Ok.json"))
        ).andExpect(status().isOk())
            .andExpect(content().json(new String(getResource("json/response/DefaultBody_True.json"))));
    }

    @Test
    void register_PostEmptyJSON_200() throws Exception {
        mvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getResource("json/EmptyJSON.json"))
        ).andExpect(status().isOk())
            .andExpect(result -> assertThat(result.getResolvedException(), instanceOf(MethodArgumentNotValidException.class)))
            .andExpect(content().json(new String(getResource("json/response/BadRequestBody_EmptyAllValues.json"))));
    }

    @Test
    void register_PostEmptyAllValues_200() throws Exception {
        mvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getResource("json/request/auth/RegisterBody_EmptyAllValues.json"))
        ).andExpect(status().isOk())
            .andExpect(result -> assertThat(result.getResolvedException(), instanceOf(MethodArgumentNotValidException.class)))
            .andExpect(content().json(new String(getResource("json/response/BadRequestBody_EmptyAllValues.json"))));
    }

    @Test
    void register_PostInvalidEmail_200() throws Exception {
        mvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getResource("json/request/auth/RegisterBody_InvalidEmail.json"))
        ).andExpect(status().isOk())
            .andExpect(result -> assertThat(result.getResolvedException(), instanceOf(MethodArgumentNotValidException.class)))
            .andExpect(content().json(new String(getResource("json/response/BadRequestBody_InvalidEmail.json"))));
    }

    @Test
    void register_PostShortPassword_200() throws Exception {
        mvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getResource("json/request/auth/RegisterBody_ShortPassword.json"))
        ).andExpect(status().isOk())
            .andExpect(result -> assertThat(result.getResolvedException(), instanceOf(MethodArgumentNotValidException.class)))
            .andExpect(content().json(new String(getResource("json/response/BadRequestBody_ShortPassword.json"))));
    }

    @Test
    void register_PostInvalidContentType_400() throws Exception {
        mvc.perform(post("/api/auth/register"))
            .andExpect(status().isBadRequest())
            .andExpect(result -> assertThat(result.getResolvedException(), instanceOf(HttpMediaTypeNotSupportedException.class)))
            .andExpect(content().json(new String(getResource("json/response/BadRequestBody_InvalidContentType.json"))));
    }

    @Test
    void register_PostNullBody_400() throws Exception {
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(result -> assertThat(result.getResolvedException(), instanceOf(HttpMessageNotReadableException.class)))
            .andExpect(content().json(new String(getResource("json/response/BadRequestBody_InvalidRequest.json"))));
    }

    @Test
    @Transactional
    @Sql({"/sql/captcha.sql", "/sql/GlobalSetting_MultiUserMode_Disable.sql"})
    void register_RegistrationIsClosed_404() throws Exception {
        mvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getResource("json/request/auth/RegisterBody_Ok.json"))
        ).andExpect(status().isNotFound());
    }
}
