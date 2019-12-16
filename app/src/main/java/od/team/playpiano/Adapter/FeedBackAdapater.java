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

import od.team.playpiano.LobbyActivity;
import od.team.playpiano.R;
import od.team.playpiano.RecyclerItemData.RecyclerRoomListData;


public class FeedBackAdapater extends RecyclerView.Adapter<FeedBackAdapater.FeedBackViewHolder> {

    ArrayList<String> teacher_name_list;
    Context mContext;
    private MyClickListener listener; // 내가 만든 인터페이스 사용하기위해 필요

    public FeedBackAdapater(Context mContext, ArrayList<String> teacher_name_list) {
        this.mContext = mContext;
        this.teacher_name_list = teacher_name_list;
    }

    @NonNull
    @Override
    public FeedBackViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.feedback_recyclerview, viewGroup,false);
        return new FeedBackViewHolder(view);
    }

    public interface MyClickListener {
        // 아이템 전체 부분 클릭
        void onItemClicked(int position,ArrayList<String> teacher_name_list);
    }

    // 메인에서 setOn클릭리스너
    public void setOnClickListener(MyClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull FeedBackViewHolder roomListViewHolder, final int position) {

        if(LobbyActivity.user_job.equals("student")){
            roomListViewHolder.teacher_name.setText(teacher_name_list.get(position)+" 선생님");
        }
        else{
            roomListViewHolder.teacher_name.setText(teacher_name_list.get(position)+" 학생");
        }

        roomListViewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    Log.d("Adapter onClick Log", "Adapter onClick!!");
                    listener.onItemClicked(position, teacher_name_list);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return teacher_name_list.size();
    }

    public static class FeedBackViewHolder extends RecyclerView.ViewHolder {

        View view;
        TextView teacher_name;

        public FeedBackViewHolder(View view) {
            super(view);
            this.view = view;
            teacher_name = view.findViewById(R.id.teacher_name_textView);
        }
    }


}

