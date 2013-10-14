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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 端末Boot検出時処理の基底クラス
 */
public abstract class BaseOnBootReceiver extends BroadcastReceiver {


    // ブロードキャストインテント検知時の処理
    @Override
    public void onReceive(final Context context, Intent intent) {

        // 端末起動時の場合
        if( Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()))
        {
            new Thread(new Runnable(){
                @Override
                public void run()
                {
                    onDeviceBoot(context);
                }
            }).start();

        }
    }

    protected abstract void onDeviceBoot(Context context);
}
