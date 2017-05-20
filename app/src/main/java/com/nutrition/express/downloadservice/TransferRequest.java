package com.nutrition.express.downloadservice;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by huang on 4/15/17.
 */

public class TransferRequest implements Parcelable {
    String videoUrl;
    String thumbnailUrl;

    public TransferRequest(String videoUrl, String thumbnailUrl) {
        this.videoUrl = videoUrl;
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.videoUrl);
        dest.writeString(this.thumbnailUrl);
    }

    protected TransferRequest(Parcel in) {
        this.videoUrl = in.readString();
        this.thumbnailUrl = in.readString();
    }

    public static final Parcelable.Creator<TransferRequest> CREATOR = new Parcelable.Creator<TransferRequest>() {
        @Override
        public TransferRequest createFromParcel(Parcel source) {
            return new TransferRequest(source);
        }

        @Override
        public TransferRequest[] newArray(int size) {
            return new TransferRequest[size];
        }
    };
}
