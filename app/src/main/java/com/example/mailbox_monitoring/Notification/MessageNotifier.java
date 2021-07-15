package com.example.mailbox_monitoring.Notification;

import android.content.Context;

import com.example.mailbox_monitoring.Connection.ImapsConnection;
import com.example.mailbox_monitoring.Serialization.Serializer;
import com.example.mailbox_monitoring.WorkSpaceActivity;

import java.io.IOException;
import java.util.TimerTask;
import javax.mail.Folder;
import javax.mail.MessagingException;

public class MessageNotifier extends TimerTask
{
    private static final String fileName = "new_message_count.txt";
    private Context context;

    public MessageNotifier(Context context){
        this.context = context;
    }

    @Override
    public void run()
    {
        int newMessageCount = 0;
        int oldMessagesCount = 0;

        try {
            Folder folder = ImapsConnection.getCurrentStore().getFolder("INBOX");
            newMessageCount = folder.getUnreadMessageCount();

            Object obj = new Serializer().deserialize(context, fileName);

            if(obj != null)
                oldMessagesCount = (int)obj;
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            //e.printStackTrace();
        } catch (ClassNotFoundException e) {
            //e.printStackTrace();
        }

        notify(oldMessagesCount, newMessageCount);
        save(oldMessagesCount, newMessageCount);
    }

    private void notify(int oldCount, int newCount)
    {
        if(oldCount < newCount)
        {
            Notification notification = new Notification();
            notification.notifyUser(context, WorkSpaceActivity.class);
        }
    }

    private void save(int oldCount, int newCount)
    {
        if(oldCount != newCount)
        {
            try {
                new Serializer().serialize(context, newCount, fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void clearData()
    {
        context = null;
        new Serializer().clear(fileName);
    }
}
