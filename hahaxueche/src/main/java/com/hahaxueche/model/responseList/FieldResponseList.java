package com.hahaxueche.model.responseList;

import android.text.TextUtils;

import com.hahaxueche.model.base.Field;

import java.util.List;

/**
 * Created by wangshirui on 2017/4/17.
 */

public class FieldResponseList {
    public int total;
    public List<Field> data;
    public int cityId;

    public Field getFieldById(String fieldId) {
        Field ret = null;
        if (data != null && data.size() > 0) {
            for (Field field : data) {
                if (field.id.contains(fieldId)) {
                    ret = field;
                    break;
                }
            }
        }
        return ret;
    }

    public String getSectionName(String fieldId) {
        String ret = "";
        if (data == null || data.size() < 1) return ret;
        for (Field field : data) {
            if (field.id.equals(fieldId)) {
                ret = (TextUtils.isEmpty(field.section) ? field.zone : field.section);
                break;
            }
        }
        return ret;
    }
}
