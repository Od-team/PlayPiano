package od.team.playpiano;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
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
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

import od.team.playpiano.Adapter.RoomListAdapter;
import od.team.playpiano.RecyclerItemData.RecyclerRoomListData;

public class LobbyActivity extends AppCompatActivity {

    TextView filter_text;

    View filter_layout;

    Button room_create_btn;

//    Spinner people_spinner;
//    ArrayList people_num_spinner_list = new ArrayList<>();
//    ArrayAdapter<String> people_num_Adapter;

//    Spinner play_time_spinner;
//    ArrayList play_time_spinner_list = new ArrayList<>();
//    ArrayAdapter<String> play_time_Adapter;

//    Spinner filter_people_spinner;
//    ArrayList filter_people_num_spinner_list = new ArrayList<>();
//    ArrayAdapter<String> filter_people_num_Adapter;

//    Spinner filter_play_time_spinner;
//    ArrayList filter_play_time_spinner_list = new ArrayList<>();
//    ArrayAdapter<String> filter_play_time_Adapter;

    RecyclerView room_list_recyclerView;
    RoomListAdapter roomListAdapter;
    LinearLayoutManager linearLayoutManager;
    public static ArrayList<RecyclerRoomListData> roomListData = new ArrayList<>();


    Context context;
    MainService service;
    Intent serviceIntent;

    String TAG = "tag " + this.getClass().getSimpleName();

    Button dialog_room_create_btn;
    Button feedback_box_btn;

    Intent getIntent;
    public static String user_id;
    public static String user_job;

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

    String master_id;

    ImageButton reroll_image_btn;

    TextView job_text;
    Button loop_btn;
    int randomNum;

    EditText ip_e_text;
    EditText ip_e_text2;
    Button ip_btn;
    Button ip_btn2;

    boolean isDialogOn = false;
    boolean isDialogOn1 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        randomNum = new Random().nextInt(700000 - 1111) + 1111;

        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            }
        };


        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("왜 거부하셨어요...\n하지만 [설정] > [권한] 에서 권한을 허용할 수 있어요.")
                .setPermissions(Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.MODIFY_AUDIO_SETTINGS,Manifest.permission.CALL_PHONE)
                .check();

        context = getApplicationContext();
        service = new MainService(getApplicationContext());
        serviceIntent = new Intent(getApplicationContext(), MainService.class);
        reroll_image_btn = findViewById(R.id.reroll_image_btn);
        job_text = findViewById(R.id.job_text);
        loop_btn = findViewById(R.id.loop_btn);
        ip_e_text = findViewById(R.id.ip_e_text);
        ip_btn = findViewById(R.id.ip_btn);
        ip_btn2 = findViewById(R.id.ip_btn2);
        ip_e_text2 = findViewById(R.id.ip_e_text2);

        loop_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /** 루프스테이션 화면으로 넘어가야함. **/
                Intent intent = new Intent(LobbyActivity.this, LoopActivity.class);
                startActivity(intent);
            }
        });

        reroll_image_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (roomListData.size() != 0) {
                                    roomListAdapter.notifyDataSetChanged();
                                    Toast.makeText(LobbyActivity.this, "방을 새로고침 할게요!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(LobbyActivity.this, "방이 없어요!", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
                    }
                }).start();
            }
        });

        getIntent = getIntent();
        user_id = getIntent.getStringExtra("id");
        user_job = getIntent.getStringExtra("job");
        Log.d("서비스", "로비 onCreate");

        if (user_job.equals("teacher")) {
            job_text.setText(user_id + " 선생님");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ip_btn.setVisibility(View.GONE);
                            ip_btn2.setVisibility(View.GONE);
                            ip_e_text.setVisibility(View.GONE);
                            ip_e_text2.setVisibility(View.GONE);
                        }
                    });
                }
            }).start();
        } else {
            job_text.setText(user_id + "학생");
        }


        ip_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!ip_e_text.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "왼손 연결중..", Toast.LENGTH_SHORT).show();

                    /** 왼손 **/
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                // 여기 아이피는 라즈베리파이 아이피를 작성해주어야함.
                                left_socket = new Socket(ip_e_text.getText().toString(), 10000);
                                left_in = new DataInputStream(left_socket.getInputStream());
                                left_out = new DataOutputStream(left_socket.getOutputStream());

                                String left = String.valueOf(left_in.readByte());
                                left_ok = (char) Integer.parseInt(left);

                                Log.d("핸들러", "왼손 받은 값" + left_ok);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "왼손 장갑 연결 성공", Toast.LENGTH_SHORT).show();
                                        isDialogOn = true;
                                        if (isDialogOn == true && isDialogOn1 == true){
                                            //튜토리얼 액티비티 생성
                                            setTutorial();
                                        }
                                    }
                                });
                            } catch (Exception e) {
                                Log.d("핸들러", "왼손 ㅇㅔ러" + e.getLocalizedMessage());
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "왼손 장갑 연결 실패", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                e.printStackTrace();
                            }
                        }
                    }).start();


                } else {
                    Toast.makeText(getApplicationContext(), "ip를 입력해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ip_btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!ip_e_text2.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "오른손 연결중..", Toast.LENGTH_SHORT).show();

                    /** 오른손 **/
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                // 여기 아이피는 라즈베리파이 아이피를 작성해주어야함.
                                right_socket = new Socket(ip_e_text2.getText().toString(), 10000);
                                right_in = new DataInputStream(right_socket.getInputStream());
                                right_out = new DataOutputStream(right_socket.getOutputStream());

                                String right = String.valueOf(right_in.readByte());
                                right_ok = (char) Integer.parseInt(right);

                                Log.d("핸들러", "오른손 받은 값" + right_ok);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "오른손 장갑 연결 성공", Toast.LENGTH_SHORT).show();
                                        isDialogOn1 = true;
                                        if (isDialogOn == true && isDialogOn1 == true){
                                            //튜토리얼 액티비티 생성
                                            setTutorial();
                                        }
                                    }
                                });

                            } catch (Exception e) {
                                Log.d("핸들러", "오른손 ㅇㅔ러" + e.getLocalizedMessage());
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "오른손 장갑 연결 실패", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                e.printStackTrace();
                            }
                        }
                    }).start();
                } else {
                    Toast.makeText(getApplicationContext(), "ip를 입력해주세요", Toast.LENGTH_SHORT).show();
                }

            }
        });





        //지금 서비스가 실행되고 있지 않다면 -> 서비스를 실행한다
        if (!isMyServiceRunning(service.getClass())) {
            startService(serviceIntent);
            Log.d("서비스", "로비- startService");

            /** 가장먼저 사용자의 아이디를 전달해준다.**/
            sendMsg(user_id);
            Log.d("서비스", "사용자 id : " + user_id);
        }


        init();
        BtnClickListener();
        roomAlertDialog();


        roomListAdapter.setOnClickListener(new RoomListAdapter.MyClickListener() {
            @Override
            public void onItemClicked(int position, ArrayList<RecyclerRoomListData> roomListDataArrayList) {
                try {


                    Intent intent = new Intent(LobbyActivity.this, GameRoomActivity.class);
                    Log.d("Sdffsd", user_job);
                    master_id = roomListDataArrayList.get(position).getMaster_id().replaceFirst("방장 : ", "");
                    master_id = master_id.replaceFirst(" 님의 방", "");
                    room_name = roomListDataArrayList.get(position).getRoom_name();
                    Log.d("Sdffsd", room_name);

                    String url = "http://54.180.26.72/room_number_get.php";
                    ContentValues values = new ContentValues();
                    values.put("master_id", master_id);
                    values.put("user_id", user_id);

                    NetWorkAsync netWorkAsync = new NetWorkAsync(url, values);
                    String get_room_number = netWorkAsync.execute().get();

                    Log.d("sdffds", "Lobby1 " + user_job + "!!!");

                    intent.putExtra("master_id", master_id);
                    intent.putExtra("user_job", user_job);
                    intent.putExtra("room_name", room_name);
                    intent.putExtra("room_number", get_room_number);
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(LobbyActivity.this, "존재하지 않는 방이네요.\n방을 새로고침 할게요!", Toast.LENGTH_SHORT).show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    roomListAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }).start();
                }

            }
        });


    }

    public void init() {
        filter_text = findViewById(R.id.filter_text);
        filter_layout = findViewById(R.id.filter_layout);
        room_create_btn = findViewById(R.id.room_create_btn);
//        filter_people_spinner = findViewById(R.id.filter_people_spinner);
//        filter_play_time_spinner = findViewById(R.id.filter_play_time_spinner);
        room_list_recyclerView = findViewById(R.id.room_list_recyclerView);
        feedback_box_btn = findViewById(R.id.feedback_box_btn);

        linearLayoutManager = new LinearLayoutManager(LobbyActivity.this, LinearLayoutManager.VERTICAL, false);
        roomListAdapter = new RoomListAdapter(LobbyActivity.this, roomListData);
        room_list_recyclerView.setLayoutManager(linearLayoutManager);
        room_list_recyclerView.setAdapter(roomListAdapter);
    }

    public void BtnClickListener() {
//        filter_text.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (filter_layout.getVisibility() == View.VISIBLE) {
//                    filter_layout.setVisibility(View.GONE);
//                    filterTextChange("필터 ▼");
//                } else {
//                    filter_layout.setVisibility(View.VISIBLE);
//                    filterTextChange("필터 ▲");
////                    filterSpinnerAdapter();
//                }
//            }
//        });

        feedback_box_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LobbyActivity.this, FeedbackBoxActivity.class);
                intent.putExtra("user_id", user_id);
                startActivity(intent);
            }
        });
    }


    public void roomAlertDialog() {
        Log.d("asdds", "Daads");
        room_create_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user_job.equals("teacher")) {

                    Toast.makeText(getApplicationContext(), "현재 [ 장르 및 bgm ] 변경 불가능합니다.", Toast.LENGTH_LONG).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(LobbyActivity.this);

                    View view = LayoutInflater.from(LobbyActivity.this).inflate(R.layout.dialog_room_create, null);

                    dialog_room_create_btn = view.findViewById(R.id.dialog_room_create_btn);

                    room_name_e_text = view.findViewById(R.id.room_name_e_text);

                    builder.setView(view);

                    alertDialog = builder.create();

                    alertDialog.show();

                    dialog_room_create_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (!room_name_e_text.getText().toString().equals("")) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        roomListData.add(new RecyclerRoomListData(
                                                " 장르 : ",
                                                room_name_e_text.getText().toString(),
                                                "방장 : " + user_id,
                                                "방 이름 : ",
                                                " 베이직",
                                                ""));
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                roomListAdapter.notifyDataSetChanged();
                                            }
                                        });
                                    }
                                }).start();

                                // 방을 만들면 db에 방번호 저장함 왜냐하면 apprtc 연동할떄 같은방에 접속하기 위함
                                String url = "http://54.180.26.72/room_save.php";
                                ContentValues values = new ContentValues();
                                values.put("id", user_id);
                                values.put("room_number", randomNum);

                                NetWorkAsync netWorkAsync = new NetWorkAsync(url, values);
                                netWorkAsync.execute();


                                Intent intent = new Intent(LobbyActivity.this, GameRoomActivity.class);
                                intent.putExtra("id", user_id);
                                intent.putExtra("user_job", user_job);
                                intent.putExtra("room_name", room_name_e_text.getText().toString());
                                intent.putExtra("room_number", String.valueOf(randomNum));
                                Log.d("sdffds", randomNum + "밑에!!!");
                                Log.d("sdffds", "Lobby2 " + user_job + "!!!");

                                startActivity(intent);
                                alertDialog.dismiss();
                                sendMsg("room_create@@" + user_id + "@@" + room_name_e_text.getText().toString());
                            } else {
                                Toast.makeText(LobbyActivity.this, "방 제목을 작성해주세요", Toast.LENGTH_SHORT).show();
                            }


                        }
                    });
                    Log.d("asdds", "선생");
                } else {
                    Toast.makeText(LobbyActivity.this, "선생님만 방(수업)을 만들 수 있어요", Toast.LENGTH_SHORT).show();
                    Log.d("asdds", "학생");
                }

            }

        });


    }

//    public void filterSpinnerAdapter() {
//
//        filter_people_num_spinner_list.clear();
//        filter_play_time_spinner_list.clear();
//
//        for (int i = 2; i <= 5; i += 1)
//            filter_people_num_spinner_list.add(i + "명");
//
//        for (int i = 10; i <= 60; i += 10)
//            filter_play_time_spinner_list.add(i + "초");
//
//        filter_people_num_Adapter = new ArrayAdapter<String>(getApplicationContext(),
//                android.R.layout.simple_spinner_dropdown_item,
//                filter_people_num_spinner_list);
//
//        filter_play_time_Adapter = new ArrayAdapter<String>(getApplicationContext(),
//                android.R.layout.simple_spinner_dropdown_item,
//                filter_play_time_spinner_list);
//
//        filter_people_spinner.setAdapter(filter_people_num_Adapter);
//        filter_play_time_spinner.setAdapter(filter_play_time_Adapter);
//
//    }


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
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        roomListAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }


    @Override
    protected void onPause() {
        isDialogOn = false;

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

                    String[] temp = new String[10];
                    temp = message.split("@@");

                    Log.d("서비스", "1 : " + temp[0]);
                    Log.d("서비스", "2 : " + temp[1]);
                    Log.d("서비스", "3 : " + temp[2]);

                    if (temp[0].equals("room_create")) {
                        roomListData.add(new RecyclerRoomListData(
                                " 장르 : ",
                                temp[2],
                                "방장 : " + temp[1],
                                "방 이름 : ",
                                " 베이직",
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
        Log.d("sdf", msg);
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