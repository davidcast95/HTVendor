package com.huang.android.logistic.Chat;

import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.gson.Gson;
import com.huang.android.logistic.API.API;
import com.huang.android.logistic.Model.Communication.CommunicationCreation;
import com.huang.android.logistic.Model.Communication.CommunicationData;
import com.huang.android.logistic.Model.Communication.CommunicationResponse;
import com.huang.android.logistic.Model.MyCookieJar;
import com.huang.android.logistic.R;
import com.huang.android.logistic.Utility;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Chat extends AppCompatActivity {

    SwipeRefreshLayout refreshLayout;
    ListView chatLV;
    EditText chatET;
    ImageButton submitChat;
    String joid = "";
    ChatAdapter chatAdapter;
    List<CommunicationData> chatList = new ArrayList<>();
    public static Chat instance;

    public static void registerInstance(Chat chat) {
        instance = chat;
    }

    public static void unregisterInstance() {
        instance = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setTitle("Administrator");

        Chat.registerInstance(this);

        chatLV = (ListView)findViewById(R.id.chat_list_view);
        chatET = (EditText)findViewById(R.id.chat_edit_text);
        joid = getIntent().getStringExtra("joid");

        refreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItems();
            }
        });

        submitChat = (ImageButton)findViewById(R.id.enter_chat);
        submitChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chatSubmitTapped();
            }
        });


        getComment();

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                Chat.unregisterInstance();
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    void refreshItems() {
        chatList.clear();
        getComment();
    }

    public void refetchItems() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshItems();
            }
        }, 2000);
    }


    void chatSubmitTapped() {
        if (!chatET.getText().toString().equals("")) {
            addComment();
        }
    }

    void addComment() {
        CommunicationData newChat = new CommunicationData();
        newChat.sender_full_name = Utility.utility.getLoggedName(this);
        newChat.sender = Utility.utility.getUsername(this);
        newChat.content = chatET.getText().toString();
        newChat.reference_doctype = "Job Order";
        newChat.reference_name = joid;
        newChat.comment_type = "Comment";
        newChat.communication_type = "Comment";
        insertComment(newChat);
        chatList.add(newChat);

        chatAdapter = new ChatAdapter(getApplicationContext(),R.layout.chat_self_user ,chatList);
        chatLV.setAdapter(chatAdapter);
        chatET.setText("");
    }

    //API
    void insertComment(CommunicationData newChat) {
        MyCookieJar cookieJar = Utility.utility.getCookieFromPreference(this);
        API api = Utility.utility.getAPIWithCookie(cookieJar);
        Call<CommunicationCreation> callInsertComment = api.insertComment(newChat);
        String a = new Gson().toJson(newChat);
        callInsertComment.enqueue(new Callback<CommunicationCreation>() {
            @Override
            public void onResponse(Call<CommunicationCreation> call, Response<CommunicationCreation> response) {
                CommunicationCreation communicationCreation = response.body();
                if (communicationCreation != null) {
                    int lastIndex = chatList.size() - 1;
                    chatList.set(lastIndex,communicationCreation.newCommunicationData);
                    chatAdapter = new ChatAdapter(getApplicationContext(),R.layout.chat_self_user ,chatList);
                    chatLV.setAdapter(chatAdapter);
                }
            }

            @Override
            public void onFailure(Call<CommunicationCreation> call, Throwable t) {
                Utility.utility.showConnectivityUnstable(getApplicationContext());
            }
        });
    }


    void getComment() {
        MyCookieJar cookieJar = Utility.utility.getCookieFromPreference(this);
        API api = Utility.utility.getAPIWithCookie(cookieJar);
        String filters = "[[\"Communication\",\"reference_name\",\"=\",\"" + joid + "\"]]";
        Call<CommunicationResponse> callGetComment = api.getComment(filters);
        callGetComment.enqueue(new Callback<CommunicationResponse>() {
            @Override
            public void onResponse(Call<CommunicationResponse> call, Response<CommunicationResponse> response) {
                refreshLayout.setRefreshing(false);
                CommunicationResponse communicationResponse = response.body();
                if (communicationResponse != null) {
                    List<CommunicationData> newChatList = communicationResponse.communicationData;
                    chatList.addAll(newChatList);
                    chatAdapter = new ChatAdapter(getApplicationContext(),R.layout.chat_other_user ,chatList);
                    chatLV.setAdapter(chatAdapter);
                }
            }

            @Override
            public void onFailure(Call<CommunicationResponse> call, Throwable t) {
                Utility.utility.showConnectivityUnstable(getApplicationContext());
            }
        });
    }
}
