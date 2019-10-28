package od.team.playpiano.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;

import od.team.playpiano.R;
import od.team.playpiano.RecyclerItemData.RecyclerRoomListData;


public class RoomListAdapter extends RecyclerView.Adapter<RoomListAdapter.RoomListViewHolder> {

    ArrayList<RecyclerRoomListData> roomListDataArrayList;
    Context mContext;
    private MyClickListener listener; // 내가 만든 인터페이스 사용하기위해 필요

    public RoomListAdapter(Context mContext, ArrayList<RecyclerRoomListData> roomListDataArrayList) {
        this.mContext = mContext;
        this.roomListDataArrayList = roomListDataArrayList;
    }

    @NonNull
    @Override
    public RoomListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.recycler_item_room_list, viewGroup,false);
        return new RoomListViewHolder(view);
    }

    public interface MyClickListener {
        // 아이템 전체 부분 클릭
        void onItemClicked();
    }

    // 메인에서 setOn클릭리스너
    public void setOnClickListener(MyClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull RoomListViewHolder roomListViewHolder, int position) {

        roomListViewHolder.room_number.setText(roomListDataArrayList.get(position).getRoom_number());
        roomListViewHolder.room_name.setText(roomListDataArrayList.get(position).getRoom_name());
        roomListViewHolder.master_id.setText(roomListDataArrayList.get(position).getMaster_id());
        roomListViewHolder.current_room_people.setText(roomListDataArrayList.get(position).getCurrent_room_people());
        roomListViewHolder.genre.setText(roomListDataArrayList.get(position).getGenre());
        roomListViewHolder.play_time.setText(roomListDataArrayList.get(position).getPlay_time());

        roomListViewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    Log.d("Adapter onClick Log", "Adapter onClick!!");
                    listener.onItemClicked();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return roomListDataArrayList.size();
    }

    public static class RoomListViewHolder extends RecyclerView.ViewHolder {

        TextView room_number;
        TextView room_name;
        TextView master_id;
        TextView current_room_people;
        TextView genre;
        TextView play_time;
        View view;

        public RoomListViewHolder(View view) {
            super(view);
            this.view = view;
            room_number = view.findViewById(R.id.room_number_text);
            room_name = view.findViewById(R.id.room_name_text);
            master_id = view.findViewById(R.id.room_master_id_text);
            current_room_people = view.findViewById(R.id.room_current_people_text);
            genre = view.findViewById(R.id.room_genre_text);
            play_time = view.findViewById(R.id.room_play_time_text);

        }
    }


}

