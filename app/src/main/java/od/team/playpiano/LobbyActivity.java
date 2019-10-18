package od.team.playpiano;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import od.team.playpiano.Adapter.RoomListAdapter;
import od.team.playpiano.RecyclerItemData.RecyclerRoomListData;

public class LobbyActivity extends AppCompatActivity {

    TextView filter_text;

    View filter_layout;

    Button room_create_btn;

    Spinner people_spinner;
    ArrayList people_num_spinner_list = new ArrayList<>();
    ArrayAdapter<String> people_num_Adapter;

    Spinner play_time_spinner;
    ArrayList play_time_spinner_list = new ArrayList<>();
    ArrayAdapter<String> play_time_Adapter;

    Spinner filter_people_spinner;
    ArrayList filter_people_num_spinner_list = new ArrayList<>();
    ArrayAdapter<String> filter_people_num_Adapter;

    Spinner filter_play_time_spinner;
    ArrayList filter_play_time_spinner_list = new ArrayList<>();
    ArrayAdapter<String> filter_play_time_Adapter;

    RecyclerView room_list_recyclerView;
    RoomListAdapter roomListAdapter;
    LinearLayoutManager linearLayoutManager;
    ArrayList<RecyclerRoomListData> roomListData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        init();
        BtnClickListener();
        roomAlertDialog();
    }

    public void init(){
        filter_text = findViewById(R.id.filter_text);
        filter_layout = findViewById(R.id.filter_layout);
        room_create_btn = findViewById(R.id.room_create_btn);
        filter_people_spinner = findViewById(R.id.filter_people_spinner);
        filter_play_time_spinner = findViewById(R.id.filter_play_time_spinner);
        room_list_recyclerView = findViewById(R.id.room_list_recyclerView);

        linearLayoutManager = new LinearLayoutManager(LobbyActivity.this, LinearLayoutManager.VERTICAL,false);
        roomListAdapter = new RoomListAdapter(LobbyActivity.this, roomListData);
        room_list_recyclerView.setLayoutManager(linearLayoutManager);
        room_list_recyclerView.setAdapter(roomListAdapter);

        for(int i = 1; i < 11; i++){
            roomListData.add(new RecyclerRoomListData(
                    "No."+i,
                    "5학년 " + i + "반 모여라~~",
                    "/ super_man님의 방",
                    "인원 : 3/5",
                    "/ 클래식",
                    "/ 10초"));
        }



    }

    public void BtnClickListener(){
        filter_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(filter_layout.getVisibility() == View.VISIBLE){
                    filter_layout.setVisibility(View.GONE);
                    filterTextChange("필터 ▼");
                }
                else{
                    filter_layout.setVisibility(View.VISIBLE);
                    filterTextChange("필터 ▲");
                    filterSpinnerAdapter();
                }
            }
        });
    }

    public void filterTextChange(final String text){
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        filter_text.setText(text);
                    }
                });
            }
        }).start();
    }

    public void roomAlertDialog(){
        room_create_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LobbyActivity.this);

                View view = LayoutInflater.from(LobbyActivity.this).inflate(R.layout.dialog_room_create,null);

                people_spinner = view.findViewById(R.id.people_spinner);
                play_time_spinner = view.findViewById(R.id.play_time_spinner);

                people_num_spinner_list.clear();
                play_time_spinner_list.clear();

                for(int i = 2 ; i <= 5 ; i += 1)
                    people_num_spinner_list.add(i + "명");

                for(int i = 10 ; i <= 60 ; i += 10)
                    play_time_spinner_list.add(i + "초");

                people_num_Adapter = new ArrayAdapter<String>(getApplicationContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        people_num_spinner_list);

                play_time_Adapter = new ArrayAdapter<String>(getApplicationContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        play_time_spinner_list);

                people_spinner.setAdapter(people_num_Adapter);
                play_time_spinner.setAdapter(play_time_Adapter);


                builder.setView(view);

                AlertDialog alertDialog = builder.create();

                alertDialog.show();
            }
        });
    }

    public void filterSpinnerAdapter(){

        filter_people_num_spinner_list.clear();
        filter_play_time_spinner_list.clear();

        for(int i = 2 ; i <= 5 ; i += 1)
            filter_people_num_spinner_list.add(i + "명");

        for(int i = 10 ; i <= 60 ; i += 10)
            filter_play_time_spinner_list.add(i + "초");

        filter_people_num_Adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item,
                filter_people_num_spinner_list);

        filter_play_time_Adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item,
                filter_play_time_spinner_list);

        filter_people_spinner.setAdapter(filter_people_num_Adapter);
        filter_play_time_spinner.setAdapter(filter_play_time_Adapter);

    }
}
