package tw.com.huang.robin.teststriprecognition;

import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.kaopiz.kprogresshud.KProgressHUD;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static java.security.AccessController.getContext;


public class PrintActivity extends AppCompatActivity {

    final String TAG = "PrintActivity";
    private String bestProvider = LocationManager.GPS_PROVIDER;
    private KProgressHUD hud;

    public String photoFileName = "photo.jpg";
    File photoFile;

    ImageView airImageView;
    ImageView mapImageView;
    ImageView tempImage;
    ImageView analysisImageView;
    LinearLayout view;
    TextView text;

    String old_file="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        airImageView = (ImageView)findViewById(R.id.airImageView);
        mapImageView = (ImageView)findViewById(R.id.mapImageView);
        analysisImageView = (ImageView)findViewById(R.id.analysisImageView);
        tempImage = (ImageView)findViewById(R.id.tempImage);
        view = findViewById(R.id.view);
        text = findViewById(R.id.text);


        SharedPreferences pref = getSharedPreferences("test", MODE_PRIVATE);
        text.setText(pref.getString("註記",""));

        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File(sdCard.getAbsolutePath() +"/" +getString(R.string.app_name));
        dir.mkdirs();

        String fileName = String.format("map.jpg");
        File outFile = new File(dir, fileName);
        if(outFile.exists())
        {
            Bitmap bm = BitmapFactory.decodeFile(outFile.toString());
            mapImageView.setImageBitmap(bm);
        }

        fileName = String.format("air.jpg");
        outFile = new File(dir, fileName);
        if(outFile.exists())
        {
            Bitmap bm = BitmapFactory.decodeFile(outFile.toString());
            airImageView.setImageBitmap(bm);
        }


        String fileName2 = String.format("temp.jpg");
        File outFile2 = new File(dir, fileName2);
        if(outFile2.exists())
        {
            //Bitmap bm = BitmapFactory.decodeFile(outFile2.toString());
            // 得到修復後的照片路徑
            tempImage.setImageBitmap(PhotoBitmapUtils.amendRotatePhoto(outFile2.getPath(), getContext()));
        }


        String analysis_filename = getSharedPreferences("filename", MODE_PRIVATE).getString("filename", "");
        String fileName3 = String.format(analysis_filename);
        File outFile3 = new File(dir, fileName3);
        if(outFile3.exists())
        {
            Bitmap bm = BitmapFactory.decodeFile(outFile3.toString());
            analysisImageView.setImageBitmap(bm);
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }




    public void deleteClick(View v)
    {
        File sdCard = Environment.getExternalStorageDirectory();
        File dir2 = new File(sdCard.getAbsolutePath() + "/" + getString(R.string.app_name) + "/" + "比色歷史紀錄");
        dir2.mkdirs();
        File outFile2 = new File(dir2, old_file);
        if (outFile2.exists()) {
            outFile2.delete();
        }
        Toast.makeText(this,"刪除成功",Toast.LENGTH_SHORT).show();
    }

    public void captureClick(View v)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 4);


                return;
            }
        }


        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());


        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        Log.e("Activity", "Pick from Camera::>>> ");

        //String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        //File destination = new File(Environment.getExternalStorageDirectory() + "/" +
        //        getString(R.string.app_name), "IMG_" + timeStamp + ".jpg");
        FileOutputStream fo;
        try {
            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File(sdCard.getAbsolutePath() +"/" +getString(R.string.app_name));
            dir.mkdirs();
            String fileName = String.format("save.jpg");
            File outFile = new File(dir, fileName);
            if(outFile.exists())
            {
                outFile.delete();
            }

            //destination.createNewFile();
            fo = new FileOutputStream(outFile);
            fo.write(bytes.toByteArray());
            fo.close();
            Toast.makeText(this,"儲存JPG成功",Toast.LENGTH_SHORT).show();
            bitmap.recycle();
            bitmap=null;


            File dir2 = new File(sdCard.getAbsolutePath() +"/" +getString(R.string.app_name)+"/"+"比色歷史紀錄");
            dir2.mkdirs();
            Long tsLong = System.currentTimeMillis()/1000;
            String ts = tsLong.toString();
            String fileName2 = String.format(ts+".jpg");
            old_file = fileName2;
            File outFile2 = new File(dir2, fileName2);
            if(outFile2.exists())
            {
                outFile2.delete();
            }

            //destination.createNewFile();
            fo = new FileOutputStream(outFile2);
            fo.write(bytes.toByteArray());
            fo.close();






        } catch (FileNotFoundException e) {
            Toast.makeText(this,"儲存JPG失敗,請重試",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (IOException e) {
            Toast.makeText(this,"儲存JPG失敗,請重試",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }



    public void openImageClick(View v) {
        Log.d(TAG, "openImageClick");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 4);


                return;
            }
        }

        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto , 1);//one can be replaced with any action code
    }





    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {

                try {
                    Uri selectedImage = data.getData();

                    //Log.d(TAG,"imgPath:"+getRealPathFromURI(selectedImage).toString());



                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

                    Log.e("Activity", "Pick from Camera::>>> ");

                    //String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                    //File destination = new File(Environment.getExternalStorageDirectory() + "/" +
                    //        getString(R.string.app_name), "IMG_" + timeStamp + ".jpg");
                    FileOutputStream fo;
                    try {
                        File sdCard = Environment.getExternalStorageDirectory();
                        File dir = new File(sdCard.getAbsolutePath() + "/" + getString(R.string.app_name));
                        dir.mkdirs();
                        String fileName = String.format("temp.jpg");
                        File outFile = new File(dir, fileName);
                        if (outFile.exists()) {
                            outFile.delete();
                        }

                        //destination.createNewFile();
                        fo = new FileOutputStream(outFile);
                        fo.write(bytes.toByteArray());
                        fo.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //imgPath = destination.getAbsolutePath();
                    //imageview.setImageBitmap(bitmap);


                } catch (Exception e) {
                    e.printStackTrace();
                }


            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 1) {
            if (resultCode == RESULT_OK) {

                File sdCard = Environment.getExternalStorageDirectory();
                File dir = new File(sdCard.getAbsolutePath() + "/" + getString(R.string.app_name));
                dir.mkdirs();
                String fileName = String.format("temp.jpg");
                File outFile = new File(dir, fileName);
                if (outFile.exists()) {
                    outFile.delete();
                }

                Uri uri = data.getData();
                String path = getPath(this, uri);

                Log.d(TAG, "imgPath:" + path);

                try {
                    fileCopy(path, outFile.getPath());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public boolean fileCopy(String oldFilePath,String newFilePath) throws IOException {
        //如果原文件不存在
        if(fileExists(oldFilePath) == false){
            return false;
        }
        //获得原文件流
        FileInputStream inputStream = new FileInputStream(new File(oldFilePath));
        byte[] data = new byte[1024];
        //输出流
        FileOutputStream outputStream =new FileOutputStream(new File(newFilePath));
        //开始处理流
        while (inputStream.read(data) != -1) {
            outputStream.write(data);
        }
        inputStream.close();
        outputStream.close();
        return true;
    }


    public static boolean fileExists(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }




    public static String getPath(final PrintActivity context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }



    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

}
