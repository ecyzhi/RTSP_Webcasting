package com.mercy.alpacalive.adapter;

public class LiveList {
    private int userID;
    private int eventID;
    private String roomCode;
    private String roomName;
    private int viewerCount;

    public LiveList(int userID, int eventID, String roomCode, String roomName, int viewerCount) {
        this.userID = userID;
        this.eventID = eventID;
        this.roomCode = roomCode;
        this.roomName = roomName;
        this.viewerCount = viewerCount;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int geteventID() {
        return eventID;
    }

    public void seteventID(int eventID) {
        this.eventID = eventID;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }


    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public int getViewerCount() {
        return viewerCount;
    }

    public void setViewerCount(int viewerCount) {
        this.viewerCount = viewerCount;
    }

    @Override
    public String toString() {
        return "LiveList{" +
                "userID=" + userID +
                ", eventID=" + eventID +
                ", roomCode='" + roomCode + '\'' +
                ", roomName='" + roomName + '\'' +
                ", viewerCount=" + viewerCount +
                '}';
    }
}
