package tw.com.huang.robin.teststriprecognition;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RecognitionResultActivity extends AppCompatActivity {

    private static int TESTSTRIP_ID;

    LinearLayout tableLayout;
    Intent intent;
    Bitmap bitmap;
    Bitmap bitmapDisplay;
    Bitmap bitmapBox;
    Canvas canvas;
    Paint paint;
    Uri uri;
    ImageView testImageView;
    TextView  resultTextView;

    TextView RedValueText;
    TextView GreenValueText;
    TextView BlueValueText;
    TextView GrayValueText;

    // 影像定位範圍
    private int cropX1 = 0;
    private int cropY1 = 0;
    private int cropX2 = 0;
    private int cropY2 = 0;

    // 平均色彩值
    private float averageRed;
    private float averageGreen;
    private float averageBlue;
    private float averageGray;

    int flipFlag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognition_result);

        tableLayout = findViewById( R.id.table );
        testImageView  = findViewById( R.id.testingImage );
        resultTextView = findViewById( R.id.resultText );

        RedValueText = findViewById( R.id.RedValue );
        GreenValueText = findViewById( R.id.GreenValue );
        BlueValueText = findViewById( R.id.BlueValue );
        GrayValueText = findViewById( R.id.GrayValue );

        intent = getIntent();

        TESTSTRIP_ID = intent.getIntExtra( "TestStripID", 0 );
        flipFlag = intent.getIntExtra( "FlipFlag", 0 );
        float featureValue = intent.getFloatExtra( "FeatureValue", 0.f );

        cropX1 = intent.getIntExtra( "x1", 0 );
        cropY1 = intent.getIntExtra( "y1", 0 );
        cropX2 = intent.getIntExtra( "x2", 0 );
        cropY2 = intent.getIntExtra( "y2", 0 );

        // 平均色彩值
        averageRed = intent.getFloatExtra( "averageRed", 0.f );
        averageGreen = intent.getFloatExtra( "averageGreen", 0.f );
        averageBlue = intent.getFloatExtra( "averageBlue", 0.f );
        averageGray = intent.getFloatExtra( "averageGray", 0.f );

        displayTestImage();
        disaplayReferenceImage( featureValue );
    }

    /* 顯示測試影像 */
    private void displayTestImage() {
        int mode = intent.getIntExtra( "PhotoMode", 0 );

        switch ( mode ) {
            case PhotoModeActivity.PHOTO_MODE.CAMERA:
                bitmap = ( Bitmap ) intent.getParcelableExtra( "BitmapImage" );
                break;
            case PhotoModeActivity.PHOTO_MODE.ABLUM:
                String imagePath = intent.getStringExtra( "ImagePath" );
                uri = Uri.parse( imagePath );
                try {
                    InputStream imageStream = getContentResolver().openInputStream( uri );
                    bitmap = BitmapFactory.decodeStream( imageStream );
                } catch ( Exception e ) {

                }
                break;
            default:
                break;
        }

        // 判斷是否需要旋轉影像
        if ( flipFlag == 1 ) {
            // 轉換定位點座標值
            int cropX1_old = cropX1;
            int cropY1_old = cropY1;
            int cropX2_old = cropX2;
            int cropY2_old = cropY2;

            int height = bitmap.getHeight();

            cropX1 = height - cropY2_old;
            cropX2 = height - cropY1_old;
            cropY1 = cropX1_old;
            cropY2 = cropX2_old;

            Matrix matrix = new Matrix();
            matrix.setRotate( 90 );
            Bitmap rotateBitmap = Bitmap.createBitmap( bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true );
            displayLocalTestImage( rotateBitmap );
        } else {
            displayLocalTestImage( bitmap );
        }
    }

    // 顯示局部測試影像
    private void displayLocalTestImage( Bitmap bitmap ) {

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int x = 0;
        int y = 0;
        int w = 0;
        int h = 0;

        int deltaX = cropX2 - cropX1;
        int deltaY = cropY2 - cropY1;

        int newX1 = 0;
        int newY1 = 0;
        int newX2 = 0;
        int newY2 = 0;


        try {
            switch (TESTSTRIP_ID) {
                case MainActivity.TESTSTRIP_ID.TESTSTRIP_ID_FIRST:

                    x = ( int )( width * 0.43 );
                    y = ( int )( height * 0.55 );
                    w = ( int )( width * 0.13 );
                    h = ( int )( height * 0.16 );

                    newX1 = cropX1 - x;
                    newY1 = cropY1 - y;
                    newX2 = newX1 + deltaX;
                    newY2 = newY1 + deltaY;

                    bitmapDisplay = Bitmap.createBitmap(bitmap, x, y, w, h);
                    testImageView.setImageBitmap(bitmapDisplay);
                    break;
                case MainActivity.TESTSTRIP_ID.TESTSTRIP_ID_SECOND:

                    x = ( int )( width * 0.43 );
                    y = ( int )( height * 0.57 );
                    w = ( int )( width * 0.09 );
                    h = ( int )( height * 0.1 );

                    newX1 = cropX1 - x;
                    newY1 = cropY1 - y;
                    newX2 = newX1 + deltaX;
                    newY2 = newY1 + deltaY;

                    bitmapDisplay = Bitmap.createBitmap(bitmap, x, y, w, h);
                    testImageView.setImageBitmap(bitmapDisplay);
                    break;
                default:
                    break;
            }

            // 繪製定位框
            canvas = new Canvas(bitmapDisplay);
            testImageView.draw(canvas);

            paint = new Paint();
            paint.setColor(0xff00ffff);

            canvas.drawLine(newX1, newY1, newX1, newY2, paint);
            canvas.drawLine(newX1, newY1, newX2, newY1, paint);
            canvas.drawLine(newX1, newY2, newX2, newY2, paint);
            canvas.drawLine(newX2, newY1, newX2, newY2, paint);

            testImageView.setImageBitmap(bitmapDisplay);
        } catch ( Exception e ) {
            // 跳出對話窗提示未安裝 App
            AlertDialog.Builder builder = new AlertDialog.Builder( this );
            builder.setTitle( "Warning!" );
            builder.setMessage( "超過定義的選取範圍，影像無法顯示!" );
            builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            } );
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    /* 根據影像辨識結果顯示對應濃度數值以及顯示參考影像 ( 0 ppm ) */
    private void disaplayReferenceImage( float value ) {
        float ppmValue;
        float diffValue = 0.f;
        float minDiff = 1000.f;
        int   minIndex = 0;
        int   referIndex = 0;


        // 顯示 RGB & Gray 的平均數值
        RedValueText.setText( String.format( "%.2f", averageRed ) );
        GreenValueText.setText( String.format( "%.2f", averageGreen ) );
        BlueValueText.setText( String.format( "%.2f", averageBlue ) );
        GrayValueText.setText( String.format( "%.2f", averageGray ) );

        switch ( TESTSTRIP_ID ) {
            case MainActivity.TESTSTRIP_ID.TESTSTRIP_ID_FIRST: // 「三聚氰胺檢測」

                GrayValueText.setText( String.format( "%.2f", value ) );

                ppmValue = ( value - 91.0612f ) / 8.58648f;

                if( ppmValue < 0.f ) {
                        ppmValue = 0.f;
                    } else if( ppmValue > 2.5f ) {
                        ppmValue = 2.5f;
                }

                resultTextView.setText( String.format( "%.2f", ppmValue ) + " ppm" );

                break;
            case MainActivity.TESTSTRIP_ID.TESTSTRIP_ID_SECOND: // 「農藥檢測」

                ppmValue = ( value - 37.04237f ) / 64.06013f;

                if( ppmValue < 0.f ) {
                    ppmValue = 0.f;
                } else if( ppmValue > 1.f ) {
                    ppmValue = 1.f;
                }

                resultTextView.setText( String.format( "%.3f", ppmValue ) + " ppm" );

                break;
            default:
                break;
        }
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


        tableLayout.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(tableLayout.getDrawingCache());


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
            Log.d("filee", dir.toString());
            dir.mkdirs();
//            String fileName = String.format("analysis.jpg");
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            File outFile = new File(dir, "IMG_" + timeStamp + ".jpg");
            if(outFile.exists())
            {
                outFile.delete();
            }

            //destination.createNewFile();
            fo = new FileOutputStream(outFile);
            fo.write(bytes.toByteArray());
            fo.close();
            Toast.makeText(this,"截圖成功",Toast.LENGTH_SHORT).show();
            bitmap.recycle();
            bitmap=null;

        } catch (FileNotFoundException e) {
            Toast.makeText(this,"截圖失敗1,請重試",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (IOException e) {
            Toast.makeText(this,"截圖失敗2,請重試",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }
}
