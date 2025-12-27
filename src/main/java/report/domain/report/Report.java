package report.domain.report;

import report.domain.urgency.UrgencyLevel;

import java.util.List;
import java.util.Map;

public class Report {
    private final List<FeedbackReportItem> feedbacks;
    private final Map<String, Long> countByDay;
    private final Map<String, Long> countByStatus;
    private final Map<UrgencyLevel, Long> countByUrgency;
    private final Map<Integer, Long> countByNota;

    public Report(List<FeedbackReportItem> feedbacks,
                  Map<String, Long> countByDay,
                  Map<String, Long> countByStatus,
                  Map<UrgencyLevel, Long> countByUrgency,
                  Map<Integer, Long> countByNota) {
        this.feedbacks = feedbacks;
        this.countByDay = countByDay;
        this.countByStatus = countByStatus;
        this.countByUrgency = countByUrgency;
        this.countByNota = countByNota;
    }

    public List<FeedbackReportItem> getFeedbacks() { return feedbacks; }
    public Map<String, Long> getCountByDay() { return countByDay; }
    public Map<String, Long> getCountByStatus() { return countByStatus; }
    public Map<UrgencyLevel, Long> getCountByUrgency() { return countByUrgency; }
    public Map<Integer, Long> getCountByNota() { return countByNota; }
}

