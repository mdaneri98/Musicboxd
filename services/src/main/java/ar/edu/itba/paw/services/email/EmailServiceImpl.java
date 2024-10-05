package ar.edu.itba.paw.services.email;

import ar.edu.itba.paw.models.VerificationType;
import ar.edu.itba.paw.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private Environment environment;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine thymeleafTemplateEngine;

    private final String MUSICBOXD_MAIL = "info.musicboxd@gmail.com";

    /**
     * This method will send compose and send a new message
     * */
    public void sendHtmlMessage(String to, String subject, String htmlBody) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");


        helper.setTo(to);
        helper.setFrom(MUSICBOXD_MAIL);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);


        mailSender.send(message);
    }

    private void sendMessageUsingThymeleafTemplate(String template, String to, String subject, Map<String, Object> params) throws MessagingException {
        Context thymeleafContext = new Context();
        thymeleafContext.setVariables(params);
        String htmlBody = thymeleafTemplateEngine.process(template, thymeleafContext);
        sendHtmlMessage(to, subject, htmlBody);
    }

    @Async
    public void sendVerification(VerificationType type, String email, String code) throws MessagingException {
        final Map<String, Object> params = new HashMap<>();

        String baseUrl = environment.getProperty("app.url.base");
        String verificationURL = "";
        String template = "";
        switch (type) {
            case VERIFY_EMAIL -> {
                verificationURL = baseUrl + "/user/email-verification?code=" + URLEncoder.encode(code, StandardCharsets.UTF_8);
                template = "user_verification";
            }
            case VERIFY_FORGOT_PASSWORD -> {
                verificationURL = baseUrl + "/user/create-password?code=" + URLEncoder.encode(code, StandardCharsets.UTF_8);
                template = "create_password";
            }
        }

        params.put("verificationURL", verificationURL);
        this.sendMessageUsingThymeleafTemplate(
                template,
                email,
                "MusicBoxd - Verification",
                params
        );
    }

}
