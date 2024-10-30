package ar.edu.itba.paw.services.email;

import ar.edu.itba.paw.models.VerificationType;
import ar.edu.itba.paw.services.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.springframework.context.i18n.LocaleContextHolder;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Locale;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Autowired
    private Environment environment;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine thymeleafTemplateEngine;

    @Autowired
    private MessageSource messageSource;

    private final String MUSICBOXD_MAIL = "info.musicboxd@gmail.com";

    public void sendHtmlMessage(String to, String subject, String htmlBody) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");

        helper.setTo(to);
        helper.setFrom(MUSICBOXD_MAIL);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);

        mailSender.send(message);
    }

    private void sendMessageUsingThymeleafTemplate(String template, String to, String subject, Map<String, Object> params, Locale locale) throws MessagingException {
        Context thymeleafContext = new Context(locale);
        thymeleafContext.setVariables(params);
        String htmlBody = thymeleafTemplateEngine.process(template, thymeleafContext);

        // Traduce el asunto del correo
        String translatedSubject = messageSource.getMessage("email.subject." + subject, null, locale);
        sendHtmlMessage(to, translatedSubject, htmlBody);
    }

    @Async
    public void sendVerification(VerificationType type, String email, String code) throws MessagingException {
        final Map<String, Object> params = new HashMap<>();
        // Obtiene el locale actual
        Locale currentLocale = LocaleContextHolder.getLocale();

        String baseUrl = environment.getProperty("app.url.base");
        String verificationURL = "";
        String template = "";
        String emailSubject = "";

        switch (type) {
            case VERIFY_EMAIL -> {
                verificationURL = baseUrl + "/user/email-verification?code=" + URLEncoder.encode(code, StandardCharsets.UTF_8);
                template = "user_verification";
                emailSubject = "verification.email";
            }
            case VERIFY_FORGOT_PASSWORD -> {
                verificationURL = baseUrl + "/user/reset-password?code=" + URLEncoder.encode(code, StandardCharsets.UTF_8);
                template = "create_password";
                emailSubject = "verification.password";
            }
            case VERIFY_GENERAL -> {
                verificationURL = baseUrl + "/general-verification?code=" + URLEncoder.encode(code, StandardCharsets.UTF_8);
                template = "general_verification";
                emailSubject = "verification.general";
            }
        }

        LOGGER.debug("Sending verification email. Type: {}, Email: {}", type, email);
        LOGGER.debug("Verification URL: {}", verificationURL);

        params.put("verificationURL", verificationURL);
        this.sendMessageUsingThymeleafTemplate(
                template,
                email,
                emailSubject,
                params,
                currentLocale
        );
    }
}