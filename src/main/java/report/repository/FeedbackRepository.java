package report.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import report.domain.Feedback;
import report.domain.report.FeedbackReportItem;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class FeedbackRepository {

    private static final Logger LOGGER = Logger.getLogger(FeedbackRepository.class);

    @Inject
    DynamoDbEnhancedClient enhancedClient;

    public List<FeedbackReportItem> findByPeriod(LocalDateTime start, LocalDateTime end) {
        String tableName = System.getenv("FEEDBACK_TABLE");

        LOGGER.infof(
                "Buscando feedbacks no período [%s] até [%s] na tabela [%s]",
                start, end, tableName
        );

        DynamoDbTable<Feedback> table = enhancedClient.table(
                tableName, TableSchema.fromBean(Feedback.class)
        );

        // Filtro por range usando string ISO
        Expression filterExpression = Expression.builder()
                .expression("dataCriacao BETWEEN :start AND :end")
                .expressionValues(Map.of(
                        ":start", AttributeValue.fromS(start.toString()),
                        ":end", AttributeValue.fromS(end.toString())
                ))
                .build();

        ScanEnhancedRequest request = ScanEnhancedRequest.builder()
                .filterExpression(filterExpression)
                .build();

        List<FeedbackReportItem> result = new ArrayList<>();

        for (Feedback fb : table.scan(request).items()) {
            LOGGER.infof(
                    "Feedback encontrado: id=%s, dataCriacao=%s, nota=%s, status=%s",
                    fb.getId(),
                    fb.getDataCriacao(),
                    fb.getNota(),
                    fb.getStatus()
            );

            result.add(new FeedbackReportItem(
                    fb.getId(),
                    fb.getDescricao(),
                    fb.getNota(),
                    fb.getDataCriacao(),
                    fb.getStatus()
            ));
        }

        LOGGER.infof("Total de feedbacks encontrados no período: %d", result.size());

        return result;
    }
}
