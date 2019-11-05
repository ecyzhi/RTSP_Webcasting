package com.mercy.alpacalive.adapter;

public class EventList {
    private String eventID;
    private String eventName;
    private String eventLocation;
    private String eventStartDate;
    private String eventEndDate;
    private String eventDetail;
    private int eventStreamCount;

    public EventList(String eventID, String eventName, String eventLocation, String eventStartDate, String eventEndDate, String eventDetail, int eventStreamCount) {
        this.eventID = eventID;
        this.eventName = eventName;
        this.eventLocation = eventLocation;
        this.eventStartDate = eventStartDate;
        this.eventEndDate = eventEndDate;
        this.eventDetail = eventDetail;
        this.eventStreamCount = eventStreamCount;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public String getEventStartDate() {
        return eventStartDate;
    }

    public void setEventStartDate(String eventStartDate) {
        this.eventStartDate = eventStartDate;
    }

    public String getEventEndDate() {
        return eventEndDate;
    }

    public void setEventEndDate(String eventEndDate) {
        this.eventEndDate = eventEndDate;
    }

    public String getEventDetail() {
        return eventDetail;
    }

    public void setEventDetail(String eventDetail) {
        this.eventDetail = eventDetail;
    }

    public int getEventStreamCount() {
        return eventStreamCount;
    }

    public void setEventStreamCount(int eventStreamCount) {
        this.eventStreamCount = eventStreamCount;
    }

    @Override
    public String toString() {
        return "EventList{" +
                "eventID='" + eventID + '\'' +
                ", eventName='" + eventName + '\'' +
                ", eventLocation='" + eventLocation + '\'' +
                ", eventStartDate=" + eventStartDate +
                ", eventEndDate=" + eventEndDate +
                ", eventDetail='" + eventDetail + '\'' +
                ", eventStreamCount=" + eventStreamCount +
                '}';
    }
}
