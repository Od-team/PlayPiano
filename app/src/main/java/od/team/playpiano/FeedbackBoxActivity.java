package od.team.playpiano;

import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import od.team.playpiano.Adapter.FeedBackAdapater;
import od.team.playpiano.Adapter.RoomListAdapter;
import od.team.playpiano.RecyclerItemData.RecyclerRoomListData;

public class FeedbackBoxActivity extends AppCompatActivity {

    LinearLayout message_box_linear;
    TextView teacher_name_textView;
    String teacher_name;

    String user_id;

    FeedBackAdapater feedBackAdapater;
    ArrayList<String> teacher_name_list;
    RecyclerView feedback_recyclerView;
    LinearLayoutManager linearLayoutManager;

    String[] feedback_list;

    TextView feedback_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_box);

        init();
        viewClickListener();

        user_id = getIntent().getStringExtra("user_id");
        feedback_text = findViewById(R.id.feedback_text);

        if (LobbyActivity.user_job.equals("teacher")) {
            feedback_text.setText("보낸 피드백");
        } else {
            feedback_text.setText("받은 피드백");
        }
        try {
            String url = "http://54.180.26.72/feedback_teacher_get.php";
            ContentValues values = new ContentValues();
            values.put("user_id", user_id);
            values.put("user_job", LobbyActivity.user_job);
            Log.d("sdffd",user_id+" 유저!");

            NetWorkAsync netWorkAsync = new NetWorkAsync(url, values);

            String get_student_name = netWorkAsync.execute().get();
            Log.d("sdffd",get_student_name+"!");
            feedBackInfo(get_student_name);

        } catch (Exception e) {
            Log.d("sdffd","pp : "+e.getMessage());
        }

    }

    public void init() {
        message_box_linear = findViewById(R.id.message_linear);
        teacher_name_textView = findViewById(R.id.teacher_name_textView);
        feedback_recyclerView = findViewById(R.id.feedback_recyclerView);

        //황준환 선생님 하드코딩
        teacher_name_list = new ArrayList<>();
        feedBackAdapater = new FeedBackAdapater(FeedbackBoxActivity.this, teacher_name_list);
        linearLayoutManager = new LinearLayoutManager(FeedbackBoxActivity.this, LinearLayoutManager.VERTICAL, false);
        feedback_recyclerView.setLayoutManager(linearLayoutManager);
        feedback_recyclerView.setAdapter(feedBackAdapater);
    }

    public void viewClickListener() {
        feedBackAdapater.setOnClickListener(new FeedBackAdapater.MyClickListener() {
            @Override
            public void onItemClicked(int position, ArrayList<String> teacher_name_list) {
                Intent intent = new Intent(FeedbackBoxActivity.this, FeedbackRoomActivity.class);
                if(LobbyActivity.user_job.equals("student")){
                    intent.putExtra("student_name", LobbyActivity.user_id);
                    intent.putExtra("teacher_name", teacher_name_list.get(position));
                }
                else{
                    intent.putExtra("teacher_name", LobbyActivity.user_id);
                    intent.putExtra("student_name", teacher_name_list.get(position));
                }

                startActivity(intent);
            }
        });
    }

    public void feedBackInfo(String jsonString) {


        String teacher = null;
        String student = null;
        String content = null;

        feedback_list = new String[8];
        try {
            JSONArray jarray = new JSONObject(jsonString).getJSONArray("feedback_list");
            for (int i = 0; i < jarray.length(); i++) {
                JSONObject jObject = jarray.getJSONObject(i);

                teacher = jObject.optString("teacher");
                student = jObject.optString("student");
                content = jObject.optString("content");

                feedback_list[0] = teacher;
                feedback_list[1] = student;
                feedback_list[2] = content;

                Log.d("sdfsdf", "t : " + teacher);
                Log.d("sdfsdf", "s : " + student);
                Log.d("sdfsdf", "c : " + content);
                if (LobbyActivity.user_job.equals("student"))
                    teacher_name_list.add(feedback_list[0]);
                else
                    teacher_name_list.add(feedback_list[1]);
            }

            ArrayList<String> resultList = new ArrayList<String>();
            for (int i = 0; i < teacher_name_list.size(); i++) {
                if (!resultList.contains(teacher_name_list.get(i))) {
                    resultList.add(teacher_name_list.get(i));
                }
            }

            teacher_name_list.clear();
            teacher_name_list.addAll(resultList);

        } catch (JSONException e) {
            Log.d("sdfsdf", "e : " + e.getMessage());

        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        feedBackAdapater.notifyDataSetChanged();
                    }
                });
            }
        }).start();

    }


}