package at.vertumnus.glacierupload;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.glacier.AmazonGlacier;
import com.amazonaws.services.glacier.AmazonGlacierClientBuilder;
import com.amazonaws.services.glacier.transfer.ArchiveTransferManager;
import com.amazonaws.services.glacier.transfer.ArchiveTransferManagerBuilder;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;

@Slf4j
public class AwsClient {

    AmazonGlacier glacierClient;
    AmazonSQS sqsClient;
    AmazonSNS snsClient;
    String vaultName;

    public AwsClient(String accessKey, String secretKey, String vaultName) {
        this.vaultName = vaultName;

        AWSStaticCredentialsProvider credentials = new AWSStaticCredentialsProvider(new BasicAWSCredentials(
                accessKey, secretKey));

        this.glacierClient =
                AmazonGlacierClientBuilder.standard()
                        .withCredentials(credentials)
                        .withRegion(Regions.EU_CENTRAL_1).build();

        this.sqsClient = AmazonSQSClientBuilder.standard()
                .withCredentials(credentials)
                .withRegion(Regions.EU_CENTRAL_1).build();

        this.snsClient = AmazonSNSClientBuilder.standard()
                .withCredentials(credentials)
                .withRegion(Regions.EU_CENTRAL_1).build();
    }

    public String upload(String archiveToUpload) throws FileNotFoundException {
        ArchiveTransferManager atm = new ArchiveTransferManagerBuilder().withSqsClient(sqsClient).withSnsClient(snsClient).withGlacierClient(glacierClient).build();

        return atm.upload(vaultName, "Backup", new File(archiveToUpload)).getArchiveId();
    }
}
