package od.team.playpiano;

import android.app.Activity;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class TutorialActivity extends Activity {

    TextView interval_text;   //계이름 표시해주는 텍스트. 손가락으로 버튼을 눌러 소리를 낼 때 마다 누른 손가락에 맞는 계이름 표시
    ImageView hand_image;     //손모양 이미지, 손가락으로 버튼을 눌러 소리를 낼 때 마다 누른 손가락에 맞는 위치에 파란색 표시된 이미지로 변경
    ImageView hand_left1_image;
    ImageView hand_left2_image;
    ImageView hand_left3_image;
    ImageView hand_left4_image;
    ImageView hand_left5_image;
    ImageView hand_right1_image;
    ImageView hand_right2_image;
    ImageView hand_right3_image;
    ImageView hand_right4_image;
    ImageView hand_right5_image;
    ImageView drum_default_image;
    ImageView drum_left_image;
    ImageView drum_right_image;
    Button complete_tutorial_btn;

    SoundPool piano_sound1, piano_sound2, piano_sound3, piano_sound4, piano_sound5,
            piano_sound6, piano_sound7, piano_sound8, piano_sound9, piano_sound10,
            drum_sound1, drum_sound2;
    int piano_soundId1, piano_soundId2, piano_soundId3, piano_soundId4, piano_soundId5,
            piano_soundId6, piano_soundId7, piano_soundId8, piano_soundId9, piano_soundId10,
            drum_soundId1, drum_soundId2;

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
        hand_left1_image = findViewById(R.id.hand_left1_imageView);
        hand_left2_image = findViewById(R.id.hand_left2_imageView);
        hand_left3_image = findViewById(R.id.hand_left3_imageView);
        hand_left4_image = findViewById(R.id.hand_left4_imageView);
        hand_left5_image = findViewById(R.id.hand_left5_imageView);
        hand_right1_image = findViewById(R.id.hand_right1_imageView);
        hand_right2_image = findViewById(R.id.hand_right2_imageView);
        hand_right3_image = findViewById(R.id.hand_right3_imageView);
        hand_right4_image = findViewById(R.id.hand_right4_imageView);
        hand_right5_image = findViewById(R.id.hand_right5_imageView);
        interval_text = findViewById(R.id.interval_textView);

        drum_default_image = findViewById(R.id.drum_default_imageView);
        drum_left_image = findViewById(R.id.drum_left_imageView);
        drum_right_image = findViewById(R.id.drum_right_imageView);
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
                        interval_text.setText("솔");
                        interval_text.setVisibility(View.VISIBLE);
                        //Glide.with(TutorialActivity.this).load(R.drawable.hand_1).into(hand_image);
                        changeHandImage(1);
                        break;
                    case 2:
                        Log.d("핸들러", String.valueOf(msg.what));
                        interval_text.setText("라");
                        interval_text.setVisibility(View.VISIBLE);
                        //Glide.with(TutorialActivity.this).load(R.drawable.hand_2).into(hand_image);
                        changeHandImage(2);
                        break;
                    case 3:
                        Log.d("핸들러", String.valueOf(msg.what));
                        interval_text.setText("시");
                        interval_text.setVisibility(View.VISIBLE);
                        //Glide.with(TutorialActivity.this).load(R.drawable.hand_3).into(hand_image);
                        changeHandImage(3);
                        break;
                    case 4:
                        Log.d("핸들러", String.valueOf(msg.what));
                        interval_text.setText("도");
                        interval_text.setVisibility(View.VISIBLE);
                        //Glide.with(TutorialActivity.this).load(R.drawable.hand_4).into(hand_image);
                        changeHandImage(4);
                        break;
                    case 5:
                        Log.d("핸들러", String.valueOf(msg.what));
                        interval_text.setText("레");
                        interval_text.setVisibility(View.VISIBLE);
                        //Glide.with(TutorialActivity.this).load(R.drawable.hand_5).into(hand_image);
                        changeHandImage(5);
                        break;
                    case 6:
                        Log.d("핸들러", String.valueOf(msg.what));
                        interval_text.setText("쿵");
                        interval_text.setVisibility(View.VISIBLE);
                        //Glide.with(TutorialActivity.this).load(R.drawable.hand_5).into(hand_image);
                        changeDrum("left");
                        break;
                    case 11:
                        Log.d("핸들러", String.valueOf(msg.what));
                        interval_text.setText("미");
                        interval_text.setVisibility(View.VISIBLE);
                        //Glide.with(TutorialActivity.this).load(R.drawable.hand_5).into(hand_image);
                        changeHandImage(6);
                        break;
                    case 12:
                        Log.d("핸들러", String.valueOf(msg.what));
                        interval_text.setText("파");
                        interval_text.setVisibility(View.VISIBLE);
                        //Glide.with(TutorialActivity.this).load(R.drawable.hand_5).into(hand_image);
                        changeHandImage(7);
                        break;
                    case 13:
                        Log.d("핸들러", String.valueOf(msg.what));
                        interval_text.setText("솔");
                        interval_text.setVisibility(View.VISIBLE);
                        //Glide.with(TutorialActivity.this).load(R.drawable.hand_5).into(hand_image);
                        changeHandImage(8);
                        break;
                    case 14:
                        Log.d("핸들러", String.valueOf(msg.what));
                        interval_text.setText("라");
                        interval_text.setVisibility(View.VISIBLE);
                        //Glide.with(TutorialActivity.this).load(R.drawable.hand_5).into(hand_image);
                        changeHandImage(9);
                        break;
                    case 15:
                        Log.d("핸들러", String.valueOf(msg.what));
                        interval_text.setText("시");
                        interval_text.setVisibility(View.VISIBLE);
                        //Glide.with(TutorialActivity.this).load(R.drawable.hand_5).into(hand_image);
                        changeHandImage(10);
                        break;
                    case 16:
                        Log.d("핸들러", String.valueOf(msg.what));
                        interval_text.setText("쿵");
                        interval_text.setVisibility(View.VISIBLE);
                        //Glide.with(TutorialActivity.this).load(R.drawable.hand_5).into(hand_image);
                        changeDrum("right");
                        break;
                    case 17:
                        Log.d("핸들러", String.valueOf(msg.what));
                        interval_text.setText("");
                        interval_text.setVisibility(View.GONE);
                        //Glide.with(TutorialActivity.this).load(R.drawable.hand_5).into(hand_image);
                        changeInstrument();
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
            changeInstrument();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LoginActivity.soundOn = 0;
    }

    public void soundPoolInit() {
        piano_sound1 = new SoundPool(1, AudioManager.STREAM_ALARM, 0);// maxStreams, streamType, srcQuality
        piano_sound2 = new SoundPool(1, AudioManager.STREAM_ALARM, 0);// maxStreams, streamType, srcQuality
        piano_sound3 = new SoundPool(1, AudioManager.STREAM_ALARM, 0);// maxStreams, streamType, srcQuality
        piano_sound4 = new SoundPool(1, AudioManager.STREAM_ALARM, 0);// maxStreams, streamType, srcQuality
        piano_sound5 = new SoundPool(1, AudioManager.STREAM_ALARM, 0);// maxStreams, streamType, srcQuality
        piano_sound6 = new SoundPool(1, AudioManager.STREAM_ALARM, 0);// maxStreams, streamType, srcQuality
        piano_sound7 = new SoundPool(1, AudioManager.STREAM_ALARM, 0);// maxStreams, streamType, srcQuality
        piano_sound8 = new SoundPool(1, AudioManager.STREAM_ALARM, 0);// maxStreams, streamType, srcQuality
        piano_sound9 = new SoundPool(1, AudioManager.STREAM_ALARM, 0);// maxStreams, streamType, srcQuality
        piano_sound10 = new SoundPool(1, AudioManager.STREAM_ALARM, 0);// maxStreams, streamType, srcQuality
        drum_sound1 = new SoundPool(1, AudioManager.STREAM_ALARM, 0);// maxStreams, streamType, srcQuality
        drum_sound2 = new SoundPool(1, AudioManager.STREAM_ALARM, 0);// maxStreams, streamType, srcQuality

        piano_soundId1 = piano_sound1.load(TutorialActivity.this, R.raw.p1, 1);
        piano_soundId2 = piano_sound2.load(TutorialActivity.this, R.raw.p2, 1);
        piano_soundId3 = piano_sound3.load(TutorialActivity.this, R.raw.p3, 1);
        piano_soundId4 = piano_sound4.load(TutorialActivity.this, R.raw.p4, 1);
        piano_soundId5 = piano_sound5.load(TutorialActivity.this, R.raw.p5, 1);
        piano_soundId6 = piano_sound6.load(TutorialActivity.this, R.raw.p6, 1);
        piano_soundId7 = piano_sound7.load(TutorialActivity.this, R.raw.p7, 1);
        piano_soundId8 = piano_sound8.load(TutorialActivity.this, R.raw.p8, 1);
        piano_soundId9 = piano_sound9.load(TutorialActivity.this, R.raw.p9, 1);
        piano_soundId10 = piano_sound10.load(TutorialActivity.this, R.raw.p10, 1);
        drum_soundId1 = drum_sound1.load(TutorialActivity.this, R.raw.drum_left, 1);
        drum_soundId2 = drum_sound2.load(TutorialActivity.this, R.raw.drum_right, 1);
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

                                if(LobbyActivity.CurrentInstrument == LobbyActivity.PIANO_FLAG){
                                    if (a == '1') {

                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                piano_sound1.play(piano_soundId1, 1.0f, 1.0f, 1, 0, 1.0f);
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
                                                piano_sound2.play(piano_soundId2, 1.0f, 1.0f, 1, 0, 1.0f);

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
                                                piano_sound3.play(piano_soundId3, 1.0f, 1.0f, 1, 0, 1.0f);

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
                                                piano_sound4.play(piano_soundId4, 1.0f, 1.0f, 1, 0, 1.0f);

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
                                                piano_sound5.play(piano_soundId5, 1.0f, 1.0f, 1, 0, 1.0f);

                                            }
                                        }).start();
                                        cutTime = (System.currentTimeMillis() - startTime) / 1000.0;
                                        raspDataToServer.add("p5_" + cutTime + "@@");

                                        Message messageId = handler.obtainMessage();
                                        messageId.what = 5;
                                        handler.sendMessage(messageId);

                                    }
                                }
                                else if(LobbyActivity.CurrentInstrument == LobbyActivity.DRUM_FLAG){
                                    if (a == '6') {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                drum_sound1.play(drum_soundId1, 1.0f, 1.0f, 1, 0, 1.0f);

                                            }
                                        }).start();
                                        cutTime = (System.currentTimeMillis() - startTime) / 1000.0;
                                        raspDataToServer.add("p8_" + cutTime + "@@");

                                        Message messageId = handler.obtainMessage();
                                        messageId.what = 6;
                                        handler.sendMessage(messageId);
                                    }
                                }

                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();


            /** 오른손 **/
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

                                if(LobbyActivity.CurrentInstrument == LobbyActivity.PIANO_FLAG){

                                    if (a == '1') {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                piano_sound6.play(piano_soundId6, 1.0f, 1.0f, 1, 0, 1.0f);

                                            }
                                        }).start();
                                        cutTime = (System.currentTimeMillis() - startTime) / 1000.0;
                                        raspDataToServer.add("p6_" + cutTime + "@@");

                                        Message messageId = handler.obtainMessage();
                                        messageId.what = 11;
                                        handler.sendMessage(messageId);

                                    } else if (a == '2') {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                piano_sound7.play(piano_soundId7, 1.0f, 1.0f, 1, 0, 1.0f);

                                            }
                                        }).start();
                                        cutTime = (System.currentTimeMillis() - startTime) / 1000.0;
                                        raspDataToServer.add("p7_" + cutTime + "@@");

                                        Message messageId = handler.obtainMessage();
                                        messageId.what = 12;
                                        handler.sendMessage(messageId);

                                    } else if (a == '3') {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                piano_sound8.play(piano_soundId8, 1.0f, 1.0f, 1, 0, 1.0f);

                                            }
                                        }).start();
                                        cutTime = (System.currentTimeMillis() - startTime) / 1000.0;
                                        raspDataToServer.add("p9_" + cutTime + "@@");

                                        Message messageId = handler.obtainMessage();
                                        messageId.what = 13;
                                        handler.sendMessage(messageId);

                                    } else if (a == '4') {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                piano_sound9.play(piano_soundId9, 1.0f, 1.0f, 1, 0, 1.0f);

                                            }
                                        }).start();
                                        cutTime = (System.currentTimeMillis() - startTime) / 1000.0;
                                        raspDataToServer.add("p9_" + cutTime + "@@");

                                        Message messageId = handler.obtainMessage();
                                        messageId.what = 14;
                                        handler.sendMessage(messageId);

                                    } else if (a == '5') {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                piano_sound10.play(piano_soundId10, 1.0f, 1.0f, 1, 0, 1.0f);

                                            }
                                        }).start();
                                        cutTime = (System.currentTimeMillis() - startTime) / 1000.0;
                                        raspDataToServer.add("p9_" + cutTime + "@@");

                                        Message messageId = handler.obtainMessage();
                                        messageId.what = 15;
                                        handler.sendMessage(messageId);

                                    } else if (a == '7') {
                                        Message messageId = handler.obtainMessage();
                                        messageId.what = 17;
                                        handler.sendMessage(messageId);

                                    }
                                }
                                else if(LobbyActivity.CurrentInstrument == LobbyActivity.DRUM_FLAG){
                                    if (a == '6') {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                drum_sound2.play(drum_soundId2, 1.0f, 1.0f, 1, 0, 1.0f);

                                            }
                                        }).start();
                                        cutTime = (System.currentTimeMillis() - startTime) / 1000.0;
                                        raspDataToServer.add("p9_" + cutTime + "@@");

                                        Message messageId = handler.obtainMessage();
                                        messageId.what = 16;
                                        handler.sendMessage(messageId);

                                    }else if (a == '7') {
                                        Message messageId = handler.obtainMessage();
                                        messageId.what = 17;
                                        handler.sendMessage(messageId);

                                    }
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
        hand_left1_image.setVisibility(View.GONE);
        hand_left2_image.setVisibility(View.GONE);
        hand_left3_image.setVisibility(View.GONE);
        hand_left4_image.setVisibility(View.GONE);
        hand_left5_image.setVisibility(View.GONE);
        hand_right1_image.setVisibility(View.GONE);
        hand_right2_image.setVisibility(View.GONE);
        hand_right3_image.setVisibility(View.GONE);
        hand_right4_image.setVisibility(View.GONE);
        hand_right5_image.setVisibility(View.GONE);
        switch (index) {
            case 1:
                hand_left1_image.setVisibility(View.VISIBLE);
                break;
            case 2:
                hand_left2_image.setVisibility(View.VISIBLE);
                break;
            case 3:
                hand_left3_image.setVisibility(View.VISIBLE);
                break;
            case 4:
                hand_left4_image.setVisibility(View.VISIBLE);
                break;
            case 5:
                hand_left5_image.setVisibility(View.VISIBLE);
                break;
            case 6:
                hand_right1_image.setVisibility(View.VISIBLE);
                break;
            case 7:
                hand_right2_image.setVisibility(View.VISIBLE);
                break;
            case 8:
                hand_right3_image.setVisibility(View.VISIBLE);
                break;
            case 9:
                hand_right4_image.setVisibility(View.VISIBLE);
                break;
            case 10:
                hand_right5_image.setVisibility(View.VISIBLE);
                break;
        }

    }

    private void changeInstrument() {
        if(LobbyActivity.CurrentInstrument == LobbyActivity.PIANO_FLAG){
            LobbyActivity.CurrentInstrument = LobbyActivity.DRUM_FLAG;
            hand_image.setVisibility(View.GONE);
            hand_left1_image.setVisibility(View.GONE);
            hand_left2_image.setVisibility(View.GONE);
            hand_left3_image.setVisibility(View.GONE);
            hand_left4_image.setVisibility(View.GONE);
            hand_left5_image.setVisibility(View.GONE);
            hand_right1_image.setVisibility(View.GONE);
            hand_right2_image.setVisibility(View.GONE);
            hand_right3_image.setVisibility(View.GONE);
            hand_right4_image.setVisibility(View.GONE);
            hand_right5_image.setVisibility(View.GONE);
            drum_default_image.setVisibility(View.VISIBLE);

            interval_text.setText("완료");

        }else if(LobbyActivity.CurrentInstrument == LobbyActivity.DRUM_FLAG){
            LobbyActivity.CurrentInstrument = LobbyActivity.PIANO_FLAG;
            drum_default_image.setVisibility(View.GONE);
            drum_left_image.setVisibility(View.GONE);
            drum_right_image.setVisibility(View.GONE);
            hand_image.setVisibility(View.VISIBLE);
        }
    }

    private void changeDrum(String index) {
        drum_default_image.setVisibility(View.GONE);
        drum_left_image.setVisibility(View.GONE);
        drum_right_image.setVisibility(View.GONE);
        switch (index) {
            case "left":
                drum_left_image.setVisibility(View.VISIBLE);
                break;
            case "right":
                drum_right_image.setVisibility(View.VISIBLE);
                break;
        }

    }
}