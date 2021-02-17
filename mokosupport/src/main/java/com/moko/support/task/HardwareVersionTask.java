package com.moko.support.task;


import com.moko.support.callback.MokoOrderTaskCallback;
import com.moko.support.entity.OrderType;

/**
 * @Date 2017/12/14 0014
 * @Author wenzheng.liu
 * @Description
 * @ClassPath com.moko.support.task.HardwareVersionTask
 */
public class HardwareVersionTask extends OrderTask {

    public byte[] data;

    public HardwareVersionTask(MokoOrderTaskCallback callback, int sendDataType) {
        super(OrderType.hardwareVersion, callback, sendDataType);
    }

    @Override
    public byte[] assemble() {
        return data;
    }
}
