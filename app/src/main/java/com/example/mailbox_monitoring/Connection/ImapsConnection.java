package com.example.mailbox_monitoring.Connection;

import com.example.mailbox_monitoring.Client.Account;
import java.io.IOException;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;

public class ImapsConnection implements IConnectable
{
    private static final String PORT = "993";
    private static Store currentStore;

    public static Store getCurrentStore(){
        return currentStore;
    }

    @Override
    public boolean connect(Account account)
    {
        Properties properties = imapProperties();

        String email = account.getMail();
        String password = account.getPassword();

        Authenticator auth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(email, password);
            }
        };

        Session session = Session.getDefaultInstance(properties, auth);
        try
        {
            currentStore = session.getStore();

            String host = getHost(email);
            currentStore.connect(host, email, password);

            return true;
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private String getHost(String mail) throws IOException
    {
        String domain = mail.substring(mail.indexOf("@"));

        if(domain.equals("@mail.ru") || domain.equals("@bk.ru")
            || domain.equals("@inbox.ru") || domain.equals("@list.ru"))
            return "imap.mail.ru";

        switch (domain)
        {
            case "@yandex.ru" : return "imap.yandex.ru";
            case "@gmail.com" : return "imap.gmail.com";
            case "@outlook.com" : return "imap-mail.outlook.com";
            case "@yahoo.com" : return "imap.mail.yahoo.com";
            case "@icloud.com" : return "imap.mail.me.com";
            default: throw new IOException("This domain is not supported!");
        }
    }

    private Properties imapProperties()
    {
        Properties prop = new Properties();

        prop.put("mail.debug", "false");
        prop.put("mail.store.protocol", "imaps");
        prop.put("mail.imap.ssl.enable", "true");
        prop.put("mail.imap.port", PORT);

        return prop;
    }

    @Override
    public void disconnect()
    {
        try {
            getCurrentStore().close();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}