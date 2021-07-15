package com.example.mailbox_monitoring.Connection;

import com.example.mailbox_monitoring.Client.Account;
import com.example.mailbox_monitoring.Encryption.PasswordEncryption;

import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SmtpConnection implements IConnectable
{
    //TODO: To create method for sending messages.

    private static final String host = "smtp.mail.ru";
    private static final Integer port = 465;
    private static Folder currentConnection;

    public static Folder getCurrentConnection(){
        return currentConnection;
    }

    @Override
    public boolean connect(Account account)
    {
        String to = "belyakov1910@yandex.ru";

        Properties properties = new Properties();

        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.auth", true);

        Authenticator auth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(account.getMail(), account.getPassword());
            }
        };

        Session session = Session.getDefaultInstance(properties, auth);

        Message message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(account.getMail()));
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        try {
            InternetAddress addresses = new InternetAddress(to);

            try {
                message.setRecipient(Message.RecipientType.TO, addresses);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            try {
                message.setSubject("First Email");
                message.setSentDate(new Date());
                message.setText("I don't know what is this...");

                Transport.send(message);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        } catch (AddressException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public void disconnect() {

    }
}
