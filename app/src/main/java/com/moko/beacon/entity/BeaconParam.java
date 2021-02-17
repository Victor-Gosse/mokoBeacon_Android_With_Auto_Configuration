package com.moko.beacon.entity;

import java.io.Serializable;

/**
 * @Date 2017/12/14 0014
 * @Author wenzheng.liu
 * @Description
 * @ClassPath com.moko.beacon.entity.BeaconParam
 */
public class BeaconParam implements Serializable {
    // Puissance de l'appareil
    public String battery;
    public String uuid;
    public String major;
    public String minor;
    // Distance d'étalonnage
    public String measurePower;
    // Puissance de diffusion
    public String transmission;
    // Cycle de diffusion
    public String broadcastingInterval;
    // ID de l'appareil
    public String serialID;
    // Adresse MAC
    public String iBeaconMAC;
    // Nom de l'appareil
    public String iBeaconName;
    // Mode de connexion
    public String connectionMode;
    // Informations système
    public BeaconDeviceInfo beaconInfo;
    // Mot de passe
    public String password;
    // Données triaxiales
    public String threeAxis;

    @Override
    public String toString() {
        return "BeaconParam{" +
                "battery=" + battery +
                ", uuid='" + uuid + '\'' +
                ", major=" + major +
                ", minor=" + minor +
                ", measurePower='" + measurePower + '\'' +
                ", transmission=" + transmission +
                ", broadcastingInterval=" + broadcastingInterval +
                ", serialID='" + serialID + '\'' +
                ", iBeaconMAC='" + iBeaconMAC + '\'' +
                ", iBeaconName='" + iBeaconName + '\'' +
                ", connectionMode='" + connectionMode + '\'' +
                ", beaconInfo=" + beaconInfo.toString() +
                '}';
    }
}
