package com.example.mailbox_monitoring;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.example.mailbox_monitoring.Client.Account;
import com.example.mailbox_monitoring.Connection.IConnectable;
import com.example.mailbox_monitoring.Connection.ImapsConnection;
import com.example.mailbox_monitoring.Encryption.PasswordEncryption;
import com.example.mailbox_monitoring.Serialization.Serializer;

import java.io.IOException;


public class MainActivity extends AppCompatActivity
{
    private EditText editTextEmail, editTextPassword;
    private CheckBox rememberMe;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Account account = tryGetAccount();
        tryConnectToMail(account);
    }

    @Override
    protected void onStart() {
        super.onStart();

        editTextEmail = findViewById(R.id.emailEditText);
        editTextPassword = findViewById(R.id.passwordEditText);
        rememberMe = findViewById(R.id.rememberCheckBox);
    }

    private Account tryGetAccount()
    {
        Account account = null;

        try {
            account = (Account) new Serializer().deserialize(getApplicationContext());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return account;
    }

    public void tryConnectToMail(Account account)
    {
        if(account != null)
        {
            try {
                connectToMail(account);

                startActivity(new Intent(MainActivity.this, WorkSpaceActivity.class));
                finish();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void LoginOnClick(View view)
    {
        Account account = Account.Init( editTextEmail.getText().toString(),
                                        editTextPassword.getText().toString(),
                                        new PasswordEncryption());
        try {
            connectToMail(account);

            startActivity(new Intent(MainActivity.this, WorkSpaceActivity.class));
            //finish();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void connectToMail(Account account) throws InterruptedException
    {
        Thread thread = new Thread(() -> {
            if(login(account, new ImapsConnection()))
            {
                if(rememberMe != null)
                {
                    if(rememberMe.isChecked())
                    {
                        try {
                            new Serializer().serialize(getApplicationContext(), account);
                        } catch (IOException e) {
                            e.printStackTrace();

                        }
                    }
                }
            }
            else
            {
                try {
                    throw new IOException("Failed to sign in.\n" +
                            "Please make sure that you've entered your login and password correctly.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        thread.join();
    }

    private boolean login(Account account, IConnectable protocol)
    {
        return protocol.connect(account);
    }
}