package com.kyr.streaming.model;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class ProcessingProgress {
    @SerializedName("1080p")
    String fullHd;

    @SerializedName("720p")
    String hd;

    @SerializedName("480p")
    String sd;

    @SerializedName("360p")
    String ld;

    @SerializedName("message")
    String message;

    public ProcessingProgress() {
        this.fullHd = "0";
        this.hd = "0";
        this.sd = "0";
        this.ld = "0";
    }
}
