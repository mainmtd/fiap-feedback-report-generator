package report.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import report.domain.report.Report;
import report.util.PdfUtil;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;

@ApplicationScoped
public class ReportGeneratorService {

    private static final Logger LOGGER = Logger.getLogger(ReportGeneratorService.class);

    @Inject
    S3Client s3Client;

    public String generateReport(Report report) {
        LOGGER.infov("Iniciando a geração do relatório em PDF");
        ByteArrayOutputStream pdfStream = PdfUtil.generateReport(
                report.getFeedbacks(),
                report.getCountByDay(),
                report.getCountByStatus(),
                report.getCountByUrgency(),
                report.getCountByNota()
        );

        String bucket = System.getenv("REPORT_BUCKET");
        return saveToS3(bucket, pdfStream.toByteArray());
    }

    private String saveToS3(String bucket, byte[] pdfBytes) {
        LOGGER.infov("Salvando o arquivo gerado no bucket s3 {0}", bucket);
        String key = "relatorios/relatorio-" + LocalDate.now() + ".pdf";
        s3Client.putObject(b -> b.bucket(bucket).key(key),
                software.amazon.awssdk.core.sync.RequestBody.fromBytes(pdfBytes));
        return key;
    }
}
