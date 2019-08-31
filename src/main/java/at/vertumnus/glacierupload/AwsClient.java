package at.vertumnus.glacierupload;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.glacier.AmazonGlacier;
import com.amazonaws.services.glacier.AmazonGlacierClientBuilder;
import com.amazonaws.services.glacier.transfer.ArchiveTransferManager;
import com.amazonaws.services.glacier.transfer.ArchiveTransferManagerBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;

@Slf4j
public class AwsClient {

    AmazonGlacier client;
    String vaultName;

    public AwsClient(String vaultName) {
        this.vaultName = vaultName;

        AWSStaticCredentialsProvider credentials = new AWSStaticCredentialsProvider(new BasicAWSCredentials(
                "accessKey", "secretKey"));

        this.client =
                AmazonGlacierClientBuilder.standard()
                        .withCredentials(credentials)
                        .withRegion(Regions.EU_CENTRAL_1).build();
    }

    public String upload(String archiveToUpload) throws FileNotFoundException {
        ArchiveTransferManager atm = new ArchiveTransferManagerBuilder().withGlacierClient(client).build();

        return atm.upload(vaultName, "Backup", new File(archiveToUpload)).getArchiveId();
    }
}
