package com.moko.support.task;


import com.moko.support.callback.MokoOrderTaskCallback;
import com.moko.support.entity.OrderType;

/**
 * @Date 2017/12/14 0014
 * @Author wenzheng.liu
 * @Description
 * @ClassPath com.moko.support.task.DevicenameTask
 */
public class DevicenameTask extends OrderTask {

    public byte[] data;

    public DevicenameTask(MokoOrderTaskCallback callback, int sendDataType) {
        super(OrderType.devicename, callback, sendDataType);
    }

    @Override
    public byte[] assemble() {
        return data;
    }
}
