package report.domain.urgency;

public enum UrgencyLevel {
    HIGH("ALTA", "alta"),
    MEDIUM("MÉDIA", "media"),
    LOW("BAIXA", "baixa");

    private final String label;

    UrgencyLevel(String label) {
        this.label = label;
    }

    public String getLabel() { return label; }

    public static UrgencyLevel fromScore(int score) {
        if (score <= 2) return HIGH;
        if (score < 4) return MEDIUM;
        return LOW;
    }
}
