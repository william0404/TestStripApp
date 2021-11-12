package tw.com.huang.robin.teststriprecognition;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.File;

import static android.content.ContentValues.TAG;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;

    // Keep all Images in array
    /*
    public Integer[] mThumbIds = {

            R.drawable.pic_1, R.drawable.pic_2,
            R.drawable.pic_3, R.drawable.pic_4,
            R.drawable.pic_5, R.drawable.pic_6,
            R.drawable.pic_7, R.drawable.pic_8,
            R.drawable.pic_9, R.drawable.pic_10,
            R.drawable.pic_11, R.drawable.pic_12,
            R.drawable.pic_13, R.drawable.pic_14,
            R.drawable.pic_15

    };
    */
    public File[] mThumbIds;
    int width;
    int height;


    // Constructor
    public ImageAdapter(Context c){
        mContext = c;

        File sdCard = Environment.getExternalStorageDirectory();
        File dir2 = new File(sdCard.getAbsolutePath() +"/比色APP/");

        File outFile2 = new File(dir2, "歷史紀錄");
        mThumbIds = outFile2.listFiles();

        Log.d(TAG,"歷史紀錄有"+mThumbIds.length+"筆");

        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        width = metrics.widthPixels;
        height = metrics.heightPixels;
    }

    @Override
    public int getCount() {
        return mThumbIds.length;
    }

    @Override
    public Object getItem(int position) {
        return mThumbIds[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = new ImageView(mContext);
        //imageView.setImageResource(mThumbIds[position]);
        Bitmap picture = BitmapFactory.decodeFile(mThumbIds[position].getAbsolutePath());
        Log.v("Path", mThumbIds[position].getAbsolutePath());
        imageView.setImageBitmap(picture);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setLayoutParams(new GridView.LayoutParams(width-20, height-20));
        return imageView;
    }




}
