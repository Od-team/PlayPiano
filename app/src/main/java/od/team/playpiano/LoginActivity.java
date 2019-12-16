package od.team.playpiano;

import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class LoginActivity extends AppCompatActivity {

    ImageView imageView;

    Button login_btn;
    Button sign_up_button;

    EditText user_id_e_text;
    EditText user_pw_e_text;

    String url = "http://54.180.26.72/login.php";
    String answer;
    public static int soundOn = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        imageView = findViewById(R.id.imageView);
        login_btn = findViewById(R.id.login_btn);
        sign_up_button = findViewById(R.id.sign_up_button);
        user_id_e_text = findViewById(R.id.user_id_e_text);
        user_pw_e_text = findViewById(R.id.user_pw_e_text);

        Glide.with(this).load(R.drawable.logo).into(imageView);

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 로그인 php로 가서 아이디가 있는지 확인 부터함.
                // 정보가 있으면, success반환, 그렇지 않으면 fail 반환

                ContentValues values = new ContentValues();
                values.put("id", user_id_e_text.getText().toString());
                values.put("pw", user_pw_e_text.getText().toString());

                NetWorkAsync netWorkAsync = new NetWorkAsync(url, values);
                try {
                    answer = netWorkAsync.execute().get();

                    Log.d("asds",answer+"1");
                    if (answer.contains("success")) {
                        // 가입완료ㅑ
                        Toast.makeText(LoginActivity.this, "로그인 완료", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, LobbyActivity.class);
                        intent.putExtra("id", user_id_e_text.getText().toString());
                        if(answer.contains("teacher")){
                            intent.putExtra("job", "teacher");
                            Log.d("asds","선생");
                        }
                        else{
                            intent.putExtra("job", "student");
                            Log.d("asds","학생");
                        }
                        startActivity(intent);
                        finish();
                    } else {
                        // 가입실패
                        Toast.makeText(LoginActivity.this, "등록 되어있지 않은 정보입니다", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.d("asds", e.getMessage());
                }


            }
        });

        sign_up_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /** 회원가입하는 화면으로 이동해야함. **/
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });


    }
}
