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

package com.kyakujin.android.autoeco.service;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.Toast;

import com.kyakujin.android.autoeco.Conf;
import com.kyakujin.android.autoeco.Conf.EcoExecFrom;
import com.kyakujin.android.autoeco.Logger;
import com.kyakujin.android.autoeco.R;
import com.kyakujin.android.autoeco.eco.AbstractEco;
import com.kyakujin.android.autoeco.eco.BluetoothProc;
import com.kyakujin.android.autoeco.eco.BrightnessProc;
import com.kyakujin.android.autoeco.eco.EcoProcRoot;
import com.kyakujin.android.autoeco.eco.RotateProc;
import com.kyakujin.android.autoeco.eco.SilentModeProc;
import com.kyakujin.android.autoeco.eco.SleepProc;
import com.kyakujin.android.autoeco.eco.SyncProc;
import com.kyakujin.android.autoeco.eco.WifiProc;

/**
 * 指定時間になったらスケジューラーから起動される節電実行用アクティビティ
 */
public class AutoEcoNotificationActivity extends Activity {
    private final String TAG = Conf.APP_NAME + ":" + this.getClass().getSimpleName();

    // private WakeLock wakelock;
    // private KeyguardLock keylock;
    private Activity mActivity;
    // private TextView mMessage;
    private final Handler handler = new Handler();
    private final Runnable mTask = new Runnable() {
        @Override
        public void run() {
            mActivity.finish();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // タイトルバーを消す
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_do_eco);

        mActivity = this;
        Logger.d(TAG, "create");

        // mMessage = (TextView) findViewById(R.id.textDoEco);

        Intent i = getIntent();
        int id = i.getIntExtra(Conf.SHARED_EXTRA_ID, 0);
        int ordinal = i.getIntExtra(Conf.SHARED_EXTRA_FROM, 0);
        EcoExecFrom from = Conf.mapEcoExecFrom.get(ordinal);

        // Eco実行
        AbstractEco eco = new WifiProc(
                new BluetoothProc(
                  new SyncProc(
                    new RotateProc(
                      new SilentModeProc(
                        new SleepProc(
                          new BrightnessProc(
                              new EcoProcRoot(this, id,from),this
                              )
                           )
                         )
                       )
                     )
                  )
                );

        eco.execute();

        Toast.makeText(this, getResources().getString(R.string.app_name_jp)
                + getResources().getString(R.string.desc_after_eco), Toast.LENGTH_LONG).show();

        // 5秒後に実行するためのタスクを設定
        handler.postDelayed(mTask, 5000);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
