package com.iskyun.im.data.local.model;

import androidx.room.Entity;
import androidx.room.Index;

import java.io.Serializable;

@Entity(tableName = "msg_type", primaryKeys = {"id"},
        indices = {@Index(value = {"type"}, unique = true)})
public class MsgType implements Serializable {
    private int id;
    private String type;
    private String extField;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setExtField(String extField) {
        this.extField = extField;
    }

    public String getExtField() {
        return extField;
    }

    public int getUnReadCount() {
        return 0;
    }
}