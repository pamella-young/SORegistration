package com.otgindonesia.soregist.Data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TicketModel {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private TicketDataModel data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public TicketDataModel getTicketData() {
        return data;
    }

    public void setTicketData(TicketDataModel ticketData) {
        this.data = ticketData;
    }
}
