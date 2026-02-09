package com.lbisoft.core.app.components.freshdesk.service;

import java.io.File;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.lbisoft.core.app.Constants;
import com.lbisoft.core.app.settings.FreshdeskSettings;
import com.lbisoft.core.framework.service.CoreServices.CoreService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@CoreService
public class FreshdeskService {

    @Autowired
    private FreshdeskSettings freshdeskSettings;

    private final JavaMailSender mailSender;

    public FreshdeskService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Sends an email to Freshdesk to create a ticket
     * Used in the case of exceptions on scans such as:
     * No Customer Found
     * No Machine Found
     * Machine in Different Location
     */
    public void createFreshdeskTicketViaEmail(String emailSubject, String emailBody, String emailCc, String regionLk,
            List<InputStreamSource> images, File scanCSV)
            throws MessagingException {
        MimeMessage m = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(m, true);
        helper.setFrom(freshdeskSettings.getFromAddress());
        helper.setTo(getFreshdeskEmailByRegion(regionLk));
        if (emailCc != null) {
            helper.setCc(emailCc);
        }
        helper.setSubject(emailSubject);
        helper.setText(emailBody);
        if (images != null) {
            for (InputStreamSource iss : images) {
                helper.addAttachment(UUID.randomUUID().toString() + ".png", iss);
            }
        }
        if (scanCSV != null) {
            helper.addAttachment(scanCSV.getName(), scanCSV);
            scanCSV.deleteOnExit();
        }
        mailSender.send(m);
    }

    /**
     * Gets the freshdesk email associated with a particular region
     */
    private String getFreshdeskEmailByRegion(String regionLk) {
        if (Constants.Regions.NOAM.equals(regionLk)) {
            return freshdeskSettings.getNoamEmail();
        } else {
            return freshdeskSettings.getEuapacEmail();
        }
    }
}
