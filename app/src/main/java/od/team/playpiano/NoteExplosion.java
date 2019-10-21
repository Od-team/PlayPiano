package od.team.playpiano;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Random;

import tyrantgit.explosionfield.ExplosionField;

import static java.lang.Thread.sleep;


public class NoteExplosion extends Activity {

    private ExplosionField mExplosionField;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_explosion);
        mExplosionField = ExplosionField.attach2Window(this);
        addListener(findViewById(R.id.root));

        handler = new Handler();
    }

    private void addListener(View root) {
        if (root instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) root;
            for (int i = 0; i < parent.getChildCount(); i++) {
                addListener(parent.getChildAt(i));
            }
        } else {

            final LinearLayout ll = (LinearLayout)findViewById(R.id.root);
            final int childCount = ll.getChildCount();

            new Thread(){
                @Override
                public void run() {
                    super.run();

                    for (int i = 0; i < childCount; i++) {
//                        int random_index = (int)(Math.random()*childCount);

                        final View v = ll.getChildAt(i);

                        try{
                            sleep(500);
                        }catch (Exception e){
                            StringWriter sw = new StringWriter();
                            e.printStackTrace(new PrintWriter(sw));
                            String ex = sw.toString();

                            Log.d("음표",ex);
                        }

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                mExplosionField.explode(v);
                            }
                        });
                    }

                }
            }.start();



//            root.setClickable(true);
//            root.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    mExplosionField.explode(v);
//                    v.setOnClickListener(null);
//                }
//            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_reset) {
            View root = findViewById(R.id.root);
            reset(root);
            addListener(root);
            mExplosionField.clear();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void reset(View root) {
        if (root instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) root;
            for (int i = 0; i < parent.getChildCount(); i++) {
                reset(parent.getChildAt(i));
            }
        } else {
            root.setScaleX(1);
            root.setScaleY(1);
            root.setAlpha(1);
        }
    }
}
