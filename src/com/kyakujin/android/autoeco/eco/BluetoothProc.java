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

import android.bluetooth.BluetoothAdapter;
import android.content.Context;

import com.kyakujin.android.autoeco.Conf;
import com.kyakujin.android.autoeco.Conf.EcoExecFrom;
import com.kyakujin.android.autoeco.Logger;
import com.kyakujin.android.autoeco.db.AutoEcoContract.EcoTbl;
import com.kyakujin.android.autoeco.db.dao.EcoDAO;

/**
 * Bluetooth機能の有効/無効を制御するクラス
 */
public class BluetoothProc extends EcoProc {
    private final String TAG = Conf.APP_NAME + ":" + this.getClass().getSimpleName();
    BluetoothAdapter mBt = null;

    public BluetoothProc(AbstractEco eco) {
        super(eco);
        mBt = BluetoothAdapter.getDefaultAdapter();        
    }

    @Override
    protected boolean isImplemented() {
        if(mBt == null) {
            Logger.d(TAG, "Bluetooth not implemented.");                    
            return false;
        } else {
            return true;
        }
    }

    @Override
    protected boolean isEnabled() {
        EcoDAO dao = new EcoDAO(getContext());
        return dao.isEcoEnabledById(getId(), getFrom(), EcoTbl.BLUETOOTH_ENABLED);
    }

    @Override
    protected void turnOn() {
        Logger.d(TAG, "Do Bluetooth ON :id="+getId());   
        mBt.enable();
    }

    @Override
    protected void turnOff() {
        Logger.d(TAG, "Do Bluetooth OFF");        
        mBt.disable();
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
