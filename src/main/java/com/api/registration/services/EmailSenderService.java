package com.api.registration.services;

import com.api.registration.config.exceptions.EmailSenderServiceBlockedException;
import org.springframework.stereotype.Service;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Service
public class EmailSenderService {

    public void sendMail(String email, String verificationCode) {
        sendSession(email, verificationCode);
    }

    private void sendSession(String email, String verificationCode) {
        Properties prop = new Properties();
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        Session session = Session.getInstance(prop,
                new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication("alanpangnzregapi@gmail.com", "D3vpassword!");
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("registration-api@gmail.com"));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(email));
            message.setSubject("Testing Subject");
            message.setText("Dear new user," +
                    "\n\n Your verification code is: " + verificationCode);

            Transport.send(message);

        } catch (MessagingException e) {
            throw new EmailSenderServiceBlockedException();
        }
    }
}

