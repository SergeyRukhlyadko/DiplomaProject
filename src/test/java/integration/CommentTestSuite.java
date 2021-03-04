package integration;

import org.diploma.app.Main;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static util.TestUtil.getResource;

@SpringBootTest(classes = Main.class)
@AutoConfigureMockMvc
public class CommentTestSuite {

    @Autowired
    MockMvc mvc;

    @Test
    @Transactional
    @Sql({"/sql/TestUser.sql", "/sql/Post.sql"})
    @WithMockUser("test@mail.com")
    void CommentAdded() throws Exception {
        mvc.perform(
            post("/api/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getResource("json/request/AddComment_Ok.json"))
        ).andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(greaterThan(0)));
    }
}
