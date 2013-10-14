package com.kyakujin.android.autoeco.db.dao;

/**
 * Entity Class of Eco.
 */
public class EcoModel {
    private int id;
    private String name;
    private boolean bluetoothEnabled;
    private boolean brightnessEnabled;
    private int brightnessValue;
    private boolean brightnessAuto;
    private boolean rotateEnabed;
    private boolean silentEnabled;
    private int silentMode;
    private boolean sleepEnabled;
    private int sleepTime;
    private boolean syncEnabled;
    private boolean wifiEnabled;
    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public boolean getBluetoothEnabled() {
        return bluetoothEnabled;
    }
    public void setBluetoothEnabled(boolean bluetoothEnabled) {
        this.bluetoothEnabled = bluetoothEnabled;
    }
    public boolean getBrightnessEnabled() {
        return brightnessEnabled;
    }
    public void setBrightnessEnabled(boolean brightnessEnabled) {
        this.brightnessEnabled = brightnessEnabled;
    }
    public int getBrightnessValue() {
        return brightnessValue;
    }
    public void setBrightnessValue(int brightnessValue) {
        this.brightnessValue = brightnessValue;
    }
    public boolean getBrightnessAuto() {
        return brightnessAuto;
    }
    public void setBrightnessAuto(boolean brightnessAuto) {
        this.brightnessAuto = brightnessAuto;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public boolean getRotateEnabled() {
        return rotateEnabed;
    }
    public void setRotateEnabled(boolean rotateEnabed) {
        this.rotateEnabed = rotateEnabed;
    }
    public boolean getSilentEnabled() {
        return silentEnabled;
    }
    public void setSilentEnabled(boolean silentEnabled) {
        this.silentEnabled = silentEnabled;
    }
    public int getSilentMode() {
        return silentMode;
    }
    public void setSilentMode(int silentMode) {
        this.silentMode = silentMode;
    }
    public boolean getSleepEnabled() {
        return sleepEnabled;
    }
    public void setSleepEnabled(boolean sleepEnabled) {
        this.sleepEnabled = sleepEnabled;
    }
    public int getSleepTimeOrdinal() {
        return sleepTime;
    }
    public void setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
    }
    public boolean getSyncEnabled() {
        return syncEnabled;
    }
    public void setSyncEnabled(boolean syncEnabled) {
        this.syncEnabled = syncEnabled;
    }
    public boolean getWifiEnabled() {
        return wifiEnabled;
    }
    public void setWifiEnabled(boolean wifiEnabled) {
        this.wifiEnabled = wifiEnabled;
    }
}
