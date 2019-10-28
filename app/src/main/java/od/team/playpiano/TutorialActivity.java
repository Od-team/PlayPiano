package od.team.playpiano;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class TutorialActivity extends Activity {

    TextView interval_text;   //계이름 표시해주는 텍스트. 손가락으로 버튼을 눌러 소리를 낼 때 마다 누른 손가락에 맞는 계이름 표시
    ImageView hand_image;     //손모양 이미지, 손가락으로 버튼을 눌러 소리를 낼 때 마다 누른 손가락에 맞는 위치에 파란색 표시된 이미지로 변경


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_tutorial);

        hand_image = findViewById(R.id.hand_imageView);
        interval_text = findViewById(R.id.interval_textView);

        //소켓 정보 받아서 소리 낼 수 있게 하면 됨


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
        finish();
    }
}