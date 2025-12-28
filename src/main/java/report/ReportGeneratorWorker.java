package report;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.jboss.logging.Logger;
import report.domain.report.Report;
import report.service.ReportDataRetrieverService;
import report.service.ReportGeneratorService;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;

@ApplicationScoped
@Named("reportGeneratorWorker")
public class ReportGeneratorWorker implements RequestHandler<JsonNode, Void> {

    private static final Logger LOGGER = Logger.getLogger(ReportGeneratorWorker.class);

    @Inject
    ReportGeneratorService reportGeneratorService;

    @Inject
    ReportDataRetrieverService reportDataRetrieverService;

    @Inject
    SnsClient snsClient;

    @Override
    public Void handleRequest(JsonNode input, Context context) {
        String reportType = input.has("reportType") ? input.get("reportType").asText() : "relatorio-semanal";

        LocalDate today = LocalDate.now();
        LocalDate lastSunday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        LocalDate start = lastSunday.minusWeeks(1);
        LocalDate end = lastSunday.minusDays(1);

        LOGGER.infov(
                "Processing a geração do relatório de tipo {0} para o período de {1} à {2}",
                reportType,
                start,
                end
        );

        Report report = reportDataRetrieverService.retrieveData(start.atStartOfDay(), end.atTime(LocalTime.MAX));

        String key = reportGeneratorService.generateReport(report);
        String bucket = System.getenv("REPORT_BUCKET");
        String s3Url = buildS3Url(bucket, key);

        String topicArn = System.getenv("REPORT_TOPIC_ARN");
        String titulo = String.format("Relatório Semanal de Feedbacks %s - %s", start, end);
        String corpo = String.format("Seu relatório de Feedbacks da semana %s a %s. Contendo os feedbacks, quantidade de feedback por dia, urgência e nota.", start, end);
        String payload = String.format(
                "{\"subject\":\"%s\",\"body\":\"%s\",\"s3Url\":\"%s\"}",
                titulo,
                corpo,
                s3Url
        );

        snsClient.publish(PublishRequest.builder()
                .topicArn(topicArn)
                .message(payload)
                .build());

        return null;
    }

    private String buildS3Url(String bucket, String key) {
        return "https://" + bucket + ".s3.amazonaws.com/" + key;
    }
}

