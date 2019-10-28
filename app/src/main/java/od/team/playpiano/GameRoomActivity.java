package od.team.playpiano;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Image;
import android.media.MediaPlayer;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import od.team.playpiano.RecyclerItemData.RecyclerRoomListData;

public class GameRoomActivity extends AppCompatActivity {

    String user_id;
    ImageView student_imageView;
    ImageView ready_imageView;

    Button ready_btn;

    TextView room_name_t;

    Intent getIntent;

    public static DataInputStream in = null;
    public static DataOutputStream out = null;
    public static Socket socket = null;
    char ok;

    TextView textView6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_room);

        student_imageView = findViewById(R.id.student_imageView);
        ready_btn = findViewById(R.id.ready_btn);
        ready_imageView = findViewById(R.id.ready_imageView);
        room_name_t = findViewById(R.id.room_name_t);

        textView6 = findViewById(R.id.textView6);

        getIntent = getIntent();

        user_id = LobbyActivity.user_id;

        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        room_name_t.setText(getIntent.getStringExtra("room_name"));
                    }
                });
            }
        }).start();

        if (user_id != null && !user_id.equals("teacher")) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ready_btn.setText("준 비");
                        }
                    });
                }
            }).start();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        socket = new Socket("192.168.0.8", 10000);
                        in = new DataInputStream(socket.getInputStream());
                        out = new DataOutputStream(socket.getOutputStream());

                        String s = String.valueOf(in.readByte());
                        ok = (char) Integer.parseInt(s);

                        Log.d("dsfd", "받은 값"+ok);

                    } catch (Exception e) {
                        Log.d("dsfd", "ㅇㅔ러"+ e.getLocalizedMessage());
                        e.printStackTrace();
                    }
                }
            }).start();

        }

        sendMsg("gameRoom@@" + user_id);


        ready_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ready_btn.getText().toString().equals("준 비")) {
                    sendMsg("ready@@ready");
                } else if (ready_btn.getText().toString().equals("게임 시작")) {
                    sendMsg("start@@start");
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, new IntentFilter("gameRoom_event"));
    }

    //서비스로 메시지를 보낸다
    void sendMsg(String msg) {
        Intent intent = new Intent(getApplicationContext(), MainService.class);
        intent.putExtra("message", msg);
        startService(intent);
    }

    //서비스에서 보낸 메시지를 받는 메소드
    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String message = intent.getStringExtra("message");
            Log.d("서비스", "GameRoomActivity message received: " + message);

            if (message.equals("user")) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                student_imageView.setBackground(getResources().getDrawable(R.drawable.image2));
                                if(ok == 'o'){
                                    Toast.makeText(GameRoomActivity.this,"소켓연결됨",Toast.LENGTH_SHORT).show();
                                }
                                if(!textView6.getText().toString().equals("teacher")){
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    textView6.setText("student");
                                                }
                                            });
                                        }
                                    }).start();
                                }
                            }
                        });
                    }
                }).start();
            }
            if (message.equals("ready")) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ready_imageView.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }).start();
            }
            if (message.equals("start")) {
                Intent intent1 = new Intent(GameRoomActivity.this, GameScreenActivity.class);
                startActivity(intent1);
            }

        }
    };




}
