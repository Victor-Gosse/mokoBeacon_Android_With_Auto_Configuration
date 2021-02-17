package com.moko.beacon.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.IdRes;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;


import com.moko.beacon.BeaconConstants;
import com.moko.beacon.R;
import com.moko.beacon.adapter.BeaconListAdapter;
import com.moko.beacon.dialog.PasswordDialog;
import com.moko.beacon.entity.BeaconDeviceInfo;
import com.moko.beacon.entity.BeaconInfo;
import com.moko.beacon.entity.BeaconParam;
import com.moko.beacon.service.MokoService;
import com.moko.beacon.utils.BeaconInfoParseableImpl;
import com.moko.beacon.utils.ToastUtils;
import com.moko.support.MokoConstants;
import com.moko.support.MokoSupport;
import com.moko.support.callback.MokoScanDeviceCallback;
import com.moko.support.entity.DeviceInfo;
import com.moko.support.entity.OrderType;
import com.moko.support.log.LogModule;
import com.moko.support.utils.MokoUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @Date 2017/12/7 0007
 * @Author wenzheng.liu
 * @Description
 * @ClassPath com.moko.beacon.activity.MainActivity
 * FORK BY VICTOR GOSSE : ADD Auto configuration fonction - 22/06/2020
 * www.linkedin.com/in/victor-gosse
 */
public class MainActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener, MokoScanDeviceCallback, AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {

    public static final int SORT_TYPE_RSSI = 0;
    public static final int SORT_TYPE_MAJOR = 1;
    public static final int SORT_TYPE_MINOR = 2;
    public static String AutoEnable = "False";
    public static String RetryListAuto;
    public static int PowerNumber;
    public ListView ListAuto;
    public static int CountListAuto;
    public static int CptListAuto;
    public List<String> ListDialogAuto;
    public String BeaconInConfig;

    @Bind(R.id.et_device_filter)
    EditText etDeviceFilter;
    @Bind(R.id.rb_sort_rssi)
    RadioButton rbSortRssi;
    @Bind(R.id.rb_sort_major)
    RadioButton rbSortMajor;
    @Bind(R.id.rb_sort_minor)
    RadioButton rbSortMinor;
    @Bind(R.id.rg_device_sort)
    RadioGroup rgDeviceSort;
    @Bind(R.id.lv_device_list)
    ListView lvDeviceList;
    @Bind(R.id.iv_refresh)
    ImageView ivRefresh;
    @Bind(R.id.tv_devices_title)
    TextView tvDevicesTitle;
    @Bind(R.id.editTextPassword)
    EditText etPass;


    private Animation animation = null;
    private BeaconListAdapter mAdapter;
    private ArrayList<BeaconInfo> mBeaconInfos;
    private int mSortType;
    private String mFilterText;
    private MokoService mMokoService;
    private HashMap<String, BeaconInfo> beaconMap;
    private BeaconInfoParseableImpl beaconInfoParseable;
    public String mSavedPassword;
    private Spinner spinner;
    private static final String[] paths = {"High", "1", "2", "3", "4", "5", "6", "7"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        LogModule.i("test-1");
        mAdapter = new BeaconListAdapter(this);
        mBeaconInfos = new ArrayList<>();
        beaconMap = new HashMap<>();
        mAdapter.setItems(mBeaconInfos);
        lvDeviceList.setAdapter(mAdapter);
        rgDeviceSort.setOnCheckedChangeListener(this);
        lvDeviceList.setOnItemClickListener(this);
        rbSortMinor.setChecked(true);
        mPassword = "Moko4321";
        etDeviceFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                mFilterText = s.toString();
            }
        });
        etPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                mPassword = s.toString();
                LogModule.i(String.valueOf(mPassword));
            }
        });
        bindService(new Intent(this, MokoService.class), mServiceConnection, BIND_AUTO_CREATE);
        AutoEnable = "False";

        spinner = (Spinner)findViewById(R.id.spinner);
        ArrayAdapter<String>adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, paths);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);
        spinner.setOnItemSelectedListener(this);

        AlertDialog.Builder builder3 = new AlertDialog.Builder(MainActivity.this);
        TextView title = new TextView(this);
        title.setText("How to use the Moko auto configuration application");
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.BLACK);
        title.setTypeface(title.getTypeface(), Typeface.BOLD);
        title.setTextSize(20);

        //builder3.setTitle("How to use the Moko auto config application");
        builder3.setCustomTitle(title);
        String Line1 = "1. Wait until all your beacons are displayed on the screen";
        String Line2 = "2. Select the power transmission with the spinner";
        String Line3 = "3. Enter the password of your beacons (By default it is \"Moko4321\")";
        String Line4 = "4. Click on the \"Auto\" Button for to run the auto configuration and wait";
        String Line5 = "5. When the configuration is complete, a result pop up is displayed";
        String Line6 = "DEVELOPPED BY WENZHENG LIU AND FORK BY VICTOR GOSSE";
        String Line7 = "www.linkedin.com/in/victor-gosse";

        builder3.setMessage(Line1 + "\n\n" + Line2 + "\n\n" + Line3 + "\n\n" + Line4 + "\n\n" + Line5 + "\n\n" + Line6 + "\n\n" + Line7);
        builder3.setCancelable(true);
        builder3.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert13 = builder3.create();
        alert13.show();

    }



    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMokoService = ((MokoService.LocalBinder) service).getService();
            // Enregistrer le récepteur de diffusion
            IntentFilter filter = new IntentFilter();
            filter.addAction(MokoConstants.ACTION_CONNECT_SUCCESS);
            filter.addAction(MokoConstants.ACTION_CONNECT_DISCONNECTED);
            filter.addAction(MokoConstants.ACTION_RESPONSE_SUCCESS);
            filter.addAction(MokoConstants.ACTION_RESPONSE_TIMEOUT);
            filter.addAction(MokoConstants.ACTION_RESPONSE_FINISH);
            filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            filter.setPriority(100);
            registerReceiver(mReceiver, filter);
            if (animation == null) {
                LogModule.i("*********************** | MainActivity - TEST 1 | ***********************");
                startScan();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    public BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String action = intent.getAction();
                if (MokoConstants.ACTION_CONNECT_SUCCESS.equals(action)) {
                    mBeaconParam = new BeaconParam();
                    BeaconDeviceInfo beaconInfo = new BeaconDeviceInfo();
                    mBeaconParam.beaconInfo = beaconInfo;
                    mBeaconParam.threeAxis = mThreeAxis;
                    //Lire toutes les données lisibles
                    mMokoService.mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mBeaconParam.password = mPassword;
                            mMokoService.getReadableData(mPassword);
                        }
                    }, 1000);
                }

                if (MokoConstants.ACTION_CONNECT_DISCONNECTED.equals(action)) {
                    dismissLoadingProgressDialog();
                    ToastUtils.showToast(MainActivity.this, "connect failed");
                    if (animation == null) {
                        LogModule.i("*********************** | MainActivity - Erreur sur l'élément "  + CptListAuto + " (Version liste avec le -1) | ***********************");
                        LogModule.i("*********************** | MainActivity - TEST 2 | ***********************");
                        if (AutoEnable.equals("True")){
                            LogModule.i("*********************** | MainActivity - 1 | ***********************");
                            LogModule.i(RetryListAuto);
                            if (RetryListAuto.equals("False")){
                                LogModule.i("test");
                                LogModule.i("*********************** | MainActivity - Seconde tentative sur l'élément "  + CptListAuto + " (Version liste avec le -1) | ***********************");
                                ListAuto.performItemClick(ListAuto.getChildAt(CptListAuto), CptListAuto, ListAuto.getItemIdAtPosition(CptListAuto));
                                RetryListAuto = "True";
                            }
                            else if (RetryListAuto.equals("True")){
                                LogModule.i("*********************** | MainActivity - Seconde tentative échoué sur l'élément "  + CptListAuto + " (Version liste avec le -1) | ***********************");
                                ListDialogAuto.add("NOK -  " + BeaconInConfig);
                                RetryListAuto = "False";
                                CptListAuto = CptListAuto + 1;
                                if (CptListAuto > (CountListAuto -1)){
                                    LogModule.i("*********************** | MainActivity - Toute la liste est traitée | ***********************");

                                    // Dialog Result

                                    final CharSequence[] SeqListDialogAuto2 = ListDialogAuto.toArray(new String[ListDialogAuto.size()]);
                                    AlertDialog.Builder builder2 = new AlertDialog.Builder(MainActivity.this);

                                    TextView title2 = new TextView(MainActivity.this);
                                    title2.setText("Auto Config Result");
                                    title2.setPadding(10, 10, 10, 10);
                                    title2.setGravity(Gravity.CENTER);
                                    //title.setBackgroundColor(Color.GRAY);
                                    title2.setTextColor(Color.BLACK);
                                    title2.setTypeface(title2.getTypeface(), Typeface.BOLD);
                                    title2.setTextSize(20);
                                    builder2.setCustomTitle(title2);

                                    builder2.setItems(SeqListDialogAuto2, null);
                                    builder2.setCancelable(true);
                                    builder2.setPositiveButton(
                                            "OK",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.cancel();
                                                }
                                            });


                                    AlertDialog alert12 = builder2.create();
                                    alert12.show();

                                    // Dialog Result End

                                    // Dialog Result End

                                    startScan();
                                }
                                else {
                                    ListAuto.performItemClick(ListAuto.getChildAt(CptListAuto), CptListAuto, ListAuto.getItemIdAtPosition(CptListAuto));
                                }

                            }
                        }
                        //startScan();
                    }
                }
                if (MokoConstants.ACTION_RESPONSE_TIMEOUT.equals(action)) {
                    OrderType orderType = (OrderType) intent.getSerializableExtra(MokoConstants.EXTRA_KEY_RESPONSE_ORDER_TYPE);
                    switch (orderType) {
                        case changePassword:
                            // Changer le délai d'expiration du mot de passe
                            dismissLoadingProgressDialog();
                            ToastUtils.showToast(MainActivity.this, "password error");
                            if (animation == null) {
                                LogModule.i("*********************** | MainActivity - TEST 3 | ***********************");
                                startScan();
                            }
                            break;
                    }
                }
                if (MokoConstants.ACTION_RESPONSE_FINISH.equals(action)) {
                }
                if (MokoConstants.ACTION_RESPONSE_SUCCESS.equals(action)) {
                    OrderType orderType = (OrderType) intent.getSerializableExtra(MokoConstants.EXTRA_KEY_RESPONSE_ORDER_TYPE);
                    byte[] value = intent.getByteArrayExtra(MokoConstants.EXTRA_KEY_RESPONSE_VALUE);
                    switch (orderType) {
                        case battery:
                            mBeaconParam.battery = Integer.parseInt(MokoUtils.bytesToHexString(value), 16) + "";
                            break;
                        case iBeaconUuid:
                            String hexString = MokoUtils.bytesToHexString(value).toUpperCase();
                            if (hexString.length() > 31) {
                                StringBuilder sb = new StringBuilder();
                                sb.append(hexString.substring(0, 8));
                                sb.append("-");
                                sb.append(hexString.substring(8, 12));
                                sb.append("-");
                                sb.append(hexString.substring(12, 16));
                                sb.append("-");
                                sb.append(hexString.substring(16, 20));
                                sb.append("-");
                                sb.append(hexString.substring(20, 32));
                                String uuid = sb.toString();
                                mBeaconParam.uuid = uuid;
                            }
                            break;
                        case major:
                            mBeaconParam.major = Integer.parseInt(MokoUtils.bytesToHexString(value), 16) + "";
                            break;
                        case minor:
                            mBeaconParam.minor = Integer.parseInt(MokoUtils.bytesToHexString(value), 16) + "";
                            //LogModule.i("*********************** | MainActivity - Value Of minor " + mBeaconParam.minor + " | ***********************");
                            break;
                        case measurePower:
                            mBeaconParam.measurePower = Integer.parseInt(MokoUtils.bytesToHexString(value), 16) + "";
                            break;
                        case transmission:
                            int transmission = Integer.parseInt(MokoUtils.bytesToHexString(value), 16);
                            if (transmission == 8) {
                                transmission = 7;
                            }
                            mBeaconParam.transmission = transmission + "";
                            break;
                        case broadcastingInterval:
                            mBeaconParam.broadcastingInterval = Integer.parseInt(MokoUtils.bytesToHexString(value), 16) + "";
                            break;
                        case serialID:
                            mBeaconParam.serialID = MokoUtils.hex2String(MokoUtils.bytesToHexString(value));
                            break;
                        case iBeaconMac:
                            String hexMac = MokoUtils.bytesToHexString(value);
                            if (hexMac.length() > 11) {
                                StringBuilder sb = new StringBuilder();
                                sb.append(hexMac.substring(0, 2));
                                sb.append(":");
                                sb.append(hexMac.substring(2, 4));
                                sb.append(":");
                                sb.append(hexMac.substring(4, 6));
                                sb.append(":");
                                sb.append(hexMac.substring(6, 8));
                                sb.append(":");
                                sb.append(hexMac.substring(8, 10));
                                sb.append(":");
                                sb.append(hexMac.substring(10, 12));
                                String mac = sb.toString().toUpperCase();
                                mBeaconParam.iBeaconMAC = mac;
                                mBeaconParam.beaconInfo.iBeaconMac = mac;
                            }
                            break;
                        case iBeaconName:
                            mBeaconParam.iBeaconName = MokoUtils.hex2String(MokoUtils.bytesToHexString(value));
                            break;
                        case connectionMode:
                            mBeaconParam.connectionMode = MokoUtils.bytesToHexString(value);
                            break;
                        case firmname:
                            mBeaconParam.beaconInfo.firmname = MokoUtils.hex2String(MokoUtils.bytesToHexString(value));
                            break;
                        case softVersion:
                            mBeaconParam.beaconInfo.softVersion = MokoUtils.hex2String(MokoUtils.bytesToHexString(value));
                            break;
                        case devicename:
                            mBeaconParam.beaconInfo.deviceName = MokoUtils.hex2String(MokoUtils.bytesToHexString(value));
                            break;
                        case iBeaconDate:
                            mBeaconParam.beaconInfo.iBeaconDate = MokoUtils.hex2String(MokoUtils.bytesToHexString(value));
                            break;
                        case hardwareVersion:
                            mBeaconParam.beaconInfo.hardwareVersion = MokoUtils.hex2String(MokoUtils.bytesToHexString(value));
                            break;
                        case firmwareVersion:
                            mBeaconParam.beaconInfo.firmwareVersion = MokoUtils.hex2String(MokoUtils.bytesToHexString(value));
                            break;
                        case writeAndNotify:
                            if ("eb59".equals(MokoUtils.bytesToHexString(Arrays.copyOfRange(value, 0, 2)).toLowerCase())) {
                                byte[] runtimeBytes = Arrays.copyOfRange(value, 4, value.length);
                                int seconds = Integer.parseInt(MokoUtils.bytesToHexString(runtimeBytes), 16);
                                int day = 0, hours = 0, minutes = 0;
                                day = seconds / (60 * 60 * 24);
                                seconds -= day * 60 * 60 * 24;
                                hours = seconds / (60 * 60);
                                seconds -= hours * 60 * 60;
                                minutes = seconds / 60;
                                seconds -= minutes * 60;
                                mBeaconParam.beaconInfo.runtime = String.format("%dD%dh%dm%ds", day, hours, minutes, seconds);
                            }
                            if ("eb5b".equals(MokoUtils.bytesToHexString(Arrays.copyOfRange(value, 0, 2)).toLowerCase())) {
                                byte[] chipModelBytes = Arrays.copyOfRange(value, 4, value.length);
                                mBeaconParam.beaconInfo.chipModel = MokoUtils.hex2String(MokoUtils.bytesToHexString(chipModelBytes));
                            }
                            break;
                        case changePassword:
                            if ("00".equals(MokoUtils.bytesToHexString(value))) {
                                mMokoService.mHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        dismissLoadingProgressDialog();
                                        LogModule.i(mBeaconParam.toString());
                                        mSavedPassword = mPassword;
                                        Intent deviceInfoIntent = new Intent(MainActivity.this, DeviceInfoActivity.class);
                                        deviceInfoIntent.putExtra(BeaconConstants.EXTRA_KEY_DEVICE_PARAM, mBeaconParam);
                                        startActivityForResult(deviceInfoIntent, BeaconConstants.REQUEST_CODE_DEVICE_INFO);
                                    }
                                }, 1000);
                            } else {
                                dismissLoadingProgressDialog();
                                ToastUtils.showToast(MainActivity.this, "password error");
                                if (animation == null) {
                                    LogModule.i("*********************** | MainActivity - TEST 4 | ***********************");
                                    startScan();
                                }
                            }
                            break;
                    }
                }
                if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                    int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                    switch (blueState) {
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            if (animation != null) {
                                mMokoService.mHandler.removeMessages(0);
                                mMokoService.stopScanDevice();
                            }
                            break;

                    }
                }
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case MokoConstants.REQUEST_CODE_ENABLE_BT:
                    if (animation == null) {
                        LogModule.i("*********************** | MainActivity - TEST 5 | ***********************");
                        startScan();
                    }
                    break;

            }
        } else {
            switch (requestCode) {
                case MokoConstants.REQUEST_CODE_ENABLE_BT:
                    // Le Bluetooth n'est pas activé
                    MainActivity.this.finish();
                    break;
                case BeaconConstants.REQUEST_CODE_DEVICE_INFO:
                    if (animation == null) {
                        LogModule.i("*********************** | MainActivity - TEST 6 | ***********************");
                        if (AutoEnable == "True"){
                            if (CptListAuto > (CountListAuto -1)){
                                LogModule.i("*********************** | MainActivity - Toute la liste est traitée | ***********************");
                                AutoEnable = "False";
                                ListDialogAuto.add("OK  -  " + BeaconInConfig); //this adds an element to the list.

                                // Dialog Result

                                final CharSequence[] SeqListDialogAuto = ListDialogAuto.toArray(new String[ListDialogAuto.size()]);
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                                builder1.setTitle("Auto Config Result");

                                TextView title3 = new TextView(this);
                                title3.setText("Auto Config Result");
                                title3.setPadding(10, 10, 10, 10);
                                title3.setGravity(Gravity.CENTER);
                                //title.setBackgroundColor(Color.GRAY);
                                title3.setTextColor(Color.BLACK);
                                title3.setTypeface(title3.getTypeface(), Typeface.BOLD);
                                title3.setTextSize(20);
                                builder1.setCustomTitle(title3);

                                builder1.setItems(SeqListDialogAuto, null);
                                builder1.setCancelable(true);
                                builder1.setPositiveButton(
                                        "OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                                startScan();
                                            }
                                        });


                                AlertDialog alert11 = builder1.create();
                                alert11.show();

                                // Dialog Result End

                                break;
                            }
                            else {
                                    ListDialogAuto.add("OK  -  " + BeaconInConfig);
                                    LogModule.i("*********************** | MainActivity - Lancement de la configuration de l'élément "  + CptListAuto + " | ***********************");
                                    ListAuto.performItemClick(ListAuto.getChildAt(CptListAuto), CptListAuto, ListAuto.getItemIdAtPosition(CptListAuto));
                            }
                        }
                    }
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        unbindService(mServiceConnection);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        switch (checkedId) {
            case R.id.rb_sort_rssi:
                mSortType = SORT_TYPE_RSSI;
                break;

            case R.id.rb_sort_major:
                mSortType = SORT_TYPE_MAJOR;
                break;

            case R.id.rb_sort_minor:
                mSortType = SORT_TYPE_MINOR;
                break;

        }
    }

    private void startScan() {
        if (!MokoSupport.getInstance().isBluetoothOpen()) {
            // Bluetooth n'est pas activé, activez Bluetooth
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, MokoConstants.REQUEST_CODE_ENABLE_BT);
            return;
        }
        animation = AnimationUtils.loadAnimation(this, R.anim.rotate_refresh);
        ivRefresh.startAnimation(animation);
        beaconInfoParseable = new BeaconInfoParseableImpl();
        if (!isLocationPermissionOpen()) {
            return;
        }
        mMokoService.startScanDevice(this);
        mMokoService.mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mMokoService.stopScanDevice();
            }
        }, 1000 * 60);
    }

    @Override
    public void onStartScan() {
        beaconMap.clear();
    }

    @Override
    public void onScanDevice(DeviceInfo device) {
        BeaconInfo beaconInfo = beaconInfoParseable.parseDeviceInfo(device);
        if (beaconInfo == null) {
            return;
        }
        beaconMap.put(beaconInfo.mac, beaconInfo);
        updateDevices();
    }

    @Override
    public void onStopScan() {
        findViewById(R.id.iv_refresh).clearAnimation();
        animation = null;
        updateDevices();
    }

    private void updateDevices() {
        mBeaconInfos.clear();
        if (!TextUtils.isEmpty(mFilterText)) {
            ArrayList<BeaconInfo> beaconXInfosFilter = new ArrayList<>(beaconMap.values());
            Iterator<BeaconInfo> iterator = beaconXInfosFilter.iterator();
            while (iterator.hasNext()) {
                BeaconInfo beaconInfo = iterator.next();
                if (!TextUtils.isEmpty(mFilterText) && (!TextUtils.isEmpty(beaconInfo.name) && (!TextUtils.isEmpty(beaconInfo.mac))
                        && (beaconInfo.name.toLowerCase().contains(mFilterText.toLowerCase()) || beaconInfo.mac.toLowerCase().replaceAll(":", "").contains(mFilterText.toLowerCase())))) {
                    continue;
                } else {
                    iterator.remove();
                }
            }
            mBeaconInfos.addAll(beaconXInfosFilter);
        } else {
            mBeaconInfos.addAll(beaconMap.values());
        }
        // Trier
        switch (mSortType) {
            case SORT_TYPE_RSSI:
                if (!mBeaconInfos.isEmpty()) {
                    Collections.sort(mBeaconInfos, new Comparator<BeaconInfo>() {
                        @Override
                        public int compare(BeaconInfo lhs, BeaconInfo rhs) {
                            if (lhs.rssi > rhs.rssi) {
                                return -1;
                            } else if (lhs.rssi < rhs.rssi) {
                                return 1;
                            }
                            return 0;
                        }
                    });
                }
                break;
            case SORT_TYPE_MAJOR:
                if (!mBeaconInfos.isEmpty()) {
                    Collections.sort(mBeaconInfos, new Comparator<BeaconInfo>() {
                        @Override
                        public int compare(BeaconInfo lhs, BeaconInfo rhs) {
                            if (lhs.major > rhs.major) {
                                return -1;
                            } else if (lhs.major < rhs.major) {
                                return 1;
                            }
                            return 0;
                        }
                    });
                }
                break;
            case SORT_TYPE_MINOR:
                if (!mBeaconInfos.isEmpty()) {
                    Collections.sort(mBeaconInfos, new Comparator<BeaconInfo>() {
                        @Override
                        public int compare(BeaconInfo lhs, BeaconInfo rhs) {
                            if (lhs.minor > rhs.minor) {
                                return -1;
                            } else if (lhs.minor < rhs.minor) {
                                return 1;
                            }
                            return 0;
                        }
                    });
                }
                break;
        }
        mAdapter.notifyDataSetChanged();
        tvDevicesTitle.setText(getString(R.string.device_list_title_num, mBeaconInfos.size()));
    }

    private BeaconParam mBeaconParam;
    private String mPassword;
    private String mThreeAxis;
    private String PasswordAuto;


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (!MokoSupport.getInstance().isBluetoothOpen()) {
            // Bluetooth n'est pas activé, activez Bluetooth
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, MokoConstants.REQUEST_CODE_ENABLE_BT);
            return;
        }
        final BeaconInfo beaconInfo = (BeaconInfo) parent.getItemAtPosition(position);
        LogModule.i("*********************** | MainActivity - Minor sélectionné : " + beaconInfo.minor + " | ***********************");
        BeaconInConfig = String.valueOf(beaconInfo.minor);
        if (beaconInfo != null && !isFinishing()) {
            LogModule.i(beaconInfo.toString());
            if (animation != null) {
                mMokoService.mHandler.removeMessages(0);
                mMokoService.stopScanDevice();
            }

            //final PasswordDialog dialog = new PasswordDialog(this);
            final PasswordDialog dialog = new PasswordDialog(this);
            //dialog.setSavedPassword(mSavedPassword);
            //dialog.AutoSetPassword("Moko4321"); // rentre automatiquement le pass

                    if (!MokoSupport.getInstance().isBluetoothOpen()) {
                        // Bluetooth n'est pas activé, activez Bluetooth
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, MokoConstants.REQUEST_CODE_ENABLE_BT);
                        return;
                    }
                    LogModule.i(mPassword);
                    mThreeAxis = beaconInfo.threeAxis;
                    mMokoService.connDevice(beaconInfo.mac);
                    showLoadingProgressDialog();
                }
        }

    private ProgressDialog mLoadingDialog;

    private void showLoadingProgressDialog() {
        mLoadingDialog = new ProgressDialog(MainActivity.this);
        mLoadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mLoadingDialog.setCanceledOnTouchOutside(false);
        mLoadingDialog.setCancelable(false);
        mLoadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mLoadingDialog.setMessage(getString(R.string.dialog_connecting));
        if (!isFinishing() && mLoadingDialog != null && !mLoadingDialog.isShowing()) {
            mLoadingDialog.show();
        }
    }

    private void dismissLoadingProgressDialog() {
        if (!isFinishing() && mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////// AUTO CONFIG ////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////

    //@Bind(R.id.et_password)
    //EditText etPassword;

    @OnClick({R.id.iv_about, R.id.iv_refresh, R.id.bt_auto})
    public void onClick(View view) {
        LogModule.i("onclick");
        switch (view.getId()) {
            case R.id.iv_about:
                LogModule.i("about");
                startActivity(new Intent(this, AboutActivity.class));
                break;
            case R.id.iv_refresh:
                LogModule.i("Refresh");
                if (!MokoSupport.getInstance().isBluetoothOpen()) {
                    // Bluetooth n'est pas activé, activez Bluetooth
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, MokoConstants.REQUEST_CODE_ENABLE_BT);
                    return;
                }
                if (animation == null) {
                    LogModule.i("*********************** | MainActivity - TEST 7 | ***********************");
                    startScan();
                } else {
                    mMokoService.mHandler.removeMessages(0);
                    mMokoService.stopScanDevice();
                }
                break;
            case R.id.bt_auto:

                // Part autoconfig

                LogModule.i("*********************** | MainActivity - Button Auto config launch | ***********************");

                AutoEnable = "True"; // Enable the auto tracker
                RetryListAuto = "False";
                ListDialogAuto = new ArrayList<String>();

                // Récupérer le nombre d'élèment dans la liste, faire -1 pour la boucle après

                ListAuto = findViewById(R.id.lv_device_list);
                CountListAuto = ListAuto.getCount(); // Nombre d'éléments dans la liste
                LogModule.i("*********************** | MainActivity - Nombre element dans la liste : " + CountListAuto + " | ***********************");


                //ArrayDialogAuto = add

                //Dialog dialog = new Dialog();



                if (CountListAuto == 0){
                    AutoEnable = "False";
                    break;
                }
                else {
                    CptListAuto = 0;
                    ListAuto.performItemClick(ListAuto.getChildAt(CptListAuto), CptListAuto, ListAuto.getItemIdAtPosition(CptListAuto));
                }

                break;
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        switch (position) {
            case 0:
                PowerNumber = 0;
                break;
            case 1:
                PowerNumber = 1;
                break;
            case 2:
                PowerNumber = 2;
                break;
            case 3:
                PowerNumber = 3;
                break;
            case 4:
                PowerNumber = 4;
                break;
            case 5:
                PowerNumber = 5;
                break;
            case 6:
                PowerNumber = 6;
                break;
            case 7:
                PowerNumber = 7;
                break;
        }
        LogModule.i(String.valueOf(PowerNumber));
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        PowerNumber = 0;
    }
}
