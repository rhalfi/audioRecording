package com.recorder.yma.audiorecorder;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.recorder.yma.audiorecorder.com.recorder.yma.audiorecorder.server.apis.Moment;

import java.util.List;

/**
 * Created by ramh on 25/06/2017.
 */

public class MomentsListViewAdapter extends RecyclerView.Adapter<MomentsListViewAdapter.ViewHolder> {

    private static String TAG = "MomentsListViewAdapter";
    private List<Object> data;

    private static final int VIEW_TYPE_MOMENT = 0;

    public class ViewHolder extends RecyclerView.ViewHolder  {
        public TextView text;

        public ViewHolder(View v) {
            super(v);
            text = (TextView) v.findViewById(R.id.text1);
        }

    }

    public MomentsListViewAdapter(List<Object> data) {
        this.data = data;
    }

    @Override
    public MomentsListViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = null;
        if (viewType == VIEW_TYPE_MOMENT) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recording_item, parent, false);
          /*  v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openItem();
                }
            });*/


        }
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MomentsListViewAdapter.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_MOMENT) {
            Moment moment = ((Moment) data.get(position));
            holder.text.setText(moment.getContent());

            Log.d(TAG, "onBindViewHolder: " +position);

        }
    }

    private void openItem() {
        Log.d(TAG, "openItem: ");

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_MOMENT;
    }
}
