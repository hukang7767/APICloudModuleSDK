package com.alpha.modulegnoga.device;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.alpha.modulegnoga.R;

import java.util.ArrayList;

public class DeviceAdapter extends RecyclerView.Adapter {

    public void setDevices(ArrayList<CnogaDevice> devices) {
        this.devices = devices;
        notifyDataSetChanged();
    }

    public interface OnRecyclerViewListener {

        void onDeviceClick(CnogaDevice device);
    }

    private OnRecyclerViewListener onRecyclerViewListener;

    public void setOnRecyclerViewListener(OnRecyclerViewListener onRecyclerViewListener) {
        this.onRecyclerViewListener = onRecyclerViewListener;
    }

    private Context context;

    private ArrayList<CnogaDevice> devices;

    public DeviceAdapter(Context context, ArrayList<CnogaDevice> devices) {
        this.context = context.getApplicationContext();
        this.devices = devices;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.device_item, null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new DeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        DeviceViewHolder holder = (DeviceViewHolder) viewHolder;
        CnogaDevice device = devices.get(position);
        holder.address.setText(device.getAddress());
        holder.name.setText(device.getName());
        if (device.isAvailable()) {
            holder.status.setImageResource(R.mipmap.device_available);
        } else {
            holder.status.setImageResource(R.mipmap.device_unavailable);
        }
    }

    @Override
    public int getItemCount() {
        return devices == null ? 0 : devices.size();
    }

    private class DeviceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public View root;

        public ImageView status;

        public TextView name;

        public TextView address;


        public DeviceViewHolder(View itemView) {
            super(itemView);
            status = (ImageView) itemView.findViewById(R.id.device_status_iv);
            name = (TextView) itemView.findViewById(R.id.device_name);
            address = (TextView) itemView.findViewById(R.id.device_address_tv);
            root = itemView.findViewById(R.id.device_item_root);
            root.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            if (pos < 0 || onRecyclerViewListener == null) {
                return;
            }
            int i = v.getId();
            if (i == R.id.device_item_root) {
                if ((devices.size() > pos) && (devices.get(pos) != null))
                    onRecyclerViewListener.onDeviceClick(devices.get(pos));

            } else {
            }
        }
    }
}