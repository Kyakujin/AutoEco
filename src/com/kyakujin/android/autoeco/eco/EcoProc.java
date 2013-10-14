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

/**
 * 各種節電機能の実行を制御するための抽象クラス。
 * (Template Pattern)
 */
public abstract class EcoProc extends AbstractEco {
    protected AbstractEco eco;
    protected abstract boolean isImplemented();
    protected abstract boolean isEnabled();
    protected abstract void turnOn();
    protected abstract void turnOff();
 
    public EcoProc(AbstractEco eco) {
        this.eco = eco;
    }

    @Override
    public void execute() {
        if(isImplemented()) {
            // 節電実行がONであれば
            if(isEnabled()) {
                  // 各種機能(wifi等)をOFFに
                turnOn();            
            } else {
                // 各種機能(wifi等)をOFFに
              turnOff();
            }            
        }
        eco.execute();
        return;
                
    }
}
