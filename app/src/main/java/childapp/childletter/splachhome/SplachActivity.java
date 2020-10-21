package childapp.childletter.splachhome;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SplachActivity extends AppCompatActivity {
    Button bt_register,bt_login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bt_register=findViewById(R.id.btn_register);
        bt_login=findViewById(R.id.btn_login);
        bt_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start active register
                startActivity(new Intent(SplachActivity.this,RgisterActivity.class));


            }
        });


        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SplachActivity.this,LoginActivity.class));
            }
        });






    }

}
