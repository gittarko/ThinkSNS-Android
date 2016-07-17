package com.thinksns.sociax.t4.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.amap.api.services.core.PoiItem;
import com.thinksns.sociax.android.R;

import java.util.List;

public class PoiAdapter extends BaseAdapter {

    private List<PoiItem> data;
    private LayoutInflater inflater;

    public PoiAdapter(@NonNull Context context, List<PoiItem> data) {
        this.data = data;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public PoiItem getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_poi_search, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        PoiItem poiItem = getItem(position);
        holder.tvLocationStreet.setText(poiItem.getTitle());
        holder.tvLocationDetail.setText(poiItem.getSnippet());

        return convertView;
    }

    private class ViewHolder {
        private TextView tvLocationStreet, tvLocationDetail;

        public ViewHolder(View parent) {
            tvLocationStreet = (TextView) parent.findViewById(R.id.tv_location_street);
            tvLocationDetail = (TextView) parent.findViewById(R.id.tv_location_detail);
        }
    }
}
