package childapp.childletter.splachhome.Models;

public class ModelChat {
    String message,receiver,sender,timestemp,type;
    boolean isSeen;

    public ModelChat() {

    }

    public ModelChat(String message, String receiver, String sender, String timestemp, String type, boolean isSeen) {
        this.message = message;
        this.receiver = receiver;
        this.sender = sender;
        this.timestemp = timestemp;
        this.type = type;
        this.isSeen = isSeen;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTimestemp() {
        return timestemp;
    }

    public void setTimestemp(String timestemp) {
        this.timestemp = timestemp;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }
}
