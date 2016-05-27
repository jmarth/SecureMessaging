package securemessaging.securemessaging;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class CreateMessageActivity extends AppCompatActivity {

    // Random variables needed
    private RequestQueue requestQueue;
    private StringRequest request;
    private EditText recipient, message, timeout, key;
    private Encryptor encryptor = new Encryptor();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_message);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        requestQueue = Volley.newRequestQueue(this);
        recipient = (EditText) findViewById(R.id.recipientEditText);
        message = (EditText) findViewById(R.id.messageEditText);
        timeout = (EditText) findViewById(R.id.timoutEditText);
        key = (EditText) findViewById(R.id.keyEditText);

        // Set 'send message' button action when pressed
        Button createAccountButton = (Button) findViewById(R.id.sendMessageButton);
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseGateway databaseGateway = new DatabaseGateway();

                // Set date variable to server time
                Date date = databaseGateway.getServerTime();
                // Add two days to date varible
                Calendar c = Calendar.getInstance();
                c.setTime(date);
                c.add(Calendar.DATE, 2);
                date = c.getTime();
                // Send message to database witht the timeout date being two days in the future
                Log.d("key",key.getText().toString());
                String status = databaseGateway.sendMessage(new Message(recipient.getText().toString(),
                        encryptor.encrypt(key.getText().toString(), message.getText().toString()),
                        Integer.parseInt(timeout.getText().toString()),
                        date));

                if(status.equals("success")){
                    // Show toast and popup dialog confiming message sent
                    Toast.makeText(getApplicationContext(), "Message sent", Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(getOuter());
                    builder1.setTitle("Secure Messaging");
                    builder1.setMessage("Your message has successfully been sent");
                    builder1.setCancelable(true);
                    builder1.setNeutralButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                } else {
                    // Show toast and popup of error when trying to save message to database
                    Toast.makeText(getApplicationContext(), "Error: " + status, Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(getOuter());
                    builder1.setTitle("Secure Messaging");
                    builder1.setMessage(status);
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
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_message, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public CreateMessageActivity  getOuter() {
        return CreateMessageActivity.this;
    }

    public void checkRecipient() {

    }
}
