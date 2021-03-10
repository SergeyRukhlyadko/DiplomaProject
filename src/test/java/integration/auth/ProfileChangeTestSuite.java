package integration.auth;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static util.TestUtil.getResource;

@SpringBootTest(classes = Main.class)
@AutoConfigureMockMvc
public class ProfileChangeTestSuite {

    @Autowired
    MockMvc mvc;

    @Test
    @Transactional
    @Sql("/sql/TestUser.sql")
    @WithMockUser("test@mail.com")
    void ProfileChanged() throws Exception {
        mvc.perform(
            post("/api/profile/my")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getResource("json/request/ProfileChange.json"))
        ).andExpect(status().isOk())
            .andExpect(content().json(new String(getResource("json/response/DefaultBody_True.json"))));
    }

    /*@Test
    @Transactional
    @Sql("/sql/TestUser.sql")
    @WithMockUser("test@mail.com")
    void DeletingFileFailed() throws Exception {
        doThrow(IOException.class).when(generalService).updateProfile(any(), any(), any(), any(), any());

        mvc.perform(
            post("/api/profile/my")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getResource("json/request/ChangeProfile.json"))
        ).andExpect(status().is5xxServerError());
    }*/
}
