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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONArray;
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

public class MapActivity extends AppCompatActivity {

    final String TAG = "MainActivity";
    private LocationManager status;
    private boolean getService = false;     //是否已開啟定位服務
    private AdvancedWebView mWebView;
    private LocationManager lms;
    private String bestProvider = LocationManager.GPS_PROVIDER;
    private Location location;
    private KProgressHUD hud;

    TextView lat_text;
    TextView lng_text;
    TextView address_text;
    LinearLayout view;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        status = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        lat_text = (TextView) findViewById(R.id.lat_text);
        lng_text = (TextView) findViewById(R.id.lng_text);
        address_text = (TextView) findViewById(R.id.address_text);
        view = findViewById(R.id.view);


        mWebView = (AdvancedWebView) findViewById(R.id.webview);
        mWebView.loadHtml("<iframe \n" +
                "      width=\"600\" \n" +
                "      height=\"450\" \n" +
                "      frameborder=\"0\" \n" +
                "      style=\"border:0\" \n" +
                "      src=\"https://www.google.com/maps/embed/v1/place?key=AIzaSyCOApQin1hLn5gm462ZEeVFK1rOy_OeahE&q=高雄市政府\" \n" +
                "      allowfullscreen>\n" +
                "  </iframe>");

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
            String fileName = String.format("map.jpg");
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
        Log.d(TAG, "查詢定位");

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
            hud = KProgressHUD.create(MapActivity.this)
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
                //HttpClient client = new DefaultHttpClient();

                lat_text.setText(""+lat);
                lng_text.setText(""+lng);



                //mWebView.loadUrl("https://www.google.com.tw/maps/@"+lat+","+lng+",15z?hl=zh-TW");

                new Thread(runnable).start();
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

            address_text.setText(val);
            mWebView.loadHtml("<iframe \n" +
                    "      width=\"600\" \n" +
                    "      height=\"450\" \n" +
                    "      frameborder=\"0\" \n" +
                    "      style=\"border:0\" \n" +
                    "      src=\"https://www.google.com/maps/embed/v1/place?key=AIzaSyCOApQin1hLn5gm462ZEeVFK1rOy_OeahE&q="+val+"\" \n"+
                    "      allowfullscreen>\n" +
                    "  </iframe>");
        }
    };



    Runnable runnable = new Runnable(){
        @Override
        public void run() {
            // TODO: http request.


            if (location != null) {
                //https://maps.googleapis.com/maps/api/geocode/json?latlng=40.714224,-73.961452&key=AIzaSyBVCWFH1HYg-VrNS5-gthRjrdp0mmzy6GE

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
                    //{"data":{"country_name":"台灣","country":"TW","city":null,"district":"東區","village":null,"zip_code":"600","street_number":"205號","street":"忠孝路","full_address":"600台灣嘉義市東區忠孝路205號","place_id":"ChIJkckkvTSUbjQRZwiYf8wDseM","lat":23.4838007,"lng":120.4536367,"url":"https://www.google.com/maps/@23.4838007,120.4536367,15z"},"status":200}
                    JSONObject obj = new JSONObject(response.body().string());
                    JSONArray data = obj.getJSONArray("results");
                    JSONObject obj2 = data.getJSONObject(0);
                    String full_address = obj2.getString("formatted_address");
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
