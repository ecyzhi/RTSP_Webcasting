package com.mercy.alpacalive.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mercy.alpacalive.R;

import java.util.List;

public class LiveListAdapter extends ArrayAdapter<LiveList> {

    public LiveListAdapter(@NonNull Context context, int resource, @NonNull List<LiveList> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LiveList liveList = getItem(position);

        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.roomlisting_item, parent, false);

        TextView roomName;

        roomName = rowView.findViewById(R.id.txtRoomName);


        roomName.setText(liveList.getRoomName());


        return rowView;
    }
}
