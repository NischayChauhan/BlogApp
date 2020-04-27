package com.example.nischay.blogapp;

/**
 * Created by Nischay on 4/27/2020.
 */

public class Blog {

    private String heading;
    private String story;
    private String user_id;
    private String image_url;

    public Blog(){
        heading = "DEFAULT heading";
        story = "DEFAULT story";
        user_id = "DEFAULT user_id";
        image_url = "https://firebasestorage.googleapis.com/v0/b/whatsapp-6572f.appspot.com/o/images%2F85f2163f-e9f5-4b10-9df9-96bb447913e2?alt=media&token=2c09a4de-8642-4aab-925a-8de45cde401b";
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

}
