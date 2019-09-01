package at.vertumnus.glacierupload;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

@Slf4j
public class GlacierUpload {

    public static void main(String... args) throws Exception {
        if (!validateInput(args)) {
            return;
        }
        String tempDir = System.getProperty("java.io.tmpdir");

        File tarFile = new File(tempDir + File.separator + "backup.tar");
        File encryptedFile = new File(tempDir + File.separator + "backup.des");
        String password = System.getProperty("encryption.password");
        String accessKey = System.getProperty("access.key");
        String secretKey = System.getProperty("secret.key");

        log.info("Creating tar file under {}", tarFile.getAbsolutePath());
        TarHelper.createTarFile(tarFile, args);

        log.info("Starting with encryption of tar file");
        FileEncryptionHelper.encrypt(tarFile.getAbsolutePath(), encryptedFile.getAbsolutePath(), password);

        log.info("Starting with upload to AWS");
        String archiveId = new AwsClient(accessKey, secretKey, "test").upload(encryptedFile.getAbsolutePath());

        log.info("Backup successfully uploaded with archive id {}", archiveId);
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

        if (!System.getProperties().containsKey("encryption.password")) {
            log.error("Encryption password was not provided");
            return false;
        }

        if (!System.getProperties().containsKey("access.key") || !System.getProperties().containsKey("secret.key")) {
            log.error("At least one of the keys for AWS is missing");
            return false;
        }

        return true;
    }
}
