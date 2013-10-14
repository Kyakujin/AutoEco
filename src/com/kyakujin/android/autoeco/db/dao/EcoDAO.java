/*
 * Copyright 2013 Yoshihiro Miyama
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kyakujin.android.autoeco.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.media.AudioManager;
import android.net.Uri;

import com.kyakujin.android.autoeco.Conf;
import com.kyakujin.android.autoeco.Conf.EcoExecFrom;
import com.kyakujin.android.autoeco.Conf.SleepTime;
import com.kyakujin.android.autoeco.db.AutoEcoContract.EcoQuery;
import com.kyakujin.android.autoeco.db.AutoEcoContract.EcoTbl;

/**
 * Data Access Object for Eco Table.
 */
public class EcoDAO {

    Context mContext;

    public EcoDAO(Context context) {
        super();
        mContext = context;
    }

    private EcoModel createDefaultEcoModel() {
        EcoModel model = new EcoModel();
        model.setName(Conf.NONE);
        model.setWifiEnabled(true);
        model.setBluetoothEnabled(true);
        model.setSyncEnabled(true);
        model.setRotateEnabled(true);
        model.setBrightnessEnabled(true);
        model.setBrightnessValue(20);
        model.setBrightnessAuto(false);
        model.setSilentEnabled(true);
        model.setSilentMode(AudioManager.RINGER_MODE_NORMAL);
        model.setSleepEnabled(true);
        model.setSleepTime(1);
        return model;
    }

    private ContentValues createDefultContentValues() {
        EcoModel model = createDefaultEcoModel();
        ContentValues val = new ContentValues();
        val.put(EcoTbl.NAME, model.getName());
        val.put(EcoTbl.WIFI_ENABLED, model.getWifiEnabled() == true ? 1 : 0);
        val.put(EcoTbl.BLUETOOTH_ENABLED, model.getBluetoothEnabled() == true ? 1 : 0);
        val.put(EcoTbl.BRIGHTNESS_ENABLED, model.getBrightnessEnabled() == true ? 1 : 0);
        val.put(EcoTbl.BRIGHTNESS_VALUE, model.getBrightnessValue());
        val.put(EcoTbl.BRIGHTNESS_ENABLED, model.getBrightnessAuto() == true ? 1 : 0);
        val.put(EcoTbl.ROTATE_ENABLED, model.getRotateEnabled() == true ? 1 : 0);
        val.put(EcoTbl.SILENT_ENABLED, model.getSilentEnabled() == true ? 1 : 0);
        val.put(EcoTbl.SILENT_MODE, model.getSilentMode());
        val.put(EcoTbl.SLEEP_ENABLED, model.getSleepEnabled() == true ? 1 : 0);
        val.put(EcoTbl.SLEEP_TIME, model.getSleepTimeOrdinal());
        val.put(EcoTbl.SYNC_ENABLED, model.getSyncEnabled() == true ? 1 : 0);
        return val;
    }

    public Uri insertDefaultEco() {
        return insertEco(createDefaultEcoModel());
    }

    public Uri insertEco(EcoModel model) {
        ContentValues val = createDefultContentValues();
        val.put(EcoTbl.NAME, model.getName());
        val.put(EcoTbl.WIFI_ENABLED, model.getWifiEnabled() == true ? 1 : 0);
        val.put(EcoTbl.BLUETOOTH_ENABLED, model.getBluetoothEnabled() == true ? 1 : 0);
        val.put(EcoTbl.BRIGHTNESS_ENABLED, model.getBrightnessEnabled() == true ? 1 : 0);
        val.put(EcoTbl.BRIGHTNESS_VALUE, model.getBrightnessValue());
        val.put(EcoTbl.BRIGHTNESS_AUTO, model.getBrightnessAuto() == true ? 1 : 0);
        val.put(EcoTbl.ROTATE_ENABLED, model.getRotateEnabled() == true ? 1 : 0);
        val.put(EcoTbl.SILENT_ENABLED, model.getSilentEnabled() == true ? 1 : 0);
        val.put(EcoTbl.SILENT_MODE, model.getSilentMode());
        val.put(EcoTbl.SLEEP_ENABLED, model.getSleepEnabled() == true ? 1 : 0);
        val.put(EcoTbl.SLEEP_TIME, model.getSleepTimeOrdinal());
        val.put(EcoTbl.SYNC_ENABLED, model.getSyncEnabled() == true ? 1 : 0);
        return mContext.getContentResolver().insert(EcoTbl.CONTENT_URI, val);
    }

    public void updateEco(EcoModel model) {
        ContentValues val = new ContentValues();
        val.put(EcoTbl.NAME, model.getName());
        val.put(EcoTbl.WIFI_ENABLED, model.getWifiEnabled() == true ? 1 : 0);
        val.put(EcoTbl.BLUETOOTH_ENABLED, model.getBluetoothEnabled() == true ? 1 : 0);
        val.put(EcoTbl.BRIGHTNESS_ENABLED, model.getBrightnessEnabled() == true ? 1 : 0);
        val.put(EcoTbl.BRIGHTNESS_VALUE, model.getBrightnessValue());
        val.put(EcoTbl.BRIGHTNESS_AUTO, model.getBrightnessAuto() == true ? 1 : 0);
        val.put(EcoTbl.ROTATE_ENABLED, model.getRotateEnabled() == true ? 1 : 0);
        val.put(EcoTbl.SILENT_ENABLED, model.getSilentEnabled() == true ? 1 : 0);
        val.put(EcoTbl.SILENT_MODE, model.getSilentMode());
        val.put(EcoTbl.SLEEP_ENABLED, model.getSleepEnabled() == true ? 1 : 0);
        val.put(EcoTbl.SLEEP_TIME, model.getSleepTimeOrdinal());
        val.put(EcoTbl.SYNC_ENABLED, model.getSyncEnabled() == true ? 1 : 0);
        mContext.getContentResolver().update(Uri.withAppendedPath(EcoTbl.CONTENT_URI,
                String.valueOf(model.getId())), val, null, null);

        return;
    }

    public void updateWifiEnabled(int ecoId, boolean value) {
        ContentValues val = new ContentValues();
        val.put(EcoTbl.WIFI_ENABLED, value == true ? 1 : 0);
        mContext.getContentResolver().update(Uri.withAppendedPath(EcoTbl.CONTENT_URI,
                String.valueOf(ecoId)), val, null, null);

        return;
    }

    public void updateBluetoothEnabled(int ecoId, boolean value) {
        ContentValues val = new ContentValues();
        val.put(EcoTbl.BLUETOOTH_ENABLED, value == true ? 1 : 0);
        mContext.getContentResolver().update(Uri.withAppendedPath(EcoTbl.CONTENT_URI,
                String.valueOf(ecoId)), val, null, null);

        return;
    }

    public void updateRotateEnabled(int ecoId, boolean value) {
        ContentValues val = new ContentValues();
        val.put(EcoTbl.ROTATE_ENABLED, value == true ? 1 : 0);
        mContext.getContentResolver().update(Uri.withAppendedPath(EcoTbl.CONTENT_URI,
                String.valueOf(ecoId)), val, null, null);

        return;
    }

    public void updateSyncEnabled(int ecoId, boolean value) {
        ContentValues val = new ContentValues();
        val.put(EcoTbl.SYNC_ENABLED, value == true ? 1 : 0);
        mContext.getContentResolver().update(Uri.withAppendedPath(EcoTbl.CONTENT_URI,
                String.valueOf(ecoId)), val, null, null);

        return;
    }

    public void updateBrightnessValue(int ecoId, int value) {
        ContentValues val = new ContentValues();
        val.put(EcoTbl.BRIGHTNESS_VALUE, value);
        mContext.getContentResolver().update(Uri.withAppendedPath(EcoTbl.CONTENT_URI,
                String.valueOf(ecoId)), val, null, null);

        return;
    }

    public void updateBrightnessAuto(int ecoId, boolean enabled) {
        ContentValues val = new ContentValues();
        val.put(EcoTbl.BRIGHTNESS_AUTO, enabled);
        mContext.getContentResolver().update(Uri.withAppendedPath(EcoTbl.CONTENT_URI,
                String.valueOf(ecoId)), val, null, null);

        return;
    }

    public void updateSilentMode(int ecoId, int mode) {
        ContentValues val = new ContentValues();
        val.put(EcoTbl.SILENT_MODE, mode);
        mContext.getContentResolver().update(Uri.withAppendedPath(EcoTbl.CONTENT_URI,
                String.valueOf(ecoId)), val, null, null);
        return;
    }

    public void updateSleepTime(int ecoId, int time) {
        ContentValues val = new ContentValues();
        val.put(EcoTbl.SLEEP_TIME, time);
        mContext.getContentResolver().update(Uri.withAppendedPath(EcoTbl.CONTENT_URI,
                String.valueOf(ecoId)), val, null, null);
        return;
    }

    public EcoModel readToEcoModelByCursor(Cursor c) {
        EcoModel model = new EcoModel();
        if (c != null && c.moveToFirst()) {
            model.setId(c.getInt(EcoQuery.Idx._ID.ordinal()));
            model.setName(c.getString(EcoQuery.Idx.NAME.ordinal()));
            model.setWifiEnabled(c.getInt(EcoQuery.Idx.WIFI_ENABLED.ordinal()) == 1 ? true : false);
            model.setBluetoothEnabled(c.getInt(EcoQuery.Idx.BLUETOOTH_ENABLED.ordinal()) == 1 ? true
                    : false);
            model.setRotateEnabled(c.getInt(EcoQuery.Idx.ROTATE_ENABLED.ordinal()) == 1 ? true
                    : false);
            model.setSyncEnabled(c.getInt(EcoQuery.Idx.SYNC_ENABLED.ordinal()) == 1 ? true : false);
            model.setBrightnessEnabled(c.getInt(EcoQuery.Idx.BRIGHTNESS_ENABLED.ordinal()) == 1 ? true
                    : false);
            model.setBrightnessValue(c.getInt(EcoQuery.Idx.BRIGHTNESS_VALUE.ordinal()));
            model.setBrightnessAuto(c.getInt(EcoQuery.Idx.BRIGHTNESS_AUTO.ordinal()) == 1 ? true
                    : false);
            model.setSilentEnabled(c.getInt(EcoQuery.Idx.SILENT_ENABLED.ordinal()) == 1 ? true
                    : false);
            model.setSilentMode(c.getInt(EcoQuery.Idx.SILENT_MODE.ordinal()));
            model.setSleepEnabled(c.getInt(EcoQuery.Idx.SLEEP_ENABLED.ordinal()) == 1 ? true
                    : false);
            model.setSleepTime(c.getInt(EcoQuery.Idx.SLEEP_TIME.ordinal()));
        }
        return model;
    }

    public EcoModel readToEcoModelById(int id) {
        EcoModel model = new EcoModel();
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(Uri.withAppendedPath(EcoTbl.CONTENT_URI,
                    String.valueOf(id)), EcoQuery.PROJECTION, null, null, null);

            if (c != null && c.moveToFirst()) {
                model.setId(c.getInt(EcoQuery.Idx._ID.ordinal()));
                model.setName(c.getString(EcoQuery.Idx.NAME.ordinal()));
                model.setWifiEnabled(c.getInt(EcoQuery.Idx.WIFI_ENABLED.ordinal()) == 1 ? true
                        : false);
                model.setBluetoothEnabled(c.getInt(EcoQuery.Idx.BLUETOOTH_ENABLED.ordinal()) == 1 ? true
                        : false);
                model.setRotateEnabled(c.getInt(EcoQuery.Idx.ROTATE_ENABLED.ordinal()) == 1 ? true
                        : false);
                model.setSyncEnabled(c.getInt(EcoQuery.Idx.SYNC_ENABLED.ordinal()) == 1 ? true
                        : false);
                model.setBrightnessEnabled(c.getInt(EcoQuery.Idx.BRIGHTNESS_ENABLED.ordinal()) == 1 ? true
                        : false);
                model.setBrightnessValue(c.getInt(EcoQuery.Idx.BRIGHTNESS_VALUE.ordinal()));
                model.setBrightnessAuto(c.getInt(EcoQuery.Idx.BRIGHTNESS_AUTO.ordinal()) == 1 ? true
                        : false);
                model.setSilentEnabled(c.getInt(EcoQuery.Idx.SILENT_ENABLED.ordinal()) == 1 ? true
                        : false);
                model.setSilentMode(c.getInt(EcoQuery.Idx.SILENT_MODE.ordinal()));
                model.setSleepEnabled(c.getInt(EcoQuery.Idx.SLEEP_ENABLED.ordinal()) == 1 ? true
                        : false);
                model.setSleepTime(c.getInt(EcoQuery.Idx.SLEEP_TIME.ordinal()));
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return model;
    }

    public boolean isEcoEnabledById(int id, EcoExecFrom from, String ecotype) {
        MappingDAO dao = new MappingDAO(mContext);
        int ecoId;
        switch (from) {
            case SCHED:
                ecoId = dao.searchEcoIdBySchedId(id);
                break;
            case BATTERY:
                ecoId = dao.searchEcoIdByBatteryId(id);
                break;
            case MANUAL:
                ecoId = dao.searchEcoIdByManualId(id);
                break;
            default:
                ecoId = 0;
        }

        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(Uri.withAppendedPath(EcoTbl.CONTENT_URI,
                    String.valueOf(ecoId)), EcoQuery.PROJECTION, ecotype + "='1'", null, null);
            if (c != null && c.moveToFirst()) {
                return true;
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }

        return false;
    }

    public int searchSilentModeById(int id, EcoExecFrom from) {
        MappingDAO dao = new MappingDAO(mContext);
        int ecoId;
        switch (from) {
            case SCHED:
                ecoId = dao.searchEcoIdBySchedId(id);
                break;
            case BATTERY:
                ecoId = dao.searchEcoIdByBatteryId(id);
                break;
            case MANUAL:
                ecoId = dao.searchMappingIdByManualId(id);
                break;
            default:
                ecoId = 0;
        }

        EcoModel model = new EcoModel();
        model = readToEcoModelById(ecoId);
        return model.getSilentMode();
    }

    public SleepTime searchSleepTimeById(int id, EcoExecFrom from) {
        MappingDAO dao = new MappingDAO(mContext);
        int ecoId;
        switch (from) {
            case SCHED:
                ecoId = dao.searchEcoIdBySchedId(id);
                break;
            case BATTERY:
                ecoId = dao.searchEcoIdByBatteryId(id);
                break;
            case MANUAL:
                ecoId = dao.searchMappingIdByManualId(id);
                break;
            default:
                ecoId = 0;
        }

        EcoModel model = new EcoModel();
        model = readToEcoModelById(ecoId);
        return Conf.mapSleepTime.get(model.getSleepTimeOrdinal());
    }

    public int searchBrightnessById(int id, EcoExecFrom from) {
        MappingDAO dao = new MappingDAO(mContext);
        int ecoId;
        switch (from) {
            case SCHED:
                ecoId = dao.searchEcoIdBySchedId(id);
                break;
            case BATTERY:
                ecoId = dao.searchEcoIdByBatteryId(id);
                break;
            case MANUAL:
                ecoId = dao.searchMappingIdByManualId(id);
                break;
            default:
                ecoId = 0;
        }

        EcoModel model = new EcoModel();
        model = readToEcoModelById(ecoId);
        return model.getBrightnessValue();
    }

    public boolean searchAutoBrightnessById(int id, EcoExecFrom from) {
        MappingDAO dao = new MappingDAO(mContext);
        int ecoId;
        switch (from) {
            case SCHED:
                ecoId = dao.searchEcoIdBySchedId(id);
                break;
            case BATTERY:
                ecoId = dao.searchEcoIdByBatteryId(id);
                break;
            case MANUAL:
                ecoId = dao.searchMappingIdByManualId(id);
                break;
            default:
                ecoId = 0;
        }

        EcoModel model = new EcoModel();
        model = readToEcoModelById(ecoId);
        return model.getBrightnessAuto();
    }

    public String getTimeStamp(int id, EcoExecFrom from) {
        String time = "";
        MappingDAO dao = new MappingDAO(mContext);
        int ecoId;
        switch (from) {
            case SCHED:
                ecoId = dao.searchEcoIdBySchedId(id);
                break;
            case BATTERY:
                ecoId = dao.searchEcoIdByBatteryId(id);
                break;
            case MANUAL:
                ecoId = dao.searchMappingIdByManualId(id);
                break;
            default:
                ecoId = 0;
        }

        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(Uri.withAppendedPath(EcoTbl.CONTENT_URI,
                    String.valueOf(ecoId)), EcoQuery.PROJECTION, null, null, null);
            if (c != null && c.moveToFirst()) {
                time = c.getString(EcoQuery.Idx.UPDATE_DATE.ordinal());
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }

        return time;
    }

}
