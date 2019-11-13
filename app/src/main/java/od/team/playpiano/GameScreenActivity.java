package od.team.playpiano;

import android.content.ContentValues;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.marcinmoskala.arcseekbar.ArcSeekBar;

import java.util.ArrayList;

public class GameScreenActivity extends AppCompatActivity {

    VideoView videoView;
    int counter = 6;

    TextView counter_textView;
    ImageView counter_imageView;
    TextView ready_text;

    Handler handler;

    int count;
    int seek_progress;

    ArcSeekBar play_time_seekBar;

    TextView time_text;
    TextView result_alert_text;

    SoundPool sound1, sound2, sound3, sound4, sound5;
    int soundId1, soundId2, soundId3, soundId4, soundId5;


    boolean isRaspOn = false;

    ArrayList<String> raspDataToServer = new ArrayList<>();

    long startTime;
    double cutTime;
    String v;
    MediaPlayer player;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_screen);


        handler = new Handler();

        counter_imageView = (ImageView) findViewById(R.id.counter_imageView);
        counter_textView = (TextView) findViewById(R.id.counter_textView);
        ready_text = findViewById(R.id.ready_text);
        play_time_seekBar = findViewById(R.id.play_time_seekBar);
        time_text = findViewById(R.id.time_text);
        result_alert_text = findViewById(R.id.result_alert_text);

        videoView = (VideoView) findViewById(R.id.videoView);
        videoView.setVideoPath("android.resource://od.team.playpiano/" + R.raw.music_video);

        soundPoolInit();

        new Thread(new Runnable() {
            @Override
            public void run() {

                sleep(1500);


                while (true) {

                    count = Integer.parseInt(counter_textView.getText().toString());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (count == 0) {
                                startTime = System.currentTimeMillis();
                                ready_text.setText("연주 시작 !!");
                            }
                            counter_textView.setText(count + "");
                        }
                    });

                    count--;

                    sleep(1000);

                    if (count == 0) {
                        sleep(300);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ready_text.setVisibility(View.GONE);
                                counter_imageView.setVisibility(View.GONE);
                                counter_textView.setVisibility(View.GONE);
                                videoView.seekTo(0);
                                videoView.start();
                                player = MediaPlayer.create(GameScreenActivity.this, R.raw.bgm);
                                player.start();
                            }
                        });

                        raspSignalON();

                        /** 연주시작
                         * 아두이노 or 라즈베리 파이 신호를 받기 시작하는 코드를 추가해야함**/
                        for (int i = play_time_seekBar.getProgress(); i >= 0; i--) {

                            seek_progress = i;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    play_time_seekBar.setProgress(seek_progress);
                                    time_text.setText(seek_progress + "초");
                                }
                            });
                            sleep(1000);
                        }

                        raspSignalOFF();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ready_text.setText("연주 종료 !!");
                                player.stop();
                                result_alert_text.setVisibility(View.VISIBLE);
                                ready_text.setVisibility(View.VISIBLE);

                                String serverToData = null;

                                StringBuilder sb = new StringBuilder();

                                for(int i = 0; i < raspDataToServer.size(); i++){
                                    //data : e1_0.028@@
                                    sb.append(raspDataToServer.get(i));
                                }
                                serverToData = String.valueOf(sb);

                                ContentValues values = new ContentValues();
                                values.put("music_data", serverToData);

                                NetWorkAsync netWorkAsync = new NetWorkAsync("http://13.209.4.168/music_page.php", values);
                                try {
                                    String get_date = netWorkAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR).get();

                                    if (get_date == null) {
                                        Log.d("ddd", "받아온 값 : null");
                                    } else {
                                        Log.d("ddd", "받아온 값 : " + get_date);
                                    }

                                    Intent intent = new Intent(GameScreenActivity.this, ResultScreenActivity.class);
                                    startActivity(intent);

                                } catch (Exception e) {
                                    Log.d("ddd", "URL Connection error : " + e.getMessage());
                                }

                            }
                        });
                        sleep(1000);


                        /** 아두이노 또는 라즈베리파이로 부터 전달받은 신호(String)을 value에 넣어야함
                         * 신호는 e1_1@@ 과 같은 형태로 구분한다.
                         * 첫번째 효과음 e1
                         * 첫번째 효과음 울린 시간 1초초**/




                        break;
                    }
                }
            }
        }).start();


    }


    public void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void soundPoolInit() {
        sound1 = new SoundPool(1, AudioManager.STREAM_ALARM, 0);// maxStreams, streamType, srcQuality
        sound2 = new SoundPool(1, AudioManager.STREAM_ALARM, 0);// maxStreams, streamType, srcQuality
        sound3 = new SoundPool(1, AudioManager.STREAM_ALARM, 0);// maxStreams, streamType, srcQuality
        sound4 = new SoundPool(1, AudioManager.STREAM_ALARM, 0);// maxStreams, streamType, srcQuality
        sound5 = new SoundPool(1, AudioManager.STREAM_ALARM, 0);// maxStreams, streamType, srcQuality

        soundId1 = sound1.load(GameScreenActivity.this, R.raw.p4, 1);
        soundId2 = sound2.load(GameScreenActivity.this, R.raw.p5, 1);
        soundId3 = sound3.load(GameScreenActivity.this, R.raw.p6, 1);
        soundId4 = sound4.load(GameScreenActivity.this, R.raw.p7, 1);
        soundId5 = sound5.load(GameScreenActivity.this, R.raw.p8, 1);
    }

    public void raspSignalON() {

        if (!LobbyActivity.user_id.equals("teacher")) {

            isRaspOn = true;
            raspDataToServer.clear();

            if (GameRoomActivity.in == null) {
                Log.d("sdffsd", "sfdfsd");
            } else {
                Log.d("sdffsd", "not null");
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        while (isRaspOn) {
//                                Log.d("ㅇㅇㅇ","메세지 기다림");

                            if (GameRoomActivity.in == null) {
                                Log.d("sdffsd", "sfdfsd");
                            }

                            v=String.valueOf(GameRoomActivity.in.readByte());
                            char a = (char) Integer.parseInt(v);
                            Log.d("ㅇㅇㅇ", "메세지 " + a);
                            if (a == '1') {

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        sound1.play(soundId1, 1.0f, 1.0f, 1, 0, 1.0f);
                                    }
                                }).start();

                                cutTime = (System.currentTimeMillis() - startTime)/1000.0;
                                raspDataToServer.add("p1_"+cutTime+"@@");

                            } else if (a == '2') {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        sound2.play(soundId2, 1.0f, 1.0f, 1, 0, 1.0f);

                                    }
                                }).start();
                                cutTime = (System.currentTimeMillis() - startTime)/1000.0;
                                raspDataToServer.add("p2_"+cutTime+"@@");

                            } else if (a == '3') {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        sound3.play(soundId3, 1.0f, 1.0f, 1, 0, 1.0f);

                                    }
                                }).start();
                                cutTime = (System.currentTimeMillis() - startTime)/1000.0;
                                raspDataToServer.add("p3_"+cutTime+"@@");
                            } else if (a == '4') {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        sound4.play(soundId4, 1.0f, 1.0f, 1, 0, 1.0f);

                                    }
                                }).start();
                                cutTime = (System.currentTimeMillis() - startTime)/1000.0;
                                raspDataToServer.add("p4_"+cutTime+"@@");
                            } else if (a == '5') {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        sound5.play(soundId5, 1.0f, 1.0f, 1, 0, 1.0f);

                                    }
                                }).start();
                                cutTime = (System.currentTimeMillis() - startTime)/1000.0;
                                raspDataToServer.add("p5_"+cutTime+"@@");
                            }


                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            }).start();
        }
    }

    public void raspSignalOFF() {
        isRaspOn = false;
    }

}
