package com.pasindu.microservices.notification_service.service;

import com.pasindu.microservices.notification_service.order.event.OrderPlacedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final JavaMailSender javaMailSender;

    @KafkaListener(topics = "order-placed")
    public void listen(OrderPlacedEvent orderPlacedEvent) {
        log.info("Got message from order-placed topic: {}", orderPlacedEvent);
        try {
            javaMailSender.send(mimeMessagePreparator -> {
                MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessagePreparator);
                messageHelper.setFrom("springshop@email.com");
                messageHelper.setTo(orderPlacedEvent.getEmail());
                messageHelper.setSubject("Order Placed Successfully");
                messageHelper.setText(String.format(
                        "Your order with order number %s has been placed successfully.",
                        orderPlacedEvent.getOrderNumber()
                ));
            });
            log.info("Order notification email sent!");
        } catch (Exception e) {
            log.error("Exception occurred when sending mail", e);
            throw new RuntimeException("Exception occurred when sending mail to " + orderPlacedEvent.getEmail(), e);
        }

    }

}
