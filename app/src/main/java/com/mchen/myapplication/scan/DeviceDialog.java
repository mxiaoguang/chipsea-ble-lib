package com.mchen.myapplication.scan;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.chipsea.entity.BroadData;
import com.mchen.myapplication.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Suzy on 2016/10/26.
 */

public class DeviceDialog extends Dialog {

    private DeviceListAdapter adapter;
    private Button scanButton;
    private Context context;
    private OnDeviceScanListener listener;
    private boolean scanning;
    private List<BroadData> listValues;
    private ListView listView;
    private final BroadData.AddressComparator comparator = new BroadData.AddressComparator();

    public void setScanning(boolean scanning) {
        this.scanning = scanning;
    }

    public interface OnDeviceScanListener {
        void scan();

        void stop();

        void connect(BroadData device);
    }

    public DeviceDialog(Context context, OnDeviceScanListener listener) {
        super(context);
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_device);
        setTitle(R.string.scanner_title);
        listValues = new ArrayList<>();
        initViews();
        initEvents();

    }

    private void initEvents() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                stopScanDevice();
                dismiss();
                listener.connect((BroadData) adapter.getItem(position));
            }
        });


        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.action_cancel) {
                    if (scanning) {
                        stopScanDevice();
                        dismiss();
                    } else {
                        startScan();
                    }
                }
            }
        });
    }

    private void initViews() {
        listView = (ListView) findViewById(android.R.id.list);
        adapter = new DeviceListAdapter(context, listValues);
        listView.setAdapter(adapter);
        scanButton = (Button) findViewById(R.id.action_cancel);
    }

    private final static int SCAN_DURATION = 5000 * 1000000;

    private Handler handler = new Handler();

    public void startScan() {
        clearDevices();
        TextView textView = (TextView) findViewById(android.R.id.empty);
        textView.setVisibility(View.GONE);
        /*if (listValues.isEmpty()) {
            listView.setEmptyView(textView);
        }*/
        scanButton.setText(R.string.scanner_action_cancel);
        if (!scanning) {
            listener.scan();
            handler.postDelayed(stopScanRunnable, SCAN_DURATION);
        }
    }

    private Runnable stopScanRunnable = new Runnable() {
        @Override
        public void run() {
            stopScanDevice();
        }
    };

    private void stopScanDevice() {
        if (scanning) {
            scanButton.setText(R.string.scanner_action_scan);
            listener.stop();
        }
    }

    public void setDevice(BroadData device) {
        comparator.address = device.getAddress();
        final int index = listValues.indexOf(comparator);
        if (index >= 0) {
            BroadData previousDevice = listValues.get(index);
            previousDevice.setRssi(device.getRssi());
            previousDevice.setName(device.getName());
            previousDevice.setBright(device.isBright());
            adapter.notifyDataSetChanged();
            return;
        }
        listValues.add(device);
        adapter.notifyDataSetChanged();
    }

    private void clearDevices() {
        listValues.clear();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        handler.removeCallbacks(stopScanRunnable);
        stopScanDevice();
    }
}
