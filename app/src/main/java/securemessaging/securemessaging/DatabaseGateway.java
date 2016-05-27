package securemessaging.securemessaging;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by marth on 4/25/2016.
 */

public class DatabaseGateway {
    // URLs for connect to the php files
    private static final String path = "http://YourIpAddress/PHP-FILES/";
    private static final String getTimeURL = path+"get_time.php";
    private static final String createAccountURL = path+"create_user.php";
    private static final String getMessagesURL = path+"get_message.php";
    private static final String userControlURL = path+"user_control.php";
    private static final String setTimerURL = path+"timer.php";
    private static final String sendMessageURL = path+"send_message.php";

    List<Message> messages = new ArrayList<Message>();
    private Date serverTime = null;
    private String loginStatus = "";
    private String timerStatus = "";
    private String messageStatus = "";
    private String newAccountStatus = "";

    /**
     * Gets the server time
     * @return Date serverTimeDate
     */
    public Date getServerTime() {
        serverTime = null;
        ServerTime task = new ServerTime();
        task.execute(new String[] {getTimeURL});
        // Wait until serverTime is set
        while(serverTime == null) {}
        return serverTime;
    }

    /**
     * Gets messages from database
     * @param username
     * @return
     */
    public List<Message> getMessages(String username) {
        messages.clear();
        messages=null;
        DBMesseages task = new DBMesseages();
        task.execute(new String[] {getMessagesURL, username});
        // Wait until messages is set
        while(messages == null) {}
        return messages;
    }

    /**
     * Get Login status
     * @param username
     * @param password
     * @return
     */
    public String getLoginStatus(String username, String password) {
        loginStatus=null;
        LoginRequest task = new LoginRequest();
        task.execute(new String[] {userControlURL, username, password});
        // Wait until loginStatus is set
        while(loginStatus==null){}
        return loginStatus;
    }

    /**
     * Update timer
     * @param id
     * @param datetime
     * @return
     */
    public String updateTimer(int id, String datetime) {
        timerStatus=null;
        TimeoutDateTime task = new TimeoutDateTime();
        task.execute(new String[] {setTimerURL, Integer.toString(id), datetime});
        // Wait until timerStatus is set
        while(timerStatus==null){}
        return timerStatus;
    }

    /**
     * Send message to database
     * @param message
     * @return
     */
    public String sendMessage(Message message) {
        messageStatus=null;
        SendMessage task = new SendMessage();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        task.execute(new String[] {sendMessageURL,
            message.getRecipient(),
            message.getMessage(),
            Integer.toString(message.getTimeout()),
            sdf.format(message.getTimeoutDateTime())});
        // Wait until messageStatus is set
        while(messageStatus==null){}
        return messageStatus;
    }



    /**
     * Used to write new account info to database
     * @param username
     * @param password
     * @return
     */
    public String createAccount(String username, String password) {
        newAccountStatus=null;
        CreateAccount task = new CreateAccount();
        task.execute(new String[]{createAccountURL, username, password});
        // Wait until newAccountStatus is set
        while(newAccountStatus==null){}
        return newAccountStatus;
    }

    /**
     * Used to set the server time
     */
    public class ServerTime extends AsyncTask<String, Void, String> {
        private String jsonResult;

        @Override
        protected String doInBackground(String... params) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(params[0]);
            try {
                HttpResponse response = httpclient.execute(httppost);
                jsonResult = inputStreamToString(
                        response.getEntity().getContent()).toString();

            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        private StringBuilder inputStreamToString(InputStream is) {
            String rLine = "";
            StringBuilder answer = new StringBuilder();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            try {
                while ((rLine = rd.readLine()) != null) {
                    answer.append(rLine);
                }
                JSONObject jsonObject = new JSONObject(answer.toString());
                JSONArray time = jsonObject.getJSONArray("result");
                JSONObject o = time.getJSONObject(0);
                DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try{
                    serverTime = formatter.parse(o.getString("time"));
                }catch(Exception e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {

            }
            return answer;
        }

        @Override
        protected void onPostExecute(String result) {

        }
    }

    /**
     * Used to set the database  messages
     */
    public class DBMesseages extends AsyncTask<String, Void, String> {
        private String jsonResult;
        List<Message> m = new ArrayList<Message>();

        @Override
        protected String doInBackground(String... params) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(params[0]);
            ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
            postParameters.add(new BasicNameValuePair("username", params[1]));

            try {
                httppost.setEntity(new UrlEncodedFormEntity(postParameters));
                HttpResponse response = httpclient.execute(httppost);
                jsonResult = inputStreamToString(
                        response.getEntity().getContent()).toString();

            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        private StringBuilder inputStreamToString(InputStream is) {
            String rLine = "";
            StringBuilder answer = new StringBuilder();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            try {
                while ((rLine = rd.readLine()) != null) {
                    answer.append(rLine);
                }
                JSONObject jsonObject = new JSONObject(answer.toString());
                JSONArray dbMessages = jsonObject.getJSONArray("result");
                DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                for(int i=0;i<dbMessages.length();i++) {
                    JSONObject o = dbMessages.getJSONObject(i);
                    // Set timeoutDateTime
                    Date timeoutDateTime = null;
                    try{
                        timeoutDateTime = formatter.parse(o.getString("timeoutDateTime"));
                    }catch(Exception e) {
                        e.printStackTrace();
                    }

                    m.add(new Message(o.getString("message"),
                            o.getInt("id"),
                            o.getInt("timeout"),
                            timeoutDateTime));
                }

            } catch (Exception e) {

            }
            messages = m;
            return answer;
        }

        @Override
        protected void onPostExecute(String result) {

        }
    }

    /**
     * Used to get login status
     */
    public class LoginRequest extends AsyncTask<String, Void, String> {
        private String jsonResult;

        @Override
        protected String doInBackground(String... params) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(params[0]);
            ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
            postParameters.add(new BasicNameValuePair("username", params[1]));
            postParameters.add(new BasicNameValuePair("password", params[2]));

            try {
                httppost.setEntity(new UrlEncodedFormEntity(postParameters));
                HttpResponse response = httpclient.execute(httppost);
                jsonResult = inputStreamToString(
                        response.getEntity().getContent()).toString();

            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        private StringBuilder inputStreamToString(InputStream is) {
            String rLine = "";
            StringBuilder answer = new StringBuilder();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            try {
                while ((rLine = rd.readLine()) != null) {
                    answer.append(rLine);
                }
                JSONObject jsonObject = new JSONObject(answer.toString());
                JSONArray time = jsonObject.getJSONArray("result");
                JSONObject o = time.getJSONObject(0);
                loginStatus = o.getString("status");
            } catch (Exception e) {

            }
            return answer;
        }

        @Override
        protected void onPostExecute(String result) {

        }
    }

    /**
     * Used to set the timeoutDateTime
     */
    public class TimeoutDateTime extends AsyncTask<String, Void, String> {
        private String jsonResult;

        @Override
        protected String doInBackground(String... params) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(params[0]);
            ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
            postParameters.add(new BasicNameValuePair("id", params[1]));
            postParameters.add(new BasicNameValuePair("dateTime", params[2]));

            try {
                httppost.setEntity(new UrlEncodedFormEntity(postParameters));
                HttpResponse response = httpclient.execute(httppost);
                jsonResult = inputStreamToString(
                        response.getEntity().getContent()).toString();

            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        private StringBuilder inputStreamToString(InputStream is) {
            String rLine = "";
            StringBuilder answer = new StringBuilder();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            try {
                while ((rLine = rd.readLine()) != null) {
                    answer.append(rLine);
                }
                JSONObject jsonObject = new JSONObject(answer.toString());
                JSONArray time = jsonObject.getJSONArray("result");
                JSONObject o = time.getJSONObject(0);
                timerStatus = o.getString("status");
            } catch (Exception e) {

            }
            return answer;
        }

        @Override
        protected void onPostExecute(String result) {

        }
    }

    /**
     * Used to send message to database
     */
    public class SendMessage extends AsyncTask<String, Void, String> {
        private String jsonResult;

        @Override
        protected String doInBackground(String... params) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(params[0]);
            ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
            Log.d("message sent", params[2].toString());
            postParameters.add(new BasicNameValuePair("recipient", params[1]));
            postParameters.add(new BasicNameValuePair("message", params[2]));
            postParameters.add(new BasicNameValuePair("timeout", params[3]));
            postParameters.add(new BasicNameValuePair("timeoutDateTime", params[4]));

            try {
                httppost.setEntity(new UrlEncodedFormEntity(postParameters, "UTF-8"));
                HttpResponse response = httpclient.execute(httppost);
                jsonResult = inputStreamToString(
                        response.getEntity().getContent()).toString();

            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        private StringBuilder inputStreamToString(InputStream is) {
            String rLine = "";
            StringBuilder answer = new StringBuilder();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            try {
                while ((rLine = rd.readLine()) != null) {
                    answer.append(rLine);
                }
                JSONObject jsonObject = new JSONObject(answer.toString());
                JSONArray time = jsonObject.getJSONArray("result");
                JSONObject o = time.getJSONObject(0);
                messageStatus = o.getString("status");
            } catch (Exception e) {

            }
            return answer;
        }

        @Override
        protected void onPostExecute(String result) {

        }
    }

    /**
     * Used to write new account info
     */
    public class CreateAccount extends AsyncTask<String, Void, String> {
        private String jsonResult;

        @Override
        protected String doInBackground(String... params) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(params[0]);
            ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
            postParameters.add(new BasicNameValuePair("username", params[1]));
            postParameters.add(new BasicNameValuePair("password", params[2]));

            try {
                httppost.setEntity(new UrlEncodedFormEntity(postParameters));
                HttpResponse response = httpclient.execute(httppost);
                jsonResult = inputStreamToString(
                        response.getEntity().getContent()).toString();

            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        private StringBuilder inputStreamToString(InputStream is) {
            String rLine = "";
            StringBuilder answer = new StringBuilder();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            try {
                while ((rLine = rd.readLine()) != null) {
                    answer.append(rLine);
                }
                JSONObject jsonObject = new JSONObject(answer.toString());
                JSONArray time = jsonObject.getJSONArray("result");
                JSONObject o = time.getJSONObject(0);
                newAccountStatus = o.getString("status");
            } catch (Exception e) {

            }
            return answer;
        }

        @Override
        protected void onPostExecute(String result) {

        }
    }
}


