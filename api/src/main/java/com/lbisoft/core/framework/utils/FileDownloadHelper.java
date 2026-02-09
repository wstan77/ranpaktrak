package com.lbisoft.core.framework.utils;

import java.io.IOException;
import java.io.InputStream;

import org.apache.tika.Tika;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public final class FileDownloadHelper {
  private FileDownloadHelper() {
  }

  private static final Logger LOGGER = LoggerFactory.getLogger(FileDownloadHelper.class);
  private static final Tika tika = new Tika();

  public static void setHeadersForFileDownload(String mimeType, String fileName, Long fileLength, boolean isPublic,
      HttpServletResponse response) {
    response.setHeader(HttpHeaders.CONTENT_TYPE, mimeType);
    response.setHeader("Content-Disposition", String.format("inline; filename=\"%s\"", fileName));
    if (fileLength != null) {
      response.setHeader(HttpHeaders.CONTENT_LENGTH, Long.toString(fileLength));
      response.setHeader(HttpHeaders.ACCEPT_RANGES, "bytes");
    }
    if (isPublic) {
      response.setHeader(HttpHeaders.CACHE_CONTROL, "public, max-age=86400");
    }
  }

  public static boolean validateExtension(byte[] data, String extension) {
    String normalizedExtension = extension.toLowerCase();
    if (!normalizedExtension.startsWith(".")) {
      normalizedExtension = "." + normalizedExtension;
    }
    String mimeTypeName = tika.detect(data, "example." + extension);
    try {
      MimeType type = MimeTypes.getDefaultMimeTypes().forName(mimeTypeName);
      return type.getExtensions().contains(normalizedExtension);
    } catch (MimeTypeException e) {
      LOGGER.error("Couldn't get mime type", e);
    }
    return false;
  }

  public static void sendFile(InputStream fileStream, String fileName, long fileLength, boolean isPublic,
      HttpServletResponse response, HttpServletRequest request) {
    String mimeType = tika.detect(fileName);

    sendFile(fileStream, fileLength, mimeType, fileName, isPublic, response, request);
  }

  public static void sendFile(InputStream fileStream, long fileLength, String mimeType, String fileName,
      boolean isPublic, HttpServletResponse response, HttpServletRequest request) {
    setHeadersForFileDownload(mimeType, fileName, fileLength, isPublic, response);
    // Check if resuming
    String range = request.getHeader(HttpHeaders.RANGE);
    Integer offset = 0;
    if (range != null) {
      int start = range.lastIndexOf('=') + 1;
      offset = NumberHelper.parseInteger(range.substring(start, range.indexOf('-'))).orElse(offset);
    }
    // Stream file download
    try (ServletOutputStream outputStream = response.getOutputStream()) {

      byte[] buffer = new byte[1024]; // Stream in 1KB chunks
      int numBytesRead = 0;

      // Handle resuming
      if (offset > 0 && fileStream.skip(offset) != offset) {
        throw new IOException("Bytes skipped were less than offset");
      }

      while ((numBytesRead = fileStream.read(buffer)) != -1) {
        outputStream.write(buffer, 0, numBytesRead);
        outputStream.flush();
      }
    } catch (IOException e) {
      LOGGER.error("Issues downloading file", e);
    } finally {
      try {
        if (fileStream != null) {
          fileStream.close();
        }
      } catch (IOException e) {
        LOGGER.error("Issues closing file streams", e);
      }
    }
  }
}
