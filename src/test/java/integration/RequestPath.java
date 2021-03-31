package integration;

public class RequestPath {

    public enum Get {
        SETTINGS("/api/settings");

        private final String value;

        Get(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public enum Post {
        REGISTRATION("/api/auth/register"),
        LOGIN("/api/auth/login"),
        PASSWORD_RESTORE("/api/auth/restore"),
        PASSWORD_CHANGE("/api/auth/password");

        private final String value;

        Post(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }
}
