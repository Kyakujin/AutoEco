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

package com.kyakujin.android.autoeco.eco;

import android.content.Context;
import android.net.wifi.WifiManager;

import com.kyakujin.android.autoeco.Conf;
import com.kyakujin.android.autoeco.Conf.EcoExecFrom;
import com.kyakujin.android.autoeco.Logger;
import com.kyakujin.android.autoeco.db.AutoEcoContract.EcoTbl;
import com.kyakujin.android.autoeco.db.dao.EcoDAO;

/**
 * Wi-Fi機能の有効/無効を制御するクラス
 */
public class WifiProc extends EcoProc {
    private final String TAG = Conf.APP_NAME + ":" + this.getClass().getSimpleName();

    WifiManager mManager;

    public WifiProc(AbstractEco eco) {
        super(eco);
        mManager = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
    }

    @Override
    protected boolean isImplemented() {
        //return mManager.isWifiEnabled();
        return true;
    }

    @Override
    protected boolean isEnabled() {
        EcoDAO dao = new EcoDAO(getContext());
        return dao.isEcoEnabledById(getId(), getFrom(), EcoTbl.WIFI_ENABLED);
    }

    @Override
    protected void turnOn() {
        Logger.d(TAG, "Do WiFi ON :id=" + getId());
        mManager.setWifiEnabled(true);
    }

    @Override
    protected void turnOff() {
        Logger.d(TAG, "Do WiFi OFF");
        mManager.setWifiEnabled(false);
    }

    @Override
    protected Context getContext() {
        return eco.getContext();
    }

    @Override
    protected int getId() {
        return eco.getId();
    }

    @Override
    protected EcoExecFrom getFrom() {
        return eco.getFrom();
    }
}
