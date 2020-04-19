package com.example.nikol.vesti;

/**
 * Created by nikol on 26-Feb-17.
 */

public class NewsItemModel {


    private String title;
    private String titleSmall;
    private String description;
    private String descriptionSmall;
    private String url;
    private String urlToImage;

    public NewsItemModel(String title, String description, String url, String urlToImage) {

        if (title.length() > 40) {
            this.titleSmall = title.substring(0, 37) + "...";
        } else {
            this.titleSmall = title;
        }

        this.title = title;
        this.description = description;
        this.url = url;
        this.urlToImage = urlToImage;

        if (description.length() > 45) {
            this.descriptionSmall = this.description.substring(0, 42) + "...";
        } else {
            this.descriptionSmall = this.description;
        }

    }

    public String getTitle() { return title; }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public String getUrlToImage() {
        return urlToImage;
    }

    public String getTitleSmall() {
        return titleSmall;
    }

    public String getDescriptionSmall() { return descriptionSmall; }


}
