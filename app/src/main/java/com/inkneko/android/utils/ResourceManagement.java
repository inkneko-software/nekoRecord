package com.inkneko.android.utils;

import com.inkneko.nekorecord.R;

import java.lang.reflect.Field;

public class ResourceManagement {
    public static int getDrawableResourceId(String resName) {
        try {
            Field idField = R.drawable.class.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
