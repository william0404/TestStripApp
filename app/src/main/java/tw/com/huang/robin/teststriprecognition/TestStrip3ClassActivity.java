package tw.com.huang.robin.teststriprecognition;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class TestStrip3ClassActivity extends AppCompatActivity {

    private static int TESTSTRIP_ID;
    private static int RECOGNITION_METHOD;

    public final class TESTSTRIP3_TYPE {
        public static final int VESICENT = 1; // 糜爛性毒劑
        public static final int NERVE= 2; // 神經性毒劑
        public static final int BLOOD = 3; // 血液姓毒劑
        public static final int INDUSTRY = 4; // 工業毒化物
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_strip3_class);

        Intent intent = getIntent();
        TESTSTRIP_ID = intent.getIntExtra( "TestStripID", 0 );
        RECOGNITION_METHOD = intent.getIntExtra( "RecognitionMethod", 0 );
    }

    public void openVESICENT( View view ) {
        Intent intent = new Intent( this, RamanMethodActivity.class );
        intent.putExtra( "TestStripID", TESTSTRIP_ID );
        intent.putExtra( "RecognitionMethod", RecognitionMethodActivity.RECOGNITION_METHOD.RAMAN );
        intent.putExtra( "TestStripType", TESTSTRIP3_TYPE.VESICENT );
        startActivity( intent );
    }
    public void openNERVE( View view ) {
        Intent intent = new Intent( this, RamanMethodActivity.class );
        intent.putExtra( "TestStripID", TESTSTRIP_ID );
        intent.putExtra( "RecognitionMethod", RecognitionMethodActivity.RECOGNITION_METHOD.RAMAN );
        intent.putExtra( "TestStripType", TESTSTRIP3_TYPE.NERVE );
        startActivity( intent );
    }
    public void openBLOOD( View view ) {
        Intent intent = new Intent( this, RamanMethodActivity.class );
        intent.putExtra( "TestStripID", TESTSTRIP_ID );
        intent.putExtra( "RecognitionMethod", RecognitionMethodActivity.RECOGNITION_METHOD.RAMAN );
        intent.putExtra( "TestStripType", TESTSTRIP3_TYPE.BLOOD );
        startActivity( intent );
    }
    public void openINDUSTRY( View view ) {
        Intent intent = new Intent( this, RamanMethodActivity.class );
        intent.putExtra( "TestStripID", TESTSTRIP_ID );
        intent.putExtra( "RecognitionMethod", RecognitionMethodActivity.RECOGNITION_METHOD.RAMAN );
        intent.putExtra( "TestStripType", TESTSTRIP3_TYPE.INDUSTRY );
        startActivity( intent );
    }
}
