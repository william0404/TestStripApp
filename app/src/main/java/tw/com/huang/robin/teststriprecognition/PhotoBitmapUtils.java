package tw.com.huang.robin.teststriprecognition;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.AccessControlContext;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 集合一些圖片工具
 *
 * Created by zhuwentao on 2016-07-22.
 */
public class PhotoBitmapUtils {

    /**
     * 存放拍攝圖片的資料夾
     */
    private static final String FILES_NAME = "/MyPhoto";
    /**
     * 獲取的時間格式
     */
    public static final String TIME_STYLE = "yyyyMMddHHmmss";
    /**
     * 圖片種類
     */
    public static final String IMAGE_TYPE = ".png";

    // 防止例項化
    private PhotoBitmapUtils() {
    }

    /**
     * 獲取手機可儲存路徑
     *
     * @param context 上下文
     * @return 手機可儲存路徑
     */
    private static String getPhoneRootPath(Context context) {
        // 是否有SD卡
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)
                || !Environment.isExternalStorageRemovable()) {
            // 獲取SD卡根目錄
            return context.getExternalCacheDir().getPath();
        } else {
            // 獲取apk包下的快取路徑
            return context.getCacheDir().getPath();
        }
    }

    /**
     * 使用當前系統時間作為上傳圖片的名稱
     *
     * @return 儲存的根路徑+圖片名稱
     */
    public static String getPhotoFileName(Context context) {
        File file = new File(getPhoneRootPath(context) + FILES_NAME);
        // 判斷檔案是否已經存在，不存在則建立
        if (!file.exists()) {
            file.mkdirs();
        }
        // 設定圖片檔名稱
        SimpleDateFormat format = new SimpleDateFormat(TIME_STYLE, Locale.getDefault());
        Date date = new Date(System.currentTimeMillis());
        String time = format.format(date);
        String photoName = "/" + time + IMAGE_TYPE;
        return file + photoName;
    }

    /**
     * 儲存Bitmap圖片在SD卡中
     * 如果沒有SD卡則存在手機中
     *
     * @param mbitmap 需要儲存的Bitmap圖片
     * @return 儲存成功時返回圖片的路徑，失敗時返回null
     */
    public static String savePhotoToSD(Bitmap mbitmap, Context context) {
        FileOutputStream outStream = null;
        String fileName = getPhotoFileName(context);
        try {
            outStream = new FileOutputStream(fileName);
            // 把資料寫入檔案，100表示不壓縮
            mbitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            return fileName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (outStream != null) {
                    // 記得要關閉流！
                    outStream.close();
                }
                if (mbitmap != null) {
                    mbitmap.recycle();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 把原圖按1/10的比例壓縮
     *
     * @param path 原圖的路徑
     * @return 壓縮後的圖片
     */
    public static Bitmap getCompressPhoto(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        //options.inSampleSize = 100;  // 圖片的大小設定為原來的十分之一
        Bitmap bmp = BitmapFactory.decodeFile(path, options);
        options = null;
        return bmp;
    }

    /**
     * 處理旋轉後的圖片
     * @param originpath 原圖路徑
     * @param context 上下文
     * @return 返回修復完畢後的圖片路徑
     */
    public static Bitmap amendRotatePhoto(String originpath, AccessControlContext context) {

        // 取得圖片旋轉角度
        int angle = readPictureDegree(originpath);

        // 把原圖壓縮後得到Bitmap物件
        Bitmap bmp = getCompressPhoto(originpath);;

        // 修復圖片被旋轉的角度
        Bitmap bitmap = rotaingImageView(angle, bmp);



        // 儲存修復後的圖片並返回儲存後的圖片路徑
        //return savePhotoToSD(bitmap, context);
        return bitmap;
    }

    /**
     * 讀取照片旋轉角度
     *
     * @param path 照片路徑
     * @return 角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 旋轉圖片
     * @param angle 被旋轉角度
     * @param bitmap 圖片物件
     * @return 旋轉後的圖片
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        Bitmap returnBm = null;
        // 根據旋轉角度，生成旋轉矩陣
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        try {
            // 將原始圖片按照旋轉矩陣進行旋轉，並得到新的圖片
            returnBm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bitmap;
        }
        if (bitmap != returnBm) {
            bitmap.recycle();
        }
        return returnBm;
    }
}