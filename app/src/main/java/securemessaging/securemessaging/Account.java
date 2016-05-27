package securemessaging.securemessaging;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by marth on 4/24/2016.
 */
public class Account implements Serializable {
    private String username;
    JSONArray dbMessages = null;
    private StringRequest request;
    private RequestQueue requestQueue;
    private String response;
    List<Message> messages = new ArrayList<Message>();
    List<MessageTimer> messageTimers = new ArrayList<MessageTimer>();

    // URLs to php files
    private static final String URL = "http://72.179.177.5:7777/PHP-FILES/create_user.php";

    // These are the chars that the password can contain
    private static final String symbols =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789$&@?<>~!%#";

    /**
     * Default constructor
     */
    public Account() {

    }

    /**
     * Username getter
     * @return String username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Username setter
     * @param u
     */
    public void setUsername(String u) {
        username = u;
    }


    /**
     * Makes a new account and writes it to the database
     * @return map of generated user username and password
     */
    public Map<String, String> createAccount() {
        DatabaseGateway databaseGateway = new DatabaseGateway();
        Map<String, String> user = new HashMap<String, String>();
        String status = "";
        String genUsername;
        String genPassword;
        // Loop until account has been successfully be created
        // Wont return 'success' until it finds an available username
        do {
            genUsername = genUsername();
            genPassword = genPassword();
            status = databaseGateway.createAccount(genUsername, genPassword);
        }while(!status.equals("success"));

        user.put("username",genUsername);
        user.put("password",genPassword);

        return user;
    }

    /**
     * generates a random password
     * at least 16 chars, one uppercase, number, and special char
     * @return String passowrd
     */
    public static String genPassword() {
        Random r = new Random();
        while(true) {
            char[] password = new char[r.nextBoolean()?16:17];
            boolean hasUpper = false, hasLower = false, hasDigit = false, hasSpecial = false;
            for(int i=0; i<password.length; i++) {
                char ch = symbols.charAt(r.nextInt(symbols.length()));
                if(Character.isUpperCase(ch))
                    hasUpper = true;
                else if(Character.isLowerCase(ch))
                    hasLower = true;
                else if(Character.isDigit(ch))
                    hasDigit = true;
                else
                    hasSpecial = true;
                password[i] = ch;
            }
            Log.d("lenght", password.length+"");
            if(hasUpper && hasLower && hasDigit && hasSpecial) {
                Log.d("password", password+"");
                return new String(password);
            }
        }
    }

    /**
     * gen username
     * 10-15 in length and numeric
     * @return String username
     */
    public static String genUsername() {
        String username = "";
        Random r = new Random();
        int passLength =  r.nextInt((15 - 10) + 1) + 10;

        for (int idx = 1; idx <= passLength; ++idx){
            int  n = r.nextInt(9) + 1;
            Log.d("Generated : ", n+"");
            username+=n;
        }

        Log.d("username", username);

        return username;
    }

    /**
     * Gets messages
     * @return
     */
    public List<Message> getMessages() {
        return messages;
    }

    /**
     * Set messages for user from database
     */
    public void setMessagesFromDatabase() {
        DatabaseGateway databaseGateway = new DatabaseGateway();
        messages = databaseGateway.getMessages(username);
    }

    /**
     * Adds MessageTimer to MessageTimers
     * @param messageTimer
     */
    public void addMessageTimer(MessageTimer messageTimer) {
        messageTimers.add(messageTimer);
    }

    /**
     * Cancels all MessageTimers and clears
     * the MessageTimers list
     */
    public void clearMessageTimers() {
        for(MessageTimer mt : messageTimers) {
            mt.Cancel();
        }
        messageTimers.clear();
    }

    public void addMessage(Message message) {
        messages.add(message);
    }

    public void clearMessage() {
        messages.clear();
    }
}
