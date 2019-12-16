package od.team.playpiano.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.ArrayList;

import od.team.playpiano.R;
import od.team.playpiano.RecyclerItemData.FeedbackContentData;


public class FeedBackContentAdapter extends RecyclerView.Adapter<FeedBackContentAdapter.FeedBackHolder> {

    ArrayList<FeedbackContentData> feedbackContentData;
    Context mContext;

    public FeedBackContentAdapter(Context mContext, ArrayList<FeedbackContentData> feedbackContentData) {
        this.mContext = mContext;
        this.feedbackContentData = feedbackContentData;
    }

    @NonNull
    @Override
    public FeedBackHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.feedback_content, viewGroup,false);
        return new FeedBackHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedBackHolder feedBackHolder, int i) {

        feedBackHolder.teacher_name.setText(feedbackContentData.get(i).getTeacher_name());
        feedBackHolder.content.setText(feedbackContentData.get(i).getContent());

    }


    @Override
    public int getItemCount() {
        return feedbackContentData.size();
    }

    public static class FeedBackHolder extends RecyclerView.ViewHolder {

        View view;
        TextView teacher_name;
        TextView content;

        public FeedBackHolder(View view) {
            super(view);
            this.view = view;
            teacher_name = view.findViewById(R.id.teacher_name_text);
            content = view.findViewById(R.id.content_text);
        }
    }


}

