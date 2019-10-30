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
import android.widget.Button;
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
    ImageView hand6_image;
    ImageView hand7_image;
    ImageView drum_default;
    ImageView drum_left;
    ImageView drum_right;
    Button complete_tutorial_btn;

    SoundPool sound1, sound2, sound3, sound4, sound5, sound6, sound7, sound8, sound9;
    int soundId1, soundId2, soundId3, soundId4, soundId5, soundId6, soundId7, soundId8, soundId9;

    boolean isRaspOn = false;

    ArrayList<String> raspDataToServer = new ArrayList<>();

    long startTime;
    double cutTime;
    String left;
    String right;

    Handler handler;
    boolean complete_tutorial;

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
        hand6_image = findViewById(R.id.hand6_imageView);
        hand7_image = findViewById(R.id.hand7_imageView);
        interval_text = findViewById(R.id.interval_textView);

        drum_default = findViewById(R.id.drum_default_imageView);
        drum_left = findViewById(R.id.drum_left_imageView);
        drum_right = findViewById(R.id.drum_right_imageView);
        complete_tutorial_btn = findViewById(R.id.complete_tutorial_btn);

        LoginActivity.soundOn = 1;
        complete_tutorial = false;

        //소켓 정보 받아서 소리 낼 수 있게 하면 됨

        soundPoolInit();
        raspSignalON();

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
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
                    case 6:
                        Log.d("핸들러", String.valueOf(msg.what));
                        interval_text.setText("6번");
                        interval_text.setVisibility(View.VISIBLE);
                        //Glide.with(TutorialActivity.this).load(R.drawable.hand_5).into(hand_image);
                        changeHandImage(6);
                        break;
                    case 7:
                        Log.d("핸들러", String.valueOf(msg.what));
                        interval_text.setText("7번");
                        interval_text.setVisibility(View.VISIBLE);
                        //Glide.with(TutorialActivity.this).load(R.drawable.hand_5).into(hand_image);
                        changeHandImage(7);
                        break;
                    case 8:
                        Log.d("핸들러", String.valueOf(msg.what));
                        interval_text.setText("8번");
                        interval_text.setVisibility(View.VISIBLE);
                        //Glide.with(TutorialActivity.this).load(R.drawable.hand_5).into(hand_image);
                        changeDrumImage(8);
                        break;
                    case 9:
                        Log.d("핸들러", String.valueOf(msg.what));
                        interval_text.setText("9번");
                        interval_text.setVisibility(View.VISIBLE);
                        //Glide.with(TutorialActivity.this).load(R.drawable.hand_5).into(hand_image);
                        changeDrumImage(9);
                        break;
                }

            }
        };

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
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
        if (complete_tutorial) {
            raspSignalOFF();
            finish();
        } else {
            complete_tutorial_btn.setText("완료");
            complete_tutorial = true;
            changeDrum();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LoginActivity.soundOn = 0;
    }

    public void soundPoolInit() {
        sound1 = new SoundPool(1, AudioManager.STREAM_ALARM, 0);// maxStreams, streamType, srcQuality
        sound2 = new SoundPool(1, AudioManager.STREAM_ALARM, 0);// maxStreams, streamType, srcQuality
        sound3 = new SoundPool(1, AudioManager.STREAM_ALARM, 0);// maxStreams, streamType, srcQuality
        sound4 = new SoundPool(1, AudioManager.STREAM_ALARM, 0);// maxStreams, streamType, srcQuality
        sound5 = new SoundPool(1, AudioManager.STREAM_ALARM, 0);// maxStreams, streamType, srcQuality
        sound6 = new SoundPool(1, AudioManager.STREAM_ALARM, 0);// maxStreams, streamType, srcQuality
        sound7 = new SoundPool(1, AudioManager.STREAM_ALARM, 0);// maxStreams, streamType, srcQuality
        sound8 = new SoundPool(1, AudioManager.STREAM_ALARM, 0);// maxStreams, streamType, srcQuality
        sound9 = new SoundPool(1, AudioManager.STREAM_ALARM, 0);// maxStreams, streamType, srcQuality

        soundId1 = sound1.load(TutorialActivity.this, R.raw.p1, 1);
        soundId2 = sound2.load(TutorialActivity.this, R.raw.p2, 1);
        soundId3 = sound3.load(TutorialActivity.this, R.raw.p3, 1);
        soundId4 = sound4.load(TutorialActivity.this, R.raw.p4, 1);
        soundId5 = sound5.load(TutorialActivity.this, R.raw.p5, 1);
        soundId6 = sound6.load(TutorialActivity.this, R.raw.p6, 1);
        soundId7 = sound7.load(TutorialActivity.this, R.raw.p7, 1);
        soundId8 = sound8.load(TutorialActivity.this, R.raw.p8, 1);
        soundId9 = sound9.load(TutorialActivity.this, R.raw.p9, 1);
    }

    public void raspSignalON() {

        if (!LobbyActivity.user_id.equals("teacher")) {

            isRaspOn = true;

            raspDataToServer.clear();


            /** 왼손 **/
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        while (isRaspOn) {
//                                Log.d("ㅇㅇㅇ","메세지 기다림");

                            if (LobbyActivity.left_in == null) {
                                Log.d("sdffsd", "sfdfsd");
                            }
                            left = String.valueOf(LobbyActivity.left_in.readByte());
                            if (LoginActivity.soundOn == 1) {

                                char a = (char) Integer.parseInt(left);
                                Log.d("tutorialㅇㅇㅇ", "메세지 " + a);
                                if (a == '1') {

                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            sound1.play(soundId1, 1.0f, 1.0f, 1, 0, 1.0f);
                                        }
                                    }).start();

                                    cutTime = (System.currentTimeMillis() - startTime) / 1000.0;
                                    raspDataToServer.add("p1_" + cutTime + "@@");

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
                                    cutTime = (System.currentTimeMillis() - startTime) / 1000.0;
                                    raspDataToServer.add("p2_" + cutTime + "@@");

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
                                    cutTime = (System.currentTimeMillis() - startTime) / 1000.0;
                                    raspDataToServer.add("p3_" + cutTime + "@@");

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
                                    cutTime = (System.currentTimeMillis() - startTime) / 1000.0;
                                    raspDataToServer.add("p4_" + cutTime + "@@");

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
                                    cutTime = (System.currentTimeMillis() - startTime) / 1000.0;
                                    raspDataToServer.add("p5_" + cutTime + "@@");

                                    Message messageId = handler.obtainMessage();
                                    messageId.what = 5;
                                    handler.sendMessage(messageId);

                                } else if (a == '8') {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            sound8.play(soundId8, 1.0f, 1.0f, 1, 0, 1.0f);

                                        }
                                    }).start();
                                    cutTime = (System.currentTimeMillis() - startTime) / 1000.0;
                                    raspDataToServer.add("p8_" + cutTime + "@@");

                                    Message messageId = handler.obtainMessage();
                                    messageId.what = 8;
                                    handler.sendMessage(messageId);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        while (isRaspOn) {
//                                Log.d("ㅇㅇㅇ","메세지 기다림");

                            if (LobbyActivity.right_in == null) {
                                Log.d("sdffsd", "sfdfsd");
                            }
                            right = String.valueOf(LobbyActivity.right_in.readByte());
                            if (LoginActivity.soundOn == 1) {

                                char a = (char) Integer.parseInt(right);
                                Log.d("tutorialㅇㅇㅇ", "메세지 " + a);

                                if (a == '6') {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            sound6.play(soundId6, 1.0f, 1.0f, 1, 0, 1.0f);

                                        }
                                    }).start();
                                    cutTime = (System.currentTimeMillis() - startTime) / 1000.0;
                                    raspDataToServer.add("p6_" + cutTime + "@@");

                                    Message messageId = handler.obtainMessage();
                                    messageId.what = 6;
                                    handler.sendMessage(messageId);

                                } else if (a == '7') {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            sound7.play(soundId7, 1.0f, 1.0f, 1, 0, 1.0f);

                                        }
                                    }).start();
                                    cutTime = (System.currentTimeMillis() - startTime) / 1000.0;
                                    raspDataToServer.add("p7_" + cutTime + "@@");

                                    Message messageId = handler.obtainMessage();
                                    messageId.what = 7;
                                    handler.sendMessage(messageId);

                                } else if (a == '9') {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            sound9.play(soundId9, 1.0f, 1.0f, 1, 0, 1.0f);

                                        }
                                    }).start();
                                    cutTime = (System.currentTimeMillis() - startTime) / 1000.0;
                                    raspDataToServer.add("p9_" + cutTime + "@@");

                                    Message messageId = handler.obtainMessage();
                                    messageId.what = 9;
                                    handler.sendMessage(messageId);

                                }


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
        LoginActivity.soundOn = 0;
    }

    private void changeHandImage(int index) {
        hand_image.setVisibility(View.GONE);
        hand1_image.setVisibility(View.GONE);
        hand2_image.setVisibility(View.GONE);
        hand3_image.setVisibility(View.GONE);
        hand4_image.setVisibility(View.GONE);
        hand5_image.setVisibility(View.GONE);
        hand6_image.setVisibility(View.GONE);
        hand7_image.setVisibility(View.GONE);
        switch (index) {
            case 1:
                hand1_image.setVisibility(View.VISIBLE);
                break;
            case 2:
                hand2_image.setVisibility(View.VISIBLE);
                break;
            case 3:
                hand3_image.setVisibility(View.VISIBLE);
                break;
            case 4:
                hand4_image.setVisibility(View.VISIBLE);
                break;
            case 5:
                hand5_image.setVisibility(View.VISIBLE);
                break;
            case 6:
                hand6_image.setVisibility(View.VISIBLE);
                break;
            case 7:
                hand7_image.setVisibility(View.VISIBLE);
                break;
        }

    }

    private void changeDrum() {
        hand_image.setVisibility(View.GONE);
        hand1_image.setVisibility(View.GONE);
        hand2_image.setVisibility(View.GONE);
        hand3_image.setVisibility(View.GONE);
        hand4_image.setVisibility(View.GONE);
        hand5_image.setVisibility(View.GONE);
        hand6_image.setVisibility(View.GONE);
        hand7_image.setVisibility(View.GONE);
        drum_default.setVisibility(View.VISIBLE);
    }

    private void changeDrumImage(int index) {
        drum_default.setVisibility(View.GONE);
        drum_left.setVisibility(View.GONE);
        drum_right.setVisibility(View.GONE);
        switch (index) {
            case 8:
                drum_left.setVisibility(View.VISIBLE);
                break;
            case 9:
                drum_right.setVisibility(View.VISIBLE);
                break;
        }

    }
}