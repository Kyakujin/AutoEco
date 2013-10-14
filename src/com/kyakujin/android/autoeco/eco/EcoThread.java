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
import com.kyakujin.android.autoeco.Conf;
import com.kyakujin.android.autoeco.Conf.EcoExecFrom;
import com.kyakujin.android.autoeco.Conf.EcoExecSwitch;

/**
 * スケジュール、バッテリー連動、マニュアル等の各々のタイミングから直接起動されるスレッド
 */
public class EcoThread extends Thread {
    private final String TAG = Conf.APP_NAME + ":" + this.getClass().getSimpleName();

    private Context mContext;
    private EcoExecSwitch mFlg;
    private EcoExecFrom mFrom;
    private int mId;

    public EcoThread(Context context, EcoExecFrom from, int id, EcoExecSwitch flg) {
        this.mContext = context;
        this.mFrom = from;
        this.mId = id;
        this.mFlg = flg;
    }

    @Override
    public void run() {
        switch (mFlg) {
            case ECO_ON:
                EcoExec.getInstance(mContext, mFrom).startEco(mId);
                break;
            case ECO_OFF:
                EcoExec.getInstance(mContext, mFrom).stopEco();
                break;
            default:
        }
    }

}
