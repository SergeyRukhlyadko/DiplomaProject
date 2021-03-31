package integration.auth;

import integration.RequestPath;
import org.diploma.app.Main;
import org.diploma.app.model.db.entity.enumeration.GlobalSetting;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.ResultMatcher.matchAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Main.class)
@AutoConfigureMockMvc
public class SettingsTestSuite {

    @Autowired
    MockMvc mvc;

    static String requestPath;

    @BeforeAll
    static void setup() {
        requestPath = RequestPath.Get.SETTINGS.value();
    }

    @Test
    void GlobalSettingsReceived() throws Exception {
        Set<String> enumNames = Arrays.stream(GlobalSetting.values()).map(Enum::name).collect(Collectors.toSet());
        ResultMatcher[] jsonMatchers = new ResultMatcher[enumNames.size()];
        int i = 0;
        for(String name : enumNames) {
            jsonMatchers[i] = jsonPath("$." + name).isBoolean();
            i++;
        }

        mvc.perform(get(requestPath)).andExpect(status().isOk()).andExpect(matchAll(jsonMatchers));
    }

    @Test
    @Transactional
    @Sql("/sql/GlobalSettings_DeleteAll.sql")
    void GlobalSettingsMissing() throws Exception {
        mvc.perform(get(requestPath)).andExpect(status().is5xxServerError());
    }
}
