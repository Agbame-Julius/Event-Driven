package helloworld;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

import java.util.Map;

public class App implements RequestHandler<Map<String, Object>, String> {

    private final String topicArn = System.getenv("SNS_TOPIC_ARN");

    @Override
    public String handleRequest(Map<String, Object> event, Context context) {
        System.out.println("EVENT: " + event);

        var records = (java.util.List<Map<String, Object>>) event.get("Records");
        var record = records.get(0);
        var s3 = (Map<String, Object>) record.get("s3");
        var bucket = ((Map<String, Object>) s3.get("bucket")).get("name");
        var key = ((Map<String, Object>) s3.get("object")).get("key");

        String message = "A new object has been uploaded:\nBucket: " + bucket + "\nKey: " + key;

        SnsClient snsClient = SnsClient.create();
        snsClient.publish(PublishRequest.builder()
                .topicArn(topicArn)
                .subject("New File Uploaded to S3")
                .message(message)
                .build());

        return "Email notification sent.";
    }
}
