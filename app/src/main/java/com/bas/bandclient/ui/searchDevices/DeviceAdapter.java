package com.bas.bandclient.ui.searchDevices;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bas.bandclient.R;
import com.bas.bandclient.models.DeviceModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> {

    private List<DeviceModel> devices = new ArrayList<>();
    private Listener listener;

    public DeviceAdapter(Listener listener){
        this.listener = listener;
    }

    public void setDevices(List<DeviceModel> devices) {
        this.devices = devices;
        notifyDataSetChanged();
    }

    @Override
    public DeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_search_devices_element, parent, false);
        return new DeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DeviceViewHolder holder, int position) {
        holder.setDevice(devices.get(position));
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    class DeviceViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvName)
        protected TextView name;

        @BindView(R.id.tvNotes)
        protected TextView tvNotes;

        private DeviceModel device;

        public DeviceViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(v -> listener.onDeviceClick(device));
        }

        public void setDevice(DeviceModel device) {
            this.device = device;
            name.setText(device.getName());
            tvNotes.setText(device.getNotes());
        }
    }

    interface Listener {
        void onDeviceClick(DeviceModel device);
    }
}
