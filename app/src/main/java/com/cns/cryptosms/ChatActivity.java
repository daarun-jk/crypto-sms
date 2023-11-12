package com.cns.cryptosms;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.telephony.SmsManager;
import com.cns.cryptosms.Adapters.DatabaseAdapter;
import com.cns.cryptosms.Adapters.MessagesAdapter;
import com.scottyab.aescrypt.AESCrypt;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private String secretKey = "password";
    private String             username;
    private String             userNumber;
    private String             contactID;
    private static ChatActivity inst;
    private EditText            messageContent;
    private ImageButton          sendMessage;
    private DatabaseAdapter helper;
    private List<MessageModel> messages = new ArrayList<>();
    private RecyclerView messagesView;
    private MessagesAdapter mAdapter;
    SmsManager smsManager = SmsManager.getDefault();
    public static ChatActivity instance() {
        return inst;
    }
    private static final int READ_SMS_PERMISSIONS_REQUEST = 1;
    @Override
    public void onStart() {
        super.onStart();
        inst = this;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //Showing back button on toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        username       = intent.getStringExtra("Contact name");
        userNumber     = intent.getStringExtra("Contact number");
        contactID      = intent.getStringExtra("Contact id");
        messageContent = (EditText) findViewById(R.id.messageContent);
        sendMessage    = (ImageButton) findViewById(R.id.sendMessage);
        messagesView = (RecyclerView) findViewById(R.id.chat_view);
        helper         = new DatabaseAdapter(this);


        if(userNumber.length() == 10) {
            userNumber = "994"+userNumber.substring(1); //remove the first zero and add 994 instead
        } else {
            userNumber = userNumber.replaceAll("[\\D]", ""); //remove all special characters from phone number
        }

        //To be sure that sent message is not empty we will disable and hide sendMessage button
        // It will be visible and enabled only when user enters something

        sendMessage.setEnabled(false);
        sendMessage.setVisibility(View.INVISIBLE);
        messageContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().equals("")) {
                    sendMessage.setEnabled(false);
                    sendMessage.setVisibility(View.INVISIBLE);
                } else {
                    sendMessage.setEnabled(true);
                    sendMessage.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //We will set username as title for acitivity
        getSupportActionBar().setTitle(username);


        SQLiteDatabase db = helper.getWritableDatabase();
        helper.createDb(db, userNumber);
        messages.addAll(helper.getAllMessages(userNumber));
        mAdapter = new MessagesAdapter(messages);
        RecyclerView.LayoutManager mLayoutManager2 = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,true);
        messagesView.setLayoutManager(mLayoutManager2);
        messagesView.setItemAnimator(new DefaultItemAnimator());
        // messagesView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
        messagesView.setAdapter(mAdapter);



        sendMessage.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                sendMessageClick(v);
            }
        });



    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void getPermissionToReadSMS() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_SMS)) {
                Toast.makeText(this, "Please allow permission!", Toast.LENGTH_SHORT).show();
            }
            requestPermissions(new String[]{Manifest.permission.READ_SMS},
                    READ_SMS_PERMISSIONS_REQUEST);
        }
    }







    //When sendMessage button is clicked we add entered message to the chat table with this user
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void sendMessageClick(View v) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
           getPermissionToReadSMS();


        }



            try {
                String messageText = messageContent.getText().toString().trim();
                String encrpyted;
                String userN = "me";
                encrpyted = AESCrypt.encrypt(secretKey,messageText); 
                System.out.println(messageText);
                smsManager.sendTextMessage("+" + userNumber, null, encrpyted, null, null);
                Toast.makeText(this, "Message sent!", Toast.LENGTH_LONG).show();
                helper.insertMessage(userN, userNumber, userNumber,messageText);
                helper.insertLastMessage(username,userNumber,messageText,contactID);

            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }
            //ArrayList<String> parts = smsManager.divideMessage(encryptedMessage);
            checker();
//            giveReply("helo");










    }

    //Checking if new message was added and refreshing our RecyclerView
    public void checker() {
        if (helper.getMessagesCount(userNumber) > messages.size()) {
            messages.clear();
            messages.addAll(helper.getAllMessages(userNumber));
            mAdapter.notifyDataSetChanged();
        }
    }
    public void giveReply(String smsMessageStr1) {
//        String UserN = "me";
//                    String messageText = AESCrypt.decrypt(secretKey,smsMessageStr1);
            helper.insertMessage(username,userNumber,userNumber, smsMessageStr1);

        helper.insertLastMessage(username,userNumber, smsMessageStr1,contactID);

        checker();

//        String plaintext = giveReply1(smsMessageStr1);

    }
    //Simple reply system from contact
//    public String  giveReply1(String msg1) {
//
//
//        try {
//           String messageText = AESCrypt.decrypt(secretKey,msg1);
//            return messageText;
//        } catch (GeneralSecurityException e) {
//            e.printStackTrace();
//        }
//
//
//    }


    public void refreshSmsInbox() throws GeneralSecurityException {
        ContentResolver contentResolver = getContentResolver();
        Cursor smsInboxCursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null);
        int indexBody = smsInboxCursor.getColumnIndex("body");

        System.out.println(indexBody);
        if (indexBody < 0 || !smsInboxCursor.moveToFirst()) return;


        do {
            String decryptedMessage = AESCrypt.decrypt(secretKey,smsInboxCursor.getString(indexBody));
            String str = decryptedMessage;

        } while (smsInboxCursor.moveToNext());
//messages.setSelection(arrayAdapter.getCount() - 1);



    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}