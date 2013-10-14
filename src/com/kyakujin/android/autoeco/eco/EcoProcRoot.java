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
import com.kyakujin.android.autoeco.Logger;

/**
 * DecoratorパターンにおけるConcreteComponent役
 */
public class EcoProcRoot extends AbstractEco {
    private final String TAG = Conf.APP_NAME + ":" + this.getClass().getSimpleName();
    private Context mContext;
    private int mId;
    private EcoExecFrom mFrom;
    
    public EcoProcRoot(Context context , int id, EcoExecFrom from) {
        this.mContext = context;
        this.mId = id;
        this.mFrom = from;
    }

    @Override
    public void execute() {
        // DO NOTHING
        Logger.d(TAG, "ECO DONE!");
    }

    @Override
    protected boolean isImplemented() {        
        // DO NOTHING
        return false;
    }

    @Override
    protected void turnOn() {
    }

    @Override
    protected void turnOff() {
    }

    @Override
    protected boolean isEnabled() {
        return false;
    }

    @Override
    protected Context getContext() {
        return mContext;
    }

    @Override
    protected int getId() {
        return mId;
    }

    @Override
    protected EcoExecFrom getFrom() {
        return mFrom;
    }

}
