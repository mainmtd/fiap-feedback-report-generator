package report.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import report.domain.report.FeedbackReportItem;
import report.domain.urgency.UrgencyLevel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class PdfUtil {

    public static ByteArrayOutputStream generateReport(List<FeedbackReportItem> feedbacks,
                                                       Map<String, Long> countByDay,
                                                       Map<String, Long> countByStatus,
                                                       Map<UrgencyLevel, Long> countByUrgency,
                                                       Map<Integer, Long> countByNota) {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream content = new PDPageContentStream(document, page);

            // Cabeçalho
            content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 16);
            content.beginText();
            content.newLineAtOffset(50, 750);
            content.showText("Relatório Semanal de Feedbacks");
            content.endText();

            // Resumo geral
            content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
            content.beginText();
            content.newLineAtOffset(50, 720);
            content.showText("Total de feedbacks coletados: " + feedbacks.size());
            content.endText();

            int y = 690;

            // Feedbacks por dia
            y = section(content, y, "Feedbacks por dia:", () -> {
                for (Map.Entry<String, Long> e : countByDay.entrySet()) {
                    line(content, e.getKey() + " : " + e.getValue());
                }
            });

            // Feedbacks por status
            y = section(content, y, "Feedbacks por status:", () -> {
                for (Map.Entry<String, Long> e : countByStatus.entrySet()) {
                    line(content, e.getKey() + " : " + e.getValue());
                }
            });

            // Feedbacks por urgência
            y = section(content, y, "Feedbacks por urgência:", () -> {
                for (Map.Entry<UrgencyLevel, Long> e : countByUrgency.entrySet()) {
                    line(content, e.getKey().getLabel() + " : " + e.getValue());
                }
            });

            // Distribuição por nota
            y = section(content, y, "Distribuição de notas:", () -> {
                for (Map.Entry<Integer, Long> e : countByNota.entrySet()) {
                    line(content, e.getKey() + " : " + e.getValue());
                }
            });

            // Rodapé
            content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_OBLIQUE), 10);
            content.beginText();
            content.newLineAtOffset(50, 50);
            content.showText("Gerado automaticamente pelo sistema de relatórios - FIAP - " + LocalDate.now());
            content.endText();

            content.close();
            document.save(out);
            return out;

        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar PDF", e);
        }
    }

    private static int section(PDPageContentStream content, int y, String title, Runnable body) throws IOException {
        if (y < 120) y = newPage(content); // simples controle de quebra
        content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 14);
        content.beginText();
        content.newLineAtOffset(50, y);
        content.showText(title);
        content.endText();
        y -= 20;

        content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
        body.run();
        return y - 10;
    }

    private static void line(PDPageContentStream content, String text) {
        try {
            content.beginText();
            content.newLineAtOffset(60, PdfCursor.nextLine());
            content.showText(text);
            content.endText();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao escrever linha no PDF", e);
        }
    }

    static class PdfCursor {
        private static int y = 670;

        public static int nextLine() {
            y -= 20;
            return y;
        }

        public static void reset(int startY) {
            y = startY;
        }
    }

    private static int newPage(){
        PdfCursor.reset(670);
        return 690;
    }
}
