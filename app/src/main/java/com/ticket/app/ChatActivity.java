package com.ticket.app;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class ChatActivity extends AppCompatActivity implements TextWatcher {

    private String name, email;
    private WebSocket webSocket;
    //private String SERVER_PATH = "ws://10.0.2.2:8000";
    private String SERVER_PATH = "ws://192.168.1.9:8000";
    private EditText messageEdit;
    private ImageView sendBtn;
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private FirebaseAuth auth;
    private DatabaseReference myRef2;
    private boolean prova = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setTitle("Chat room");

        sendBtn = findViewById(R.id.sendBtn);
        sendBtn.setImageAlpha(50);

        auth = FirebaseAuth.getInstance();

        email = auth.getCurrentUser().getEmail();
        myRef2= FirebaseDatabase.getInstance().getReference("Users");

        Query profilo=myRef2.orderByChild("email").equalTo(email);
        profilo.addListenerForSingleValueEvent(evento);


        initiateSocketConnection();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!prova) {

                    new AlertDialog.Builder(ChatActivity.this)
                            .setMessage("Socket Connection Failed")
                            .setCancelable(false)
                            .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    finish();
                                    overridePendingTransition(0, 0);
                                    startActivity(getIntent());
                                    overridePendingTransition(0, 0);
                                }
                            })
                            .setNegativeButton("Back",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    startActivity(new Intent(ChatActivity.this, MainActivity.class));
                                }
                            })
                            .show();

                    /*
                    Toast.makeText(ChatActivity.this, "Socket connection Failed!", Toast.LENGTH_SHORT).show();

*/
                }
            }
        }, 250);

    }

    private void initiateSocketConnection() {


        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(SERVER_PATH).build();

        webSocket = client.newWebSocket(request, new SocketListener());


    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

        String string = s.toString().trim();

        if (string.isEmpty()) {
            resetMessageEdit();
        } else {
            sendBtn.setImageAlpha(255);
        }

    }

    private void resetMessageEdit() {

        messageEdit.removeTextChangedListener(this);

        messageEdit.setText("");
        sendBtn.setImageAlpha(50);

        messageEdit.addTextChangedListener(this);

    }

    private class SocketListener extends WebSocketListener {

        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            super.onOpen(webSocket, response);

            runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    prova = true;

                    if (prova) {

                        Toast.makeText(ChatActivity.this,
                                "Socket Connection Successful!",
                                Toast.LENGTH_SHORT).show();

                        initializeView();
                    }
                }
            });

        }

        @Override
        public void onMessage(WebSocket webSocket, final String text) {
            super.onMessage(webSocket, text);

            runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    try {
                        JSONObject jsonObject = new JSONObject(text);
                        jsonObject.put("isSent", false);

                        messageAdapter.addItem(jsonObject);

                        recyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        }
    }

    private void initializeView() {


        messageEdit = findViewById(R.id.messageEdit);




        recyclerView = findViewById(R.id.recyclerView);

        messageAdapter = new MessageAdapter(getLayoutInflater());
        recyclerView.setAdapter(messageAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        messageEdit.addTextChangedListener(this);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!(messageEdit.getText().toString().equals(""))) {

                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("name", name);
                        jsonObject.put("message", messageEdit.getText().toString());

                        webSocket.send(jsonObject.toString());

                        jsonObject.put("isSent", true);
                        messageAdapter.addItem(jsonObject);

                        recyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);

                        resetMessageEdit();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });



    }


    ValueEventListener evento = new ValueEventListener() {
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        for (DataSnapshot snap: dataSnapshot.getChildren())
        {
            User user=snap.getValue(User.class);

                name = user.getName().substring(0, 1).toUpperCase() + user.getName().substring(1) + " " + user.getSurname().substring(0, 1).toUpperCase() + user.getSurname().substring(1);

        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
    }
};

    public void onBackPressed() {
        webSocket.close(1000, "bye");
        startActivity(new Intent(ChatActivity.this,MainActivity.class));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }

}
