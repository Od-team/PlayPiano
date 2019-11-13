package od.team.playpiano;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;
import java.util.ArrayList;

import od.team.playpiano.Adapter.RoomListAdapter;
import od.team.playpiano.RecyclerItemData.RecyclerRoomListData;

public class LobbyActivity extends AppCompatActivity {

    TextView filter_text;

    View filter_layout;

    Button room_create_btn;

    Spinner people_spinner;
    ArrayList people_num_spinner_list = new ArrayList<>();
    ArrayAdapter<String> people_num_Adapter;

//    Spinner play_time_spinner;
//    ArrayList play_time_spinner_list = new ArrayList<>();
//    ArrayAdapter<String> play_time_Adapter;

    Spinner filter_people_spinner;
    ArrayList filter_people_num_spinner_list = new ArrayList<>();
    ArrayAdapter<String> filter_people_num_Adapter;

    Spinner filter_play_time_spinner;
    ArrayList filter_play_time_spinner_list = new ArrayList<>();
    ArrayAdapter<String> filter_play_time_Adapter;

    RecyclerView room_list_recyclerView;
    RoomListAdapter roomListAdapter;
    LinearLayoutManager linearLayoutManager;
    ArrayList<RecyclerRoomListData> roomListData = new ArrayList<>();


    Context context;
    MainService service;
    Intent serviceIntent;

    String TAG = "tag " + this.getClass().getSimpleName();

    Button dialog_room_create_btn;
    Button feedback_box_btn;

    Intent getIntent;
    public static String user_id;

    EditText room_name_e_text;

    String room_name;
    AlertDialog alertDialog;

    public static DataInputStream left_in = null;
    public static DataOutputStream left_out = null;
    public static Socket left_socket = null;

    public static DataInputStream right_in = null;
    public static DataOutputStream right_out = null;
    public static Socket right_socket = null;

    public static int CurrentInstrument = 1;
    public static int PIANO_FLAG = 1;
    public static int DRUM_FLAG = 2;

    char left_ok;
    char right_ok;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);


        context = getApplicationContext();
        service = new MainService(getApplicationContext());
        serviceIntent = new Intent(getApplicationContext(), MainService.class);


        getIntent = getIntent();
        user_id = getIntent.getStringExtra("user_id");
        Log.d("서비스", "로비 onCreate");



        if (user_id != null && !user_id.equals("teacher")) {

//            /** 왼손 **/
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        left_socket = new Socket("220.119.10.159", 10000);
                        left_in = new DataInputStream(left_socket.getInputStream());
                        left_out = new DataOutputStream(left_socket.getOutputStream());

                        String left = String.valueOf(left_in.readByte());
                        left_ok = (char) Integer.parseInt(left);
6
                        Log.d("핸들러", "왼손 받은 값" + left_ok);

                    } catch (Exception e) {
                        Log.d("핸들러", "왼손 ㅇㅔ러" + e.getLocalizedMessage());
                        e.printStackTrace();
                    }
                }
            }).start();

            /** 오른손 **/
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        //right_socket = new Socket("220.119.10.159", 10000);
                        right_in = new DataInputStream(right_socket.getInputStream());
                        right_out = new DataOutputStream(right_socket.getOutputStream());

                        String right = String.valueOf(right_in.readByte());
                        right_ok = (char) Integer.parseInt(right);

                        Log.d("핸들러", "오른손 받은 값" + right_ok);

                    } catch (Exception e) {
                        Log.d("핸들러", "오른손 ㅇㅔ러" + e.getLocalizedMessage());
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        //튜토리얼 액티비티 생성
        setTutorial();


        //지금 서비스가 실행되고 있지 않다면 -> 서비스를 실행한다
        if (!isMyServiceRunning(service.getClass())) {
            startService(serviceIntent);
            Log.d("서비스", "로비- startService");

            /** 가장먼저 사용자의 아이디를 전달해준다.**/
            sendMsg(user_id);
        }


        init();
        BtnClickListener();
        roomAlertDialog();


        roomListAdapter.setOnClickListener(new RoomListAdapter.MyClickListener() {
            @Override
            public void onItemClicked() {
                if (!user_id.equals("teacher")) {
                    Intent intent = new Intent(LobbyActivity.this, GameRoomActivity.class);
                    intent.putExtra("room_name", room_name);
                    startActivity(intent);
                    finish();
                }

            }
        });


    }

    public void init() {
        filter_text = findViewById(R.id.filter_text);
        filter_layout = findViewById(R.id.filter_layout);
        room_create_btn = findViewById(R.id.room_create_btn);
        filter_people_spinner = findViewById(R.id.filter_people_spinner);
        filter_play_time_spinner = findViewById(R.id.filter_play_time_spinner);
        room_list_recyclerView = findViewById(R.id.room_list_recyclerView);
        feedback_box_btn = findViewById(R.id.feedback_box_btn);

        linearLayoutManager = new LinearLayoutManager(LobbyActivity.this, LinearLayoutManager.VERTICAL, true);
        roomListAdapter = new RoomListAdapter(LobbyActivity.this, roomListData);
        room_list_recyclerView.setLayoutManager(linearLayoutManager);
        room_list_recyclerView.setAdapter(roomListAdapter);


        for (int i = 5; i > 0; i--) {
            roomListData.add(0, new RecyclerRoomListData(
                    "No." + i,
                    "5학년 " + i + "반 모여라~~",
                    "/ super_man님의 방",
                    "인원 : 3/3",
                    "/ 동요",
                    ""));
        }


    }

    public void BtnClickListener() {
        filter_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (filter_layout.getVisibility() == View.VISIBLE) {
                    filter_layout.setVisibility(View.GONE);
                    filterTextChange("필터 ▼");
                } else {
                    filter_layout.setVisibility(View.VISIBLE);
                    filterTextChange("필터 ▲");
                    filterSpinnerAdapter();
                }
            }
        });

        feedback_box_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LobbyActivity.this, FeedbackBoxActivity.class);
                startActivity(intent);
            }
        });
    }

    public void filterTextChange(final String text) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        filter_text.setText(text);
                    }
                });
            }
        }).start();
    }

    public void roomAlertDialog() {
        room_create_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LobbyActivity.this);

                View view = LayoutInflater.from(LobbyActivity.this).inflate(R.layout.dialog_room_create, null);

                people_spinner = view.findViewById(R.id.people_spinner);
                dialog_room_create_btn = view.findViewById(R.id.dialog_room_create_btn);

                room_name_e_text = view.findViewById(R.id.room_name_e_text);

                people_num_spinner_list.clear();

                people_num_spinner_list.add("3명");
                people_num_spinner_list.add("2명");
                people_num_spinner_list.add("4명");
                people_num_spinner_list.add("5명");

                people_num_Adapter = new ArrayAdapter<String>(getApplicationContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        people_num_spinner_list);

                people_spinner.setAdapter(people_num_Adapter);

                builder.setView(view);

                alertDialog = builder.create();

                alertDialog.show();

                dialog_room_create_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(LobbyActivity.this, GameRoomActivity.class);
                        intent.putExtra("user_id", user_id);
                        intent.putExtra("room_name", room_name_e_text.getText().toString());
                        startActivity(intent);
                        alertDialog.dismiss();
                        sendMsg("room_create@@" + room_name_e_text.getText().toString());

                    }
                });
            }
        });
    }

    public void filterSpinnerAdapter() {

        filter_people_num_spinner_list.clear();
        filter_play_time_spinner_list.clear();

        for (int i = 2; i <= 5; i += 1)
            filter_people_num_spinner_list.add(i + "명");

        for (int i = 10; i <= 60; i += 10)
            filter_play_time_spinner_list.add(i + "초");

        filter_people_num_Adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item,
                filter_people_num_spinner_list);

        filter_play_time_Adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item,
                filter_play_time_spinner_list);

        filter_people_spinner.setAdapter(filter_people_num_Adapter);
        filter_play_time_spinner.setAdapter(filter_play_time_Adapter);

    }


    //현재 서비스가 실행중인지 확인
    private boolean isMyServiceRunning(Class<?> seviceClass) {

        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (seviceClass.getName().equals(service.service.getClassName())) {
                Log.d("서비스", "isMyServiceRunning? " + true);
                return true;
            }
        }
        Log.d("서비스", "isMyServiceRunning? " + false);
        return false;
    }


    /*서비스로부터 메시지를 받기 위한 준비*/
    @Override
    protected void onResume() {
        super.onResume();

        //이 액티비티가 화면에 떠 있을 때만 서비스로부터 메시지를 받는다
        //lobby_event 라는 이름을 가진 이벤트를 수신한다
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, new IntentFilter("lobby_event"));
    }


    @Override
    protected void onPause() {
        super.onPause();

        //화면에서 이 액티비티가 사라지면 - 브로드캐스트를 끊는다
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (alertDialog != null && alertDialog.isShowing())
            alertDialog.dismiss();
    }

    //서비스에서 보낸 메시지를 받는 메소드
    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String message = intent.getStringExtra("message");
            Log.d("서비스", "11111111Receiver) message received: " + message);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (message.split("@@")[0].equals("room_create")) {
                        room_name = message.split("@@")[1];
                        roomListData.add(new RecyclerRoomListData(
                                "No." + 11,
                                message.split("@@")[1],
                                "teacher님의 방",
                                "인원 : 1/5",
                                "/ 클래식",
                                ""));
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            roomListAdapter.notifyDataSetChanged();
                        }
                    });

                }
            }).start();


        }
    };


    //서비스로 메시지를 보낸다
    void sendMsg(String msg) {
        Intent intent = new Intent(getApplicationContext(), MainService.class);
        intent.putExtra("message", msg);
        startService(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(LobbyActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    //튜토리얼 액티비티 생성 메서드
    public void setTutorial() {
        Intent intent = new Intent(this, TutorialActivity.class);
        startActivity(intent);
    }

}