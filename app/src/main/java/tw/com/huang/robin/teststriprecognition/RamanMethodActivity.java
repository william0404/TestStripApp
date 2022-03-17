package tw.com.huang.robin.teststriprecognition;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RamanMethodActivity extends AppCompatActivity {

    private float ramanValue;
    private float ppmValue;
    private static int TESTSTRIP_ID;
    private static int TESTSTRIP_TYPE;
    private EditText ramanEditText;
    private EditText resultEditText;
    private LinearLayout tableLayout;
    private TextView ramanTilte;
    private Button okButton;
    private SharedPreferences filename_pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raman_method);

        ramanEditText = ( EditText )findViewById( R.id.ramanText );
        resultEditText = ( EditText )findViewById( R.id.resultText );
        tableLayout = ( LinearLayout )findViewById( R.id.ramanTable );
        ramanTilte = ( TextView )findViewById( R.id.ramanTitle );
        okButton = ( Button )findViewById( R.id.okButton );
        filename_pref = getSharedPreferences("filename", MODE_PRIVATE);
        // 獲取當下試紙類別
        Intent intent = getIntent();
        TESTSTRIP_ID = intent.getIntExtra( "TestStripID", 0 );
        TESTSTRIP_TYPE = intent.getIntExtra( "TestStripType", 0 );

        switch ( TESTSTRIP_ID ) {
            case ColorActivity.TESTSTRIP_ID.TESTSTRIP_ID_FIRST: // 「三聚氰胺檢測」
                ramanTilte.setText( "請輸入拉曼圖譜\n特徵峰(699 cm-1)強度值" );
                break;
            case ColorActivity.TESTSTRIP_ID.TESTSTRIP_ID_SECOND: // 「農藥檢測」
                switch ( TESTSTRIP_TYPE ) {
                    case TestStrip2ClassActivity.TESTSTRIP2_TYPE.CARBOFURAN: // 「保加扶」檢測
                        ramanTilte.setText( "請輸入拉曼圖譜\n特徵峰(689 cm-1)強度值" );
                    case TestStrip2ClassActivity.TESTSTRIP2_TYPE.FINNEY: // 「芬普尼」檢測
                        // TODO
                        break;
                    default:
                        break;
                }
            case ColorActivity.TESTSTRIP_ID.TESTSTRIP_ID_THIRD:// 「化學毒劑檢測」
                switch ( TESTSTRIP_TYPE ) {
                    case TestStrip3ClassActivity.TESTSTRIP3_TYPE.VESICENT: // 「糜爛性」檢測
                        ramanTilte.setText( "請輸入拉曼圖譜\n特徵峰(123 cm-1)強度值" );
                        break;
                    case TestStrip3ClassActivity.TESTSTRIP3_TYPE.BLOOD: // 「血液性」檢測
                        ramanTilte.setText( "請輸入拉曼圖譜\n特徵峰(234 cm-1)強度值" );
                        break;
                    case TestStrip3ClassActivity.TESTSTRIP3_TYPE.INDUSTRY: // 「工業毒」檢測
                        ramanTilte.setText( "請輸入拉曼圖譜\n特徵峰(345 cm-1)強度值" );
                        break;
                    case TestStrip3ClassActivity.TESTSTRIP3_TYPE.NERVE: // 「神經性」檢測
                        ramanTilte.setText( "請輸入拉曼圖譜\n特徵峰(456 cm-1)強度值" );
                        break;
                    default:
                        break;
                }
            case ColorActivity.TESTSTRIP_ID.TESTSTRIP_ID_FOURTH: // 「火炸藥檢測」
                // TODO
                break;
            default:
                break;
        }
    }

    // 計算結果
    public void calculateResult( View view ) {
        // 檢查是否有輸入值
        if( !( "".equals( ramanEditText.getText().toString() ) ) ) {
            ramanValue = Float.parseFloat( ramanEditText.getText().toString() );

            // ppmValue = ( ramanValue - wavelength _value) / baselin_value
            switch ( TESTSTRIP_ID ) {
                case ColorActivity.TESTSTRIP_ID.TESTSTRIP_ID_FIRST: // 「三聚氰胺檢測」
                    ppmValue = ( ramanValue - 479.5f ) / 1548f;

                     if( ppmValue < 0.f ) {
                        ppmValue = 0.f;
                    }

                    resultEditText.setText( String.format( "%.2f", ppmValue ) + " ppm" ); // 顯示 ppm 數值
                    break;
                case ColorActivity.TESTSTRIP_ID.TESTSTRIP_ID_SECOND: // 「農藥檢測」
                    switch ( TESTSTRIP_TYPE ) {
                        case TestStrip2ClassActivity.TESTSTRIP2_TYPE.CARBOFURAN: // 「加保扶」檢測
                            ppmValue = ( ramanValue - 2004.1f ) / 10297f;

                            if( ppmValue < 0.01f ) {
                                ppmValue = 0.01f;
                            }
                            break;
                        case TestStrip2ClassActivity.TESTSTRIP2_TYPE.FINNEY: // 「芬普尼」檢測
                            // TODO
                            break;
                        default:
                            break;
                    }

                    resultEditText.setText( String.format( "%.3f", ppmValue ) + " ppm" ); // 顯示 ppm 數值
                    break;
                case ColorActivity.TESTSTRIP_ID.TESTSTRIP_ID_THIRD:// 「化學毒劑檢測」
                    switch ( TESTSTRIP_TYPE ) {
                        case TestStrip3ClassActivity.TESTSTRIP3_TYPE.VESICENT: // 「糜爛性」檢測
                            ppmValue = ( ramanValue - 2020.4f ) / 10001.4f;

                            if( ppmValue < 0.01f ) {
                                ppmValue = 0.01f;
                            }
                            resultEditText.setText( String.format( "%.2f", ppmValue ) + " ppm" ); // 顯示 ppm 數值
                            break;

                        case TestStrip3ClassActivity.TESTSTRIP3_TYPE.BLOOD: // 「血液性」檢測
                            ppmValue = ( ramanValue - 2020.4f ) / 10001.4f;

                            if( ppmValue < 0.01f ) {
                                ppmValue = 0.01f;
                            }
                            resultEditText.setText( String.format( "%.2f", ppmValue ) + " ppm" ); // 顯示 ppm 數值
                            break;
                        case TestStrip3ClassActivity.TESTSTRIP3_TYPE.INDUSTRY: // 「工業毒」檢測
                            ppmValue = ( ramanValue - 2020.4f ) / 10001.4f;

                            if( ppmValue < 0.01f ) {
                                ppmValue = 0.01f;
                            }
                            resultEditText.setText( String.format( "%.2f", ppmValue ) + " ppm" ); // 顯示 ppm 數值
                            break;
                        case TestStrip3ClassActivity.TESTSTRIP3_TYPE.NERVE: // 「神經性」檢測
                            ppmValue = ( ramanValue - 2020.4f ) / 10001.4f;

                            if( ppmValue < 0.01f ) {
                                ppmValue = 0.01f;
                            }
                            resultEditText.setText( String.format( "%.2f", ppmValue ) + " ppm" ); // 顯示 ppm 數值
                            break;
                        default:
                            break;
                    }
                case ColorActivity.TESTSTRIP_ID.TESTSTRIP_ID_FOURTH: // 「火炸藥檢測」
                    // TODO
                    break;
                default:
                    break;
            } // TESTSTRIP_ID

        } // if-else
    }

    public void ramanCaptureClick(View v)
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

        okButton.setVisibility( View.INVISIBLE );

        tableLayout.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(tableLayout.getDrawingCache());


//        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        Log.e("Activity", "Pick from Camera::>>> ");

        //String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());  (yyyy.MM.dd_G'at'_HH:mm:ss_z新紀錄嘗試)
        //File destination = new File(Environment.getExternalStorageDirectory() + "/" +
        //        getString(R.string.app_name), "IMG_" + timeStamp + ".jpg");

        try {
            File dir = new File(Environment.getExternalStorageDirectory() +  File.separator + getString(R.string.app_name));
            if (!dir.exists()) {
                dir.mkdirs();
            }
            Log.d("filee", dir.toString());
            String fileName = new SimpleDateFormat("yyyyMMddHHmmss'.jpg'", Locale.getDefault()).format(new Date());
            filename_pref.edit()
                    .putString("filename",fileName).commit();
            File outFile = new File(dir, fileName);
            outFile.createNewFile();
//            if(outFile.exists())
//            {
//                outFile.delete();
//            }

            FileOutputStream fo  = new FileOutputStream(outFile);;
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fo);
//            fo.write(bytes.toByteArray());
            fo.close();
            Toast.makeText(this,"截圖成功",Toast.LENGTH_SHORT).show();
            bitmap.recycle();
            bitmap=null;

        } catch (FileNotFoundException e) {
            Toast.makeText(this,"截圖失敗3,請重試",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (IOException e) {
            Toast.makeText(this,"截圖失敗4,請重試",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        okButton.setVisibility( View.VISIBLE );

    }
}
