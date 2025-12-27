package report.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import report.domain.report.FeedbackReportItem;
import report.domain.report.Report;
import report.domain.urgency.UrgencyLevel;
import report.repository.FeedbackRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class ReportDataRetrieverService {

    private static final Logger LOGGER = Logger.getLogger(ReportGeneratorService.class);

    @Inject
    FeedbackRepository repository;

    public Report retrieveData(LocalDateTime start, LocalDateTime end) {
        LOGGER.infov("Buscando registros de Feedbacks no intervalo de {0} à {1}", start, end);
        List<FeedbackReportItem> feedbacks = repository.findByPeriod(start, end);

        Map<String, Long> countByDay = new HashMap<>();
        Map<String, Long> countByStatus = new HashMap<>();
        Map<UrgencyLevel, Long> countByUrgency = new HashMap<>();
        Map<Integer, Long> countByNota = new HashMap<>();


        for (FeedbackReportItem item : feedbacks) {
            String day = item.getDataCriacao().substring(0, 10);
            countByDay.put(day, countByDay.getOrDefault(day, 0L) + 1);

            countByStatus.put(item.getStatus(), countByStatus.getOrDefault(item.getStatus(), 0L) + 1);

            UrgencyLevel urg = item.getUrgency();
            countByUrgency.put(urg, countByUrgency.getOrDefault(urg, 0L) + 1);

            countByNota.put(item.getNota(), countByNota.getOrDefault(item.getNota(), 0L) + 1);
        }

        return new Report(feedbacks, countByDay, countByStatus, countByUrgency, countByNota);
    }
}

