package com.moko.beaconsupport.task;

import com.moko.beaconsupport.callback.OrderTaskCallback;
import com.moko.beaconsupport.entity.OrderType;
import com.moko.beaconsupport.utils.Utils;

/**
 * @Date 2017/12/14 0014
 * @Author wenzheng.liu
 * @Description
 * @ClassPath com.moko.beaconsupport.task.SerialIDTask
 */
public class SerialIDTask extends OrderTask {

    public byte[] data;

    public SerialIDTask(OrderTaskCallback callback, int sendDataType) {
        super(OrderType.serialID, callback, sendDataType);
    }

    public void setData(String deviceId) {
        data = Utils.hex2bytes(Utils.string2Hex(deviceId));
    }

    @Override
    public byte[] assemble() {
        return data;
    }
}
