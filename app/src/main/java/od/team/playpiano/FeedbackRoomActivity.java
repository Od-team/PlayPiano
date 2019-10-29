package od.team.playpiano;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class FeedbackRoomActivity extends AppCompatActivity {

    TextView room_teacher_name_textView;        //액티비티 가장 상단에 선생님 이름을 보여주는 텍스트뷰
    String teacher_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_room);

        Intent intent  = getIntent();
        teacher_name = intent.getStringExtra("teacher_name");

        init();
        viewClickListener();

        room_teacher_name_textView.setText(teacher_name);
    }

    public void init(){
        room_teacher_name_textView = findViewById(R.id.room_teacher_name_textView);


    }
    public void viewClickListener(){


    }
}