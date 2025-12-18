package report;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

public class PdfUtil {

    public static ByteArrayOutputStream generateReport(JsonNode feedbacks,
                                                       JsonNode countByDay,
                                                       JsonNode countByUrgency) {
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

            // Estatísticas por dia
            content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 16);
            content.beginText();
            content.newLineAtOffset(50, 690);
            content.showText("Feedbacks por dia:");
            content.endText();

            int y = 670;
            content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);

            for (Map.Entry<String, JsonNode> entry : countByDay.properties()) {
                content.beginText();
                content.newLineAtOffset(60, y);
                content.showText(LocalDate.parse(entry.getKey()) + " : " + entry.getValue().asInt());
                content.endText();
                y -= 20;
            }

            // Estatísticas por urgência
            content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 16);
            content.beginText();
            content.newLineAtOffset(50, y - 10);
            content.showText("Feedbacks por urgência:");
            content.endText();

            y -= 30;
            content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);


            for (Map.Entry<String, JsonNode> entry : countByUrgency.properties()) {
                content.beginText();
                content.newLineAtOffset(60, y);
                content.showText(entry.getKey() + " : " + entry.getValue().asInt());
                content.endText();
                y -= 20;
            }

            // Rodapé
            content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_OBLIQUE), 10);
            content.beginText();
            content.newLineAtOffset(50, 50);
            content.showText("Gerado automaticamente pelo sistema de relatórios - FIAP -" + LocalDate.now());
            content.endText();

            content.close();
            document.save(out);
            return out;

        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar PDF", e);
        }
    }
}
