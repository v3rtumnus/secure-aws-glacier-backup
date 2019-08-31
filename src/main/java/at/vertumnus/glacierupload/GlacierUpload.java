package at.vertumnus.glacierupload;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

@Slf4j
public class GlacierUpload {

    public static void main(String... args) throws IOException {
        if (!validateInput(args)) {
            return;
        }

        Path tarFile = TarHelper.createTarFile(args);

        log.info("Tar file was created under {}", tarFile.toFile().getAbsolutePath());
    }

    private static boolean validateInput(String... args) {
        if (args.length == 0) {
            log.error("At least one directory must be provided");
            return false;
        }

        for (String file : args) {
            if (!new File(file).exists()) {
                log.error("File/Directory {} does not exist", file);
                return false;
            }
        }

        if (System.getProperties().containsKey("encryption.password")) {
            log.error("Encryption password was not provided");
            return false;
        }

        return true;
    }
}
