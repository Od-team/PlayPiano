package od.team.playpiano;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class LoopActivity extends AppCompatActivity {

    Toast bpm_toast;
    float play_rate = 1;

    // 메인화면 아이템
    boolean dynamic_flag = true;

    // effect버튼 클릭시 아이템
    SoundPool effect_pool;
    int effect[] = new int[30];
    ImageButton effect_img[] = new ImageButton[30];
    TextView effect_text[] = new TextView[30];
    boolean isClicked[] = new boolean[30];
    int eff[] = new int[30];
    TextView loop_bpm_value_text;
    Button loop_up_btn;
    Button loop_down_btn;
    AlertDialog dialog;
    Button loop_video_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loop_acitivity);

        permissionCheck();
        init();

        dialog();

        loop_video_btn = findViewById(R.id.loop_video_btn);

        loop_video_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoopActivity.this, VideoShowActivity.class);
                startActivity(intent);
            }
        });

        loop_up_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (bpm_toast != null)
                    bpm_toast.cancel();
                bpm_toast = new Toast(LoopActivity.this);
                float u = Float.parseFloat(String.valueOf(loop_bpm_value_text.getText()));
                u += 0.1f;
                if (u > 2.0f) {
                    Toast.makeText(LoopActivity.this, "최대값 입니다.", Toast.LENGTH_SHORT).show();
                } else {
                    String format = String.format("%.1f", u);
                    loop_bpm_value_text.setText(format);
                    bpm_toast = Toast.makeText(LoopActivity.this, "지금부터 " + format + " bpm으로 진행됩니다.", Toast.LENGTH_SHORT);
                    bpm_toast.show();
                    play_rate += 0.1f;
                }
            }
        });

        loop_down_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bpm_toast != null)
                    bpm_toast.cancel();
                bpm_toast = new Toast(LoopActivity.this);
                float u = Float.parseFloat(String.valueOf(loop_bpm_value_text.getText()));
                u -= 0.1f;
                if (u < 0.1f) {
                    Toast.makeText(LoopActivity.this, "최소값 입니다.", Toast.LENGTH_SHORT).show();
                } else {
                    String format = String.format("%.1f", u);
                    loop_bpm_value_text.setText(format);
                    bpm_toast = Toast.makeText(LoopActivity.this, "지금부터 " + format + " bpm으로 진행됩니다.", Toast.LENGTH_SHORT);
                    bpm_toast.show();
                    play_rate -= 0.1f;
                }
            }
        });


    }

    public void init() {

        // 현재화면에 있는 아이템들

        effect_img[1] = findViewById(R.id.effect_img1);
        effect_img[2] = findViewById(R.id.effect_img2);
        effect_img[3] = findViewById(R.id.effect_img3);
        effect_img[4] = findViewById(R.id.effect_img4);
        effect_img[5] = findViewById(R.id.effect_img5);
        effect_img[6] = findViewById(R.id.effect_img6);
        effect_img[7] = findViewById(R.id.effect_img7);
        effect_img[8] = findViewById(R.id.effect_img8);
        effect_img[9] = findViewById(R.id.effect_img9);
        effect_img[10] = findViewById(R.id.effect_img10);
        effect_img[11] = findViewById(R.id.effect_img11);
        effect_img[12] = findViewById(R.id.effect_img12);
        effect_img[13] = findViewById(R.id.effect_img13);
        effect_img[14] = findViewById(R.id.effect_img14);
        effect_img[15] = findViewById(R.id.effect_img15);
        effect_img[16] = findViewById(R.id.effect_img16);
        effect_img[17] = findViewById(R.id.effect_img17);
        effect_img[18] = findViewById(R.id.effect_img18);
        effect_img[19] = findViewById(R.id.effect_img19);
        effect_img[20] = findViewById(R.id.effect_img20);
        effect_img[21] = findViewById(R.id.effect_img21);
        effect_img[22] = findViewById(R.id.effect_img22);
        effect_img[23] = findViewById(R.id.effect_img23);
        effect_img[24] = findViewById(R.id.effect_img24);

        effect_text[1] = findViewById(R.id.effect_1);
        effect_text[2] = findViewById(R.id.effect_2);
        effect_text[3] = findViewById(R.id.effect_3);
        effect_text[4] = findViewById(R.id.effect_4);
        effect_text[5] = findViewById(R.id.effect_5);
        effect_text[6] = findViewById(R.id.effect_6);
        effect_text[7] = findViewById(R.id.effect_7);
        effect_text[8] = findViewById(R.id.effect_8);
        effect_text[9] = findViewById(R.id.effect_9);
        effect_text[10] = findViewById(R.id.effect_10);
        effect_text[11] = findViewById(R.id.effect_11);
        effect_text[12] = findViewById(R.id.effect_12);
        effect_text[13] = findViewById(R.id.effect_13);
        effect_text[14] = findViewById(R.id.effect_14);
        effect_text[15] = findViewById(R.id.effect_15);
        effect_text[16] = findViewById(R.id.effect_16);
        effect_text[17] = findViewById(R.id.effect_17);
        effect_text[18] = findViewById(R.id.effect_18);
        effect_text[19] = findViewById(R.id.effect_19);
        effect_text[20] = findViewById(R.id.effect_20);
        effect_text[21] = findViewById(R.id.effect_21);
        effect_text[22] = findViewById(R.id.effect_22);
        effect_text[23] = findViewById(R.id.effect_23);
        effect_text[24] = findViewById(R.id.effect_24);

        loop_bpm_value_text = findViewById(R.id.loop_bpm_value_text);
        loop_up_btn = findViewById(R.id.loop_up_btn);
        loop_down_btn = findViewById(R.id.loop_down_btn);


    }

    public void mOnClick(View v) {


        switch (v.getId()) {
//                효과음///////////////////////////////////////////////////////////////////////
            // loop가 -1 이면 무한반복 0이면 한번 반복한다.
            case R.id.effect_img1:
                if (isClicked[1] == false) {
                    eff[1] = effect_pool.play(effect[1], 1, 1, 0, -1, play_rate);
                    isClicked[1] = true;
                    effect_img[1].setBackgroundResource(R.drawable.red_one);
                    effect_text[1].setTextColor(Color.RED);

                } else {
                    effect_pool.stop(eff[1]);
                    effect_text[1].setTextColor(Color.WHITE);
                    isClicked[1] = false;
                }

                break;
            case R.id.effect_img2:
                if (isClicked[2] == false) {
                    eff[2] = effect_pool.play(effect[2], 1, 1, 0, -1, play_rate);
                    isClicked[2] = true;
                    effect_img[2].setBackgroundResource(R.drawable.red_one);
                    effect_text[2].setTextColor(Color.RED);
                } else {
                    effect_pool.stop(eff[2]);
                    effect_text[2].setTextColor(Color.WHITE);
                    isClicked[2] = false;
                    effect_img[2].setBackgroundResource(R.drawable.effect_item_drawable_red);

                }
                break;
            case R.id.effect_img3:
                if (isClicked[3] == false) {
                    eff[3] = effect_pool.play(effect[3], 1, 1, 0, -1, play_rate);
                    isClicked[3] = true;
                    effect_img[3].setBackgroundResource(R.drawable.red_one);
                    effect_text[3].setTextColor(Color.RED);

                } else {
                    effect_pool.stop(eff[3]);
                    effect_text[3].setTextColor(Color.WHITE);
                    isClicked[3] = false;
                    effect_img[3].setBackgroundResource(R.drawable.effect_item_drawable_red);

                }
                break;
            case R.id.effect_img4:
                if (isClicked[4] == false) {
                    eff[4] = effect_pool.play(effect[4], 1, 1, 0, -1, play_rate);
                    isClicked[4] = true;
                    effect_img[4].setBackgroundResource(R.drawable.red_one);
                    effect_text[4].setTextColor(Color.RED);

                } else {
                    effect_pool.stop(eff[4]);
                    effect_text[4].setTextColor(Color.WHITE);
                    isClicked[4] = false;

                    effect_img[4].setBackgroundResource(R.drawable.effect_item_drawable_red);

                }
                break;
            case R.id.effect_img5:
                if (isClicked[5] == false) {
                    eff[5] = effect_pool.play(effect[5], 1, 1, 0, -1, play_rate);
                    isClicked[5] = true;
                    effect_img[5].setBackgroundResource(R.drawable.orange_one);
                    effect_text[5].setTextColor(getResources().getColor(R.color.orange));

                } else {
                    effect_pool.stop(eff[5]);
                    effect_text[5].setTextColor(Color.WHITE);
                    isClicked[5] = false;

                    effect_img[5].setBackgroundResource(R.drawable.effect_item_drawable_orange);

                }
                break;
            case R.id.effect_img6:
                if (isClicked[6] == false) {
                    eff[6] = effect_pool.play(effect[6], 1, 1, 0, -1, play_rate);
                    isClicked[6] = true;
                    effect_img[6].setBackgroundResource(R.drawable.orange_one);
                    effect_text[6].setTextColor(getResources().getColor(R.color.orange));

                } else {
                    effect_pool.stop(eff[6]);
                    effect_text[6].setTextColor(Color.WHITE);
                    isClicked[6] = false;
                    effect_img[6].setBackgroundResource(R.drawable.effect_item_drawable_orange);

                }
                break;
            case R.id.effect_img7:
                if (isClicked[7] == false) {
                    eff[7] = effect_pool.play(effect[7], 1, 1, 0, -1, play_rate);
                    isClicked[7] = true;
                    effect_img[7].setBackgroundResource(R.drawable.orange_one);
                    effect_text[7].setTextColor(getResources().getColor(R.color.orange));

                } else {
                    effect_pool.stop(eff[7]);
                    effect_text[7].setTextColor(Color.WHITE);
                    isClicked[7] = false;
                    effect_img[7].setBackgroundResource(R.drawable.effect_item_drawable_orange);

                }
                break;
            case R.id.effect_img8:
                if (isClicked[8] == false) {
                    eff[8] = effect_pool.play(effect[8], 1, 1, 0, -1, play_rate);
                    isClicked[8] = true;
                    effect_img[8].setBackgroundResource(R.drawable.orange_one);
                    effect_text[8].setTextColor(getResources().getColor(R.color.orange));


                } else {
                    effect_pool.stop(eff[8]);
                    effect_text[8].setTextColor(Color.WHITE);
                    isClicked[8] = false;
                    effect_img[8].setBackgroundResource(R.drawable.effect_item_drawable_orange);


                }

                break;
            case R.id.effect_img9:
                if (isClicked[9] == false) {
                    eff[9] = effect_pool.play(effect[9], 1, 1, 0, -1, play_rate);
                    isClicked[9] = true;
                    effect_img[9].setBackgroundResource(R.drawable.yellow_one);
                    effect_text[9].setTextColor(getResources().getColor(R.color.yellow));


                } else {
                    effect_pool.stop(eff[9]);
                    effect_text[9].setTextColor(Color.WHITE);
                    isClicked[9] = false;
                    effect_img[9].setBackgroundResource(R.drawable.effect_item_drawable_yellow);

                }
                break;
            case R.id.effect_img10:
                if (isClicked[10] == false) {
                    eff[10] = effect_pool.play(effect[10], 1, 1, 0, -1, play_rate);
                    isClicked[10] = true;
                    effect_img[10].setBackgroundResource(R.drawable.yellow_one);
                    effect_text[10].setTextColor(getResources().getColor(R.color.yellow));

                } else {
                    effect_pool.stop(eff[10]);
                    effect_text[10].setTextColor(Color.WHITE);
                    isClicked[10] = false;
                    effect_img[10].setBackgroundResource(R.drawable.effect_item_drawable_yellow);

                }
                break;
            case R.id.effect_img11:
                if (isClicked[11] == false) {
                    eff[11] = effect_pool.play(effect[11], 1, 1, 0, -1, play_rate);
                    isClicked[11] = true;
                    effect_img[11].setBackgroundResource(R.drawable.yellow_one);
                    effect_text[11].setTextColor(getResources().getColor(R.color.yellow));

                } else {
                    effect_pool.stop(eff[11]);
                    effect_text[11].setTextColor(Color.WHITE);
                    isClicked[11] = false;
                    effect_img[11].setBackgroundResource(R.drawable.effect_item_drawable_yellow);

                }
                break;
            case R.id.effect_img12:
                if (isClicked[12] == false) {
                    eff[12] = effect_pool.play(effect[12], 1, 1, 0, -1, play_rate);
                    isClicked[12] = true;
                    effect_img[12].setBackgroundResource(R.drawable.yellow_one);
                    effect_text[12].setTextColor(getResources().getColor(R.color.yellow));

                } else {
                    effect_pool.stop(eff[12]);
                    effect_text[12].setTextColor(Color.WHITE);
                    isClicked[12] = false;
                    effect_img[12].setBackgroundResource(R.drawable.effect_item_drawable_yellow);

                }
                break;
            case R.id.effect_img13:
                if (isClicked[13] == false) {
                    eff[13] = effect_pool.play(effect[13], 1, 1, 0, -1, play_rate);
                    isClicked[13] = true;
                    effect_img[13].setBackgroundResource(R.drawable.green_one);
                    effect_text[13].setTextColor(getResources().getColor(R.color.green));

                } else {
                    effect_pool.stop(eff[13]);
                    effect_text[13].setTextColor(Color.WHITE);
                    isClicked[13] = false;
                    effect_img[13].setBackgroundResource(R.drawable.effect_item_drawable_green);

                }
                break;
            case R.id.effect_img14:
                if (isClicked[14] == false) {
                    eff[14] = effect_pool.play(effect[14], 1, 1, 0, -1, play_rate);
                    isClicked[14] = true;
                    effect_img[14].setBackgroundResource(R.drawable.green_one);
                    effect_text[14].setTextColor(getResources().getColor(R.color.green));

                } else {
                    effect_pool.stop(eff[14]);
                    effect_text[14].setTextColor(Color.WHITE);
                    isClicked[14] = false;
                    effect_img[14].setBackgroundResource(R.drawable.effect_item_drawable_green);

                }
                break;
            case R.id.effect_img15:
                if (isClicked[15] == false) {
                    eff[15] = effect_pool.play(effect[15], 1, 1, 0, -1, play_rate);
                    isClicked[15] = true;
                    effect_img[15].setBackgroundResource(R.drawable.green_one);
                    effect_text[15].setTextColor(getResources().getColor(R.color.green));

                } else {
                    effect_pool.stop(eff[15]);
                    effect_text[15].setTextColor(Color.WHITE);
                    isClicked[15] = false;
                    effect_img[15].setBackgroundResource(R.drawable.effect_item_drawable_green);

                }
                break;
            case R.id.effect_img16:
                if (isClicked[16] == false) {
                    eff[16] = effect_pool.play(effect[16], 1, 1, 0, -1, play_rate);
                    isClicked[16] = true;
                    effect_img[16].setBackgroundResource(R.drawable.green_one);
                    effect_text[16].setTextColor(getResources().getColor(R.color.green));

                } else {
                    effect_pool.stop(eff[16]);
                    effect_text[16].setTextColor(Color.WHITE);
                    isClicked[16] = false;
                    effect_img[16].setBackgroundResource(R.drawable.effect_item_drawable_green);

                }
                break;
            case R.id.effect_img17:
                if (isClicked[17] == false) {
                    eff[17] = effect_pool.play(effect[17], 1, 1, 0, -1, play_rate);
                    isClicked[17] = true;
                    effect_img[17].setBackgroundResource(R.drawable.blue_one);
                    effect_text[17].setTextColor(getResources().getColor(R.color.blue));
                } else {
                    effect_pool.stop(eff[17]);
                    effect_text[17].setTextColor(Color.WHITE);
                    isClicked[17] = false;
                    effect_img[17].setBackgroundResource(R.drawable.effect_item_drawable_green);

                }
                break;
            case R.id.effect_img18:
                if (isClicked[18] == false) {
                    eff[18] = effect_pool.play(effect[18], 1, 1, 0, -1, play_rate);
                    isClicked[18] = true;
                    effect_img[18].setBackgroundResource(R.drawable.blue_one);
                    effect_text[18].setTextColor(getResources().getColor(R.color.blue));

                } else {
                    effect_pool.stop(eff[18]);
                    effect_text[18].setTextColor(Color.WHITE);
                    isClicked[18] = false;
                    effect_img[18].setBackgroundResource(R.drawable.effect_item_drawable_green);


                }
                break;
            case R.id.effect_img19:
                if (isClicked[19] == false) {
                    eff[19] = effect_pool.play(effect[19], 1, 1, 0, -1, play_rate);
                    isClicked[19] = true;
                    effect_img[19].setBackgroundResource(R.drawable.blue_one);
                    effect_text[19].setTextColor(getResources().getColor(R.color.blue));

                } else {
                    effect_pool.stop(eff[19]);
                    effect_text[19].setTextColor(Color.WHITE);
                    isClicked[19] = false;
                    effect_img[19].setBackgroundResource(R.drawable.effect_item_drawable_green);


                }
                break;
            case R.id.effect_img20:
                if (isClicked[20] == false) {
                    eff[20] = effect_pool.play(effect[20], 1, 1, 0, -1, play_rate);
                    isClicked[20] = true;
                    effect_img[20].setBackgroundResource(R.drawable.blue_one);
                    effect_text[20].setTextColor(getResources().getColor(R.color.blue));

                } else {
                    effect_pool.stop(eff[20]);
                    effect_text[20].setTextColor(Color.WHITE);
                    isClicked[20] = false;
                    effect_img[20].setBackgroundResource(R.drawable.effect_item_drawable_green);


                }
                break;
            case R.id.effect_img21:
                if (isClicked[21] == false) {
                    eff[21] = effect_pool.play(effect[21], 1, 1, 0, -1, play_rate);
                    isClicked[21] = true;
                    effect_img[21].setBackgroundResource(R.drawable.purple_one);
                    effect_text[21].setTextColor(getResources().getColor(R.color.purple));

                } else {
                    effect_pool.stop(eff[21]);
                    effect_text[21].setTextColor(Color.WHITE);
                    isClicked[21] = false;
                    effect_img[21].setBackgroundResource(R.drawable.effect_item_drawable_purple);


                }
                break;
            case R.id.effect_img22:
                if (isClicked[22] == false) {
                    eff[22] = effect_pool.play(effect[22], 1, 1, 0, -1, play_rate);
                    isClicked[22] = true;
                    effect_img[22].setBackgroundResource(R.drawable.purple_one);
                    effect_text[22].setTextColor(getResources().getColor(R.color.purple));

                } else {
                    effect_pool.stop(eff[22]);
                    effect_text[22].setTextColor(Color.WHITE);
                    isClicked[22] = false;
                    effect_img[22].setBackgroundResource(R.drawable.effect_item_drawable_purple);


                }
                break;
            case R.id.effect_img23:
                if (isClicked[23] == false) {
                    eff[23] = effect_pool.play(effect[23], 1, 1, 0, -1, play_rate);
                    isClicked[23] = true;
                    effect_img[23].setBackgroundResource(R.drawable.purple_one);
                    effect_text[23].setTextColor(getResources().getColor(R.color.purple));

                } else {
                    effect_pool.stop(eff[23]);
                    effect_text[23].setTextColor(Color.WHITE);
                    isClicked[23] = false;
                    effect_img[23].setBackgroundResource(R.drawable.effect_item_drawable_purple);

                }
                break;
            case R.id.effect_img24:
                if (isClicked[24] == false) {
                    eff[24] = effect_pool.play(effect[24], 1, 1, 0, -1, play_rate);
                    isClicked[24] = true;
                    effect_img[24].setBackgroundResource(R.drawable.purple_one);
                    effect_text[24].setTextColor(getResources().getColor(R.color.purple));

                } else {
                    effect_pool.stop(eff[24]);
                    effect_text[24].setTextColor(Color.WHITE);
                    isClicked[24] = false;
                    effect_img[24].setBackgroundResource(R.drawable.effect_item_drawable_purple);


                }
                break;
        }
    }


    public void effectSoundCreate() {
        effect_pool = new SoundPool(24, AudioManager.STREAM_MUSIC, 0);
        effect[1] = effect_pool.load(this, R.raw.effect_1, 1);
        effect[2] = effect_pool.load(this, R.raw.effect_2, 1);
        effect[3] = effect_pool.load(this, R.raw.effect_3, 1);
        effect[4] = effect_pool.load(this, R.raw.effect_4, 1);
        effect[5] = effect_pool.load(this, R.raw.effect_5, 1);
        effect[6] = effect_pool.load(this, R.raw.effect_6, 1);
        effect[7] = effect_pool.load(this, R.raw.effect_7, 1);
        effect[8] = effect_pool.load(this, R.raw.effect_8, 1);
        effect[9] = effect_pool.load(this, R.raw.effect_9, 1);
        effect[10] = effect_pool.load(this, R.raw.effect_10, 1);
        effect[11] = effect_pool.load(this, R.raw.effect_11, 1);
        effect[12] = effect_pool.load(this, R.raw.effect_12, 1);
        effect[13] = effect_pool.load(this, R.raw.effect_13, 1);
        effect[14] = effect_pool.load(this, R.raw.effect_14, 1);
        effect[15] = effect_pool.load(this, R.raw.effect_15, 1);
        effect[16] = effect_pool.load(this, R.raw.effect_16, 1);
        effect[17] = effect_pool.load(this, R.raw.effect_17, 1);
        effect[18] = effect_pool.load(this, R.raw.effect_18, 1);
        effect[19] = effect_pool.load(this, R.raw.effect_19, 1);
        effect[20] = effect_pool.load(this, R.raw.effect_20, 1);
        effect[21] = effect_pool.load(this, R.raw.effect_21, 1);
        effect[22] = effect_pool.load(this, R.raw.effect_22, 1);
        effect[23] = effect_pool.load(this, R.raw.effect_23, 1);
        effect[24] = effect_pool.load(this, R.raw.effect_24, 1);

    }

    public void permissionCheck() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, 1);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        dynamic_flag = true;
        effectSoundCreate();
    }

    @Override
    protected void onPause() {
        super.onPause();
        dynamic_flag = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (effect_pool != null) {
            effect_pool.release();
            effect_pool = null;
        }
    }

    public void dialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(LoopActivity.this);

        builder.setTitle("배경음악 만들기");
        builder.setMessage("사용자가 원하는 배경음악을 만들 수 있습니다.\n\n** 현재 노래는 작곡 할 수는 있으나 저장은 불가능 합니다");

        builder.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog = builder.create();
        dialog.show();
    }

}