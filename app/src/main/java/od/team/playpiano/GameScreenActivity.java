package od.team.playpiano;

import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.Timer;
import java.util.TimerTask;

public class GameScreenActivity extends AppCompatActivity {

    VideoView videoView;
    int counter = 6;

    TextView counter_textView;
    ImageView counter_imageView;

    Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_screen);


        handler = new Handler();

        counter_imageView = (ImageView)findViewById(R.id.counter_imageView);
        counter_textView = (TextView)findViewById(R.id.counter_textView);


        videoView = (VideoView)findViewById(R.id.videoView);
        videoView.setVideoPath("android.resource://od.team.playpiano/"+R.raw.musical_notes_floating_video);
        videoView.setVisibility(View.INVISIBLE);

        TimerTask timertask = new TimerTask() {
            @Override
            public void run() {


                counter--;

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        counter_textView.setText(String.valueOf(counter));
                    }
                });

                try{
                    Thread.sleep(1000);
                }catch (Exception e){

                }

                if(counter == 0){
                    counter_textView.setVisibility(View.GONE);
                    counter_imageView.setVisibility(View.GONE);

                    videoView.setVisibility(View.VISIBLE);
                    videoView.start();
                }

            }
        };

        timertask.run();

    }



}
