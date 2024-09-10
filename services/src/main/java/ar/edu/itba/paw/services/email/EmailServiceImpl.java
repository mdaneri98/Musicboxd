package ar.edu.itba.paw.services.email;

import ar.edu.itba.paw.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
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
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
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

    //@Async
    public void sendVerification(String email, String code) throws MessagingException {
        final Map<String, Object> params = new HashMap<>();

        String url = "www.localhost:8080/webapp_war";
        url = url + "/user/verification?code=" + code;

        params.put("verificationURL", url);
        this.sendMessageUsingThymeleafTemplate(
                "user_verification",
                email,
                "MusicBoxd - Verification",
                params
        );
    }

}
