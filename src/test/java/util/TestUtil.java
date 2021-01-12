package util;

import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

public class TestUtil {

    public static byte[] getResource(String path) throws IOException {
        return new ClassPathResource(path).getInputStream().readAllBytes();
    }
}
