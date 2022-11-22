package com.iskyun.im.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;

import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;

import com.iceteck.silicompressorr.SiliCompressor;
import com.iskyun.im.ImLiveApp;
import com.iskyun.im.common.Constant;
import com.iskyun.im.common.UploadType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FileUtils {

    /**
     * @param contentUri
     * @return
     */
    public static String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = ImLiveApp.get().getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                res = cursor.getString(column_index);
            }
            cursor.close();
        }
        return res;
    }

    /**
     * uri  视频第一帧图片
     *
     * @param contentUri
     * @return
     */
    public static Bitmap getFrameAtTime(Uri contentUri) {
        MediaMetadataRetriever mmr = getMediaMetadataRetriever(contentUri);
        return mmr.getFrameAtTime();
    }

    /**
     * 根据 uri 获得视频时长
     *
     * @param contentUri
     * @return
     */
    public static int getMetadataDuration(Uri contentUri) {
        MediaMetadataRetriever mmr = getMediaMetadataRetriever(contentUri);
        //时长(毫秒)
        String duration = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION);
        return Integer.parseInt(duration);
    }

    public static MediaMetadataRetriever getMediaMetadataRetriever(Uri contentUri) {
        String path = getRealPathFromURI(contentUri);
        return getMediaMetadataRetriever(path);
    }

    public static MediaMetadataRetriever getMediaMetadataRetriever(String path) {
        File file = new File(path);
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(file.getAbsolutePath());
        return mmr;
    }

    /**
     * 压缩视频目录
     *
     * @return
     */
    public static String getCompressorVideosDir() {
        return ImLiveApp.get().getExternalFilesDir(Environment.DIRECTORY_MOVIES).getAbsolutePath();
    }

    /**
     * app  album目录
     *
     * @return
     */
    public static String getAlbumDir() {
        return ImLiveApp.get().getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();
    }

    /**
     * app download目录
     *
     * @return
     */
    public static String getDownloadDir() {
        return ImLiveApp.get().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
    }

    /**
     * 拍照 保存路径
     *
     * @return
     */
    public static Uri getTakePhotoOutputPath() {
        String filePath = getAlbumDir();
        //如果目录不存在则必须创建目录
        File cameraFolder = new File(filePath);
        if (!cameraFolder.exists()) {
            cameraFolder.mkdirs();
        }
        //根据时间随机生成图片名
        String photoName = DateFormat.format("yyyy_MM_dd_hh_mm_ss",
                Calendar.getInstance(Locale.CHINA)) + ".jpg";
        filePath = filePath + File.separator + photoName;
        File mOutImage = new File(filePath);
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(ImLiveApp.get(), Constant.AUTHORITY, mOutImage);
        } else {
            uri = Uri.fromFile(mOutImage);
        }
        return uri;
    }

    public static String compressVideo(String videoFilePath, int outWidth, int outHeight, int bitrate) {
        String compressorPath = null;
        try {
            compressorPath = SiliCompressor.with(ImLiveApp.get()).compressVideo(videoFilePath,
                    getCompressorVideosDir(), outWidth, outHeight, bitrate);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return compressorPath;
    }

    /**
     * 将视频压缩成 720*1280 bitrate为原来0.4
     */
    public static String compressVideo(String videoFilePath) {
        MediaMetadataRetriever mmr = getMediaMetadataRetriever(videoFilePath);
        String bitrate = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE);
        String videoWidth = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
        String videoHeight = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
        int outWidth = 0;
        int outHeight = 0;
        int width = Integer.parseInt(videoWidth);
        int height = Integer.parseInt(videoHeight);
        if (width >= 1080) {
            outWidth = 720;
        } else {
            outWidth = width;
        }
        if (height >= 1920) {
            outHeight = 1280;
        } else {
            outHeight = height;
        }
        long fileSize = 0;
        try {
            fileSize = getFileSize(new File(videoFilePath));
        } catch (Exception e) {
            e.printStackTrace();
        }
        float ratio = fileSize > 15 * 1024 * 1024 ? 0.2f : 0.4f;
        int intBitrate = (int) (Integer.parseInt(bitrate) * ratio);
        return compressVideo(videoFilePath, outWidth, outHeight, intBitrate);
    }

    /**
     * 图片压缩
     * @param imageUri
     * @return
     */
    public static String compressImage(String imageUri) {
        return compressImage(imageUri, new File(getAlbumDir()));
    }

    /**
     * 删除图片
     * @param imageUri
     * @return
     */
    public static boolean deleteImageFile(String imageUri){
        Uri uri = Uri.parse(imageUri);
        int cnt = ImLiveApp.get().getContentResolver().delete(uri, null, null);
        return cnt > 0;
    }


    /**
     * 裁剪
     *
     * @param activity
     * @param sourceUri
     * @return
     */
    public static File gotoCrop(Activity activity, Uri sourceUri) {
        File outputCropFile = FileUtils.getCropImageFile(true);
        if (outputCropFile != null) {
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            intent.putExtra("crop", "true");
            //X方向上的比例
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            //裁剪区的宽
            intent.putExtra("outputX", DisplayUtils.getWidthPixels() * 0.5f);
            //裁剪区的高
            intent.putExtra("outputY", DisplayUtils.getWidthPixels() * 0.5f);
            //是否保留比例
            intent.putExtra("scale ", true);
            intent.putExtra("return-data", false);
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            intent.setDataAndType(sourceUri, "image/*"); //设置数据源
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                //android 11 以上 App没有权限不能访问公共存储空间，需要通过 MediaStore API来操作
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, outputCropFile.getAbsolutePath());
                values.put(MediaStore.Images.Media.DISPLAY_NAME, outputCropFile.getName());
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                Uri uri = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            } else {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outputCropFile));
            }
            activity.startActivityForResult(intent, UploadType.CROP.getValue());
        }
        return outputCropFile;
    }

    /**
     * 创建裁剪图片文件
     *
     * @param isCrop
     * @return
     */
    private static File getCropImageFile(boolean isCrop) {
        try {
            String timeStamp = DateFormat.format("yyyy_MM_dd_hh_mm_ss",
                    Calendar.getInstance(Locale.CHINA)).toString();
            String fileName;
            if (isCrop) {
                fileName = "IMG_" + timeStamp + "_CROP.jpg";
            } else {
                fileName = "IMG_" + timeStamp + ".jpg";
            }
            File rootFile = new File(ImLiveApp.get().getExternalFilesDir(null).getAbsolutePath()
                    + File.separator + "capture");
            if (!rootFile.exists()) {
                rootFile.mkdirs();
            }
            File outputImageFile;
            /**
             * android11系统裁剪访问不了app的私有目录
             */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                outputImageFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                        + File.separator + fileName);
            } else {
                outputImageFile = new File(rootFile.getAbsolutePath() + File.separator + fileName);
            }
            return outputImageFile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        }
        return size;
    }

    /**
     * 格式化文件大小
     *
     * @param size
     * @return
     */
    public static String formatFileSize(long size) {
        if (size <= 0) return "0";
        final String[] units = new String[]{"b", "kb", "M", "G", "T"};
        //计算单位的，原理是利用lg,公式是 lg(1024^n) = nlg(1024)，最后 nlg(1024)/lg(1024) = n。
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        //计算原理是，size/单位值。单位值指的是:比如说b = 1024,KB = 1024^2
        return new DecimalFormat("#,##0.##").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public static String compressImage(String uriString, File destDirectory) {
        Context mContext = ImLiveApp.get();
        try {
            Uri imageUri = Uri.parse(uriString);
            Bitmap scaledBitmap = null;

            BitmapFactory.Options options = new BitmapFactory.Options();

            // by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
            // you try the use the bitmap here, you will get null.
            options.inJustDecodeBounds = true;
            Bitmap bmp = BitmapFactory.decodeStream(mContext.getContentResolver().openInputStream(imageUri), null, options);

            int actualHeight = options.outHeight;
            int actualWidth = options.outWidth;

            // max Height and width values of the compressed image is taken as 816x612
            float maxHeight = 1280f;
            float maxWidth = 720f;
            float imgRatio = actualWidth / actualHeight;
            float maxRatio = maxWidth / maxHeight;

            // width and height values are set maintaining the aspect ratio of the image

            if (actualHeight > maxHeight || actualWidth > maxWidth) {
                if (imgRatio < maxRatio) {
                    imgRatio = maxHeight / actualHeight;
                    actualWidth = (int) (imgRatio * actualWidth);
                    actualHeight = (int) maxHeight;
                } else if (imgRatio > maxRatio) {
                    imgRatio = maxWidth / actualWidth;
                    actualHeight = (int) (imgRatio * actualHeight);
                    actualWidth = (int) maxWidth;
                } else {
                    actualHeight = (int) maxHeight;
                    actualWidth = (int) maxWidth;

                }
            }

            // setting inSampleSize value allows to load a scaled down version of the original image
            options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

            // inJustDecodeBounds set to false to load the actual bitmap
            options.inJustDecodeBounds = false;

            // this options allow android to claim the bitmap memory if it runs low on memory
            options.inPurgeable = true;
            options.inInputShareable = true;
            options.inTempStorage = new byte[16 * 1024];

            // load the bitmap from its path
            bmp = BitmapFactory.decodeStream(mContext.getContentResolver().openInputStream(imageUri), null, options);
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);


            float ratioX = actualWidth / (float) options.outWidth;
            float ratioY = actualHeight / (float) options.outHeight;
            float middleX = actualWidth / 2.0f;
            float middleY = actualHeight / 2.0f;

            Matrix scaleMatrix = new Matrix();
            scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

            Canvas canvas = new Canvas(scaledBitmap);
            canvas.setMatrix(scaleMatrix);
            canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

            // check the rotation of the image and display it properly
            androidx.exifinterface.media.ExifInterface exif;
            try {
                exif = new ExifInterface(mContext.getContentResolver().openInputStream(imageUri));

                int orientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION, 0);
                Log.d("EXIF", "Exif: " + orientation);
                Matrix matrix = new Matrix();
                if (orientation == 6) {
                    matrix.postRotate(90);
                    Log.d("EXIF", "Exif: " + orientation);
                } else if (orientation == 3) {
                    matrix.postRotate(180);
                    Log.d("EXIF", "Exif: " + orientation);
                } else if (orientation == 8) {
                    matrix.postRotate(270);
                    Log.d("EXIF", "Exif: " + orientation);
                }
                scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                        scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                        true);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                @SuppressLint("SimpleDateFormat") String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".jpg";
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/compress/");
                values.put(MediaStore.Images.Media.IS_PENDING, 1);

                Uri collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
                Uri resultUri = mContext.getContentResolver().insert(collection, values);

                OutputStream out = mContext.getContentResolver().openOutputStream(resultUri);

                // write the compressed bitmap at the destination specified by filename.
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
                values.clear();
                values.put(MediaStore.Images.Media.IS_PENDING, 0);
                mContext.getContentResolver().update(resultUri, values, null, null);

                return resultUri.toString();

            } else {
                String filename = getFilename(uriString, destDirectory);

                FileOutputStream out = new FileOutputStream(filename);

                //write the compressed bitmap at the destination specified by filename.
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

                return filename;
            }

        } catch (FileNotFoundException | OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }

    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = Math.min(heightRatio, widthRatio);
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    /**
     * Get the file path of the compressed file
     *
     * @param filename
     * @param file     Destination directory
     * @return
     */
    private static String getFilename(String filename, File file) {
        if (!file.exists()) {
            file.mkdirs();
        }
        String ext = ".jpg";
        //get extension
        /*if (Pattern.matches("^[.][p][n][g]", filename)){
            ext = ".png";
        }*/
        @SuppressLint("SimpleDateFormat")
        String nameFirstPart = file.getAbsolutePath() + "/IMG_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String nameFull = nameFirstPart + ext;
        int x = 1;
        while (new File(nameFull).exists()) {
            nameFull = nameFirstPart + "_" + x + ext;
            x++;
        }

        return nameFull;
    }
}
