package tw.com.huang.robin.teststriprecognition;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    public void openPhotoMode01( View view ){

        Intent intent = new Intent( this, RecognitionMethodActivity.class );
        intent.putExtra( "TestStripID", ColorActivity.TESTSTRIP_ID.TESTSTRIP_ID_FIRST );
        startActivity( intent );

    }

    // 開啟辨識分析法選擇介面，並指定試紙2
    public void openPhotoMode02( View view ){

        Intent intent = new Intent( this, RecognitionMethodActivity.class );
        intent.putExtra( "TestStripID", ColorActivity.TESTSTRIP_ID.TESTSTRIP_ID_SECOND );
        startActivity( intent );

    }

    // 開啟辨識分析法選擇介面，並指定試紙3
    public void openPhotoMode03( View view ){

        Intent intent = new Intent( this, RecognitionMethodActivity.class );
        intent.putExtra( "TestStripID", ColorActivity.TESTSTRIP_ID.TESTSTRIP_ID_THIRD );
        startActivity( intent );

    }

    // 開啟辨識分析法選擇介面，並指定試紙4
    public void openPhotoMode04( View view ){

        Intent intent = new Intent( this, RecognitionMethodActivity.class );
        intent.putExtra( "TestStripID", ColorActivity.TESTSTRIP_ID.TESTSTRIP_ID_FOURTH );
        startActivity( intent );

    }


    public void seeList(View v)
    {

        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setType("image/*");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        /*
        Intent intent = new Intent( this, GridViewActivity.class );
        //intent.putExtra( "TestStripID", TESTSTRIP_ID.TESTSTRIP_ID_FOURTH );
        startActivity( intent );
        */
    }

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
