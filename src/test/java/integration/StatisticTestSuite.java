package integration;

import org.diploma.app.Main;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Main.class)
@AutoConfigureMockMvc
public class StatisticTestSuite {

    @Autowired
    MockMvc mvc;

    @Test
    void AllStatisticReceived() throws Exception {
        mvc.perform(get("/api/statistics/all")).andExpect(status().isOk());
    }

    @Test
    @Transactional
    @Sql("/sql/GlobalSetting_StatisticsIsPublic_Disable.sql")
    void GlobalSettingDisabled_UserNotAuthenticated() throws Exception {
        mvc.perform(get("/api/statistics/all")).andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional
    @Sql({"/sql/User.sql", "/sql/GlobalSetting_StatisticsIsPublic_Disable.sql"})
    @WithMockUser("user@mail.com")
    void GlobalSettingDisabled_UserAuthenticatedButNotModerator() throws Exception {
        mvc.perform(get("/api/statistics/all")).andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional
    @Sql({"/sql/Moderator.sql", "/sql/GlobalSetting_StatisticsIsPublic_Disable.sql"})
    @WithMockUser("moderator@mail.com")
    void GlobalSettingDisabled_UserAuthenticatedAndModerator() throws Exception {
        mvc.perform(get("/api/statistics/all")).andExpect(status().isOk());
    }

    @Test
    @Transactional
    @Sql("/sql/GlobalSetting_StatisticsIsPublic_Disable.sql")
    @WithMockUser
    void AuthenticatedUserNotFound() throws Exception {
        mvc.perform(get("/api/statistics/all")).andExpect(status().isInternalServerError());
    }
}
