package ai.caria.historical.app;

import io.gridgo.utils.exception.RuntimeIOException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SwaggerStore {

    public static String getContent(@NonNull String configFilePath) {
        try {
            return new String(Files.readAllBytes(new File(configFilePath).toPath()));
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }
}

