package org.diploma.app.util;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Component
public class OperatingSystemUtil {

    OperatingSystem os;

    @Value("${win.local-disk}")
    String localDisk;

    /*
        throws UnsupportedOperatingSystemException
     */
    public OperatingSystemUtil() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            os = OperatingSystem.WINDOWS;
        } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("aix")) {
            os = OperatingSystem.LINUX;
        } else {
            throw new UnsupportedOperatingSystemException(osName + " not supported");
        }
    }

    public OperatingSystem getCurrent() {
        return os;
    }

    public void createDirectories(String dirs) throws IOException {
        switch(os) {
            case WINDOWS:
                Files.createDirectories(Path.of(localDisk + dirs));
                break;
            case LINUX:
                Files.createDirectories(Path.of(dirs));
        }
    }

    public void createFileThenWrite(String path, byte[] bytes) throws IOException {
        Path absolutePath = null;
        switch(os) {
            case WINDOWS:
                absolutePath = Path.of(localDisk + path);
                break;
            case LINUX:
                absolutePath = Path.of(path);
        }

        Files.createFile(absolutePath);
        Files.write(absolutePath, bytes);
    }

    public void deleteFile(String path) throws IOException {
        switch(os) {
            case WINDOWS:
                Files.delete(Path.of(localDisk + path));
                break;
            case LINUX:
                Files.delete(Path.of(path));
        }
    }
}
