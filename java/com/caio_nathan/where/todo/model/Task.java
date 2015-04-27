package com.caio_nathan.where.todo.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by caiolopes on 4/25/15.
 */
public class Task implements Parcelable {
    private long taskId;
    private String title;
    private String description;
    private String address;
    private double lat;
    private double lng;
    private boolean showed = false;

    public Task() {

    }

    public Task(String title, String description, String address, double lat, double lng) {
        this.title = title;
        this.description = description;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
    }

    public long getId() { return taskId; }

    public void setId(long taskId) { this.taskId = taskId; }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public boolean isShowed() { return showed; }

    public void setShowed(boolean showed) { this.showed = showed; }

    protected Task(Parcel in) {
        taskId = in.readLong();
        title = in.readString();
        description = in.readString();
        address = in.readString();
        lat = in.readDouble();
        lng = in.readDouble();
        showed = in.readByte() != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(taskId);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(address);
        dest.writeDouble(lat);
        dest.writeDouble(lng);
        dest.writeByte((byte) (showed ? 1 : 0));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Task> CREATOR = new Parcelable.Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };
}