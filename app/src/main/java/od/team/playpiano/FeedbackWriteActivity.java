package od.team.playpiano;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class FeedbackWriteActivity extends AppCompatActivity {

    TextView student_textView;              //학생이름 표시해주는 텍스트 뷰. 아직은 하드 코딩이라 1명밖에 없음.
    EditText feedback_content_editText;     //선생님이 적는 피드백 내용.
    Button complete_btn;                    //피드백 작성 완료 버튼

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_write);

        student_textView = findViewById(R.id.student_textView);
        feedback_content_editText = findViewById(R.id.feedback_content_editTextView);
        complete_btn = findViewById(R.id.feedback_complete_btn);

        //피드백 작성 완료하면 액티비티 종료
        complete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
