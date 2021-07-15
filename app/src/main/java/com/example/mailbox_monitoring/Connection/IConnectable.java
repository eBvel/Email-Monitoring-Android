package com.example.mailbox_monitoring.Connection;

import com.example.mailbox_monitoring.Client.Account;

public interface IConnectable
{
    boolean connect(Account account);
    void disconnect();
}
