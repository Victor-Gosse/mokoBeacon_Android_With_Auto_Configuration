package com.moko.beacon.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.moko.beacon.BeaconConstants;
import com.moko.beacon.R;
import com.moko.beacon.service.BeaconService;
import com.moko.beacon.utils.ToastUtils;
import com.moko.beaconsupport.entity.OrderType;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @Date 2017/12/18 0018
 * @Author wenzheng.liu
 * @Description
 * @ClassPath com.moko.beacon.activity.SetDeviceIdActivity
 */
public class SetDeviceIdActivity extends Activity {
    @Bind(R.id.et_device_id)
    EditText etDeviceId;
    private BeaconService mBeaconService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_id);
        ButterKnife.bind(this);
        bindService(new Intent(this, BeaconService.class), mServiceConnection, BIND_AUTO_CREATE);
        String deviceId = getIntent().getStringExtra(BeaconConstants.EXTRA_KEY_DEVICE_DEVICE_ID);

        etDeviceId.setText(deviceId);
        etDeviceId.setSelection(String.valueOf(deviceId).length());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        unbindService(mServiceConnection);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            abortBroadcast();
            if (intent != null) {
                String action = intent.getAction();
                if (BeaconConstants.ACTION_CONNECT_DISCONNECTED.equals(action)) {
                    ToastUtils.showToast(SetDeviceIdActivity.this, "设备断开连接");
                    SetDeviceIdActivity.this.setResult(BeaconConstants.RESULT_CONN_DISCONNECTED);
                    finish();
                }
                if (BeaconConstants.ACTION_RESPONSE_TIMEOUT.equals(action)) {
                    OrderType orderType = (OrderType) intent.getSerializableExtra(BeaconConstants.EXTRA_KEY_RESPONSE_ORDER_TYPE);
                    switch (orderType) {
                        case serialID:
                            // 修改deviceId失败
                            ToastUtils.showToast(SetDeviceIdActivity.this, "修改deviceId失败");
                            finish();
                            break;
                    }
                }
                if (BeaconConstants.ACTION_RESPONSE_SUCCESS.equals(action)) {
                    OrderType orderType = (OrderType) intent.getSerializableExtra(BeaconConstants.EXTRA_KEY_RESPONSE_ORDER_TYPE);
                    switch (orderType) {
                        case serialID:
                            // 修改deviceId成功
                            ToastUtils.showToast(SetDeviceIdActivity.this, "修改deviceId成功");
                            Intent i = new Intent();
                            i.putExtra(BeaconConstants.EXTRA_KEY_DEVICE_DEVICE_ID, etDeviceId.getText().toString());
                            SetDeviceIdActivity.this.setResult(RESULT_OK, i);
                            finish();
                            break;
                    }
                }
            }
        }
    };

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBeaconService = ((BeaconService.LocalBinder) service).getService();
            // 注册广播接收器
            IntentFilter filter = new IntentFilter();
            filter.addAction(BeaconConstants.ACTION_CONNECT_SUCCESS);
            filter.addAction(BeaconConstants.ACTION_CONNECT_DISCONNECTED);
            filter.addAction(BeaconConstants.ACTION_RESPONSE_SUCCESS);
            filter.addAction(BeaconConstants.ACTION_RESPONSE_TIMEOUT);
            filter.addAction(BeaconConstants.ACTION_RESPONSE_FINISH);
            filter.setPriority(300);
            registerReceiver(mReceiver, filter);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @OnClick({R.id.tv_back, R.id.iv_save})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.iv_save:
                if (TextUtils.isEmpty(etDeviceId.getText().toString())) {
                    ToastUtils.showToast(this, "deviceId is empty");
                    return;
                }
                if (etDeviceId.getText().toString().length() > 5){
                    ToastUtils.showToast(this, "deviceId长度不能超过5");
                    return;
                }
                mBeaconService.sendOrder(mBeaconService.setSerialID(etDeviceId.getText().toString()));
                break;

        }
    }
}
