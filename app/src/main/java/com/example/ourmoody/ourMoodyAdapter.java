package com.example.ourmoody;
import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.ourmoody.util.Constants;

import java.util.ArrayList;

public class ourMoodyAdapter extends RecyclerView.Adapter<ourMoodyAdapter.MoodViewHolder> {

    private static final String TAG = "ourMoodyAdapter";
    private Context mContext;
    private int mCurrentDate;
    private ArrayList<Integer> mMoods;
    private ArrayList<String> mComments;

    // Constructor
    public ourMoodyAdapter(Context context, int currentDay, ArrayList<Integer> moods, ArrayList<String> comments) {
        this.mContext = context;
        this.mCurrentDate = currentDay;
        this.mMoods = moods;
        this.mComments = comments;
    }

    @Override
    public MoodViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_mood, viewGroup, false);
        return new MoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MoodViewHolder moodViewHolder, int i) {
        switch (i) {
            case 1:
                moodViewHolder.daysTextView.setText(R.string.yesterday);
                break;
            case 0:
                moodViewHolder.daysTextView.setText(R.string.today);
                break;
            default:
                String daysAgoText = i + " " + mContext.getString(R.string.days_ago);
                moodViewHolder.daysTextView.setText(daysAgoText);
        }


        int mood = mMoods.get(i);
        moodViewHolder.leftFrameLayout.setBackgroundResource(Constants.moodColorsArray[mood]);



        // if there's a comment, show the icon and a toast on click
        final String comment = mComments.get(i);
        if (comment != null && !comment.isEmpty()) {
            moodViewHolder.commentButton.setVisibility(View.VISIBLE);
            moodViewHolder.commentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, comment, Toast.LENGTH_LONG).show();
                }
            });
        } else {
            moodViewHolder.commentButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {

        return mMoods.size();
    }

    public class MoodViewHolder extends RecyclerView.ViewHolder {
        private FrameLayout leftFrameLayout;
        private FrameLayout rightFrameLayout;
        private ImageButton commentButton;
        private TextView daysTextView;

        public MoodViewHolder(View itemView) {
            super(itemView);

            leftFrameLayout = itemView.findViewById(R.id.left_frame_layout);
            rightFrameLayout = itemView.findViewById(R.id.right_frame_layout);
            commentButton = itemView.findViewById(R.id.btn_show_comment);
            daysTextView = itemView.findViewById(R.id.tv_days);
        }
    }
}