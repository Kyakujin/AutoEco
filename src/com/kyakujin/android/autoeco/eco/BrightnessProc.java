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

import android.app.Activity;
import android.content.Context;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.view.WindowManager.LayoutParams;
import android.widget.Toast;

/**
 * 画面輝度設定を行うクラス
 */
public class BrightnessProc extends EcoProc {
    private final String TAG = Conf.APP_NAME + ":" + this.getClass().getSimpleName();

    private float mSystemBrightness = 20;
    private Activity mActivity;

    public BrightnessProc(AbstractEco eco, Activity activity) {
        super(eco);
        this.mActivity = activity;
        try {
            Settings.System.getInt(mActivity.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS);
        } catch (SettingNotFoundException e) {
            e.printStackTrace();
        }

        EcoDAO dao = new EcoDAO(getContext());
        mSystemBrightness = dao.searchBrightnessById(getId(), getFrom());
    }

    @Override
    protected boolean isImplemented() {
        return true;
    }

    @Override
    protected boolean isEnabled() {
        EcoDAO dao = new EcoDAO(getContext());
        return dao.isEcoEnabledById(getId(), getFrom(), EcoTbl.BRIGHTNESS_ENABLED);
    }

    @Override
    protected void turnOn() {
        Logger.d(TAG, "Do Brightness set:id=" + getId());
        EcoDAO dao = new EcoDAO(getContext());        
        if(dao.searchAutoBrightnessById(getId(), getFrom())) {
            BrightnessSetAuto();
        } else {
            BrightnessControl(mSystemBrightness);            
        }
    }

    @Override
    protected void turnOff() {
        Logger.d(TAG, "Brightness do nothing...:id=" + getId());
        /** DO NOTHING */
    }

    private void BrightnessSetAuto() {
        Settings.System.putInt(mActivity.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
    }
    
    private void BrightnessControl(float brightness) {
        // brightnessは輝度を表す。0にするとブラックアウトになるので注意。
        if (brightness < 10) { // 念の為9以下に設定された場合はreturn。
            Toast.makeText(mActivity, "brightness < 10 !!", Toast.LENGTH_LONG).show();
            return;
        }

        // step1: 輝度を設定(0～255)
        Settings.System.putInt(mActivity.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        Settings.System.putInt(mActivity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS,
                (int) brightness);

        // step2: 設定を変換(輝度を0.0～1.0に変換)
        float brf = (float) brightness / 255.0f;

        // android.view.WindowManager.LayoutParamsを取得
        LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.screenBrightness = brf;

        // step3: 輝度を反映
        mActivity.getWindow().setAttributes(lp);
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
