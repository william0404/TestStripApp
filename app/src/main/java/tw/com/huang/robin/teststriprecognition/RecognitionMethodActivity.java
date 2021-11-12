package tw.com.huang.robin.teststriprecognition;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class RecognitionMethodActivity extends AppCompatActivity {

    private static int TESTSTRIP_ID;

    public final class RECOGNITION_METHOD {
        public static final int COLOR = 1; // 比色法
        public static final int RAMAN = 2; // 拉曼法
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 獲取當下試紙類別
        Intent intent = getIntent();
        TESTSTRIP_ID = intent.getIntExtra( "TestStripID", 0 );
        if ( TESTSTRIP_ID == ColorActivity.TESTSTRIP_ID.TESTSTRIP_ID_THIRD ){
            setContentView(R.layout.activity_recognition_method_posion);
        }else {
            setContentView(R.layout.activity_recognition_method);
        }

    }

    // 開啟照片選擇介面，並指定使用比色法
    public void openPhotoModeColor( View view ){

        // 若試紙種類為「三聚氰胺檢測」或「農藥檢測」
        if ( TESTSTRIP_ID == ColorActivity.TESTSTRIP_ID.TESTSTRIP_ID_FIRST ||  TESTSTRIP_ID == ColorActivity.TESTSTRIP_ID.TESTSTRIP_ID_SECOND ) {
            Intent intent = new Intent( this, PhotoModeActivity.class );
            intent.putExtra( "TestStripID", TESTSTRIP_ID );
            intent.putExtra( "RecognitionMethod", RECOGNITION_METHOD.COLOR );
            startActivity( intent );
        } else if ( TESTSTRIP_ID == ColorActivity.TESTSTRIP_ID.TESTSTRIP_ID_THIRD ) { // 若試紙種類為「化學毒劑檢測」
            Intent intent = new Intent( this, TestStrip3ClassActivity.class );
            intent.putExtra( "TestStripID", TESTSTRIP_ID );
            intent.putExtra( "RecognitionMethod", RECOGNITION_METHOD.COLOR );
            startActivity( intent );
        } else if ( TESTSTRIP_ID == ColorActivity.TESTSTRIP_ID.TESTSTRIP_ID_FOURTH ) { // 若試紙種類為「火炸藥檢測」
            // TODO
        }
    }

    // 開啟照片選擇介面，並指定使用拉曼法
    public void openPhotoModeRaman( View view ){

        if ( TESTSTRIP_ID == ColorActivity.TESTSTRIP_ID.TESTSTRIP_ID_FIRST ) {// 若試紙種類為「三聚氰胺檢測」
            Intent intent = new Intent( this, RamanMethodActivity.class );
            intent.putExtra( "TestStripID", TESTSTRIP_ID );
            intent.putExtra( "RecognitionMethod", RECOGNITION_METHOD.RAMAN );
            startActivity( intent );
        } else if ( TESTSTRIP_ID == ColorActivity.TESTSTRIP_ID.TESTSTRIP_ID_SECOND ) { // 若試紙種類為「農藥檢測」
            Intent intent = new Intent( this, TestStrip2ClassActivity.class );
            intent.putExtra( "TestStripID", TESTSTRIP_ID );
            intent.putExtra( "RecognitionMethod", RECOGNITION_METHOD.RAMAN );
            startActivity( intent );
        } else if ( TESTSTRIP_ID == ColorActivity.TESTSTRIP_ID.TESTSTRIP_ID_THIRD ) { // 若試紙種類為「化學毒劑檢測」
            Intent intent = new Intent( this, TestStrip3ClassActivity.class );
            intent.putExtra( "TestStripID", TESTSTRIP_ID );
            intent.putExtra( "RecognitionMethod", RECOGNITION_METHOD.RAMAN );
            startActivity( intent );
        } else if ( TESTSTRIP_ID == ColorActivity.TESTSTRIP_ID.TESTSTRIP_ID_FOURTH ) { // 若試紙種類為「火炸藥檢測」
            Intent intent = new Intent( this, TestStrip4ClassActivity.class );
            intent.putExtra( "TestStripID", TESTSTRIP_ID );
            intent.putExtra( "RecognitionMethod", RECOGNITION_METHOD.RAMAN );
            startActivity( intent );
        }
    }
}
