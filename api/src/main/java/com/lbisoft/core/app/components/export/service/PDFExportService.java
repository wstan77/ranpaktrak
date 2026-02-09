package com.lbisoft.core.app.components.export.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.lbisoft.core.app.components.export.model.PDFExportForm;
import com.lbisoft.core.framework.service.CoreServices.CoreService;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.util.Matrix;

@CoreService
public class PDFExportService {

    private static final float PAGE_NUMBER_OFFSET = 8;

    public File exportPDF(PDFExportForm form) throws IOException {
        // Fix connection to local development server
        String html = form.getHtml().replaceAll("localhost", "webapp");
        File htmlFile = File.createTempFile("export_html", ".html");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(htmlFile))) {
            writer.write(html);
        }
        return exportPDF(htmlFile);
    }

    public File exportPDF(File htmlFile) throws IOException {
        File out = File.createTempFile("export", ".pdf");
        Process process = new ProcessBuilder("/usr/bin/chromium", "--headless", "--disable-gpu", "--no-sandbox",
                "--print-to-pdf-no-header", "--print-to-pdf=" + out.getAbsolutePath(), htmlFile.getAbsolutePath())
                        .inheritIO().start();
        int errorCode = -1;
        try {
            errorCode = process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (errorCode != 0) {
            return null;
        }
        try {
            addFooter(out);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return out;
    }

    private void addFooter(File pdfFile) throws IOException {
        PDFont font = PDType1Font.HELVETICA;
        float fontSize = 10.0f;

        try (PDDocument doc = PDDocument.load(pdfFile)) {
            for (int pageNum = 0; pageNum < doc.getNumberOfPages(); pageNum++) {
                PDPage page = doc.getPage(pageNum);
                String message = (pageNum + 1) + "/" + doc.getNumberOfPages();

                // Calculate position for page number text
                PDRectangle pageSize = page.getMediaBox();
                float stringWidth = font.getStringWidth(message) * fontSize / 1000f;
                float centerX = pageSize.getUpperRightX() - stringWidth - PAGE_NUMBER_OFFSET;
                float centerY = pageSize.getLowerLeftY() + PAGE_NUMBER_OFFSET;

                int rotation = page.getRotation();
                boolean rotate = rotation == 90 || rotation == 270;
                // append the content to the existing stream
                try (PDPageContentStream contentStream = new PDPageContentStream(doc, page, AppendMode.APPEND, true,
                        true)) {
                    contentStream.beginText();
                    // set font and font size
                    contentStream.setFont(font, fontSize);
                    // set text color to black
                    contentStream.setNonStrokingColor(0f, 0f, 0f);
                    if (rotate) {
                        // rotate the text according to the page rotation
                        contentStream.setTextMatrix(Matrix.getRotateInstance(Math.PI / 2, centerX, centerY));
                    } else {
                        contentStream.setTextMatrix(Matrix.getTranslateInstance(centerX, centerY));
                    }
                    contentStream.showText(message);
                    contentStream.endText();
                }
            }

            doc.save(pdfFile);
        }
    }
}
