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

package com.kyakujin.android.autoeco.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.kyakujin.android.autoeco.Conf;
import com.kyakujin.android.autoeco.R;
import com.kyakujin.android.autoeco.db.AutoEcoContract.EcoTbl;
import com.kyakujin.android.autoeco.db.dao.EcoDAO;
import com.kyakujin.android.autoeco.db.dao.MappingDAO;
import com.kyakujin.android.autoeco.db.dao.MappingModel;

/**
 * バッテリー連動機能の設定画面アクティビティ<br>
 * 以下の画面(フラグメント)をこのアクティビティ上に表示
 * {@link BatteryFragment} <br>
 * {@link EcoFragment}
 */
public class BatterySettingActivity extends FragmentActivity {
    
    private Uri mEcoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 

        setContentView(R.layout.activity_battery);
        
        int batteryId = 0;
        batteryId = getIntent().getExtras().getInt(Conf.SHARED_BATTERYID);

        connectToMapping(batteryId);
        
        FragmentManager fm = this.getSupportFragmentManager();        
        FragmentTransaction ft = fm.beginTransaction();
        
        // バッテリーFragment
        Bundle bundle = new Bundle();
        bundle.putInt(Conf.SHARED_BATTERYID, batteryId);
        BatteryFragment batteryFragment = BatteryFragment.newInstance();
        batteryFragment.setArguments(bundle);

        // 節電Fragment
        bundle.putInt(Conf.SHARED_ECOID, Integer.valueOf(mEcoUri.getLastPathSegment()));
        EcoFragment ecoFragment = EcoFragment.newInstance();
        ecoFragment.setArguments(bundle);
        
        ft.add(R.id.batteryFragmentContainer, batteryFragment, Conf.FRAGTAG_BATTERY);
        ft.add(R.id.ecoFragmentContainer, ecoFragment, Conf.FRAGTAG_ECO);
        //ft.addToBackStack(null);
        ft.commit();        
        
    }
    
    private int connectToMapping(int batteryId) {
        int mappingId = 0;

        if (batteryId == 0)
            return mappingId;

        // Mappingテーブルにスケジュールが登録されているか確認
        MappingDAO mappingDao = new MappingDAO(this);
        mappingId = mappingDao.searchMappingIdByBatteryId(batteryId);
        
        // Mappingテーブルに未登録であれば
        if (mappingId == 0 ) {
            // 節電データ新規作成
          EcoDAO ecoDao = new EcoDAO(this);
          mEcoUri = ecoDao.insertDefaultEco();        
          
          // Mappingデータを新規作成
          MappingModel mappingModel = new MappingModel();
          mappingModel.setEcoid(Integer.valueOf(mEcoUri.getLastPathSegment()));
          mappingModel.setSchedid(0);
          mappingModel.setManualid(0);
          mappingModel.setBatteryid(batteryId);
          mappingId = Integer.valueOf(mappingDao.insertMapping(mappingModel).getLastPathSegment());            
          
        } else {
            // Mappingテーブルから紐づいている節電データを取得
            mEcoUri = Uri.withAppendedPath(EcoTbl.CONTENT_URI, 
                    String.valueOf(mappingDao.searchEcoIdByBatteryId(batteryId)));            
        }
        
        return mappingId;
    }    
}
