package childapp.childletter.splachhome.notofications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAARPS8GIM:APA91bG0yVcwYPBhFIB6Ih1wI_jREri1qwEvxhhktdHZW-ZtiIjmsh87LaG5djJyg2bVhfwDZO5ZnY-Mu4y_zglGUGy5Wvj3FdMMV0Esdzw63UVXEQ8w78bCgn5frzRsfOWyeDQL9QQ8"
    })
    @POST("fcm/send")
    Call<Response>SendNotofication(@Body Sender body);
}
