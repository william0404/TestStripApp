package tw.com.huang.robin.teststriprecognition;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class ColorActivity extends AppCompatActivity {

    public final class TESTSTRIP_ID {
        public static final int TESTSTRIP_ID_FIRST = 1; // 三聚氰胺檢測
        public static final int TESTSTRIP_ID_SECOND = 2; // 農藥檢測
        public static final int TESTSTRIP_ID_THIRD = 3; // 化學毒劑檢測
        public static final int TESTSTRIP_ID_FOURTH = 4; // 火炸藥檢測
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
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


    // 開啟辨識分析法選擇介面，並指定試紙1
    public void openPhotoMode01( View view ){

        Intent intent = new Intent( this, RecognitionMethodActivity.class );
        intent.putExtra( "TestStripID", TESTSTRIP_ID.TESTSTRIP_ID_FIRST );
        startActivity( intent );

    }

    // 開啟辨識分析法選擇介面，並指定試紙2
    public void openPhotoMode02( View view ){

        Intent intent = new Intent( this, RecognitionMethodActivity.class );
        intent.putExtra( "TestStripID", TESTSTRIP_ID.TESTSTRIP_ID_SECOND );
        startActivity( intent );

    }

    // 開啟辨識分析法選擇介面，並指定試紙3
    public void openPhotoMode03( View view ){

        Intent intent = new Intent( this, RecognitionMethodActivity.class );
        intent.putExtra( "TestStripID", TESTSTRIP_ID.TESTSTRIP_ID_THIRD );
        startActivity( intent );

    }

    // 開啟辨識分析法選擇介面，並指定試紙4
    public void openPhotoMode04( View view ){

        Intent intent = new Intent( this, RecognitionMethodActivity.class );
        intent.putExtra( "TestStripID", TESTSTRIP_ID.TESTSTRIP_ID_FOURTH );
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




}
