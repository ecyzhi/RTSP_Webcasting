package com.mercy.alpacalive.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.mercy.alpacalive.R;

import java.util.List;

public class EventListAdapter extends ArrayAdapter<EventList> {


    public EventListAdapter(@NonNull Context context, int resource, @NonNull List<EventList> list) {
        super(context, resource, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EventList eventList = getItem(position);

        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.eventlisting_item, parent, false);

        TextView eventName, eventLoc, startDate, endDate, details, roomCount;

        eventName = rowView.findViewById(R.id.eventName);
        eventLoc = rowView.findViewById(R.id.eventLocation);
        startDate = rowView.findViewById(R.id.eventStart);
        endDate = rowView.findViewById(R.id.eventEnd);
        details = rowView.findViewById(R.id.eventDetails);
        roomCount = rowView.findViewById(R.id.roomCount);

        eventName.setText(eventList.getEventName());
        eventLoc.setText(eventList.getEventLocation());
        startDate.setText(eventList.getEventStartDate());
        endDate.setText(eventList.getEventEndDate());
        details.setText(eventList.getEventDetail());
        roomCount.setInputType(eventList.getEventStreamCount());

        return rowView;
    }

}
