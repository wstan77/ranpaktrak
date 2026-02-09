package com.lbisoft.core.app.components.export.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lbisoft.core.app.components.export.model.PDFExportForm;
import com.lbisoft.core.app.components.export.service.PDFExportService;
import com.lbisoft.core.framework.utils.FileDownloadHelper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(value = "/api/export")
public class ExportController {
  @Autowired
  PDFExportService pdfService;

  @PostMapping("/pdf")
  public void exportPDF(@RequestBody PDFExportForm form, HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    File pdf = pdfService.exportPDF(form);
    try (InputStream fileStream = new FileInputStream(pdf)) {
      FileDownloadHelper.sendFile(fileStream, pdf.getName(), pdf.length(), false, response, request);
    } catch (IOException e) {
      throw e;
    }
  }
}
