package app.ecommerce.notification.mail;

import app.ecommerce.notification.dto.OrderMessage;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private SpringTemplateEngine templateEngine;
    final String messageTemplate = "order-message.html";

    @Value("${app.spring.mail.config.subject}")
    private String mailSubject;
    @Value("${app.spring.mail.config.from}")
    private String senderAddress;

    private Logger log = LoggerFactory.getLogger(EmailService.class);

    @Async
    public void sendOrderMail(String receiverMail, OrderMessage orderMessage) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
        mimeMessageHelper.setFrom(senderAddress);
        Map<String, Object> map = new HashMap<>();
        map.put("CustomerId", orderMessage.customerId());
        map.put("CustomerName", orderMessage.customerName());
        map.put("TotalBill", orderMessage.totalAmount());
        map.put("PaymentMethod", orderMessage.paymentMethod());
        map.put("ItemsPurchased", orderMessage.productList());
        Context context = new Context();
        context.setVariables(map);
        mimeMessageHelper.setSubject(mailSubject);

        try {
            String html = templateEngine.process(messageTemplate, context);
            mimeMessageHelper.setText(html, true);
            mimeMessageHelper.setTo(receiverMail);
            mailSender.send(mimeMessage);
            log.info("Email sent successfully to {} with template {}", receiverMail, messageTemplate);
        } catch (MessagingException e) {
            log.error("Error occurred while sending mail");
            e.printStackTrace();
        }
    }
}
