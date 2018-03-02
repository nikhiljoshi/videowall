

package com.niks.youtube.parser;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.niks.youtube.models.videos.High;
import com.niks.youtube.models.videos.Id;
import com.niks.youtube.models.videos.Item;
import com.niks.youtube.models.videos.Main;
import com.niks.youtube.models.videos.Snippet;
import com.niks.youtube.models.videos.Thumbnails;
import com.niks.youtube.models.videos.Video;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class Parser extends AsyncTask<String, Void, String> {

    private OnTaskCompleted onComplete;
    public static final int ORDER_DATE = 1;
    public static final int ORDER_VIEW_COUNT = 2;


    @Deprecated
    public String generateRequest(String channelID, int maxResult, String key) {

        String urlString = "https://www.googleapis.com/youtube/v3/search?part=snippet&channelId=";
        urlString = urlString + channelID + "&maxResults=" + maxResult + "&order=date&key=" + key;
        return urlString;
    }


    public String generateRequest(String channelID, int maxResult, int orderType, String key) {

        String urlString = "https://www.googleapis.com/youtube/v3/search?&part=snippet&channelId=";
        String order = "";
        switch (orderType) {

            case ORDER_DATE:
                order = "date";
                break;

            case ORDER_VIEW_COUNT:
                order = "viewcount";
                break;

            default:
                break;
        }

        urlString = urlString + channelID + "&maxResults=" + maxResult + "&order=" + order + "&key=" + key;
        return urlString;
    }


    public String generateMoreDataRequest(String channelID, int maxResult, int orderType, String key, String nextToken) {

        String urlString = "https://www.googleapis.com/youtube/v3/search?pageToken=";
        String order = "";
        switch (orderType) {

            case ORDER_DATE:
                order = "date";
                break;

            case ORDER_VIEW_COUNT:
                order = "viewcount";
                break;

            default:
                break;
        }

        urlString = urlString + nextToken + "&part=snippet&channelId=" + channelID + "&maxResults="
                + maxResult + "&order=" + order + "&key=" + key;
        return urlString;
    }


    public void onFinish(OnTaskCompleted onComplete) {
        this.onComplete = onComplete;
    }

    @Override
    protected String doInBackground(String... ulr) {

        Response response;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(ulr[0])
                .build();

        try {
            response = client.newCall(request).execute();
            if (response.isSuccessful())
                return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            onComplete.onError();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {

        if (result != null) {

            try {

                ArrayList<Video> videos = new ArrayList<>();

                Gson gson = new GsonBuilder().create();
                Main data = gson.fromJson(result, Main.class);

                //Begin parsing Json data
                List<Item> itemList = data.getItems();
                String nextToken = data.getNextPageToken();

                for (int i = 0; i < itemList.size(); i++) {

                    Item item = itemList.get(i);
                    Snippet snippet = item.getSnippet();

                    Id id = item.getId();

                    Thumbnails image = snippet.getThumbnails();

                    High high = image.getHigh();

                    String title = snippet.getTitle();
                    String videoId = id.getVideoId();
                    String imageLink = high.getUrl();
                    String sDate = snippet.getPublishedAt();

                    Locale.setDefault(Locale.getDefault());
                    TimeZone tz = TimeZone.getDefault();
                    Calendar cal = Calendar.getInstance(tz);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    sdf.setCalendar(cal);
                    cal.setTime(sdf.parse(sDate));
                    Date date = cal.getTime();

                    SimpleDateFormat sdf2 = new SimpleDateFormat("dd MMMM yyyy");
                    String pubDateString = sdf2.format(date);

                    Video tempVideo = new Video(title, videoId, imageLink, pubDateString);
                    videos.add(tempVideo);
                }

                Log.i("YoutubeParser", "Youtube data parsed correctly!");
                onComplete.onTaskCompleted(videos, nextToken);

            } catch (Exception e) {

                e.printStackTrace();
                onComplete.onError();
            }
        } else
            onComplete.onError();
    }


    public interface OnTaskCompleted {
        void onTaskCompleted(ArrayList<Video> list, String nextPageToken);
        void onError();
    }
}