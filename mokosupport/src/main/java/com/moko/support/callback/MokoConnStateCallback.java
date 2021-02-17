package com.moko.support.callback;

/**
 * @Date 2017/5/10
 * @Author wenzheng.liu
 * @Description
 */

public interface MokoConnStateCallback {

    /**
     * @Date 2017/5/10
     * @Author wenzheng.liu
     * @Description Connexion réussie
     */
    void onConnectSuccess();

    /**
     * @Date 2017/5/10
     * @Author wenzheng.liu
     * @Description Déconnecté
     */
    void onDisConnected();
}
