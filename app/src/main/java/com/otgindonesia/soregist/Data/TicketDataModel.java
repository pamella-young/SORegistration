package com.otgindonesia.soregist.Data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TicketDataModel {

    @SerializedName("given_name")
    @Expose
    private String givenName;
    @SerializedName("upline_name")
    @Expose
    private String uplineName;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("photo")
    @Expose
    private String photo;

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getUplineName() {
        return uplineName;
    }

    public void setUplineName(String uplineName) {
        this.uplineName = uplineName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
