package com.example.sbwaserviceapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

public class CustomHashMap implements Parcelable {
    private HashMap<String, String> data;

    public CustomHashMap(HashMap<String, String> data) {
        this.data = data;
    }

    public HashMap<String, String> getData() {
        return data;
    }

    // Parcelable methods

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(data);
    }

    public static final Creator<CustomHashMap> CREATOR = new Creator<CustomHashMap>() {
        @Override
        public CustomHashMap createFromParcel(Parcel in) {
            HashMap<String, String> data = (HashMap<String, String>) in.readSerializable();
            return new CustomHashMap(data);
        }

        @Override
        public CustomHashMap[] newArray(int size) {
            return new CustomHashMap[size];
        }
    };
}