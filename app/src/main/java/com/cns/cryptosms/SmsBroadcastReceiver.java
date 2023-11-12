package com.cns.cryptosms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;

import androidx.annotation.RequiresApi;

import com.cns.cryptosms.Adapters.DatabaseAdapter;
import com.scottyab.aescrypt.AESCrypt;

import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class SmsBroadcastReceiver extends BroadcastReceiver {

    public static final String SMS_BUNDLE = "pdus";
    private DatabaseAdapter helper;
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onReceive(Context context, Intent intent) {
        Bundle intentExtras = intent.getExtras();

        if (intentExtras != null) {
            Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);
//            String smsMessageStr = "";
            String smsMessageStr1 = "";
            String smsMessageStr2  = "";
            for (int i = 0; i < sms.length; ++i) {
                String format = intentExtras.getString("format");
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i], format);

                String smsBody = smsMessage.getMessageBody();
                 String address = smsMessage.getOriginatingAddress();


//                smsMessageStr += "SMS From: " + address + "\n";

                smsMessageStr1 += smsBody;
                smsMessageStr2 += address;



            }


            ChatActivity inst = ChatActivity.instance();
            String messageText = "";
            String UserN = "me";
            String key = "password";
            try {
                messageText = AESCrypt.decrypt(key,smsMessageStr1);

            } catch (NullPointerException e) {
                e.printStackTrace();
            }catch (GeneralSecurityException e){
                e.printStackTrace();
            }
System.out.println(UserN+smsMessageStr2+messageText);
            inst.giveReply(messageText);


            //            try {
//                inst.updateInbox(smsMessageStr1,smsMessageStr);
//            } catch (NoSuchPaddingException e) {
//                e.printStackTrace();
//            } catch (NoSuchAlgorithmException e) {
//                e.printStackTrace();
//            } catch (IllegalBlockSizeException e) {
//                e.printStackTrace();
//            } catch (BadPaddingException e) {
//                e.printStackTrace();
//            } catch (InvalidKeyException e) {
//                e.printStackTrace();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }
    }



}