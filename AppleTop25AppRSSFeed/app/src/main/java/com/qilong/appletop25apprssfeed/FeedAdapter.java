package com.qilong.appletop25apprssfeed;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class FeedAdapter extends ArrayAdapter {

    private static final String TAG = "FeedAdapter";
    private final int layoutResource;
    private final LayoutInflater layoutInflater;
    private List<AppEntry> entries;

    public FeedAdapter(@NonNull Context context, int resource, List<AppEntry> entries) {
        super(context, resource);
       this.layoutResource = resource;
        this.layoutInflater = LayoutInflater.from(context);
        this.entries = entries;
    }

    @Override
    public int getCount() {
        return entries.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;

        if(convertView == null){
            convertView = layoutInflater.inflate(layoutResource, parent,false);

            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
//
//        TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
//        TextView tvArtist = (TextView) convertView.findViewById(R.id.tvArtist);
//        TextView tvSummary = (TextView) convertView.findViewById(R.id.tvSummary);

        AppEntry curApp = entries.get(position);

       viewHolder.tvName.setText(curApp.getName());
        viewHolder.tvArtist.setText(curApp.getArtist());
        viewHolder.tvSummary.setText(curApp.getSummary());

        return convertView;
    }

    private class ViewHolder{
        final TextView tvName;
        final TextView tvArtist;
        final TextView tvSummary;

        ViewHolder(View v){
            this.tvName = (TextView) v.findViewById(R.id.tvName);
            this.tvArtist = (TextView) v.findViewById(R.id.tvArtist);
            this.tvSummary= (TextView) v.findViewById(R.id.tvSummary);

        }
    }
}
