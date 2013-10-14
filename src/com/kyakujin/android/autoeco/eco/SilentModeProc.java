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
import android.media.AudioManager;

/**
 * マナーモード設定を制御するクラス
 */
public class SilentModeProc extends EcoProc {
    private final String TAG = Conf.APP_NAME + ":" + this.getClass().getSimpleName();

    private AudioManager mAm = null;
    private int mMode = 0;

    public SilentModeProc(AbstractEco eco) {
        super(eco);
        mAm = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        EcoDAO dao = new EcoDAO(getContext());
        mMode = dao.searchSilentModeById(getId(), getFrom());
    }

    @Override
    protected boolean isImplemented() {
        return true;
    }

    @Override
    protected boolean isEnabled() {
        EcoDAO dao = new EcoDAO(getContext());
        return dao.isEcoEnabledById(getId(), getFrom(), EcoTbl.SILENT_ENABLED);
    }
    
    // RINGER_MODE_NORMAL(2) ノーマルモード(着信音、バイブレーションを有効)にします
    // RINGER_MODE_SILENT(0) サイレントモード(着信音、バイブレーションを無効)にします
    // RINGER_MODE_VIBRATE(1) バイブレーションモード(バイブレーションのみ有効)にします    
    @Override
    protected void turnOn() {
        Logger.d(TAG, "Do SilentMode set:id=" + getId());
        Logger.d(TAG, "マナーモード種別:" + mMode);
        mAm.setRingerMode(mMode);
    }

    @Override
    protected void turnOff() {
        // mAm.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        Logger.d(TAG, "SilentMode do nothing...:id=" + getId());        
        /** DO NOTHING */
    }
    
    public void setMode(int mode) {
        mMode = mode;
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
