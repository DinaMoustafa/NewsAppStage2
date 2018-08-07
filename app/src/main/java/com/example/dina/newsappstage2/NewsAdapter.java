package com.example.dina.newsappstage2;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
public class NewsAdapter extends ArrayAdapter<News> {
    public NewsAdapter(Context context, List<News> newsList) {

        super(context, 0, newsList);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.news_list_item, parent, false);
        }
        News currentNews = getItem(position);
        TextView newsSectionView = listItemView.findViewById(R.id.news_section);
        newsSectionView.setText(currentNews.getmNewsSection());
        TextView newsDate = listItemView.findViewById(R.id.news_date);
        TextView newsTime = listItemView.findViewById(R.id.news_time);
        TextView dateView = null;
        TextView timeView = null;
        // check if the date exists first
        if (currentNews.getmPublishedDate() != null) {
            dateView = listItemView.findViewById(R.id.news_date);
            String formattedDate = formatDate(currentNews.getmPublishedDate()).concat(",");
            dateView.setText(formattedDate);
            timeView = listItemView.findViewById(R.id.news_time);
            String formattedTime = formatTime(currentNews.getmPublishedDate());
            timeView.setText(formattedTime);
            dateView.setVisibility(View.VISIBLE);
            timeView.setVisibility(View.VISIBLE);
        } else {
            dateView.setVisibility(View.GONE);
            timeView.setVisibility(View.GONE);
        }
        TextView newsTitleView = listItemView.findViewById(R.id.news_title);
        newsTitleView.setText(currentNews.getmNewsTitle());
        TextView newsAuthor = listItemView.findViewById(R.id.news_author);
        //check if the author name exists first
        if (currentNews.getmNewsAuthor() != "") {
            newsAuthor.setText(currentNews.getmNewsAuthor());
            newsAuthor.setVisibility(View.VISIBLE);
        } else {
            newsAuthor.setVisibility(View.GONE);
        }
        return listItemView;
    }


    public String formatDate(Date date) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd,yyyy");
        return dateFormat.format(date);
    }


    /**
     * Return the formatted date string (i.e. "4:30 PM") from a Date object.
     */
    private String formatTime(Date dateObject) {

        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return timeFormat.format(dateObject);
    }
}
