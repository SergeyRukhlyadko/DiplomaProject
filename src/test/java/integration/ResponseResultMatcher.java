package integration;

import org.springframework.test.web.servlet.ResultMatcher;

import static org.hamcrest.Matchers.blankString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ResponseResultMatcher {

    public static ResultMatcher emailExists() {
        return ResultMatcher.matchAll(
            status().isOk(),
            jsonPath("$.result").value(false),
            jsonPath("$.errors.email").value(is(not(blankString())))
        );
    }

    public static ResultMatcher emailExists(String emailProperty) {
        return ResultMatcher.matchAll(
            status().isOk(),
            jsonPath("$.result").value(false),
            jsonPath("$.errors." + emailProperty).value(is(not(blankString())))
        );
    }

    public static ResultMatcher invalidEmailFormat() {
        return ResultMatcher.matchAll(
            status().isOk(),
            jsonPath("$.result").value(false),
            jsonPath("$.errors.email").value(is(not(blankString())))
        );
    }

    public static ResultMatcher invalidEmailFormat(String emailProperty) {
        return ResultMatcher.matchAll(
            status().isOk(),
            jsonPath("$.result").value(false),
            jsonPath("$.errors." + emailProperty).value(is(not(blankString())))
        );
    }
}
