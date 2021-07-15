package com.example.mailbox_monitoring;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.example.mailbox_monitoring.Notification.MessageNotifier;
import com.example.mailbox_monitoring.Connection.ImapsConnection;
import com.example.mailbox_monitoring.Serialization.Serializer;
import com.example.mailbox_monitoring.Settings.SettingsActivity;
import java.util.Timer;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Store;

public class WorkSpaceActivity extends AppCompatActivity
{
    private static final int  DELAY = 1000;
    private static final String  DEF_VALUE_PERIOD = "60 seconds";
    private ListView listView;
    private MessageNotifier messageNotifier;
    private Timer messageNotifierTimer;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.work_space);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        listView = findViewById(R.id.messagesListView);

        loadMessages();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        notifications();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_work_space, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_settings:
                actionSettings();
                return true;
            case R.id.action_exit:
                actionExit();
                return true;
            default : return super.onOptionsItemSelected(item);
        }
    }

    private void actionSettings()
    {
        Intent intent = new Intent(WorkSpaceActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    private void actionExit()
    {
        turnOffNotification();

        new Thread(() -> new ImapsConnection().disconnect()).start();
        new Serializer().clear();

        startActivity( new Intent(WorkSpaceActivity.this, MainActivity.class));
        finish();
    }

    private void notifications()
    {
        if(messageNotifierTimer != null)
            turnOffNotification();

        boolean isNotify = preferences.getBoolean("switch_preference_get_notifications", true);
        if(isNotify)
        {
            messageNotifier = new MessageNotifier(getApplicationContext());
            int period = Integer.parseInt(preferences.getString("list_preference_period", DEF_VALUE_PERIOD)
                                .replace(" seconds", "000")
                                .trim());
            turnOnNotification(DELAY, period);
        }
    }

    private void loadMessages()
    {
        MailMessages mailMessages = getMailMessages();
        ConvertedMessages convertedMessages = getConvertedMessages(mailMessages);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(  getApplicationContext(),
                android.R.layout.simple_list_item_1,
                convertedMessages.getMessages());
        listView.setAdapter(adapter);
    }

    private ConvertedMessages getConvertedMessages(MailMessages mailMessages)
    {
        ConvertedMessages convertedMessages = new ConvertedMessages(mailMessages.getMessages());

        Thread thread = new Thread(convertedMessages);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return convertedMessages;
    }

    private MailMessages getMailMessages()
    {
        MailMessages mailMessages = new MailMessages(ImapsConnection.getCurrentStore());

        Thread gmThread = new Thread(mailMessages);
        gmThread.start();
        try {
            gmThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return mailMessages;
    }

    private void turnOnNotification(int delayInMilliseconds, int periodInMilliseconds)
    {
        messageNotifierTimer = new Timer();
        messageNotifierTimer.schedule(messageNotifier,
                delayInMilliseconds, periodInMilliseconds);
    }

    private void turnOffNotification()
    {
        messageNotifierTimer.cancel();
        messageNotifierTimer = null;

        messageNotifier.clearData();
        messageNotifier = null;
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        new ImapsConnection().disconnect();
    }
}

class MailMessages implements Runnable
{
    private Message[] messages;
    Store store;

    public MailMessages(Store store)
    {
        this.store = store;
    }

    @Override
    public void run()
    {
        try {
            int lastMessages = 50;
            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);

            int count = folder.getMessageCount();

            messages = new Message[lastMessages];
            messages = folder.getMessages(count-lastMessages, count);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public Message[] getMessages(){
        return messages;
    }
}

class ConvertedMessages implements Runnable
{
    private final String[] convertedMessages;
    private final Message[] messages;

    public ConvertedMessages(Message[] messages)
    {
        convertedMessages = new String[messages.length];
        this.messages = messages;
    }

    @Override
    public void run()
    {
        int i = messages.length;

        for (Message message : messages)
        {
            try {
                convertedMessages[i-1] = message.getSubject();
                i--;
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
    }

    public String[] getMessages(){
        return convertedMessages;
    }
}