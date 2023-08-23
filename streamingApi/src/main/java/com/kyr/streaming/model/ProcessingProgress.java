package com.kyr.streaming.model;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class ProcessingProgress {
    @SerializedName("1080p")
    Double fullHd;

    @SerializedName("720p")
    Double hd;

    @SerializedName("480p")
    Double sd;

    @SerializedName("360p")
    Double ld;

    public ProcessingProgress() {
        this.fullHd = 0.;
        this.hd = 0.;
        this.sd = 0.;
        this.ld = 0.;
    }
}
