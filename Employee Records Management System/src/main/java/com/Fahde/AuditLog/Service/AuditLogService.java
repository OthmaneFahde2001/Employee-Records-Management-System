package com.Fahde.AuditLog.Service;

import com.Fahde.AuditLog.Entity.AuditLog;
import com.Fahde.AuditLog.Repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public byte[] generateAuditLogPdf() throws IOException {
        List<AuditLog> auditLogs = auditLogRepository.findAll();

        try (PDDocument document = new PDDocument(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.beginText();
                contentStream.setLeading(14.5f);
                contentStream.newLineAtOffset(50, 750);

                contentStream.showText("Audit Log Report");
                contentStream.newLine();
                contentStream.newLine();

                contentStream.showText("ID | Action | Changed By | Timestamp | Employee Name | Before Value | After Value");
                contentStream.newLine();
                contentStream.showText("--------------------------------------------------------------------------------------");
                contentStream.newLine();

                for (AuditLog log : auditLogs) {
                    String logEntry = String.format(
                            "%d | %s | %s | %s | %s | %s | %s",
                            log.getId(),
                            log.getAction(),
                            log.getChangedBy(),
                            log.getChangeTimestamp(),
                            log.getEmployeeFullName(),
                            truncateValue(log.getBeforeValue()),
                            truncateValue(log.getAfterValue())
                    );

                    contentStream.showText(logEntry);
                    contentStream.newLine();
                }

                contentStream.endText();
            }

            document.save(out);
            return out.toByteArray();
        }
    }


    private String truncateValue(String value) {
        if (value != null && value.length() > 20) {
            return value.substring(0, 20) + "...";
        }
        return value;
    }
}
