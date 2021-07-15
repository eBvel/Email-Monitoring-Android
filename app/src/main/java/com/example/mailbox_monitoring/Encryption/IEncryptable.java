package com.example.mailbox_monitoring.Encryption;

import java.io.Serializable;

public interface IEncryptable extends Serializable
{
    String encrypt(String value);
    String decrypt(String value);
}
