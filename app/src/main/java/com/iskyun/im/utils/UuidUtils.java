package com.iskyun.im.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;

import com.iskyun.im.utils.manager.SpManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

/**
 * 设备唯一标识符
 */
public class UuidUtils {

    private static String mUniqueId;

    public static String getUniqueId(Context context) {
        //内存中取
        if (!TextUtils.isEmpty(mUniqueId)) {
            return mUniqueId;
        }
        //sp 取
        //mUniqueId = SpManager.getInstance().getUuid();
        if (!TextUtils.isEmpty(mUniqueId)) {
            return mUniqueId;
        }
        //文件中取
        mUniqueId = readUniqueIdForFile(context);
        if (!TextUtils.isEmpty(mUniqueId)) {
            return mUniqueId;
        }
        mUniqueId = createUniqueId(context);
        SpManager.getInstance().setUuid(mUniqueId);
        return mUniqueId;
    }

    /**
     * 设备androidId
     *
     * @param context
     */
    @SuppressLint("HardwareIds")
    private static String getAndroidId(Context context) {
        String androidId = null;
        try {
            androidId = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            if ("9774d56d682e549c".equals(androidId)) {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return androidId;
    }

    private static String createUniqueId(Context context) {
        String uniqueId = getAndroidId(context);
        if (TextUtils.isEmpty(uniqueId)) {
            uniqueId = UUID.randomUUID().toString();
        }
        LogUtils.i("uniqueId:" + uniqueId);
        //uniqueId  Md5加密
        uniqueId =  MD5.encrypt2MD5(uniqueId);
        saveUniqueIdForFile(context,uniqueId);
        return uniqueId;
    }

    private static void saveUniqueIdForFile(Context context, String uniqueId) {
        File file = getUniqueIdFile(context);
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            outputStream.write(uniqueId.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static String readUniqueIdForFile(Context context) {
        File file = getUniqueIdFile(context);
        String uniqueId = null;
        if (file.exists()) {
            FileInputStream inputStream = null;
            try {
                inputStream = new FileInputStream(file);
                byte[] bytes = new byte[(int) file.length()];
                inputStream.read(bytes);
                uniqueId = new String(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return uniqueId;
    }

    /**
     * 目录
     */
    private static File getUniqueIdFileDir(Context context) {
        String uniqueIdDirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();
        File file = new File(uniqueIdDirPath + File.separator + context.getPackageName());
        if (!file.exists()) {
            file.mkdir();
        }
        return file;
    }

    /**
     * 文件
     * @param context
     * @return
     */
    private static File getUniqueIdFile(Context context) {
        String uniqueIdFile = "unique.txt";
        File file = new File(getUniqueIdFileDir(context), uniqueIdFile);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public static void clearUniqueIdFile(Context context) {
        File filesDir = getUniqueIdFileDir(context);
        deleteFile(filesDir);
    }

    private static void deleteFile(File file) {
        if (file.isDirectory()) {
            for (File listFile : Objects.requireNonNull(file.listFiles())) {
                deleteFile(listFile);
            }
        } else {
            file.delete();
        }
    }
}
