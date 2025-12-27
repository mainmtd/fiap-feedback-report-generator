package report.domain.report;

import report.domain.urgency.UrgencyLevel;

public class FeedbackReportItem {
    private final String id;
    private final String descricao;
    private final Integer nota;
    private final String dataCriacao;
    private final String status;
    private final UrgencyLevel urgency;

    public FeedbackReportItem(String id,
                              String descricao,
                              Integer nota,
                              String dataCriacao,
                              String status) {
        this.id = id;
        this.descricao = descricao;
        this.nota = nota;
        this.dataCriacao = dataCriacao;
        this.status = status;
        this.urgency = UrgencyLevel.fromScore(nota);
    }

    public String getId() { return id; }
    public String getDescricao() { return descricao; }
    public Integer getNota() { return nota; }
    public String getDataCriacao() { return dataCriacao; }
    public String getStatus() { return status; }
    public UrgencyLevel getUrgency() { return urgency; }
}
