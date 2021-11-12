package tw.com.huang.robin.teststriprecognition;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.InputStream;

public class PhotoPositionActivity extends AppCompatActivity {

    private static int TESTSTRIP_ID;
    private static int RECG_METHOD;

    BaseLoaderCallback baseLoaderCallback;
    Mat matImage;

    GestureImageView imageView;
    Bitmap bitmap;
    Bitmap bitmapSend;
    Uri uri;
    Intent intent;
    int photomode = 0;
    private int flipFlag = 0; // 控制影像轉正

    // 平均色彩值
    private float averageRed;
    private float averageGreen;
    private float averageBlue;
    private float averageGray;

    // 影像定位範圍
    private int cropX1 = 0;
    private int cropY1 = 0;
    private int cropX2 = 0;
    private int cropY2 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_position);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // 透過 ID 來指定 imageView
        imageView = findViewById( R.id.photoView );

        Intent intent = getIntent();
        TESTSTRIP_ID = intent.getIntExtra( "TestStripID", 0 );
        RECG_METHOD = intent.getIntExtra( "RecognitionMethod", 0 );

        displayViewFromSource();

        baseLoaderCallback = new BaseLoaderCallback( this ) {
            @Override
            public void onManagerConnected(int status) {
                switch ( status ) {
                    case BaseLoaderCallback.SUCCESS:
                        break;
                    default:
                        super.onManagerConnected(status);
                        break;
                }
            }
        };
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


    @Override
    protected void onResume() {
        super.onResume();

        if ( OpenCVLoader.initDebug() ) {
            baseLoaderCallback.onManagerConnected( BaseLoaderCallback.SUCCESS );
        } else {
            OpenCVLoader.initAsync( OpenCVLoader.OPENCV_VERSION, this, baseLoaderCallback );
        }
    }

    /* 顯示影像 */
    private void displayViewFromSource() {
        Intent intent = getIntent();
        photomode = intent.getIntExtra( "PhotoMode", 0 );

        switch ( photomode ) {
            case PhotoModeActivity.PHOTO_MODE.CAMERA:
                bitmap = ( Bitmap ) intent.getParcelableExtra( "BitmapImage" );
                imageView.setImageBitmap( bitmap );
                break;
            case PhotoModeActivity.PHOTO_MODE.ABLUM:
                String imagePath = intent.getStringExtra( "ImagePath" );
                uri = Uri.parse( imagePath );
                imageView.setImageURI( uri );
                break;
             default:
                 break;
        }
    }

    /* 取消定位結果 */
    public void cancelPositioningResult( View view ) {
        imageView.resetCanvas();
        imageView.resetGestureMode();
    }

    /* 確定定位結果 */
    public void adoptPoistioningResult( View view ) {
        // 檢查是否已定位完成
        if ( imageView.mode == GestureImageView.GESTURE_MODE.GESTURE_MODE_CROP_REFINE ) {

            if ( RECG_METHOD == RecognitionMethodActivity.RECOGNITION_METHOD.COLOR ) {
                float featureValue = calcTestStripFeature();

                switch ( photomode ) {
                    case PhotoModeActivity.PHOTO_MODE.CAMERA:
                        intent = new Intent( this, RecognitionResultActivity.class );
                        intent.putExtra( "TestStripID", TESTSTRIP_ID );
                        intent.putExtra( "PhotoMode", PhotoModeActivity.PHOTO_MODE.CAMERA );
                        intent.putExtra( "FlipFlag", flipFlag );
                        intent.putExtra( "BitmapImage", bitmap );
                        intent.putExtra( "FeatureValue", featureValue );
                        intent.putExtra( "x1", cropX1 );
                        intent.putExtra( "y1", cropY1 );
                        intent.putExtra( "x2", cropX2 );
                        intent.putExtra( "y2", cropY2 );

                        intent.putExtra( "averageBlue", averageBlue );
                        intent.putExtra( "averageGreen", averageGreen );
                        intent.putExtra( "averageRed", averageRed );
                        intent.putExtra( "averageGray", averageGray );
                        startActivity( intent );
                        break;
                    case PhotoModeActivity.PHOTO_MODE.ABLUM:
                        intent = new Intent( this, RecognitionResultActivity.class );
                        intent.putExtra( "TestStripID", TESTSTRIP_ID );
                        intent.putExtra( "PhotoMode", PhotoModeActivity.PHOTO_MODE.ABLUM );
                        intent.putExtra( "FlipFlag", flipFlag );
                        intent.putExtra( "ImagePath", uri.toString() );
                        intent.putExtra( "FeatureValue", featureValue );
                        intent.putExtra( "x1", cropX1 );
                        intent.putExtra( "y1", cropY1 );
                        intent.putExtra( "x2", cropX2 );
                        intent.putExtra( "y2", cropY2 );

                        intent.putExtra( "averageBlue", averageBlue );
                        intent.putExtra( "averageGreen", averageGreen );
                        intent.putExtra( "averageRed", averageRed );
                        intent.putExtra( "averageGray", averageGray );
                        startActivity( intent );
                        break;
                    default:
                        break;
                }
            } // if-else
        } // if-else
    }

    /* 計算試紙特徵值 */
    private float calcTestStripFeature() {

        float featureValue = -1.f;

        // 將影像轉成 OpenCV 的 Mat 格式
        switch ( photomode ) {
            case PhotoModeActivity.PHOTO_MODE.CAMERA:
                matImage = new Mat( bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC4 );
                Utils.bitmapToMat( bitmap, matImage );
                break;
            case PhotoModeActivity.PHOTO_MODE.ABLUM:
                // 從 Uri 讀取影像資料
                try {
                    InputStream imageStream = getContentResolver().openInputStream( uri );
                    bitmap = BitmapFactory.decodeStream( imageStream );
                    matImage = new Mat( bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC4 );
                    Utils.bitmapToMat( bitmap, matImage );
                } catch ( Exception e ) {

                }
                break;
            default:
                break;
        }

        // 檢查是否有讀取到影像
        if ( matImage.empty() == false ) {
            PointF imageSize = imageView.getImageSize();

            // 判斷之後使否需要轉正影像
            if ( imageSize.x > imageSize.y ) {
                flipFlag = 1;
            }

            float scaleX = ( float )( matImage.cols() ) / imageSize.x;
            float scaleY = ( float )( matImage.rows() ) / imageSize.y;

            float[] cropPos = imageView.getCropPosition(); // 獲取定位框座標

            // 計算真實影像定位框座標
            cropX1 = ( int )( cropPos[0] * scaleX + 0.5f );
            cropY1 = ( int )( cropPos[1] * scaleY + 0.5f );
            cropX2 = ( int )( cropPos[2] * scaleX + 0.5f );
            cropY2 = ( int )( cropPos[3] * scaleY + 0.5f );
            int w = cropX2 - cropX1;
            int h = cropY2 - cropY1;

            // 擷取定位框範圍之影像
            Rect rect = new Rect( cropX1, cropY1, w, h );
            Mat cropImage = new Mat( matImage, rect );

            double sumRed = 0.0;
            double sumGreen = 0.0;
            double sumBlue = 0.0;

            switch ( TESTSTRIP_ID ) {
                case MainActivity.TESTSTRIP_ID.TESTSTRIP_ID_FIRST: // 試紙1
                    double sumWhite = 0.0;
                    double avgWhite = 0.0;
                    double sumGray = 0.0;
                    double avgGray = 0.0;

                    Mat grayImage = new Mat(); // 灰階影像
                    Mat maskImage = new Mat(); // 遮罩影像
                    Mat erodeMask = new Mat(); // 經「侵蝕」的遮罩影像
                    Mat dilateMask = new Mat(); // 經「膨脹」的遮罩影像

                    // RGB 轉 Gray
                    Imgproc.cvtColor( cropImage, grayImage, Imgproc.COLOR_RGBA2GRAY );

                    // Otsu 二值化演算法
                    double thresh = Imgproc.threshold( grayImage, maskImage, 0, 255, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU );

                    Imgproc.erode( maskImage, erodeMask, Imgproc.getStructuringElement( Imgproc.MORPH_ELLIPSE, new Size( 3, 3 ) ) ); // 形態學侵蝕
                    Imgproc.dilate( maskImage, dilateMask, Imgproc.getStructuringElement( Imgproc.MORPH_ELLIPSE, new Size( 3, 3 ) ) ); // 形態學膨脹

                    // 計算應為白色區域的灰階值
                    int area = 0;
                    for ( int i = 0; i < grayImage.rows(); ++i ) {
                        for ( int j = 0; j < grayImage.cols(); ++j ) {
                            double[] maskValues = erodeMask.get( i, j );

                            if ( maskValues[0] == 255 ) {
                                double[] grayValues = grayImage.get( i, j );
                                sumWhite += grayValues[0];
                                area++;
                            }
                        } // j
                    } // i

                    avgWhite = sumWhite / ( double )( area ); // 取平均

                    // 計算試紙區域的校正灰階值
                    area = 0;
                    for ( int i = 0; i < grayImage.rows(); ++i ) {
                        for ( int j = 0; j < grayImage.cols(); ++j ) {
                            double[] maskValues = dilateMask.get( i, j );

                            if ( maskValues[0] == 0 ) {
                                double[] grayValues = grayImage.get( i, j );
                                sumGray += ( 127 - avgWhite ) + grayValues[0];
                                area++;

                                double[] values = cropImage.get( i, j );
                                sumRed += values[0];
                                sumGreen += values[1];
                                sumBlue += values[2];
                            }
                        } // j
                    } // i

                    averageRed = ( float )( sumRed / ( double )( area ) );
                    averageGreen = ( float )( sumGreen / ( double )( area ) );
                    averageBlue = ( float )( sumBlue / ( double )( area ) );
                    averageGray = averageRed * 0.299f + averageGreen * 0.587f + averageBlue * 0.114f;

                    avgGray = sumGray / ( double )( area ); // 取平均
                    featureValue = ( float )avgGray;
                    break;
                case MainActivity.TESTSTRIP_ID.TESTSTRIP_ID_SECOND: // 試紙2
                    double sumValue = 0.0;
                    double avgValue = 0.0;

                    for ( int i = 0; i < cropImage.rows(); ++i ) {
                        for ( int j = 0; j < cropImage.cols(); ++j ) {
                            double[] values = cropImage.get( i, j );
                            sumValue += values[2]; // 提取 Blue channel 的值並累加

                            sumRed += values[0];
                            sumGreen += values[1];
                            sumBlue += values[2];
                        }
                    }

                    averageRed = ( float )( sumRed / ( double )( w * h ) );
                    averageGreen = ( float )( sumGreen / ( double )( w * h ) );
                    averageBlue = ( float )( sumBlue / ( double )( w * h ) );
                    averageGray = averageRed * 0.299f + averageGreen * 0.587f + averageBlue * 0.114f;

                    avgValue = sumValue / ( double )( w * h ); // 取平均
                    featureValue = ( float )avgValue;

                    break;
                default:
                    break;
            } // switch
        } // if

        return featureValue;
    }
}
