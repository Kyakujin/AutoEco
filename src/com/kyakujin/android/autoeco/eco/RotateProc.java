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

import com.kyakujin.android.autoeco.Conf;
import com.kyakujin.android.autoeco.Conf.EcoExecFrom;
import com.kyakujin.android.autoeco.Logger;
import com.kyakujin.android.autoeco.db.AutoEcoContract.EcoTbl;
import com.kyakujin.android.autoeco.db.dao.EcoDAO;

import android.content.Context;
import android.provider.Settings;

/**
 * 画面回転の有効/無効を制御するクラス
 */
public class RotateProc extends EcoProc {
    private final String TAG = Conf.APP_NAME + ":" + this.getClass().getSimpleName();
    private static final int ROTATE_ON = 1;
    private static final int ROTATE_OFF = 0;

    public RotateProc(AbstractEco eco) {
        super(eco);
    }

    @Override
    protected boolean isImplemented() {
        return true;
    }

    @Override
    protected boolean isEnabled() {
        EcoDAO dao = new EcoDAO(getContext());
        return dao.isEcoEnabledById(getId(), getFrom(), EcoTbl.ROTATE_ENABLED);
    }

    @Override
    protected void turnOn() {
        Logger.d(TAG, "Do Rotate ON :id=" + getId());
        setRotate(ROTATE_ON);
    }

    @Override
    protected void turnOff() {
        Logger.d(TAG, "Do Rotate OFF :id=" + getId());
        setRotate(ROTATE_OFF);
    }

    private void setRotate(int i) {
        try {
            Settings.System.putInt(getContext().getContentResolver(),
                    Settings.System.ACCELEROMETER_ROTATION, i);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
