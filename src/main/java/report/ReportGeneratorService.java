package report;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;

@ApplicationScoped
public class ReportGeneratorService {

    @Inject
    S3Client s3Client;

    public void generateReport(JsonNode json) {
        JsonNode feedbacks = json.get("feedbacks");
        JsonNode countByDay = json.get("countByDay");
        JsonNode countByUrgency = json.get("countByUrgency");

        // Gerar PDF
        ByteArrayOutputStream pdfStream = PdfUtil.generateReport(feedbacks, countByDay, countByUrgency);

        // Salva no bucket S3
        String bucket = System.getenv("REPORT_BUCKET");
        saveToS3(bucket, pdfStream.toByteArray());

    }

    private void saveToS3(String bucket, byte[] pdfBytes) {
        String key = "relatorios/relatorio-" + LocalDate.now() + ".pdf";
        s3Client.putObject(b -> b.bucket(bucket).key(key),
                software.amazon.awssdk.core.sync.RequestBody.fromBytes(pdfBytes));

    }
}
