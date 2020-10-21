package childapp.childletter.splachhome.Models;

public class Modeluser {

   private String name, email, phone, image, cover,uid,OnlineState,TypingTo ;


    public Modeluser() {
    }

    public Modeluser(String name, String email, String phone, String image, String cover, String uid, String OnlineState, String typingTo) {
        this.name = name;
        this.email = email;

        this.phone = phone;
        this.image = image;
        this.cover = cover;
        this.uid = uid;
        this.OnlineState = OnlineState;
        this.TypingTo = typingTo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }



    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getOnlineStatus() {
        return OnlineState;
    }

    public void setOnlineStatus(String onlineStatus) {
        this.OnlineState= onlineStatus;
    }

    public String getTypingTo() {
        return TypingTo;
    }

    public void setTypingTo(String typingTo) {
        TypingTo = typingTo;
    }
}
