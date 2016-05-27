package securemessaging.securemessaging;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.AlertDialog;

public class HomeActivity extends AppCompatActivity {

    // Random varialbes needed
    private String buttonText;
    JSONArray dbmessages = null;
    private String key = "";
    Encryptor encryptor = new Encryptor();
    Map<Integer, Button> messageButtons = new HashMap<Integer, Button>();;
    Map<Integer, MessageTimer> messageTimers = new HashMap<Integer, MessageTimer>();
    ScheduledExecutorService scheduleTaskExecutor = Executors.newScheduledThreadPool(5);
    Account account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Set account
        account = (Account)getIntent().getSerializableExtra("account");

        //hide create account button if they are not an admin
        ImageButton createAccountButton = (ImageButton) findViewById(R.id.createAccountImageButton);
        if(!getIntent().getBooleanExtra("createAccountVisible",true)) {
            createAccountButton.setVisibility(LinearLayout.GONE);
        }

        // Sechudle task to listen from messages every 5 sec
        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                // The UI can only be updated in the original thread
                // so this is needed
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.adminHomeLinearLayout);
                        account.setMessagesFromDatabase();
                        for(Message m : account.getMessages()) {
                            if(messageButtons.isEmpty() || !messageButtons.containsKey(m.getId())) {
                                Log.d("key", Integer.toString(m.getId()));
                                showMessageAndTimerView(m);
                            }
                        }
                    }
                });
            }
        }, 0, 10, TimeUnit.SECONDS);

        // Show the CreateMessageActivity when the '+' button is pressed
        Button createMessageButton = (Button) findViewById(R.id.createMessage);
        createMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, CreateMessageActivity.class));
            }
        });

        // Create account when create account button is pressed
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, String> user = account.createAccount();
                TextView showText = new TextView(getOuter());
                showText.setText("username: " + user.get("username") + "\npassword: " + user.get("password"));
                showText.setTextIsSelectable(true);
                showPopup("Account Created", "username: " + user.get("username") + "\npassword: " + user.get("password"));
            }
        });
    }

    /**
     * @return itself
     */
    public HomeActivity getOuter() {
        return HomeActivity.this;
    }

    /**
     * Adds/Deletes the message buttons and timers
     * @param dbmessage
     */
    public void showMessageAndTimerView(final Message dbmessage) {

        // Find the LinearLayout
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.adminHomeLinearLayout);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        // Add spacing between message buttons
        layoutParams.setMargins(0, 30, 0, 0);

        // Message Buttons
        final Button button = new Button(getApplicationContext());
        button.setText("View Message");
        button.setWidth(2000);
        button.setBackgroundColor(Color.parseColor("#2196F3"));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMessage(dbmessage);
            }
        });

        // Timer Labels
        final TextView textView = new TextView(getApplicationContext());
        textView.setBackgroundColor(Color.parseColor("#2196F3"));
        textView.setTextColor(Color.parseColor("#ffffff"));
        textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

        // Timer stuff
        MessageTimer messageTimer = new MessageTimer(dbmessage, messageButtons, textView );
        account.addMessageTimer(messageTimer);

        //Add button and Label
        messageButtons.put(dbmessage.getId(),button);
        messageTimers.put(dbmessage.getId(),messageTimer);
        linearLayout.addView(button, layoutParams);
        linearLayout.addView(textView);
    }

    /**
     * Makes a simple popup dialog
     * @param title
     * @param message
     */
    public void showPopup(String title, String message) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getOuter());
        builder1.setTitle(title);
        builder1.setMessage(message);
        builder1.setCancelable(true);
        builder1.setNeutralButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    /**
     * Shows the recieved message
     * @param dbmessage
     */
    public void showMessage(final Message dbmessage) {

        // Create new pop up dialog with input field
        AlertDialog.Builder builder = new AlertDialog.Builder(getOuter());
        builder.setTitle("Enter encryption key");

        // Set up the input
        final EditText input = new EditText(getOuter());
        // Specify the type of input expected
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons for popup dialog
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                key = input.getText().toString();
                DatabaseGateway databaseGateway = new DatabaseGateway();
                long deletionDate = databaseGateway.getServerTime().getTime() + (dbmessage.getTimeout() * 1000);
                String decryptedMessage = encryptor.decrypt(key, dbmessage.getMessage());
                // Check if decrypted successfully
                if (decryptedMessage == null) {
                    int count = dbmessage.getFailedDecryptionCount() + 1;
                    dbmessage.setFailedDecryptionCount(count);
                    String m = "Key is incorrect. You can try " + (3 - count) + " more times";
                    showPopup("View Message", m);
                    if (count >= 3) {
                        deletionDate = new Date().getTime();
                        updateMessageTimer(dbmessage, deletionDate);
                    }
                } else {
                    showPopup("View Message", decryptedMessage);
                    updateMessageTimer(dbmessage, deletionDate);
                }

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void updateMessageTimer(final Message dbmessage, long deletionDate) {
        DatabaseGateway databaseGateway = new DatabaseGateway();
        // Update message timer
        final java.text.SimpleDateFormat sdf =
                new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String status = databaseGateway.updateTimer(dbmessage.getId(), sdf.format(deletionDate));
        dbmessage.setTimeoutDateTime(new Date(deletionDate));
        if (status.equals("success")) {
            TextView countDownTextView = messageTimers.get(dbmessage.getId()).getTextView();
            messageTimers.get(dbmessage.getId()).Cancel();
            MessageTimer messageTimer = new MessageTimer(dbmessage, messageButtons, countDownTextView);
            messageTimers.put(dbmessage.getId(), messageTimer);
            Toast.makeText(getApplicationContext(), "Timer started", Toast.LENGTH_SHORT).show();
        } else if (status.equals("error")) {
            Toast.makeText(getApplicationContext(), "Failed to start timer", Toast.LENGTH_SHORT).show();
        }
    }
}
