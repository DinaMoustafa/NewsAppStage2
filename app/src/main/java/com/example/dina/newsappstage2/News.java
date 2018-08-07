package com.example.dina.newsappstage2;
import java.util.Date;
/**
 * An {@link News} object contains information related to a single news.
 */
public class News {
    // News Section
    private String mNewsSection;
    // News title
    private String mNewsTitle;
    // News date
    private Date mPublishedDate;
    // News writer or Author
    private String mNewsAuthor;
    // News URL
    private String mNewsUrl;


    public News(String section, String title, Date date, String author, String url) {

        this.mNewsSection = section;
        this.mNewsTitle = title;
        this.mPublishedDate = date;
        this.mNewsAuthor = author;
        this.mNewsUrl = url;
    }


    public String getmNewsSection() {

        return mNewsSection;
    }


    public String getmNewsTitle() {

        return mNewsTitle;
    }


    public Date getmPublishedDate() {

        return mPublishedDate;
    }


    public String getmNewsAuthor() {

        return mNewsAuthor;
    }


    public String getmNewsUrl() {

        return mNewsUrl;
    }
}
