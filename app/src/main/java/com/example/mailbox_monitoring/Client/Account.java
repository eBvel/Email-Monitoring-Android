package com.example.mailbox_monitoring.Client;

import android.nfc.FormatException;
import com.example.mailbox_monitoring.Encryption.IEncryptable;
import com.example.mailbox_monitoring.Validators.MailValidator;
import java.io.Serializable;

public class Account implements Serializable
{
    private String mail;
    private String password;

    private final IEncryptable cryptographer;

    private Account(String email, String password, IEncryptable cryptographer)
    {
        this.cryptographer = cryptographer;

        try {
            setMail(email);
        } catch (FormatException e) {
            e.printStackTrace();
        }

        setPassword(password);
    }

    public static Account Init(String mail, String password, IEncryptable cryptographer)
    {
        return new Account(mail, password, cryptographer);
    }

    private void setMail(String mail) throws FormatException {
        //TODO: Select an abstraction for validation.
        if(MailValidator.validate(mail))
        {
            this.mail = mail;
        }
        else
        {
            throw new FormatException("Incorrect format of the mail box.");
        }
    }

    private void setPassword(String password)
    {
        if(password.isEmpty())
            throw new NullPointerException("The password field is empty!" +
                    "\nPlease, enter password of the email address: " + this.mail);

        this.password = this.cryptographer.encrypt(password);
    }

    public String getMail(){
        return this.mail;
    }

    public String getPassword(){
        return this.cryptographer.decrypt(this.password);
    }
}
