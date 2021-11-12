package tw.com.huang.robin.teststriprecognition;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    public final class TESTSTRIP_ID {
        //
        public static final int TESTSTRIP_ID_FIRST = 1;
        public static final int TESTSTRIP_ID_SECOND = 2;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }




    public void gotoColor( View view ){
        Intent intent = new Intent( this, ColorActivity.class );
        intent.putExtra( "TestStripID", TESTSTRIP_ID.TESTSTRIP_ID_FIRST );
        startActivity( intent );
    }


    public void gotoAir( View view ){
        Intent intent = new Intent( this, AirActivity.class );
        intent.putExtra( "TestStripID", TESTSTRIP_ID.TESTSTRIP_ID_SECOND );
        startActivity( intent );
    }

    public void gotoMap( View view ){
        Intent intent = new Intent( this, MapActivity.class );
        intent.putExtra( "TestStripID", TESTSTRIP_ID.TESTSTRIP_ID_SECOND );
        startActivity( intent );
    }

    public void gotoMark( View view ){
        Intent intent = new Intent( this, MarkActivity.class );
        intent.putExtra( "TestStripID", TESTSTRIP_ID.TESTSTRIP_ID_SECOND );
        startActivity( intent );
    }
}
