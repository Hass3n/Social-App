package childapp.childletter.splachhome.Models;

public class ModelChatlist {

    String id;   // we wiil need this to get chatlist ,sender/reciever uid

    public ModelChatlist(String id) {
        this.id = id;
    }

    public ModelChatlist() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
