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

    private static final Logger LOGGER =
            Logger.getLogger(ReportDataRetrieverService.class);

    @Inject
    FeedbackRepository repository;

    public Report retrieveData(LocalDateTime start, LocalDateTime end) {
        LOGGER.infov("Iniciando coleta de dados do relatório. Período: {0} até {1}", start, end);

        List<FeedbackReportItem> feedbacks = repository.findByPeriod(start, end);

        LOGGER.infov("Total de feedbacks recebidos do repository: {0}", feedbacks.size());

        Map<String, Long> countByDay = new HashMap<>();
        Map<String, Long> countByStatus = new HashMap<>();
        Map<UrgencyLevel, Long> countByUrgency = new HashMap<>();
        Map<Integer, Long> countByNota = new HashMap<>();

        for (FeedbackReportItem item : feedbacks) {

            LOGGER.debugf(
                    "Processando feedback: id=%s, data=%s, nota=%s, status=%s, urg=%s",
                    item.getId(),
                    item.getDataCriacao(),
                    item.getNota(),
                    item.getStatus(),
                    item.getUrgency()
            );

            LocalDateTime created = LocalDateTime.parse(item.getDataCriacao());
            String day = created.toLocalDate().toString();

            countByDay.put(day, countByDay.getOrDefault(day, 0L) + 1);
            countByStatus.put(item.getStatus(), countByStatus.getOrDefault(item.getStatus(), 0L) + 1);
            countByNota.put(item.getNota(), countByNota.getOrDefault(item.getNota(), 0L) + 1);

            if (item.getUrgency() != null) {
                countByUrgency.put(
                        item.getUrgency(),
                        countByUrgency.getOrDefault(item.getUrgency(), 0L) + 1
                );
            }
        }

        LOGGER.infov(
                "Resumo do relatório: dias={0}, status={1}, notas={2}, urgencias={3}",
                countByDay.size(),
                countByStatus.size(),
                countByNota.size(),
                countByUrgency.size()
        );

        return new Report(feedbacks, countByDay, countByStatus, countByUrgency, countByNota);
    }
}