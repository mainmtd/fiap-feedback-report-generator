package report.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import report.domain.Feedback;
import report.domain.report.FeedbackReportItem;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class FeedbackRepository {

    @Inject
    DynamoDbEnhancedClient enhancedClient;

    public List<FeedbackReportItem> findByPeriod(LocalDateTime start, LocalDateTime end) {
        String tableName = System.getenv("FEEDBACK_TABLE");

        DynamoDbTable<Feedback> table = enhancedClient.table(
                tableName, TableSchema.fromBean(Feedback.class)
        );

        List<FeedbackReportItem> result = new ArrayList<>();
        for (Feedback fb : table.scan().items()) {
            LocalDateTime created = LocalDateTime.parse(fb.getDataCriacao());
            if (!created.isBefore(start) && !created.isAfter(end)) {
                result.add(new FeedbackReportItem(
                        fb.getId(),
                        fb.getDescricao(),
                        fb.getNota(),
                        fb.getDataCriacao(),
                        fb.getStatus()
                ));
            }
        }
        return result;
    }
}
