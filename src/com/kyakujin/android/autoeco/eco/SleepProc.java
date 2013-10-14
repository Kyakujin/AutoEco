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
import android.provider.Settings;

import com.kyakujin.android.autoeco.Conf;
import com.kyakujin.android.autoeco.Conf.EcoExecFrom;
import com.kyakujin.android.autoeco.Conf.SleepTime;
import com.kyakujin.android.autoeco.Logger;
import com.kyakujin.android.autoeco.db.AutoEcoContract.EcoTbl;
import com.kyakujin.android.autoeco.db.dao.EcoDAO;

/**
 * スリープ設定を制御するクラス
 */
public class SleepProc extends EcoProc {
    private final String TAG = Conf.APP_NAME + ":" + this.getClass().getSimpleName();
    private int mTime; 
    
    public SleepProc(AbstractEco eco) {
        super(eco);
        EcoDAO dao = new EcoDAO(getContext());
        SleepTime time = dao.searchSleepTimeById(getId(), getFrom());
        mTime = time.toTimeValue();
    }

    @Override
    protected boolean isImplemented() {
        return true;
    }

    @Override
    protected boolean isEnabled() {
        EcoDAO dao = new EcoDAO(getContext());
        return dao.isEcoEnabledById(getId(), getFrom(), EcoTbl.SLEEP_ENABLED);
    }

    @Override
    protected void turnOn() {
        Logger.d(TAG, "Do Sleep ON :id="+getId());   
        int def =
        Settings.System.getInt(getContext().getContentResolver(),Settings.System.SCREEN_OFF_TIMEOUT,-1);
        //スクリーンオフの時間を設定
        Settings.System.putInt(getContext().getContentResolver(),
        Settings.System.SCREEN_OFF_TIMEOUT, mTime);
    }

    @Override
    protected void turnOff() {
        Logger.d(TAG, "Do Sleep OFF");    
        /** DO NOTHING*/
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
