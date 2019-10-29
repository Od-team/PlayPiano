package od.team.playpiano;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import od.team.playpiano.Adapter.RoomListAdapter;
import od.team.playpiano.RecyclerItemData.RecyclerRoomListData;

public class FeedbackBoxActivity extends AppCompatActivity {

    LinearLayout message_box_linear;
    TextView teacher_name_textView;
    String teacher_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_box);

        init();
        viewClickListener();

        //황준환 선생님 하드코딩
        teacher_name_textView.setText(teacher_name);

    }

    public void init(){
        message_box_linear = findViewById(R.id.message_linear);
        teacher_name_textView = findViewById(R.id.teacher_name_textView);

        //황준환 선생님 하드코딩
        teacher_name = "황준환 선생님";


    }
    public void viewClickListener(){
        message_box_linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FeedbackBoxActivity.this,FeedbackRoomActivity.class);
                intent.putExtra("teacher_name",teacher_name);
                startActivity(intent);
            }
        });

    }
}