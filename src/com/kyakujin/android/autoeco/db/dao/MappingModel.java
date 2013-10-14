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
 * Entity Class of Mapping.
 */
public class MappingModel {
    private int id;
    private int ecoid;
    private int schedid;
    private int batteryid;
    private int manualid;
    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getEcoid() {
        return ecoid;
    }
    public void setEcoid(int ecoid) {
        this.ecoid = ecoid;
    }
    public int getSchedid() {
        return schedid;
    }
    public void setSchedid(int schedid) {
        this.schedid = schedid;
    }
    public int getBatteryid() {
        return batteryid;
    }
    public void setBatteryid(int batteryid) {
        this.batteryid = batteryid;
    }
    public int getManualid() {
        return manualid;
    }
    public void setManualid(int manualid) {
        this.manualid = manualid;
    }
}
