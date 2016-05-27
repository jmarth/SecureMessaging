package securemessaging.securemessaging;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;

import java.util.Date;
import java.util.Map;

/**
 * Created by marth on 4/24/2016.
 */
public class MessageTimer {
    private CountDownTimer countDownTimer;
    private  Date timeoutDateTime;
    private TextView textView;
    private RequestQueue requestQueue;
    private StringRequest request;
    private Date serverTime = null;
    private Map<Integer, Button> messageButtons;
    private int messageId;


    public MessageTimer(Message message, Map<Integer, Button> messageButtons, TextView textView) {
        this.timeoutDateTime = message.getTimeoutDateTime();
        this.textView = textView;
        this.messageButtons = messageButtons;
        messageId = message.getId();

        startTimer();
    }

    public void Cancel() {
        countDownTimer.cancel();
    }

    public void startTimer() {
        DatabaseGateway datebase = new DatabaseGateway();

        // Caculate time left before message deletion
        long timeLeft = timeoutDateTime.getTime() - datebase.getServerTime().getTime();

        // Counter
        countDownTimer = new CountDownTimer(timeLeft, 1000) {
            public void onTick(long millisUntilFinished) {
                long x = millisUntilFinished / 1000;
                long seconds = x % 60;
                x /= 60;
                long minutes = x % 60;
                x /= 60;
                long hours = x % 24;
                x /= 24;
                long days = x;
                textView.setText(String.format("%02d", days) +":" +
                        String.format("%02d", hours) + ":" +
                        String.format("%02d", minutes) + ":" +
                        String.format("%02d", seconds));
            }
            public void onFinish() {
                textView.setText("Message has expired. Deleting message...");
                if(messageButtons.get(messageId) != null)
                    messageButtons.get(messageId).setVisibility(View.GONE);
                    messageButtons.remove(messageId);
                if(textView != null)
                    textView.setVisibility(View.GONE);

                cancel();
            }
        }.start();
    }

    public TextView getTextView() {
        return textView;
    }

}
