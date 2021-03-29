package integration;

import java.util.List;
import java.util.Random;

public enum RequestPath {

    REGISTRATION("/api/auth/register"),
    LOGIN("/api/auth/login"),
    PASSWORD_RESTORE("/api/auth/restore"),
    PASSWORD_CHANGE("/api/auth/password");

    private final String value;
    private static final List<RequestPath> VALUES = List.of(values());
    private static final int SIZE = VALUES.size();
    private static final Random RANDOM = new Random();

    RequestPath(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public static RequestPath randomPath() {
        return VALUES.get(RANDOM.nextInt(SIZE));
    }
}
