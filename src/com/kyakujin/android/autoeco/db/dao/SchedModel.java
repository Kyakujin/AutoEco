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

package com.kyakujin.android.autoeco.db.dao;

/**
 * Entity Class of Sched.
 */
public class SchedModel {
    private int id;
    private boolean enabled;
    private int hour;
    private int minute;
    private String hour_minute_string;
    private int pattern;
    private int reserve; // DBバージョン V2以降から対応
    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public boolean getEnabled() {
        return enabled;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    public int getHour() {
        return hour;
    }
    public void setHour(int hour) {
        this.hour = hour;
    }
    public int getMinute() {
        return minute;
    }
    public void setMinute(int minute) {
        this.minute = minute;
    }
    public String getHour_minute_string() {
        return hour_minute_string;
    }
    public void setHour_minute_string(String hour_minute_string) {
        this.hour_minute_string = hour_minute_string;
    }
    public int getPattern() {
        return pattern;
    }
    public void setPattern(int pattern) {
        this.pattern = pattern;
    }
    public int getReserve() {
        return reserve;
    }
    public void setReserve(int reserve) {
        this.reserve = reserve;
    }
}
