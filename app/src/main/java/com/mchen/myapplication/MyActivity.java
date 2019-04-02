package com.mchen.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chipsea.bleprofile.BleProfileService;
import com.chipsea.bleprofile.BleProfileServiceReadyActivity;
import com.chipsea.entity.BodyFatData;
import com.chipsea.entity.BroadData;
import com.chipsea.entity.CsFatScale;
import com.chipsea.entity.User;
import com.chipsea.utils.BleConfig;
import com.chipsea.utils.L;
import com.chipsea.utils.ParseData;
import com.chipsea.wby.WBYService;
import com.mchen.myapplication.scan.DeviceDialog;
import com.mchen.myapplication.utils.T;

import java.util.ArrayList;
import java.util.List;


public class MyActivity extends BleProfileServiceReadyActivity implements DeviceDialog.OnDeviceScanListener, View.OnClickListener {

    private final static String TAG = "MyActivity";
    private Menu menu;
    private Toolbar toolbar;
    private DeviceDialog devicesDialog;

    private WBYService.WBYBinder binder;

    private Button btn_sync_history, btn_sync_list, btn_sync_user, btn_sync_time, btn_update_user, btn_get_decimal, btn_version;
    private RadioGroup rg_change_unit;

    private TextView tv_age, tv_height, tv_weight, tv_temp, text_view_weight, tv_adc;
    private SeekBar seek_bar_age, seek_bar_height, seek_bar_weight, seek_bar_adc;

    private RadioGroup rg_sex;

    private ListView lv_data;
    private ArrayAdapter listAdapter;
    private List<String> dataList = new ArrayList<>();

//    private List<User> userList = new ArrayList<>();
//    private User user = null;

    private byte unit = BleConfig.UNIT_KG;

    private Button btn_auth, btn_set_did, btn_query_did;
    private EditText et_did;

    private FloatingActionButton fab_log;
    private CoordinatorLayout coordinator_layout;

    private boolean showListView = false;

    private BroadData cacheBroadData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        initPermission();
        initData();
        initViews();
        initEvents();
        if (!ensureBLESupported()) {
            T.showShort(this, R.string.not_support_ble);
            finish();
        }
        if (!isBLEEnabled()) {
            showBLEDialog();
        }
        devicesDialog = new DeviceDialog(this, this);
    }



    private void initPermission() {
        if (ContextCompat.checkSelfPermission(MyActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {//未开启定位权限
            //开启定位权限,200是标识码
            ActivityCompat.requestPermissions(MyActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
        } else {
            Toast.makeText(MyActivity.this, "已开启定位权限", Toast.LENGTH_LONG).show();
        }

        if (ContextCompat.checkSelfPermission(MyActivity.this, Manifest.permission.BLUETOOTH)
                != PackageManager.PERMISSION_GRANTED) {//未开启定位权限
            //开启定位权限,200是标识码
            ActivityCompat.requestPermissions(MyActivity.this, new String[]{Manifest.permission.BLUETOOTH}, 201);
        } else {
            Toast.makeText(MyActivity.this, "已开启蓝牙权限", Toast.LENGTH_LONG).show();
        }

        if (ContextCompat.checkSelfPermission(MyActivity.this, Manifest.permission.BLUETOOTH_ADMIN)
                != PackageManager.PERMISSION_GRANTED) {//未开启定位权限
            //开启定位权限,200是标识码
            ActivityCompat.requestPermissions(MyActivity.this, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, 202);
        } else {
            Toast.makeText(MyActivity.this, "已开启蓝牙admin权限", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (this.binder == null) {
            bindService(null);
        }
    }

    private void initData() {
//        user = new User(1, 2, 28, 170, 768, 551);
//        userList.add(user);
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setTitle(getString(R.string.app_name) + " V" + BuildConfig.VERSION_NAME);
        }

        coordinator_layout = findViewById(R.id.coordinator_layout);

        btn_sync_history = findViewById(R.id.btn_sync_history);
        btn_sync_list = findViewById(R.id.btn_sync_list);
        btn_sync_user = findViewById(R.id.btn_sync_user);
        btn_sync_time = findViewById(R.id.btn_sync_time);
        btn_update_user = findViewById(R.id.btn_update_user);
        btn_get_decimal = findViewById(R.id.btn_get_decimal);
        btn_version = findViewById(R.id.btn_version);

        rg_change_unit = findViewById(R.id.rg_change_unit);
        rg_change_unit.check(R.id.rb_kg);

        tv_age = findViewById(R.id.tv_age);
        setAgeText();

        tv_height = findViewById(R.id.tv_height);
        setHeightText();

        tv_weight = findViewById(R.id.tv_weight);
        tv_temp = findViewById(R.id.tv_temp);

        text_view_weight = findViewById(R.id.text_view_weight);
        setWeightText();

        tv_adc = findViewById(R.id.tv_adc);
        setAdcText();

        seek_bar_age = findViewById(R.id.seek_bar_age);
        seek_bar_age.setMax(82);
//        seek_bar_age.setProgress(user.getAge() - 18);

        seek_bar_height = findViewById(R.id.seek_bar_height);
        seek_bar_height.setMax(205);
//        seek_bar_height.setProgress(user.getHeight() - 50);

        seek_bar_weight = findViewById(R.id.seek_bar_weight);
        seek_bar_weight.setMax(1800);
//        seek_bar_weight.setProgress(user.getWeight());

        seek_bar_adc = findViewById(R.id.seek_bar_adc);
        seek_bar_adc.setMax(1000);
//        seek_bar_adc.setProgress(user.getAdc());

        rg_sex = findViewById(R.id.rg_sex);
//        if (user.getSex() == 1) {
//            rg_sex.check(R.id.rb_male);
//        } else {
//            rg_sex.check(R.id.rb_female);
//        }

        lv_data = findViewById(R.id.lv_data);
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataList);
        lv_data.setAdapter(listAdapter);

        btn_auth = findViewById(R.id.btn_auth);

        btn_set_did = findViewById(R.id.btn_set_did);
        btn_query_did = findViewById(R.id.btn_query_did);
        et_did = findViewById(R.id.et_did);

        fab_log = findViewById(R.id.fab_log);
        showListView();
    }

    private void showListView() {
        lv_data.setVisibility(showListView ? View.VISIBLE : View.GONE);
        showListView = !showListView;
    }

    private void setAdcText() {
//        tv_adc.setText(getString(R.string.adc, String.valueOf(user.getAdc())));
    }

    private void setWeightText() {
//        text_view_weight.setText(getString(weight, String.valueOf(user.getWeight() / 10d)));
    }

    private void setHeightText() {
//        tv_height.setText(getString(R.string.height, user.getHeight()));
    }

    private void setAgeText() {
//        tv_age.setText(getString(R.string.age, user.getAge()));
    }

    private void initEvents() {
        btn_sync_history.setOnClickListener(this);
        btn_sync_list.setOnClickListener(this);
        btn_sync_user.setOnClickListener(this);
        btn_sync_time.setOnClickListener(this);
        btn_update_user.setOnClickListener(this);
        btn_get_decimal.setOnClickListener(this);
        btn_version.setOnClickListener(this);

        btn_auth.setOnClickListener(this);
        btn_set_did.setOnClickListener(this);
        btn_query_did.setOnClickListener(this);

        rg_change_unit.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isDeviceConnected()) {
                    switch (checkedId) {
                        case R.id.rb_kg:
                            unit = BleConfig.UNIT_KG;
                            binder.syncUnit(BleConfig.UNIT_KG);
                            break;
                        case R.id.rb_lb:
                            unit = BleConfig.UNIT_LB;
                            binder.syncUnit(BleConfig.UNIT_LB);
                            break;
                        case R.id.rb_st:
                            unit = BleConfig.UNIT_LB;
                            binder.syncUnit(BleConfig.UNIT_LB);
                            break;
                        case R.id.rb_jin:
                            unit = BleConfig.UNIT_LB;
                            binder.syncUnit(BleConfig.UNIT_LB);
                            break;
                    }
                }
            }
        });

        rg_sex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_male:
//                        user.setSex(1);
                        break;
                    case R.id.rb_female:
//                        user.setSex(2);
                        break;
                }
            }
        });

        seek_bar_age.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                user.setAge(progress + 18);
                setAgeText();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seek_bar_height.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                user.setHeight(progress + 50);
                setHeightText();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seek_bar_weight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                user.setWeight(progress);
                setWeightText();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seek_bar_adc.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                user.setAdc(progress);
                setAdcText();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        fab_log.setOnClickListener(this);
    }

    private void setDefault() {
        tv_weight.setText(R.string.default_weight);
        tv_temp.setText(R.string.default_temp);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fab_log) {
            showListView();
            return;
        }
        if (isDeviceConnected()) {
            switch (v.getId()) {
                case R.id.btn_sync_history:
//                    binder.syncHistory();
                    break;
                case R.id.btn_sync_list:
//                    binder.syncUserList(userList);
                    break;
                case R.id.btn_sync_user:
//                    binder.syncUser(user);
                    break;
                case R.id.btn_sync_time:
//                    binder.syncDate();
                    break;
                case R.id.btn_update_user:
//                    binder.updateUser(user);
                    break;
                case R.id.btn_auth:
//                    binder.auth();
                    break;
                case R.id.btn_get_decimal:
//                    binder.getDecimalInfo();
                    break;
                case R.id.btn_set_did:
                    String etStr = et_did.getText().toString().trim();
                    if (TextUtils.isEmpty(etStr)) {
                        T.showShort(this, getString(R.string.did_is_null));
                    } else {
                        int did = Integer.valueOf(etStr);
                        if (did > -1 && did < 65536) {
//                            binder.setDID(did);
                        } else {
                            T.showShort(this, R.string.did_error);
                        }
                    }
                    break;
                case R.id.btn_query_did:
//                    binder.queryDID();
                    break;
                case R.id.btn_version:
//                    binder.queryBleVersion();
                    break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_scan:
                if (!isBLEEnabled()) {
                    showBLEDialog();
                } else {
                    if (isDeviceConnected()) {
                        binder.disconnect();
                    } else {
                        if (cacheBroadData == null) {
                            devicesDialog.show();
                            devicesDialog.startScan();
                        } else {
                            cacheBroadData = null;
                            setStateTitle("", BleProfileService.STATE_DISCONNECTED);
                            stopLeScan();
                        }
                    }
                }
                break;
        }

        return true;
    }

    @Override
    protected void onServiceBinded(WBYService.WBYBinder binder) {
        this.binder = binder;
        L.e("2017-11-20", TAG + ", onServiceBinded: binder = " + binder);
    }

    @Override
    protected void onServiceUnbinded() {
        this.binder = null;
        L.e("2017-11-20", TAG + ", onServiceUnbinded");
    }

//    @Override
//    protected void getAicareDevice(final BroadData broadData) {
//        if (broadData != null) {
//            L.e(TAG, broadData.toString());
//            L.e(TAG, "是否是主线程：" + (Looper.myLooper() == Looper.getMainLooper()));
//            if (devicesDialog.isShowing()) {
//                devicesDialog.setDevice(broadData);
//            }
//            if (cacheBroadData != null && TextUtils.equals(cacheBroadData.getAddress(), broadData.getAddress())) {
//                if (broadData.getDeviceType() == AicareBleConfig.BM_09) {
//                    if (broadData.getSpecificData() != null) {
//                        BM09Data data = AicareBleConfig.getBm09Data(broadData.getAddress(), broadData.getSpecificData());
//                        if (isNewData(data) && data.getWeight() != 0) {
//                            showInfo(data.toString(), false);
//                        }
//                    }
//                } else if (broadData.getDeviceType() == AicareBleConfig.BM_15) {
//                    if (broadData.getSpecificData() != null) {
//                        rg_change_unit.setOnCheckedChangeListener(null);
//                        BM15Data data = AicareBleConfig.getBm15Data(broadData.getAddress(), broadData.getSpecificData());
//                        WeightData weightData = new WeightData();
//                        weightData.setDeviceType(broadData.getDeviceType());
//                        switch (data.getUnitType()) {
//                            case 1:
//                                unit = AicareBleConfig.UNIT_KG;
//                                rg_change_unit.check(R.id.rb_kg);
//                                weightData.setDecimalInfo(new DecimalInfo(1, 1, 1, 1, 1, 2));
//                                break;
//                            case 2:
//                                unit = AicareBleConfig.UNIT_LB;
//                                rg_change_unit.check(R.id.rb_lb);
//                                weightData.setDecimalInfo(new DecimalInfo(1, 1, 1, 1, 1, 2));
//                                break;
//                            case 3:
//                                unit = AicareBleConfig.UNIT_ST;
//                                rg_change_unit.check(R.id.rb_st);
//                                weightData.setDecimalInfo(new DecimalInfo(1, 1, 1, 1, 1, 2));
//                                break;
//                            case 4:
//                                unit = AicareBleConfig.UNIT_KG;
//                                rg_change_unit.check(R.id.rb_kg);
//                                weightData.setDecimalInfo(new DecimalInfo(2, 1, 1, 1, 1, 2));
//                                break;
//                            case 5:
//                                unit = AicareBleConfig.UNIT_LB;
//                                rg_change_unit.check(R.id.rb_lb);
//                                weightData.setDecimalInfo(new DecimalInfo(2, 1, 1, 1, 1, 2));
//                                break;
//                            case 6:
//                                unit = AicareBleConfig.UNIT_ST;
//                                rg_change_unit.check(R.id.rb_st);
//                                weightData.setDecimalInfo(new DecimalInfo(2, 1, 1, 1, 1, 2));
//                                break;
//                        }
//                        weightData.setWeight(data.getWeight());
//                        weightData.setTemp(data.getTemp());
//                        onGetWeightData(weightData);
//                        if (isNewBM15Data(data)) {
//                            showInfo(data.toString(), false);
//                        }
//                    }
//                } else {
//                    if (broadData.getSpecificData() != null) {
//                        WeightData weightData = AicareBleConfig.getWeightData(broadData.getSpecificData());
//                        onGetWeightData(weightData);
//                    }
//                }
//            }
//        }
//
//    }

    @Override
    protected void onDestroy() {
        stopScan();
        if (isDeviceConnected()) {
            this.binder.disconnect();
        }
        super.onDestroy();
    }

    private Handler handler = new Handler();

    private void startLeScan() {
        startScan();
    }

    private void stopLeScan() {
        stopScan();
    }

    @Override
    public void scan() {
        startScan();
        devicesDialog.setScanning(true);
    }

    @Override
    public void stop() {
        stopScan();
        devicesDialog.setScanning(false);
    }

    @Override
    public void connect(BroadData device) {
//        //
//        if (device.getDeviceType() == BleConfig.BM_CS) {
//            cacheBroadData = device;
//            showInfo(getString(R.string.state_bound, device.getAddress()), true);
//            setStateTitle(device.getAddress(), -1);
//            startLeScan();
//        } else {
//            startConnect(device.getAddress());
//        }

        //连接
        startConnect(device.getAddress());
    }

//    @Override
//    public void connect(BroadData device) {
//        if (device.getDeviceType() == AicareBleConfig.TYPE_WEI_BROAD || device.getDeviceType() == AicareBleConfig.TYPE_WEI_TEMP_BROAD || device.getDeviceType() == AicareBleConfig.BM_09 || device.getDeviceType() == AicareBleConfig.BM_15) {
//            cacheBroadData = device;
//            showInfo(getString(R.string.state_bound, device.getAddress()), true);
//            setStateTitle(device.getAddress(), -1);
//            startLeScan();
//        } else {
//            startConnect(device.getAddress());
//        }
//    }

    @Override
    public void onStateChanged(String deviceAddress, int state) {
        super.onStateChanged(deviceAddress, state);
        Log.i("TAG", "onStateChanged  state " + state);
        switch (state) {
            case BleProfileService.STATE_CONNECTED:
                showInfo(getString(R.string.state_connected, deviceAddress), true);
                setStateTitle(deviceAddress, state);
                break;
            case BleProfileService.STATE_DISCONNECTED:
                showInfo(getString(R.string.state_disconnected), true);
                setStateTitle(deviceAddress, state);
                break;
            case BleProfileService.STATE_SERVICES_DISCOVERED:
                showInfo(getString(R.string.state_service_discovered), true);
                break;
            case BleProfileService.STATE_INDICATION_SUCCESS:
                showInfo(getString(R.string.state_indication_success), true);

                User user = new User();
                user.setId(1000);
                user.setSex((byte) 0x00);
                user.setAge(25);
                user.setHeight(172);
                user.setAdc(100);
                /**
                 * 同步用户
                 */
                binder.syncUser(user);
                break;
            case BleProfileService.STATE_TIME_OUT:
                showInfo(getString(R.string.state_time_out), true);
                break;
            case BleProfileService.STATE_CONNECTING:
                showInfo(getString(R.string.state_connecting), true);
                break;
        }
    }

    private void showInfo(String str, boolean showSnackBar) {
        if (showSnackBar) {
            showSnackBar(str);

        }

        String time = ParseData.getCurrentTime() + "\n----" + str;
        dataList.add(time);
        listAdapter.notifyDataSetChanged();
        lv_data.setSelection(dataList.size() - 1);
    }

    private void setStateTitle(final String deviceAddress, final int state) {
        switch (state) {
            case BleProfileService.STATE_CONNECTED:
                L.e(TAG, "STATE_CONNECTED");
                toolbar.setSubtitle(deviceAddress);
                menu.getItem(0).setTitle(R.string.disconnect);
                break;
            case BleProfileService.STATE_DISCONNECTED:
                L.e(TAG, "STATE_DISCONNECTED");
                toolbar.setSubtitle("");
                menu.getItem(0).setTitle(R.string.start_scan);
                setDefault();
                break;
            case -1:
                toolbar.setSubtitle(deviceAddress);
                menu.getItem(0).setTitle(R.string.unbound);
                break;
        }
    }

    @Override
    public void onError(final String errMsg, final int errCode) {
        L.e(TAG, "Message = " + errMsg + " errCode = " + errCode);
        showInfo(getString(R.string.state_error, errMsg, errCode), true);
    }

    @Override
    protected void getBLEDevice(BroadData broadData) {
        if (broadData != null) {
            L.e(TAG, broadData.toString());
            L.e(TAG, "是否是主线程：" + (Looper.myLooper() == Looper.getMainLooper()));
            if (devicesDialog.isShowing()) {
                devicesDialog.setDevice(broadData);
            }
            if (cacheBroadData != null && TextUtils.equals(cacheBroadData.getAddress(), broadData.getAddress())) {
            }
        }
    }

    @Override
    protected void onWeightData(boolean isHistory, CsFatScale csFatScale) {
        setWeighDataText(csFatScale.getWeight()/10.0f + "");
    }

    @Override
    protected void onBodyFatData(boolean isHistory, BodyFatData bodyFatData) {
        if (bodyFatData == null) {
            return;
        }
        Log.i("TAG", ">>>>>>>>>>> isHistory >>>>" + isHistory + ">>>>>>>BodyFatData" + bodyFatData.toString());
    }

    private void setWeighDataText(String weight) {
        tv_weight.setText(getString(R.string.weight, weight));
    }


    private Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            binder.syncUser(new User());
        }
    };

    private void showSnackBar(String info) {
        Snackbar snackbar = Snackbar.make(coordinator_layout, info, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }
}
