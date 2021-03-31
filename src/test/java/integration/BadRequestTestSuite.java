package integration;

import org.diploma.app.Main;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static util.TestUtil.getResource;

@SpringBootTest(classes = Main.class)
@AutoConfigureMockMvc
public class BadRequestTestSuite {

    @Autowired
    MockMvc mvc;

    @ParameterizedTest
    @EnumSource(RequestPath.Post.class)
    void NotSupportedContentType(RequestPath.Post path) throws Exception {
        mvc.perform(post(path.value()))
            .andExpect(status().isBadRequest())
            .andExpect(
                content().json(new String(getResource("json/response/BadRequestBody_NotSupportedContentType.json"))));
    }

    @ParameterizedTest
    @EnumSource(RequestPath.Post.class)
    void EmptyBody(RequestPath.Post path) throws Exception {
        mvc.perform(post(path.value()).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(content().json(new String(getResource("json/response/BadRequestBody_InvalidRequestBody.json"))));
    }

    @ParameterizedTest
    @EnumSource(RequestPath.Post.class)
    void InvalidJSON(RequestPath.Post path) throws Exception {
        mvc.perform(
            post(path.value())
                .contentType(MediaType.APPLICATION_JSON)
                .content(getResource("json/InvalidJSON.json"))
        ).andExpect(status().isBadRequest())
            .andExpect(content().json(new String(getResource("json/response/BadRequestBody_InvalidRequestBody.json"))));
    }
}
