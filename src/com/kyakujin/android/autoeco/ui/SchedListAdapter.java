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

import java.util.HashMap;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.kyakujin.android.autoeco.Conf;
import com.kyakujin.android.autoeco.Logger;
import com.kyakujin.android.autoeco.R;
import com.kyakujin.android.autoeco.db.AutoEcoContract.SchedQuery;
import com.kyakujin.android.autoeco.db.dao.SchedDAO;
import com.kyakujin.android.autoeco.db.dao.SchedModel;
import com.kyakujin.android.autoeco.service.SchedAlarmManager;

/**
 * スケジュールリストアダプタ
 */
public class SchedListAdapter extends SimpleCursorAdapter implements OnClickListener {
    private final String TAG = Conf.APP_NAME + ":" + this.getClass().getSimpleName();

    private final Context mContext;

    private class ViewHolder {
        private CheckBox checkBox;
        private TextView TimeString;
    }

    public SchedListAdapter(Context context, int layout, Cursor c,
            String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        mContext = context;
    }

    // ビューの生成
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(context);
                
        View rowView = inflater.inflate(R.layout.list_item_sched, null, true);  
        
        ViewHolder holder = new ViewHolder();
        holder.TimeString = (TextView) rowView.findViewById(R.id.textHourMinute);
        holder.checkBox = (CheckBox) rowView.findViewById(R.id.checkSchedItem);
        
        rowView.setTag(holder);

        return rowView;
    }

    // Cursorデータのせット
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();

        // カーソルからデータを取得
        int schedId = cursor.getInt(SchedQuery.Idx._ID.ordinal());
        String time = cursor.getString(SchedQuery.Idx.HOUR_MINUTE_STRING.ordinal());        
        boolean checked = cursor.getInt(SchedQuery.Idx.ENABLED.ordinal()) == 1 ? true : false;

        // 時刻文字列設定
        holder.TimeString.setText(time);

        // チェックボックス設定
        holder.checkBox.setChecked(checked);
        holder.checkBox.setTag(schedId);        
        holder.checkBox.setOnClickListener(this);
        
        if(map == null) {
            map = new HashMap<Integer, SchedModel>();
        }

        SchedModel model = new SchedModel();
        model.setId(schedId);
        model.setHour_minute_string(time);
        model.setHour(cursor.getInt(SchedQuery.Idx.HOUR.ordinal()));
        model.setMinute(cursor.getInt(SchedQuery.Idx.MINUTE.ordinal()));
        map.put(schedId, model);     
    }
    
    private HashMap<Integer, SchedModel> map;
    
    @Override
    public void onClick(View v) {
        CheckBox cb = (CheckBox) v;
        Logger.d(TAG, "option="+cb.getTag());                        
        Logger.d(TAG, "time="+map.get(cb.getTag()).getHour_minute_string());                        

        // DBの更新
        SchedDAO dao = new SchedDAO(mContext);
        dao.updateEnabled((Integer)cb.getTag(), cb.isChecked());  

        // アラームマネージャの更新
        SchedAlarmManager am = new SchedAlarmManager(mContext);
        if(cb.isChecked()) {
            am.addAlarm(map.get(cb.getTag()));            
        } else {
            am.cancelAlarm(map.get(cb.getTag()));                        
        }                        
    }
}
