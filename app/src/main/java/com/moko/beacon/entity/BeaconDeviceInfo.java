package com.moko.beacon.entity;

import java.io.Serializable;

/**
 * @Date 2017/12/14 0014
 * @Author wenzheng.liu
 * @Description
 * @ClassPath com.moko.beacon.entity.BeaconDeviceInfo
 */
public class BeaconDeviceInfo implements Serializable{
    // Version du logiciel
    public String softVersion;
    // Constructeur
    public String firmname;
    // Modèle de produit
    public String deviceName;
    // Date de production
    public String iBeaconDate;
    // Adresse MAC
    public String iBeaconMac;
    // Modèle de puce
    public String chipModel;
    // Version du matériel
    public String hardwareVersion;
    // Version du firmware
    public String firmwareVersion;
    // Durée
    public String runtime;

    @Override
    public String toString() {
        return "BeaconDeviceInfo{" +
                "softVersion='" + softVersion + '\'' +
                ", firmname='" + firmname + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", iBeaconDate='" + iBeaconDate + '\'' +
                ", iBeaconMac='" + iBeaconMac + '\'' +
                ", chipModel='" + chipModel + '\'' +
                ", hardwareVersion='" + hardwareVersion + '\'' +
                ", firmwareVersion='" + firmwareVersion + '\'' +
                ", runtime='" + runtime + '\'' +
                '}';
    }
}
