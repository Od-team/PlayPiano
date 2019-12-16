package od.team.playpiano;

import android.content.ContentValues;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import od.team.playpiano.Adapter.FeedBackAdapater;
import od.team.playpiano.Adapter.FeedBackContentAdapter;
import od.team.playpiano.RecyclerItemData.FeedbackContentData;

public class FeedbackRoomActivity extends AppCompatActivity {

    TextView room_teacher_name_textView;        //액티비티 가장 상단에 선생님 이름을 보여주는 텍스트뷰
    String teacher_name;
    String student_name;

    RecyclerView feedback_content_recyclerView;
    FeedBackContentAdapter feedBackContentAdapter;
    ArrayList<FeedbackContentData> feedbackContentData;
    LinearLayoutManager linearLayoutManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_room);

        Intent intent  = getIntent();
        teacher_name = intent.getStringExtra("teacher_name");
        student_name = intent.getStringExtra("student_name");

        init();
        getContent();
        if(LobbyActivity.user_job.equals("student"))
            room_teacher_name_textView.setText(teacher_name + " 선생님 피드백 내용");
        else
            room_teacher_name_textView.setText(student_name + " 학생 피드백 내용");
    }

    public void init(){
        room_teacher_name_textView = findViewById(R.id.room_teacher_name_textView);
        feedback_content_recyclerView = findViewById(R.id.feedback_content_recyclerView);

        feedbackContentData = new ArrayList<>();
        feedBackContentAdapter = new FeedBackContentAdapter(FeedbackRoomActivity.this,feedbackContentData);
        linearLayoutManager = new LinearLayoutManager(FeedbackRoomActivity.this, LinearLayoutManager.VERTICAL, false);
        feedback_content_recyclerView.setLayoutManager(linearLayoutManager);
        feedback_content_recyclerView.setAdapter(feedBackContentAdapter);

    }

    public void getContent(){
        try{
            String url = "http://54.180.26.72/get_content.php";
            ContentValues values = new ContentValues();
            values.put("teacher", teacher_name);
            values.put("student", student_name);

            NetWorkAsync netWorkAsync = new NetWorkAsync(url, values);

            String get_content_json = netWorkAsync.execute().get();

            feedBackInfo(get_content_json);
        }
        catch (Exception e){

        }

    }

    public void feedBackInfo(String jsonString) {


        String teacher = null;
        String content = null;


        try {
            JSONArray jarray = new JSONObject(jsonString).getJSONArray("feedback_list");
            for (int i = 0; i < jarray.length(); i++) {
                JSONObject jObject = jarray.getJSONObject(i);

                teacher = jObject.optString("teacher");
                content = jObject.optString("content");

                Log.d("sdfsdf","t : " + teacher);
                Log.d("sdfsdf","c : " + content);
                feedbackContentData.add(new FeedbackContentData("피드백 : ",content));
            }

        } catch (JSONException e) {
            Log.d("sdfsdf","e : " + e.getMessage());

        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        feedBackContentAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();

    }
}