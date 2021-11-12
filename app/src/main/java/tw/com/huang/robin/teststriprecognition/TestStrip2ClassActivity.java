package tw.com.huang.robin.teststriprecognition;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class TestStrip2ClassActivity extends AppCompatActivity {

    private static int TESTSTRIP_ID;
    private static int RECOGNITION_METHOD;

    public final class TESTSTRIP2_TYPE {
        public static final int CARBOFURAN = 1; // 加保扶
        public static final int FINNEY = 2; // 芬普尼
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_strip2_class);

        Intent intent = getIntent();
        TESTSTRIP_ID = intent.getIntExtra( "TestStripID", 0 );
        RECOGNITION_METHOD = intent.getIntExtra( "RecognitionMethod", 0 );
    }

    public void openCarbofuran( View view ) {
        Intent intent = new Intent( this, RamanMethodActivity.class );
        intent.putExtra( "TestStripID", TESTSTRIP_ID );
        intent.putExtra( "RecognitionMethod", RecognitionMethodActivity.RECOGNITION_METHOD.RAMAN );
        intent.putExtra( "TestStripType", TESTSTRIP2_TYPE.CARBOFURAN );
        startActivity( intent );
    }
}
