package at.vertumnus.glacierupload;

import com.amazonaws.services.glacier.model.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Calendar;

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

        //define main vault and secondary vault for this month
        int backupIndex = (Calendar.getInstance().get(Calendar.MONTH) + 1) % 2;

        log.info("Creating tar file under {}", tarFile.getAbsolutePath());
        TarHelper.createTarFile(tarFile, args);

        log.info("Starting with encryption of tar file");
        FileEncryptionHelper.encrypt(tarFile.getAbsolutePath(), encryptedFile.getAbsolutePath(), password);

        //first, we delete the backup vault, create the new main one and then upload data
        AwsClient awsClient = new AwsClient(accessKey, secretKey);

        if (System.getProperties().containsKey("delete.archive")) {
            log.info("Removing archive");
            awsClient.deleteArchiveInVault("backup", System.getProperty("delete.archive"));
        }

        //log.info("Starting with upload to AWS");
        String archiveId = awsClient.upload("backup", encryptedFile.getAbsolutePath());

        log.info("Backup successfully uploaded with archive id {}", archiveId);

        log.info("Removing temporary file");
        boolean tarFileDeleted = tarFile.delete();
        boolean encryptedFileDeleted = encryptedFile.delete();

        if (tarFileDeleted && encryptedFileDeleted) {
            log.info("Temporary files deleted, backup finished");
        } else {
            log.error("Could not delete temporary files, backup finished");
        }
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
