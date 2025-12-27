package report.util;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import io.quarkus.qute.Template;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import report.domain.report.FeedbackReportItem;
import report.domain.urgency.UrgencyLevel;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class PdfUtil {

    @Inject
    Template report;

    public ByteArrayOutputStream generateReport(List<FeedbackReportItem> feedbacks,
                                                Map<String, Long> countByDay,
                                                Map<UrgencyLevel, Long> countByUrgency,
                                                Map<Integer, Long> countByNota) {

        String html = report
                .data("totalFeedbacks", feedbacks.size())
                .data("feedbacks", feedbacks)
                .data("feedbacksPorDia", countByDay)
                .data("feedbacksPorUrgencia", countByUrgency)
                .data("feedbacksPorNota", countByNota)
                .data("data", java.time.LocalDate.now())
                .render();

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.toStream(out);
            builder.run();
            return out;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF", e);
        }
    }
}
