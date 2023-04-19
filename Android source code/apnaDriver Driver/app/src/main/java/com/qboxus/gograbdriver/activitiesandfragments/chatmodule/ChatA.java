package com.qboxus.gograbdriver.activitiesandfragments.chatmodule;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.qboxus.gograbdriver.appinterfaces.CallbackResponce;
import com.qboxus.gograbdriver.helpingclasses.ApiRequest;
import com.qboxus.gograbdriver.helpingclasses.ApisList;
import com.qboxus.gograbdriver.helpingclasses.Functions;
import com.qboxus.gograbdriver.helpingclasses.Preferences;
import com.qboxus.gograbdriver.helpingclasses.Variables;
import com.qboxus.gograbdriver.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;


public class ChatA extends AppCompatActivity {

    public String token = "null";
    public DatabaseReference mchatRefReteriving;
    public DatabaseReference receiveTypingIndication;
    public String senderid, receiverid, receiverName, receiverPic;
    public static String orderId= "0";
    public String senderName, riderPic;
    DatabaseReference rootref;
    EditText message;
    RecyclerView chatrecyclerview;
    TextView reciverName;
    ChatAdapter mAdapter;
    ProgressBar pBar;
    Query queryGetchat;
    Preferences preferences;
    ValueEventListener valueEventListener;
    ChildEventListener eventListener;
    // receive the type indication to show that your friend is typing or not
    LinearLayout mainlayout;
    private DatabaseReference sendTypingIndication;
    private List<ChatModel> mChats = new ArrayList<>();
    private String chatchild = "";
    private ImageView sendbtn;
    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        preferences = new Preferences(this);
        Intent bundle = getIntent();

        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(downloadReceiver, filter);

        // intialize the database refer

        rootref = FirebaseDatabase.getInstance().getReference();
        message = findViewById(R.id.msgedittext);
        reciverName = findViewById(R.id.reciver_name);

        riderPic = preferences.getKeyUserImage();

        if (riderPic == null || (riderPic.equals("") || riderPic.equals("null"))) {
            riderPic = "";
        }

        senderName = preferences.getKeyUserName();

        if (bundle != null) {

            senderid = bundle.getStringExtra("senderid");
            receiverid = bundle.getStringExtra("user_id");
            receiverName = bundle.getStringExtra("user_name");
            receiverPic = bundle.getStringExtra("user_img");
            orderId = bundle.getStringExtra("order_id");
            if (orderId.equalsIgnoreCase("0")) {
                chatchild = receiverid + "-" + senderid;
            }
            else {
                chatchild = orderId;
            }

            reciverName.setText(receiverName);
        }

        token = "null";
        rootref.child("Users").child(receiverid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    token = dataSnapshot.child("token").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


        pBar =findViewById(R.id.progress_bar);

        chatrecyclerview = findViewById(R.id.chatlist);
        final LinearLayoutManager layout = new LinearLayoutManager(this);
        layout.setStackFromEnd(true);
        chatrecyclerview.setLayoutManager(layout);
        chatrecyclerview.setHasFixedSize(false);
        ((SimpleItemAnimator) chatrecyclerview.getItemAnimator()).setSupportsChangeAnimations(false);
        OverScrollDecoratorHelper.setUpOverScroll(chatrecyclerview, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
        mAdapter = new ChatAdapter(mChats, senderid, this, new ChatAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int postion, ChatModel item, View v) {

            }
        }, new ChatAdapter.OnLongClickListener() {
            @Override
            public void onLongclick(ChatModel item, View view) {

                if (senderid.equals(item.getSender_id()))
                    deleteMessageDialog(item);

            }
        });

        chatrecyclerview.setAdapter(mAdapter);

        chatrecyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean userScrolled;
            int scrollOutitems;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    userScrolled = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                scrollOutitems = layout.findFirstCompletelyVisibleItemPosition();

                if (userScrolled && (scrollOutitems == 0 && mChats.size() > 9)) {
                    userScrolled = false;

                    rootref.child("chat").child(chatchild).orderByChild("chat_id")
                            .endAt(mChats.get(0).getChat_id()).limitToLast(20)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    ArrayList<ChatModel> arrayList = new ArrayList<>();
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        ChatModel item = snapshot.getValue(ChatModel.class);
                                        arrayList.add(item);
                                    }
                                    for (int i = arrayList.size() - 2; i >= 0; i--) {
                                        mChats.add(0, arrayList.get(i));
                                    }

                                    mAdapter.notifyDataSetChanged();

                                    if (arrayList.size() > 8) {
                                        chatrecyclerview.scrollToPosition(arrayList.size());
                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                }
            }
        });
        sendbtn = findViewById(R.id.sendbtn);
        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(message.getText().toString())) {
                    sendMessage(message.getText().toString());
                    message.setText(null);
                }
            }
        });


        findViewById(R.id.Goback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Functions.hideSoftKeyboard(ChatA.this);
                finish();
            }
        });

        message.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    sendTypingIndicator(false);
                }
            }
        });

        message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    sendTypingIndicator(true);
                    sendbtn.setVisibility(View.VISIBLE);
                    sendbtn.setEnabled(true);
                    sendbtn.setBackground(getResources().getDrawable(R.drawable.ic_meesage_send_app_color));
                } else {
                    sendbtn.setEnabled(false);
                    sendbtn.setBackground(getResources().getDrawable(R.drawable.ic_meesage_send_grey));
                    sendTypingIndicator(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        receivetypeIndication();

    }


    @Override
    public void onStart() {
        super.onStart();

        mChats.clear();
        mchatRefReteriving = FirebaseDatabase.getInstance().getReference();

        queryGetchat = mchatRefReteriving.child("chat").child(chatchild);

        // this will get all the messages between two users
        eventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                try {
                    ChatModel model = dataSnapshot.getValue(ChatModel.class);
                    mChats.add(model);
                    mAdapter.notifyDataSetChanged();
                    chatrecyclerview.scrollToPosition(mChats.size() - 1);
                } catch (Exception ex) {
                    Log.e("", ex.getMessage());
                }
                changeStatus();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                if (dataSnapshot != null && dataSnapshot.getValue() != null) {

                    try {
                        ChatModel model = dataSnapshot.getValue(ChatModel.class);

                        for (int i = mChats.size() - 1; i >= 0; i--) {
                            if (mChats.get(i).getTimestamp().equals(dataSnapshot.child("timestamp").getValue())) {
                                mChats.remove(i);
                                mChats.add(i, model);
                                break;
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                        chatrecyclerview.scrollToPosition(mChats.size() - 1);
                    } catch (Exception ex) {
                        Log.e("", ex.getMessage());
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("", databaseError.getMessage());
            }
        };
        // this will check the two user are do chat before or not
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(chatchild)) {
                    pBar.setVisibility(View.GONE);
                    queryGetchat.removeEventListener(valueEventListener);
                } else {
                    pBar.setVisibility(View.GONE);
                    queryGetchat.removeEventListener(valueEventListener);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        queryGetchat.limitToLast(20).addChildEventListener(eventListener);
        mchatRefReteriving.child("chat").addValueEventListener(valueEventListener);
    }

    // this method will change the status to ensure that
    // user is seen all the message or not (in both chat node and Chatinbox node)
    public void changeStatus() {
        final Date c = Calendar.getInstance().getTime();
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        final Query query1 = reference.child("chat").child(chatchild).orderByChild("status").equalTo("0");
        final Query query2 = reference.child("chat").child(chatchild).orderByChild("status").equalTo("0");


        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot nodeDataSnapshot : dataSnapshot.getChildren()) {
                    if (!nodeDataSnapshot.child("sender_id").getValue().equals(senderid)) {
                        String key = nodeDataSnapshot.getKey(); // this key is `K1NRz9l5PU_0CFDtgXz`
                        String path = "chat" + "/" + dataSnapshot.getKey() + "/" + key;
                        HashMap<String, Object> result = new HashMap<>();
                        result.put("status", "1");
                        result.put("time", Variables.df2.format(c));
                        reference.child(path).updateChildren(result);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        query2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot nodeDataSnapshot : dataSnapshot.getChildren()) {
                    if (!nodeDataSnapshot.child("sender_id").getValue().equals(senderid)) {
                        String key = nodeDataSnapshot.getKey(); // this key is `K1NRz9l5PU_0CFDtgXz`
                        String path = "chat" + "/" + dataSnapshot.getKey() + "/" + key;
                        HashMap<String, Object> result = new HashMap<>();
                        result.put("status", "1");
                        result.put("time", Variables.df2.format(c));
                        reference.child(path).updateChildren(result);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    // this will add the new message in chat node and update the ChatInbox by new message by present date
    public void sendMessage(final String message) {
        Date c = Calendar.getInstance().getTime();
        final String formattedDate = Variables.df.format(c);

        final String currentUserRef = "chat" + "/" + chatchild;
        final String chatUserRef = "chat" + "/" + chatchild;

        DatabaseReference reference = rootref.child("chat").child(chatchild).push();
        final String pushid = reference.getKey();
        final HashMap messageUserMap = new HashMap<>();
        messageUserMap.put("receiver_id", receiverid);
        messageUserMap.put("sender_id", senderid);
        messageUserMap.put("sender_image", preferences.getKeyUserImage());
        messageUserMap.put("chat_id", pushid);
        messageUserMap.put("text", message);
        messageUserMap.put("type", "text");
        messageUserMap.put("pic_url", "");
        messageUserMap.put("status", "0");
        messageUserMap.put("time", "");
        messageUserMap.put("sender_name", senderName);
        messageUserMap.put("timestamp", formattedDate);

        final HashMap userMap = new HashMap<>();
        userMap.put(currentUserRef + "/" + pushid, messageUserMap);
        userMap.put(chatUserRef + "/" + pushid, messageUserMap);

        rootref.updateChildren(userMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                //if first message then set the visibility of whoops layout gone

                HashMap sendermap = new HashMap<>();
                sendermap.put("id", senderid);
                sendermap.put("name", senderName);
                sendermap.put("msg", message);
                sendermap.put("sender_image", preferences.getKeyUserImage());
                sendermap.put("pic", riderPic);
                sendermap.put("status", "0");
                sendermap.put("type", "store");
                sendermap.put("timestamp", -1 * System.currentTimeMillis());
                sendermap.put("date", formattedDate);


                HashMap receivermap = new HashMap<>();
                receivermap.put("id", receiverid);
                receivermap.put("name", receiverName);
                receivermap.put("msg", message);
                receivermap.put("sender_image", receiverPic);
                receivermap.put("pic", receiverPic);
                receivermap.put("status", "1");
                receivermap.put("type", "store");
                receivermap.put("timestamp", -1 * System.currentTimeMillis());
                receivermap.put("date", formattedDate);

                sendNotificationFication(message, "message");
            }
        });
    }

    private void sendNotificationFication(String message, String type) {
        JSONObject sendobj = new JSONObject();

        try {

            sendobj.put("sender_id", "" + senderid);
            sendobj.put("receiver_id", "" + receiverid);
            sendobj.put("title", "" + preferences.getKeyUserName());
            sendobj.put("full_name", "" + preferences.getKeyUserName());
            sendobj.put("type", type);
            sendobj.put("message", "" + message);
            if (!(orderId.equals("0")))
                sendobj.put("request_id", "" + orderId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ApiRequest.callApi(this, ApisList.sendMessageNotification, sendobj, new CallbackResponce() {
            @Override
            public void responce(String resp) {
                //get response here
            }
        });
    }

    // this is the delete message diloge which will show after long press in chat message
    private void deleteMessageDialog(final ChatModel chat_getSet) {
        final CharSequence[] options;
        if (chat_getSet.getType().equals("text")) {
            options = new CharSequence[]{ChatA.this.getString(R.string.copy), ChatA.this.getString(R.string.delete_this_message), ChatA.this.getString(R.string.cancel)};
        } else {

            options = new CharSequence[]{ChatA.this.getString(R.string.delete_this_message), ChatA.this.getString(R.string.cancel)};
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogStyle);
        builder.setTitle(null);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals(ChatA.this.getString(R.string.delete_this_message))) {
                    updateMessage(chat_getSet);
                } else if (options[item].equals(ChatA.this.getString(R.string.cancel))) {
                    dialog.dismiss();
                } else if (options[item].equals(ChatA.this.getString(R.string.copy))) {

                    ClipboardManager clipboard = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("text", chat_getSet.getText());
                    clipboard.setPrimaryClip(clip);
                }
            }
        });
        builder.show();
    }

    // we will update the privious message means we will tells the other user that we have seen your message
    public void updateMessage(ChatModel item) {
        final String currentUserRef = "chat" + "/" + chatchild;
        final String chatUserRef = "chat" + "/" + chatchild;


        final HashMap messageUserMap = new HashMap<>();
        messageUserMap.put("receiver_id", item.getReceiver_id());
        messageUserMap.put("sender_id", item.getSender_id());
        messageUserMap.put("chat_id", item.getChat_id());
        messageUserMap.put("text", "Delete this message");
        messageUserMap.put("type", "delete");
        messageUserMap.put("pic_url", "");
        messageUserMap.put("status", "0");
        messageUserMap.put("time", "");
        messageUserMap.put("sender_name", senderName);
        messageUserMap.put("timestamp", item.getTimestamp());

        final HashMap userMap = new HashMap<>();
        userMap.put(currentUserRef + "/" + item.getChat_id(), messageUserMap);
        userMap.put(chatUserRef + "/" + item.getChat_id(), messageUserMap);

        rootref.updateChildren(userMap);

    }

    // send the type indicator if the user is typing message
    public void sendTypingIndicator(boolean indicate) {
        // if the type incator is present then we remove it if not then we create the typing indicator
        if (indicate) {
            final HashMap messageUserMap = new HashMap<>();
            messageUserMap.put("receiver_id", receiverid);
            messageUserMap.put("sender_id", senderid);

            sendTypingIndication = FirebaseDatabase.getInstance().getReference().child("typing_indicator");
            sendTypingIndication.child(chatchild).setValue(messageUserMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    sendTypingIndication.child(chatchild).setValue(messageUserMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        }
                    });
                }
            });
        } else {
            sendTypingIndication = FirebaseDatabase.getInstance().getReference().child("typing_indicator");

            sendTypingIndication.child(chatchild).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    sendTypingIndication.child(chatchild).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                        }
                    });

                }
            });
        }
    }

    public void receivetypeIndication() {
        mainlayout = findViewById(R.id.typeindicator);

        receiveTypingIndication = FirebaseDatabase.getInstance().getReference().child("typing_indicator");
        receiveTypingIndication.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(chatchild).exists()) {
                    String receiver = String.valueOf(dataSnapshot.child(chatchild).child("sender_id").getValue());
                    if (receiver.equals(receiverid)) {
                        mainlayout.setVisibility(View.VISIBLE);
                    }
                } else {
                    mainlayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    // on destory delete the typing indicator
    @Override
    public void onDestroy() {
        Variables.isNotificationShow = false;
        orderId="0";
        sendTypingIndicator(false);
        queryGetchat.removeEventListener(eventListener);
        super.onDestroy();
    }

    // on stop delete the typing indicator and remove the value event listener
    @Override
    public void onStop() {
        super.onStop();
        sendTypingIndicator(false);
        queryGetchat.removeEventListener(eventListener);
    }

}
