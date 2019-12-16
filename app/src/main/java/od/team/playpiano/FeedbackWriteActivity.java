package od.team.playpiano;

import android.content.ContentValues;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

public class FeedbackWriteActivity extends AppCompatActivity {

    TextView student_textView;              //학생이름 표시해주는 텍스트 뷰. 아직은 하드 코딩이라 1명밖에 없음.
    EditText feedback_content_editText;     //선생님이 적는 피드백 내용.
    Button complete_btn;                    //피드백 작성 완료 버튼

    String student_name;
    String master_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_write);

        student_name = getIntent().getStringExtra("student_name");
        Log.d("sdfsfd",student_name+"!");
        master_name = getIntent().getStringExtra("master_name");

        student_textView = findViewById(R.id.student_textView);
        feedback_content_editText = findViewById(R.id.feedback_content_editTextView);
        complete_btn = findViewById(R.id.feedback_complete_btn);


        student_textView.setText(student_name+" 학생");

        //피드백 작성 완료하면 액티비티 종료
        complete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ( feedback_content_editText.getText().toString().equals("")){
                    Toast.makeText(FeedbackWriteActivity.this,"피드백을 작성해주세요",Toast.LENGTH_SHORT).show();
                }
                else{
                    try {
                        String url = "http://54.180.26.72/feedback_save.php";
                        ContentValues values = new ContentValues();
                        values.put("teacher", master_name);
                        values.put("student", student_name);
                        values.put("content", feedback_content_editText.getText().toString());

                        Log.d("asdasd",master_name);
                        Log.d("asdasd",student_name);
                        Log.d("asdasd",feedback_content_editText.getText().toString());

                        NetWorkAsync netWorkAsync = new NetWorkAsync(url, values);
                        netWorkAsync.execute();
                    } catch (Exception e) {
                        Log.d("asdasd",e.getMessage());
                    }

                    Toast.makeText(FeedbackWriteActivity.this, "작성을 완료하였습니다", Toast.LENGTH_SHORT).show();
                    finish();
                }


            }
        });
    }
}
