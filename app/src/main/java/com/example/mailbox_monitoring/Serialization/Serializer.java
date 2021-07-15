package com.example.mailbox_monitoring.Serialization;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;


public class Serializer
{
    private static final String name = "acc_data.txt";
    private static final String path = "//data//data//com.example.mailbox_monitoring//files//";

    public void serialize(Context context, Serializable object) throws IOException
    {
        FileOutputStream fileStream = context.openFileOutput(name, Context.MODE_PRIVATE);
        ObjectOutputStream objectStream = new ObjectOutputStream(fileStream);
        objectStream.writeObject(object);
        objectStream.close();
        fileStream.close();
    }

    public void serialize(Context context, Serializable object, String fileName) throws IOException
    {
        FileOutputStream fileStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
        ObjectOutputStream objectStream = new ObjectOutputStream(fileStream);
        objectStream.writeObject(object);
        objectStream.close();
        fileStream.close();
    }

    public Object deserialize(Context context) throws IOException, ClassNotFoundException
    {
        File file = new File(path+name);

        if(file.exists())
        {
            FileInputStream fileStream = context.openFileInput(name);
            ObjectInputStream objectStream = new ObjectInputStream(fileStream);
            Object object = objectStream.readObject();
            objectStream.close();
            fileStream.close();

            return object;
        }

        return null;
    }

    public Object deserialize(Context context, String fileName) throws IOException, ClassNotFoundException
    {
        File file = new File(path+fileName);

        if(file.exists())
        {
            FileInputStream fileStream = context.openFileInput(fileName);
            ObjectInputStream objectStream = new ObjectInputStream(fileStream);
            Object object = objectStream.readObject();
            objectStream.close();
            fileStream.close();

            return object;
        }

        return null;
    }

    public void clear()
    {
        File file = new File(path+name);

        if(file.exists())
            file.delete();
    }

    public void clear(String fileName)
    {
        File file = new File(path+fileName);

        if(file.exists())
            file.delete();
    }
}
