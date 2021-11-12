package tw.com.huang.robin.teststriprecognition;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class PhotoModeActivity extends AppCompatActivity {

    public final class PHOTO_MODE{
        public static final int CAMERA = 0;
        public static final int ABLUM = 1;
    }

    Intent intent;
    Bitmap bitmap;
    private static int TESTSTRIP_ID;
    private static int RECG_METHOD;
    private static String CAMERAFV5_PACKAGE_NAME = "com.flavionet.android.camera.lite";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_mode);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // 獲取當下試紙類別
        Intent intent = getIntent();
        TESTSTRIP_ID = intent.getIntExtra( "TestStripID", 0 );
        RECG_METHOD = intent.getIntExtra( "RecognitionMethod", 0 );
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



    /* 開啟相機拍照 */
    public void openCamera( View view ) {

        // 若試紙種類為「三聚氰胺檢測」或「農藥檢測」且指定使用「比色法」
        if ( TESTSTRIP_ID == ColorActivity.TESTSTRIP_ID.TESTSTRIP_ID_FIRST || TESTSTRIP_ID == ColorActivity.TESTSTRIP_ID.TESTSTRIP_ID_SECOND && RECG_METHOD == RecognitionMethodActivity.RECOGNITION_METHOD.COLOR ) {
            // 檢查是否取得相機權限
            if ( ActivityCompat.checkSelfPermission( this, Manifest.permission.CAMERA ) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions( this, new String[]{ Manifest.permission.CAMERA }, 1 );
            } else {
                Intent intent = new Intent( MediaStore.ACTION_IMAGE_CAPTURE );
                startActivityForResult( intent, PHOTO_MODE.CAMERA );
            }
        } else {
            // TODO
        }

    }

    /* 開啟相簿選取照片 */
    public void openAlbum( View view ) {
        // 若試紙種類為「三聚氰胺檢測」或「農藥檢測」且指定使用「比色法」
        if ( TESTSTRIP_ID == ColorActivity.TESTSTRIP_ID.TESTSTRIP_ID_FIRST || TESTSTRIP_ID == ColorActivity.TESTSTRIP_ID.TESTSTRIP_ID_SECOND && RECG_METHOD == RecognitionMethodActivity.RECOGNITION_METHOD.COLOR ) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PHOTO_MODE.ABLUM);
        } else {
            // TODO
        }
    }

    @Override
    protected void onActivityResult( int requestCode, int resultCode, @Nullable Intent data ) {
        super.onActivityResult( requestCode, resultCode, data );

        if ( resultCode == RESULT_OK ) {
            switch ( requestCode ) {
                case PHOTO_MODE.CAMERA:
                    // 影像資料轉成 Bitmap 格式
                    bitmap = ( Bitmap ) data.getExtras().get( "data" );

                    // 啟動影像定位 Activity 並將影像資料傳送過去
                    intent = new Intent( this, PhotoPositionActivity.class );
                    intent.putExtra( "TestStripID", TESTSTRIP_ID );
                    intent.putExtra( "RecognitionMethod", RECG_METHOD );
                    intent.putExtra( "PhotoMode", PHOTO_MODE.CAMERA );
                    intent.putExtra( "BitmapImage", bitmap );
                    startActivity( intent );
                    break;
                case PHOTO_MODE.ABLUM:
                    // 讀取影像路徑
                    Uri uri = data.getData();

                    // 啟動影像定位 Activity 並將影像路徑傳送過去
                    intent = new Intent( this, PhotoPositionActivity.class );
                    intent.putExtra( "TestStripID", TESTSTRIP_ID );
                    intent.putExtra( "RecognitionMethod", RECG_METHOD );
                    intent.putExtra( "PhotoMode", PHOTO_MODE.ABLUM );
                    intent.putExtra( "ImagePath", uri.toString() );
                    startActivity( intent );
                    break;
                default:
                    break;
            }
        }
    }

    /* 開啟 Camera FV-5 App */
    public void openCameraFV5App( View view ) {
        try {
            Intent intent = getPackageManager().getLaunchIntentForPackage( CAMERAFV5_PACKAGE_NAME );
            startActivity( intent );
        } catch ( Exception e ) {
            // 跳出對話窗提示未安裝 App
            AlertDialog.Builder builder = new AlertDialog.Builder( this );
            builder.setTitle( "Warning!" );
            builder.setMessage( "未安裝 Camera FV-5" );
            builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            } );
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
}
