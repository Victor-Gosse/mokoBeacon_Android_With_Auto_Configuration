package com.moko.support.callback;

import com.moko.support.entity.OrderType;

/**
 * @Date 2017/5/10
 * @Author wenzheng.liu
 * @Description Classe de rappel de données de retour
 * @ClassPath com.moko.support.callback.OrderCallback
 */
public interface MokoOrderTaskCallback {

    void onOrderResult(OrderType orderType, byte[] value, int responseType);

    void onOrderTimeout(OrderType orderType);

    void onOrderFinish();
}
