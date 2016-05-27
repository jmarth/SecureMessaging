package securemessaging.securemessaging;

import java.util.Date;

/**
 * Created by marth on 4/24/2016.
 */
public class Message {
    private String recipient;
    private String message;

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    private int id;
    private int timeout;
    private Date timeoutDateTime;
    private int failedDecryptionCount;

    /**
     * Defualt constructor
     */
    public Message() {}

    /**
     * Constructor
     * @param message
     * @param id
     * @param timeout
     * @param timeoutDateTime
     */
    public Message(String message, int id, int timeout, Date timeoutDateTime) {
        this.message = message;
        this.id = id;
        this.timeout = timeout;
        this.timeoutDateTime = timeoutDateTime;
        failedDecryptionCount = 0;
    }

    public Message(String recipient, String message, int timeout, Date timeoutDateTime) {
        this.recipient = recipient;
        this.message = message;
        this.id = id;
        this.timeout = timeout;
        this.timeoutDateTime = timeoutDateTime;
        failedDecryptionCount = 0;
    }

    /**
     * Getter for message
     * @return String message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Setter for message
     * @param message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Getter for message
     * @return int id
     */
    public int getId() {
        return id;
    }

    /**
     * Setter for id
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Getter for timeout
     * @return int timeout
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * Setter for timeout
     * @param timeout
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * Getter for timeoutDateTime
     * @return Date timeoutDateTime
     */
    public Date getTimeoutDateTime() {
        return timeoutDateTime;
    }

    /**
     * Setter for timeoutDateTime
     * @param timeoutDateTime
     */
    public void setTimeoutDateTime(Date timeoutDateTime) {
        this.timeoutDateTime = timeoutDateTime;
    }

    /**
     * Setter for failedDecryptionCount
     * @param num
     */
    public void setFailedDecryptionCount(int num) {
        failedDecryptionCount = num;
    }

    /**
     * Getter for failedDecryptionCount
     * @return
     */
    public int getFailedDecryptionCount(){
        return failedDecryptionCount;
    }
}
