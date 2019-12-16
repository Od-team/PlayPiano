package od.team.playpiano;

import android.content.ContentValues;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

public class SignUpActivity extends AppCompatActivity {

    EditText id_e_text;
    EditText pw_e_text;
    Button sign_up_btn;
    ImageButton back_btn;
    RadioGroup radioGroup;
    RadioButton radioButton1;
    RadioButton radioButton2;

    String url = "http://54.180.26.72/sign_up.php";
    String answer;

    boolean isStudent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        init();
        radioListener();

        sign_up_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (id_e_text.getText().toString().equals("")) {
                    Toast.makeText(SignUpActivity.this, "ID를 입력해주세요", Toast.LENGTH_SHORT).show();
                } else if (pw_e_text.getText().toString().equals("")) {
                    Toast.makeText(SignUpActivity.this, "PW를 입력해주세요", Toast.LENGTH_SHORT).show();
                } else{
                    // 아이디 중복 확인 하러가야함.


                    ContentValues values = new ContentValues();
                    values.put("id", id_e_text.getText().toString());
                    values.put("pw", pw_e_text.getText().toString());
                    if (isStudent == true){
                        values.put("job", "student");
                    }
                    else{
                        values.put("job", "teacher");
                    }
                    NetWorkAsync netWorkAsync = new NetWorkAsync(url, values);

                    try {
                        answer = netWorkAsync.execute().get();

                        Log.d("asds",answer+"1");

                        if (answer.equals("success")) {
                            // 가입완료ㅑ
                            Toast.makeText(SignUpActivity.this, "가입완료 환영합니다.", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            // 가입실패
                            Toast.makeText(SignUpActivity.this, "이미 가입되어 있는 아이디입니다.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.d("asds", e.getMessage());
                    }
                }
            }
        });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public void init() {
        id_e_text = findViewById(R.id.id_e_text);
        pw_e_text = findViewById(R.id.pw_e_text);
        sign_up_btn = findViewById(R.id.sign_up_btn);
        back_btn = findViewById(R.id.back_btn);
        radioGroup = findViewById(R.id.radioGroup);
        radioButton1 = findViewById(R.id.radioButton1);
        radioButton2 = findViewById(R.id.radioButton2);

    }

    public void radioListener() {
        RadioGroup.OnCheckedChangeListener radioGroupButtonChangeListener = new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if (i == R.id.radioButton1) {
//                    Toast.makeText(SignUpActivity.this, "라디오 그룹 버튼1 눌렸습니다.", Toast.LENGTH_SHORT).show();
                    isStudent = false;
                } else if (i == R.id.radioButton2) {
//                    Toast.makeText(SignUpActivity.this, "라디오 그룹 버튼2 눌렸습니다.", Toast.LENGTH_SHORT).show();
                    isStudent = true;
                }
            }
        };
        radioGroup.setOnCheckedChangeListener(radioGroupButtonChangeListener);
    }
}
