

package com.niks.youtube.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.niks.youtube.R;
import com.niks.youtube.activity.VideoActivity;
import com.niks.youtube.models.videos.Video;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {

    private ArrayList<Video> mVideos;
    private int rowLayout;
    private Context mContext;
    //TODO: delete
    private final String API_KEY = "";


    public VideoAdapter(ArrayList<Video> list, int rowLayout, Context context) {
        this.mVideos = list;
        this.rowLayout = rowLayout;
        this.mContext = context;
    }

    public void clearData(){
        if (mVideos != null)
            mVideos.clear();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position)  {

        final Video currentVideo = mVideos.get(position);

        String pubDateString = currentVideo.getDate();
        final String videoTitle = currentVideo.getTitle();

        //retrieve video link
        final String videoId = currentVideo.getVideoId();
        final String link = "https://www.youtube.com/watch?v=" + videoId;

        viewHolder.title.setText(currentVideo.getTitle());
        viewHolder.pubDate.setText(pubDateString);

        Picasso.with(mContext)
                .load(currentVideo.getCoverLink())
                .placeholder(R.drawable.placeholder)
                .fit().centerCrop()
                .into(viewHolder.image);

        //show statistic of the selected video
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* VideoStats videoStats = new VideoStats();
                String url = videoStats.generateStatsRequest(videoId, API_KEY);
                videoStats.execute(url);
                videoStats.onFinish(new VideoStats.OnTaskCompleted() {
                    @Override
                    public void onTaskCompleted(Statistics stats) {

                        String body = "Views: " + stats.getViewCount() + "\n" +
                                      "Like: " + stats.getLikeCount() + "\n" +
                                      "Dislike: " + stats.getDislikeCount() + "\n" +
                                      "Number of comment: " + stats.getCommentCount() + "\n" +
                                      "Number of favourite: " + stats.getFavoriteCount();

                        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
                        dialogBuilder.setTitle("Stats for: \"" + videoTitle + "\"");
                        dialogBuilder.setMessage(body);
                        dialogBuilder.setCancelable(false);
                        dialogBuilder.setNegativeButton(android.R.string.ok,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        dialogBuilder.show();
                    }

                    @Override
                    public void onError() {
                        Toast.makeText(mContext, "Unable to get statistic for this video. Please try again", Toast.LENGTH_SHORT).show();
                    }
                });*/

                startActivity(videoId);
            }
        });

        //open the video on Youtube
        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                startActivity(videoId);
                return false;
            }
        });
    }




    private void  startActivity(String videoId)
    {
        Intent intent = new Intent(mContext,VideoActivity.class);
        Log.e("w222","-------"+videoId);
        intent.putExtra("videoId",videoId);
        mContext.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return mVideos == null ? 0 : mVideos.size();
    }

    public ArrayList<Video> getList() {
        return mVideos;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public TextView pubDate;
        ImageView image;

        public ViewHolder(View itemView) {

            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            pubDate = (TextView) itemView.findViewById(R.id.pubDate);
            image = (ImageView)itemView.findViewById(R.id.image);
        }
    }
}