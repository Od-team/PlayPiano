package od.team.playpiano;

import android.app.Activity;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class TutorialActivity extends Activity {

    TextView interval_text;   //계이름 표시해주는 텍스트. 손가락으로 버튼을 눌러 소리를 낼 때 마다 누른 손가락에 맞는 계이름 표시
    ImageView hand_image;     //손모양 이미지, 손가락으로 버튼을 눌러 소리를 낼 때 마다 누른 손가락에 맞는 위치에 파란색 표시된 이미지로 변경
    ImageView hand1_image;
    ImageView hand2_image;
    ImageView hand3_image;
    ImageView hand4_image;
    ImageView hand5_image;

    SoundPool sound1, sound2, sound3, sound4, sound5;
    int soundId1, soundId2, soundId3, soundId4, soundId5;

    boolean isRaspOn = false;

    ArrayList<String> raspDataToServer = new ArrayList<>();

    long startTime;
    double cutTime;
    String v;

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_tutorial);

        hand_image = findViewById(R.id.hand_imageView);
        hand1_image = findViewById(R.id.hand1_imageView);
        hand2_image = findViewById(R.id.hand2_imageView);
        hand3_image = findViewById(R.id.hand3_imageView);
        hand4_image = findViewById(R.id.hand4_imageView);
        hand5_image = findViewById(R.id.hand5_imageView);
        interval_text = findViewById(R.id.interval_textView);

        LoginActivity.soundOn = 1;

        //소켓 정보 받아서 소리 낼 수 있게 하면 됨

        soundPoolInit();
        raspSignalON();

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 1:
                        Log.d("핸들러", String.valueOf(msg.what));
                        interval_text.setText("1번");
                        interval_text.setVisibility(View.VISIBLE);
                        //Glide.with(TutorialActivity.this).load(R.drawable.hand_1).into(hand_image);
                        changeHandImage(1);
                        break;
                    case 2:
                        Log.d("핸들러", String.valueOf(msg.what));
                        interval_text.setText("2번");
                        interval_text.setVisibility(View.VISIBLE);
                        //Glide.with(TutorialActivity.this).load(R.drawable.hand_2).into(hand_image);
                        changeHandImage(2);
                        break;
                    case 3:
                        Log.d("핸들러", String.valueOf(msg.what));
                        interval_text.setText("3번");
                        interval_text.setVisibility(View.VISIBLE);
                        //Glide.with(TutorialActivity.this).load(R.drawable.hand_3).into(hand_image);
                        changeHandImage(3);
                        break;
                    case 4:
                        Log.d("핸들러", String.valueOf(msg.what));
                        interval_text.setText("4번");
                        interval_text.setVisibility(View.VISIBLE);
                        //Glide.with(TutorialActivity.this).load(R.drawable.hand_4).into(hand_image);
                        changeHandImage(4);
                        break;
                    case 5:
                        Log.d("핸들러", String.valueOf(msg.what));
                        interval_text.setText("5번");
                        interval_text.setVisibility(View.VISIBLE);
                        //Glide.with(TutorialActivity.this).load(R.drawable.hand_5).into(hand_image);
                        changeHandImage(5);
                        break;
                }

            }
        };

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }
    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }

    //확인 버튼
    public void completeTutorial(View view) {
        raspSignalOFF();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LoginActivity.soundOn = 0;
        isRaspOn = false;
    }

    public void soundPoolInit() {
        sound1 = new SoundPool(1, AudioManager.STREAM_ALARM, 0);// maxStreams, streamType, srcQuality
        sound2 = new SoundPool(1, AudioManager.STREAM_ALARM, 0);// maxStreams, streamType, srcQuality
        sound3 = new SoundPool(1, AudioManager.STREAM_ALARM, 0);// maxStreams, streamType, srcQuality
        sound4 = new SoundPool(1, AudioManager.STREAM_ALARM, 0);// maxStreams, streamType, srcQuality
        sound5 = new SoundPool(1, AudioManager.STREAM_ALARM, 0);// maxStreams, streamType, srcQuality

        soundId1 = sound1.load(TutorialActivity.this, R.raw.p1, 1);
        soundId2 = sound2.load(TutorialActivity.this, R.raw.p2, 1);
        soundId3 = sound3.load(TutorialActivity.this, R.raw.p3, 1);
        soundId4 = sound4.load(TutorialActivity.this, R.raw.p4, 1);
        soundId5 = sound5.load(TutorialActivity.this, R.raw.p5, 1);
    }

    public void raspSignalON() {

        if (!LobbyActivity.user_id.equals("teacher")) {

            isRaspOn = true;

            raspDataToServer.clear();

            if (LobbyActivity.in == null) {
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

                            if (LobbyActivity.in == null) {
                                Log.d("sdffsd", "sfdfsd");
                            }
                            v=String.valueOf(LobbyActivity.in.readByte());
                            Log.d("핸들러", "튜토리얼 로그 readByte");

                            if(LoginActivity.soundOn == 1){

                                char a = (char) Integer.parseInt(v);
                                Log.d("tutorialㅇㅇㅇ", "메세지 " + a);
                                if (a == '1') {

                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            sound1.play(soundId1, 1.0f, 1.0f, 1, 0, 1.0f);
                                        }
                                    }).start();

                                    cutTime = (System.currentTimeMillis() - startTime)/1000.0;
                                    raspDataToServer.add("p1_"+cutTime+"@@");

                                    Message messageId = handler.obtainMessage();
                                    messageId.what = 1;
                                    handler.sendMessage(messageId);

                                } else if (a == '2') {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            sound2.play(soundId2, 1.0f, 1.0f, 1, 0, 1.0f);

                                        }
                                    }).start();
                                    cutTime = (System.currentTimeMillis() - startTime)/1000.0;
                                    raspDataToServer.add("p2_"+cutTime+"@@");

                                    Message messageId = handler.obtainMessage();
                                    messageId.what = 2;
                                    handler.sendMessage(messageId);

                                } else if (a == '3') {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            sound3.play(soundId3, 1.0f, 1.0f, 1, 0, 1.0f);

                                        }
                                    }).start();
                                    cutTime = (System.currentTimeMillis() - startTime)/1000.0;
                                    raspDataToServer.add("p3_"+cutTime+"@@");

                                    Message messageId = handler.obtainMessage();
                                    messageId.what = 3;
                                    handler.sendMessage(messageId);

                                } else if (a == '4') {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            sound4.play(soundId4, 1.0f, 1.0f, 1, 0, 1.0f);

                                        }
                                    }).start();
                                    cutTime = (System.currentTimeMillis() - startTime)/1000.0;
                                    raspDataToServer.add("p4_"+cutTime+"@@");

                                    Message messageId = handler.obtainMessage();
                                    messageId.what = 4;
                                    handler.sendMessage(messageId);

                                } else if (a == '5') {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            sound5.play(soundId5, 1.0f, 1.0f, 1, 0, 1.0f);

                                        }
                                    }).start();
                                    cutTime = (System.currentTimeMillis() - startTime)/1000.0;
                                    raspDataToServer.add("p5_"+cutTime+"@@");

                                    Message messageId = handler.obtainMessage();
                                    messageId.what = 5;
                                    handler.sendMessage(messageId);

                                }
                            }




                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d("핸들러", "튜토리얼 while 쪽에 에러");
                    }

                    Log.d("핸들러", "튜토리얼 while 문 종료");


                }
            }).start();
        }
    }

    public void raspSignalOFF() {
        isRaspOn = false;
        LoginActivity.soundOn = 0;
    }

    private void changeHandImage(int index){
        hand_image.setVisibility(View.GONE);
        hand1_image.setVisibility(View.GONE);
        hand2_image.setVisibility(View.GONE);
        hand3_image.setVisibility(View.GONE);
        hand4_image.setVisibility(View.GONE);
        hand5_image.setVisibility(View.GONE);
        switch (index){
            case 1 :
                hand1_image.setVisibility(View.VISIBLE);
                break;
            case 2 :
                hand2_image.setVisibility(View.VISIBLE);
                break;
            case 3 :
                hand3_image.setVisibility(View.VISIBLE);
                break;
            case 4 :
                hand4_image.setVisibility(View.VISIBLE);
                break;
            case 5 :
                hand5_image.setVisibility(View.VISIBLE);
                break;
        }

    }
}