package com.example.mailbox_monitoring.Encryption;

import android.os.Build;
import android.support.annotation.RequiresApi;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class PasswordEncryption implements IEncryptable
{
    private static final String PRE_SALT = "HjnK9462";
    private static final String POST_SALT = "LpUnBffE";

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public String encrypt(String password)
    {
        String cipherPassword = PRE_SALT + password + POST_SALT;

        byte[] cipherPasswordBytes = cipherPassword.getBytes(StandardCharsets.UTF_8);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            return Base64.getEncoder().encodeToString(cipherPasswordBytes);
        }

        return cipherPassword;
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public String decrypt(String cipherPasswordBytes)
    {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            byte[] encryptedPasswordBytes = Base64.getDecoder().decode(cipherPasswordBytes);
            String encryptedPassword = new String(encryptedPasswordBytes, StandardCharsets.UTF_8);

            return removeSalt(encryptedPassword);
        }
        else
        {
            return removeSalt(cipherPasswordBytes);
        }
    }

    private String removeSalt(String cipherPassword)
    {
        return cipherPassword.replace(PRE_SALT, "")
                .replace(POST_SALT, "");
    }
}
