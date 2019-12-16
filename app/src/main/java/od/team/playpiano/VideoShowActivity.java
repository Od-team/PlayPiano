package od.team.playpiano;

import android.app.Activity;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

public class VideoShowActivity extends AppCompatActivity {

    VideoView videoView;
    TextView text;
    int time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_show);

        videoView = findViewById(R.id.videoView);


        text = findViewById(R.id.text);


        videoView.setVideoPath("http://clips.vorwaerts-gmbh.de/VfE_html5.mp4");

        // 비디오뷰를 커스텀하기 위해서 미디어컨트롤러 객체 생성

        MediaController mediaController = new MediaController(this);

        // 비디오뷰에 연결

        mediaController.setAnchorView(videoView);

        // 안드로이드 res폴더에 raw폴더를 생성 후 재생할 동영상파일을 넣습니다.

        // 경로에 주의할 것

        // 실제 모바일에서 테스트 할 것

        // 위 두가지를 대충 넘겼다가 많은 시간을 허비했다. ㅜㅜ...

        Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.loop_video);

        //비디오뷰의 컨트롤러를 미디어컨트롤로러 사용

        videoView.setMediaController(mediaController);


        //비디오뷰에 재생할 동영상주소를 연결

        videoView.setVideoURI(video);

        //비디오뷰를 포커스하도록 지정

        videoView.requestFocus();

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 3; i > 0; i--) {
                    time = i;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            text.setText(time+"초 뒤에\n영상이\n시작됩니다");
                        }
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        videoView.setVisibility(View.VISIBLE);
                        text.setVisibility(View.INVISIBLE);
                    }
                });
                videoView.start();
            }
        }).start();


        //동영상 재생


    }
}
