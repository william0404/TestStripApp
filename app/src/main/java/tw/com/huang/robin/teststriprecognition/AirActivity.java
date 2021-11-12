package tw.com.huang.robin.teststriprecognition;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import im.delight.android.webview.AdvancedWebView;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AirActivity extends AppCompatActivity {

    final String TAG = "MainActivity";
    private LocationManager status;
    private boolean getService = false;     //是否已開啟定位服務
    private AdvancedWebView mWebView;
    private LocationManager lms;
    private String bestProvider = LocationManager.GPS_PROVIDER;
    private Location location;
    private KProgressHUD hud;
    LinearLayout view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_air);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        status = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        view = findViewById(R.id.view);

        mWebView = (AdvancedWebView) findViewById(R.id.webview);
        //mWebView.setListener(this, this);
        //mWebView.loadUrl("https://www.cwb.gov.tw/V8/C/W/OBS_County.html?ID=63");
        mWebView.loadUrl("https://weather.com/zh-TW/weather/today/");

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


        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());


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
            dir.mkdirs();
            String fileName = String.format("air.jpg");
            File outFile = new File(dir, fileName);
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
            Toast.makeText(this,"截圖失敗,請重試",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (IOException e) {
            Toast.makeText(this,"截圖失敗,請重試",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }


    /**
     * 方位改變時觸發，進行呼叫
     */
    private final LocationListener locationListener = new LocationListener() {
        String tempCityName;

        public void onLocationChanged(Location location) {
            //tempCityName = updateWithNewLocation(location);
            //if ((tempCityName != null) && (tempCityName.length() != 0)) {
            //    cityName = tempCityName;
            //}
        }

        public void onProviderDisabled(String provider) {
            //tempCityName = updateWithNewLocation(null);
            //if ((tempCityName != null) && (tempCityName.length() != 0)) {
            //    cityName = tempCityName;
            //}
        }
        public void onProviderEnabled(String provider) {
        }
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };


    public void getAir(View v) {
        Log.d(TAG, "查詢天氣");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);


                return;
            }
            else if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);


                return;
            }
        }



        if (status.isProviderEnabled(LocationManager.GPS_PROVIDER) || status.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            location = status.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            if(location == null){
                status.requestLocationUpdates("gps", 5000, 1, locationListener);
            }

            /*
            hud = KProgressHUD.create(AirActivity.this)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel("Please wait")
                    .setDetailsLabel("")
	                .setCancellable(true)
                    .setAnimationSpeed(2)
                    .setDimAmount(0.5f)
                    .show();
                    */


            Log.d(TAG, "loc:" + location);
            if (location != null) {
                double lat = location.getLatitude();
                Log.d(TAG, "latitude: " + lat);
                double lng = location.getLongitude();
                Log.d(TAG, "longitude: " + lng);

                mWebView.loadUrl("https://weather.com/zh-TW/weather/today/"+lat+","+lng);
                //HttpClient client = new DefaultHttpClient();


                //new Thread(runnable).start();
            }
            else {
                //hud.dismiss();
            }




        } else {
            Toast.makeText(this, "請開啟定位服務", Toast.LENGTH_LONG).show();
            getService = true; //確認開啟定位服務
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)); //開啟設定頁面
        }



    }





    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String val = data.getString("value");
            Log.i(TAG,"请求结果:" + val);

            if(val.contains("基隆市"))
            {
                mWebView.loadUrl("https://www.cwb.gov.tw/V8/C/W/OBS_County.html?ID=10017");
            }
            else if(val.contains("臺北")||val.contains("台北"))
            {
                mWebView.loadUrl("https://www.cwb.gov.tw/V8/C/W/OBS_County.html?ID=63");
            }
            else if(val.contains("新北"))
            {
                mWebView.loadUrl("https://www.cwb.gov.tw/V8/C/W/OBS_County.html?ID=65");
            }
            else if(val.contains("桃園"))
            {
                mWebView.loadUrl("https://www.cwb.gov.tw/V8/C/W/OBS_County.html?ID=68");
            }
            else if(val.contains("新竹市"))
            {
                mWebView.loadUrl("https://www.cwb.gov.tw/V8/C/W/OBS_County.html?ID=10018");
            }
            else if(val.contains("新竹縣"))
            {
                mWebView.loadUrl("https://www.cwb.gov.tw/V8/C/W/OBS_County.html?ID=10004");
            }
            else if(val.contains("苗栗"))
            {
                mWebView.loadUrl("https://www.cwb.gov.tw/V8/C/W/OBS_County.html?ID=10005");
            }
            else if(val.contains("臺中")|val.contains("台中"))
            {
                mWebView.loadUrl("https://www.cwb.gov.tw/V8/C/W/OBS_County.html?ID=66");
            }
            else if(val.contains("彰化"))
            {
                mWebView.loadUrl("https://www.cwb.gov.tw/V8/C/W/OBS_County.html?ID=10007");
            }
            else if(val.contains("南投"))
            {
                mWebView.loadUrl("https://www.cwb.gov.tw/V8/C/W/OBS_County.html?ID=10008");
            }
            else if(val.contains("雲林"))
            {
                mWebView.loadUrl("https://www.cwb.gov.tw/V8/C/W/OBS_County.html?ID=10009");
            }
            else if(val.contains("嘉義市"))
            {
                mWebView.loadUrl("https://www.cwb.gov.tw/V8/C/W/OBS_County.html?ID=10020");
            }
            else if(val.contains("嘉義縣"))
            {
                mWebView.loadUrl("https://www.cwb.gov.tw/V8/C/W/OBS_County.html?ID=10010");
            }
            else if(val.contains("臺南")|val.contains("台南"))
            {
                mWebView.loadUrl("https://www.cwb.gov.tw/V8/C/W/OBS_County.html?ID=67");
            }
            else if(val.contains("高雄"))
            {
                mWebView.loadUrl("https://www.cwb.gov.tw/V8/C/W/OBS_County.html?ID=64");
            }
            else if(val.contains("屏東"))
            {
                mWebView.loadUrl("https://www.cwb.gov.tw/V8/C/W/OBS_County.html?ID=10013");
            }
            else if(val.contains("宜蘭"))
            {
                mWebView.loadUrl("https://www.cwb.gov.tw/V8/C/W/OBS_County.html?ID=10002");
            }
            else if(val.contains("花蓮"))
            {
                mWebView.loadUrl("https://www.cwb.gov.tw/V8/C/W/OBS_County.html?ID=10015");
            }
            else if(val.contains("臺東"))
            {
                mWebView.loadUrl("https://www.cwb.gov.tw/V8/C/W/OBS_County.html?ID=10014");
            }
            else if(val.contains("澎湖"))
            {
                mWebView.loadUrl("https://www.cwb.gov.tw/V8/C/W/OBS_County.html?ID=10016");
            }
            else if(val.contains("金門"))
            {
                mWebView.loadUrl("https://www.cwb.gov.tw/V8/C/W/OBS_County.html?ID=09020");
            }
            else if(val.contains("連江"))
            {
                mWebView.loadUrl("https://www.cwb.gov.tw/V8/C/W/OBS_County.html?ID=09007");
            }
        }
    };



    Runnable runnable = new Runnable(){
        @Override
        public void run() {
            // TODO: http request.


            if (location != null) {
                //https://maps.googleapis.com/maps/api/geocode/json?latlng=40.714224,-73.961452&key=AIzaSyCOApQin1hLn5gm462ZEeVFK1rOy_OeahE
                //
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        //.url("http://api.opencube.tw/location?lat="+location.getLatitude()+"&lng="+location.getLongitude())
                        .url("https://maps.googleapis.com/maps/api/geocode/json?latlng="+location.getLatitude()+","+location.getLongitude()+"&language=zh-TW"+"&key=AIzaSyCOApQin1hLn5gm462ZEeVFK1rOy_OeahE")
                        .get()
                        //.post(formBody)
                        .build();

                Call call = client.newCall(request);
                try {
                    Response response = call.execute();
                    //Log.d(TAG, response.body().string());


                    //{"data":{"country_name":"台灣","country":"TW","city":null,"district":"東區","village":null,"zip_code":"600","street_number":"205號","street":"忠孝路","full_address":"600台灣嘉義市東區忠孝路205號","place_id":"ChIJkckkvTSUbjQRZwiYf8wDseM","lat":23.4838007,"lng":120.4536367,"url":"https://www.google.com/maps/@23.4838007,120.4536367,15z"},"status":200}
                    JSONObject obj = new JSONObject(response.body().string());
                    JSONObject data = obj.getJSONObject("plus_code");
                    String full_address = data.getString("compound_code");
                    Log.d(TAG, "full_address:" + full_address);


                    Message msg = new Message();
                    Bundle b = new Bundle();
                    b.putString("value", full_address);
                    msg.setData(b);
                    handler.sendMessage(msg);



                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                finally {
                    //hud.dismiss();
                }
            }

        }
    };


}
