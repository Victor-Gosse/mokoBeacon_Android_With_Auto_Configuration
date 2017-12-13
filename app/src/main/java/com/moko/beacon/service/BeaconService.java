package com.moko.beacon.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.moko.beacon.BeaconConstants;
import com.moko.beaconsupport.beacon.BeaconModule;
import com.moko.beaconsupport.callback.BeaconConnStateCallback;
import com.moko.beaconsupport.callback.ScanDeviceCallback;
import com.moko.beaconsupport.log.LogModule;

/**
 * @Date 2017/12/7 0007
 * @Author wenzheng.liu
 * @Description
 * @ClassPath com.moko.beacon.service.BeaconService
 */
public class BeaconService extends Service implements BeaconConnStateCallback {
    private IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        public BeaconService getService() {
            return BeaconService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogModule.i("启动后台服务");
    }

    @Override
    public void onDestroy() {
        LogModule.i("关闭后台服务");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }


    public void startScanDevice(ScanDeviceCallback callback) {
        BeaconModule.getInstance().startScanDevice(callback);
    }

    public void stopScanDevice() {
        BeaconModule.getInstance().stopScanDevice();
    }

    public void connDevice(String address) {
        BeaconModule.getInstance().connDevice(this, address, this);
    }

    @Override
    public void onConnectSuccess() {
        Intent intent = new Intent(BeaconConstants.ACTION_CONNECT_SUCCESS);
        sendOrderedBroadcast(intent, null);
    }

    @Override
    public void onDisConnected() {
        Intent intent = new Intent(BeaconConstants.ACTION_CONNECT_DISCONNECTED);
        sendOrderedBroadcast(intent, null);
    }

}
