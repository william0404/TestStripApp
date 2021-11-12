package tw.com.huang.robin.teststriprecognition;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatImageView;

public class GestureImageView extends AppCompatImageView {

    // 手勢模式
    public final class GESTURE_MODE {
        public static final int GESTURE_MODE_NONE = 0;
        public static final int GESTURE_MODE_PAN = 1;
        public static final int GESTURE_MODE_ZOOM = 2;
        public static final int GESTURE_MODE_CROP = 3;
        public static final int GESTURE_MODE_CROP_REFINE = 4;
    }

    // 定位邊方向
    private final class CROP_BORDER_DIRECTION {
        private static final int DIRECTION_X = 0;
        private static final int DIRECTION_Y = 1;
    }

    public int mode = 0;
    private int direction = 0; // 定位邊方向紀錄

    private Matrix imageMatrix;   // 影像轉換矩陣
    private PointF viewSize; // View 元件尺寸
    private PointF imageSize; // 原始影像尺寸
    private float basicScale; // 影像縮放置 View 元件的 Scale
    private float[] matrixValue = new float[9]; // Matrix 中的值

    // 定位文字顯示區
    private float textSzie = 65.f; // 文字大小
    private float WidthL = 0.f; // 寬度資訊欄左邊界座標
    private float WidthT = 0.f; // 寬度資訊欄上邊界座標
    private float WidthR = 0.f; // 寬度資訊欄右邊界座標
    private float WidthB = 0.f; // 寬度資訊欄下邊界座標
    private float HeightL = 0.f; // 高度資訊欄左邊界座標
    private float HeightT = 0.f; // 高度資訊欄上邊界座標
    private float HeightR = 0.f; // 高度資訊欄右邊界座標
    private float HeightB = 0.f; // 高度資訊欄下邊界座標

    // 單指拖曳
    private PointF refPos;
    private PointF curPos;

    // 雙指手勢縮放
    private final static float minScale = 1.0f; // 最小縮放比例
    private final static float maxScale = 12.0f; // 最大縮放比例
    private final static float zoomInRatio = 0.95f;
    private final static float zoomOutRatio = 1.05f;
    private static float startDoubleFingerDist = 0.f; // 雙指觸控當下的兩指距離
    private static float moveDoubleFingerDist = 0.f; // 雙指移動當下的兩指距離
    private static PointF fingersCenter = new PointF();

    // 點擊手動定位
    private static long startTouchTime = 0;
    private static long endTouchTime = 0;
    private static long clickTimeThresh = 100; // "點擊"的最長時間值 ( ms )
    private static int textYoffset = -50;
    private static final int pointRadius = 10; // 定位框角點半徑
    private static PointF cropPoint1 = new PointF(); // 定位框第一角點座標
    private static PointF cropPoint2 = new PointF(); // 定位框第二角點座標

    Bitmap bitmap;
    Canvas canvas;
    Paint paint;
    Paint paintText;
    Paint paintRectangle;

    public void GestureImageViewInit() {
        this.setScaleType( ScaleType.MATRIX );
        imageMatrix   = new Matrix();
        viewSize = new PointF();
        imageSize = new PointF();
        refPos   = new PointF();
        curPos   = new PointF();
        paint    = new Paint();
        paintText = new Paint();
        paintRectangle = new Paint();
        paint.setStrokeWidth( 5 );
        paint.setColor( 0xff00ffff );
        paint.setTextSize( 50 );
        paintText.setTextAlign( Paint.Align.CENTER );
        paintText.setTextSize( textSzie );
        paintText.setColor( 0xff000000 );
        paintRectangle.setColor( 0xffffffff );
    }

    public GestureImageView( Context context ) {
        super( context );
        GestureImageViewInit();
    }

    public GestureImageView( Context context, AttributeSet attrs ) {
        super( context, attrs );
        GestureImageViewInit();
    }

    public GestureImageView( Context context, AttributeSet attrs, int defStyleArr ) {
        super( context, attrs, defStyleArr );
        GestureImageViewInit();
    }

    /* 畫布繪圖 */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap( bitmap, 0, 0, paint );
    }

    /*  尺寸量測函數 */
    @Override
    protected void onMeasure( int widthMeasureSpec, int heightMeasureSpec ) {
        super.onMeasure( widthMeasureSpec, heightMeasureSpec );

        float view_w = ( float )MeasureSpec.getSize( widthMeasureSpec  );
        float view_h = ( float )MeasureSpec.getSize( heightMeasureSpec );
        viewSize.set( view_w, view_h ); // 紀錄 View 元件尺寸

        Drawable drawable = getDrawable();
        float image_w = ( float )drawable.getMinimumWidth();
        float image_h = ( float )drawable.getMinimumHeight();
        imageSize.set( image_w, image_h ); // 紀錄原始影像尺寸

        // 計算範圍文字顯示區座標
        float TextRegionWRatio = viewSize.x / 4.0f;
        float TextRegionHRatio = viewSize.y / 15.f;

        WidthL = viewSize.x / 2.f - TextRegionWRatio - 20.f;
        WidthR = viewSize.x / 2.f - 20.f;
        HeightL = viewSize.x / 2.f + 20.f;
        HeightR = viewSize.x / 2.f + TextRegionWRatio + 20.f;

        WidthT = viewSize.y - TextRegionHRatio - 20.f;
        WidthB = viewSize.y - 20.f;
        HeightT = WidthT;
        HeightB = WidthB;

        float scale_w = view_w / image_w;
        float scale_h = view_h / image_h;

        // 建立透明畫布，用於後續繪製定位框
        bitmap = Bitmap.createBitmap( (int)viewSize.x, (int)viewSize.y, Bitmap.Config.ARGB_8888 );
        canvas = new Canvas( bitmap );

        /* ----- 圖片移至中央並適應 View 元件尺寸 ----- */

        // 選擇較小的縮放比例確保整個圖片會完整顯示再畫面中
        float scale = scale_w < scale_h ? scale_w : scale_h;
        imageMatrix.setScale( scale, scale );
        this.setImageMatrix( imageMatrix );

        // 紀錄此縮放比例
        basicScale = scale;

        // 根據縮放比例將影像調整到 View 元件的中心
        PointF scaleSize = new PointF( scale * image_w, scale * image_h );

        if ( scale_w > scale_h ) {
            imageMatrix.postTranslate( view_w / 2.f - scaleSize.x / 2.f, 0.f );
            this.setImageMatrix( imageMatrix );
        } else {
            imageMatrix.postTranslate( 0.f, view_h / 2.f - scaleSize.y / 2.f );
            this.setImageMatrix( imageMatrix );
        }
    }


    /*  手勢觸發事件 */
    @Override
    public boolean onTouchEvent( MotionEvent event ) {

        try {
            // 定義手勢觸發事件
            switch ( event.getAction() ) {
                case MotionEvent.ACTION_DOWN:
                    /* -----------------------------------------------------------------------------
                    * 手指按下時觸發的事件
                    *   1.  MODE_CROP: 在定位模式下，繪製手指兩點構成之定位框
                    *   2.  MODE_CROP_REFINE: 在定位調整模式下，根據最接近手指位置的角點進行定位框修改
                    *   3.  Others: 在其餘模式下，將模式改為 MODE_PAN ，並記錄當下手指座標和時間
                    * --------------------------------------------------------------------------- */
                    if ( mode == GESTURE_MODE.GESTURE_MODE_CROP )
                        {
                        cropPoint2.set( event.getX(), event.getY() ); // 紀錄當下手指座標點
                        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR ); // 清除畫布
                        darwCropBoundingBox( cropPoint1, cropPoint2 ); // 繪製定位框
                        cropRegionTextDisplay();
                        invalidate(); // 觸發 onDraw 函式繪圖
                        }
                    else if ( mode == GESTURE_MODE.GESTURE_MODE_CROP_REFINE )
                        {
                        sortCropBoxPoints( event ); // 檢查角點座標並排列
                        canvas.drawColor( Color.TRANSPARENT, PorterDuff.Mode.CLEAR ); // 清除畫布
                        darwCropBoundingBox( cropPoint1, cropPoint2 ); // 繪製定位框
                        cropRegionTextDisplay();
                        invalidate(); // 觸發 onDraw 函式繪圖
                        }
                    else
                        {
                        mode = GESTURE_MODE.GESTURE_MODE_PAN;
                        refPos.set( event.getX(), event.getY() ); // 獲取當下手指座標做為參考位置
                        startTouchTime = System.currentTimeMillis();
                        }
                    break;
                case MotionEvent.ACTION_UP:
                    /* -----------------------------------------------------------------------------
                    * 手指鬆開時觸發的事件
                    *   1. MODE_CROP: 在定位模式下，手指鬆開事件觸發代表第一次定位已完成，因此將模式
                    *      改為 MODE_CROP_REFINE 來進行定位調整
                    *   2. Others: 在其餘模式下，紀錄當下手指鬆開之時間，並計算手指按下與鬆開之時間
                    *      差來判斷是否為「點擊」動作，若是則進入 MODE_CROP 來進行手動影像定位
                    * --------------------------------------------------------------------------- */
                    if ( mode == GESTURE_MODE.GESTURE_MODE_CROP )
                        {
                        mode = GESTURE_MODE.GESTURE_MODE_CROP_REFINE;
                        }
                    else
                        {
                        endTouchTime = System.currentTimeMillis();

                        if ( endTouchTime - startTouchTime < clickTimeThresh )
                            {
                            mode = GESTURE_MODE.GESTURE_MODE_CROP;

                            cropPoint1.set( refPos ); // 紀錄點擊當下座標點
                            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR ); // 清除畫布
                            canvas.drawCircle( cropPoint1.x, cropPoint1.y, pointRadius,  paint ); // 繪製第一角點
                            invalidate(); // 觸發 onDraw 函式繪圖
                            }
                        }
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    /*  -----------------------------------------------------------------------------
                    * 螢幕上已有一點按住，再按下一點時觸發的事件
                    *   1. MODE_CROP: 若在手動影像定位模式下，則不做任何事
                    *   2. Others: 在其他模式下，進入 MODE_ZOOM 來進行影像縮放功能
                    *  -------------------------------------------------------------------------- */
                    if ( mode == GESTURE_MODE.GESTURE_MODE_CROP )
                        {
                        break;
                        }
                    else
                        {
                        mode = GESTURE_MODE.GESTURE_MODE_ZOOM;
                        startDoubleFingerDist = getDoubleFingerDistance( event );
                        fingersCenter = getDoubleFingerCenter( event );
                        }
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    /* -----------------------------------------------------------------------------
                     * 螢幕上已有兩點按住，再鬆開一點時觸發的事件
                     * -------------------------------------------------------------------------- */
                    break;
                case MotionEvent.ACTION_MOVE:
                    /* -----------------------------------------------------------------------------
                    * 手指移動時觸發的事件
                    *   1. MODE_CROP 或 MODE_CROP_REFINE: 在手勢定位模式下，根據手指的移動位置來更新
                    *      定位框的範圍
                    *   2. MODE_PAN: 若當下螢幕上的觸控點數量為 1 ，則進行影像移動功能
                    *   3. Others: 在其餘模式下根據手指手勢進行影像縮放
                    * --------------------------------------------------------------------------- */
                    if ( mode == GESTURE_MODE.GESTURE_MODE_CROP )
                        {
                        cropPoint2.set( event.getX(), event.getY() ); // 紀錄當下手指座標點
                        canvas.drawColor( Color.TRANSPARENT, PorterDuff.Mode.CLEAR ); // 清除畫布
                        darwCropBoundingBox( cropPoint1, cropPoint2 ); // 繪製定位框
                        cropRegionTextDisplay();
                        invalidate(); // 觸發 onDraw 函式繪圖
                        }
                    else if ( mode == GESTURE_MODE.GESTURE_MODE_CROP_REFINE )
                        {
                        if ( direction == CROP_BORDER_DIRECTION.DIRECTION_X )
                            {
                            cropPoint2.set( event.getX(), cropPoint2.y );
                            }
                        else
                            {
                            cropPoint2.set( cropPoint2.x, event.getY() );
                            }
                        canvas.drawColor( Color.TRANSPARENT, PorterDuff.Mode.CLEAR ); // 清除畫布
                        darwCropBoundingBox( cropPoint1, cropPoint2 ); // 繪製定位框
                        cropRegionTextDisplay();
                        invalidate(); // 觸發 onDraw 函式繪圖
                        }
                    else if ( event.getPointerCount() == 1 && mode == GESTURE_MODE.GESTURE_MODE_PAN )
                        {
                        curPos.set( event.getX(), event.getY() ); // 獲取當下手指座標
                        imageMatrix.postTranslate(curPos.x - refPos.x, curPos.y - refPos.y );
                        this.setImageMatrix( imageMatrix );
                        limitBorder();

                        refPos.set( curPos ); // 更新參考位置
                        }
                    else
                        {
                        moveDoubleFingerDist = getDoubleFingerDistance(event);

                        if ( moveDoubleFingerDist - startDoubleFingerDist > 0 )
                            {
                            calcMatrixAfterScaling( zoomOutRatio, new PointF( viewSize.x / 2.f, viewSize.y / 2.f ) );
                            }
                        else if ( moveDoubleFingerDist - startDoubleFingerDist < 0 )
                            {
                            calcMatrixAfterScaling( zoomInRatio, new PointF( viewSize.x / 2.f, viewSize.y / 2.f ) );
                            }
                        this.setImageMatrix( imageMatrix );
                        restrictScale();
                        limitBorder();

                        startDoubleFingerDist = moveDoubleFingerDist; // 更新參考距離
                        }
                    break;
                default:
                    break;
            }
        } catch ( Exception e ) {

        }
        return true; // Return 要為 true，否則只會觸發按下事件
    }

    /* 計算影像縮放後的校正位移量 */
    private void calcMatrixAfterScaling( float scale, PointF center ) {
        // 獲得當下縮放尺寸
        float curScale = getMatrixValue( Matrix.MSCALE_X, imageMatrix );

        // 獲得當下位移量
        float offsetX = getMatrixValue( Matrix.MTRANS_X, imageMatrix );
        float offsetY = getMatrixValue( Matrix.MTRANS_Y, imageMatrix );

        // 計算更新的 Scale 值
        float newScale =  scale * curScale;
        imageMatrix.setScale( newScale, newScale );

        float deltaX = center.x - offsetX;
        float deltaY = center.y - offsetY;

        float newOffsetX = deltaX - deltaX * scale + offsetX;
        float newOffsetY = deltaY - deltaY * scale + offsetY;

        imageMatrix.postTranslate( newOffsetX, newOffsetY );
    }

    /* 限制縮放比例 */
    private void restrictScale() {
        // 獲得當下縮放尺寸
        float curScale = getMatrixValue( Matrix.MSCALE_X, imageMatrix );

        float minRealScale = minScale * basicScale;
        float maxRealScale = maxScale * basicScale;

        if ( curScale < minRealScale ) {
            calcMatrixAfterScaling( zoomOutRatio, new PointF( viewSize.x / 2.f, viewSize.y / 2.f ) );
        } else if ( curScale > maxRealScale ) {
            calcMatrixAfterScaling( zoomInRatio, new PointF( viewSize.x / 2.f, viewSize.y / 2.f ) );
        } else {
            return;
        }
        this.setImageMatrix( imageMatrix );
    }

    /* 限制影像邊界 */
    private void limitBorder() {
        // 獲得當下縮放尺寸
        float curScale = getMatrixValue( Matrix.MSCALE_X, imageMatrix );

        // 獲得當下位移量
        float offsetX = getMatrixValue( Matrix.MTRANS_X, imageMatrix );
        float offsetY = getMatrixValue( Matrix.MTRANS_Y, imageMatrix );

        // 計算當下縮放後影像尺寸
        float w = imageSize.x * curScale;
        float h = imageSize.y * curScale;

        // 計算影像角落4點座標
        float x1 = offsetX;
        float y1 = offsetY;
        float x2 = w + offsetX;
        float y2 = h + offsetY;

        // 初始化更新位移量
        float newOffsetX = 0.f;
        float newOffsetY = 0.f;

        // 檢查 x 方向
        if ( w > viewSize.x ) {
            if ( x1 > 0.f ) {
                newOffsetX = -x1;
            } else if ( x2 < viewSize.x ) {
                newOffsetX = viewSize.x - x2;
            }
        } else {
            if ( x1 < 0.f ) {
                newOffsetX = -x1;
            } else if ( x2 > viewSize.x ) {
                newOffsetX = viewSize.x - x2;
            }
        }

        // 檢查 y 方向
        if ( h > viewSize.y ) {
            if ( y1 > 0.f ) {
                newOffsetY = -y1;
            } else if ( y2 < viewSize.y ) {
                newOffsetY = viewSize.y - y2;
            }
        } else {
            if ( y1 < 0.f ) {
                newOffsetY = -y1;
            } else if ( y2 > viewSize.y ) {
                newOffsetY = viewSize.y - y2;
            }
        }

        // 更新 Matrix 位移量
        imageMatrix.postTranslate( newOffsetX, newOffsetY );
        this.setImageMatrix( imageMatrix );
    }

    /* 獲得 Matrix 中的值 */
    private float getMatrixValue( int name, Matrix matrix ) {
        matrix.getValues( matrixValue );
        return matrixValue[name];
    }

    /* 獲得雙指間距離 */
    private float getDoubleFingerDistance( MotionEvent event ) {
        float x = event.getX( 0 ) - event.getX( 1 );
        float y = event.getY( 0 ) - event.getY( 1 );
        return ( float )Math.sqrt( x * x + y * y );
    }

    /* 獲得雙指中心點座標 */
    private PointF getDoubleFingerCenter( MotionEvent event ) {
        float x = ( event.getX( 0 ) + event.getX( 1 ) ) / 2.f;
        float y = ( event.getY( 0 ) + event.getY( 1 ) ) / 2.f;
        return new PointF( x, y );
    }

    /* 計算兩點間距離 */
    private float calcTwoPointsDist( PointF pt1, PointF pt2 ) {
        float x = pt2.x - pt1.x;
        float y = pt2.y - pt1.y;
        return ( float )Math.sqrt( x * x + y * y );
    }

    /* 定位框角點排列 */
    private void sortCropBoxPoints( MotionEvent event ) {
        PointF tmpPoints = new PointF( event.getX(), event.getY() ); // 紀錄當下手指座標點

        PointF LPt = new PointF( cropPoint1.x, ( cropPoint1.y + cropPoint2.y ) / 2.f );
        PointF RPt = new PointF( cropPoint2.x, ( cropPoint1.y + cropPoint2.y ) / 2.f );
        PointF TPt = new PointF( ( cropPoint1.x + cropPoint2.x ) / 2.f, cropPoint1.y );
        PointF BPt = new PointF( ( cropPoint1.x + cropPoint2.x ) / 2.f, cropPoint2.y );

        float diffPointL = calcTwoPointsDist( LPt, tmpPoints );
        float diffPointR = calcTwoPointsDist( RPt, tmpPoints );
        float diffPointT = calcTwoPointsDist( TPt, tmpPoints );
        float diffPointB = calcTwoPointsDist( BPt, tmpPoints );

        float minDiff = Math.min( diffPointB, Math.min( diffPointT, Math.min( diffPointL, diffPointR ) ) );

        if ( diffPointL == minDiff ) {
            float pt1Y = cropPoint1.y;
            cropPoint1.set( cropPoint2 );
            cropPoint2.set( tmpPoints.x, pt1Y );
            direction = CROP_BORDER_DIRECTION.DIRECTION_X;
        } else if ( diffPointT == minDiff ) {
            float pt1X = cropPoint1.x;
            cropPoint1.set( cropPoint2 );
            cropPoint2.set( pt1X, tmpPoints.y );
            direction = CROP_BORDER_DIRECTION.DIRECTION_Y;
        } else if ( diffPointR == minDiff ) {
            cropPoint2.set( tmpPoints.x, cropPoint2.y );
            direction = CROP_BORDER_DIRECTION.DIRECTION_X;
        } else {
            cropPoint2.set( cropPoint2.x, tmpPoints.y );
            direction = CROP_BORDER_DIRECTION.DIRECTION_Y;
        }
    }

    /* 繪製定位框 */
    private void darwCropBoundingBox( PointF pt1, PointF pt2 ) {
        canvas.drawCircle( pt1.x, pt1.y, pointRadius,  paint ); // 繪製第一角點
        canvas.drawCircle( pt2.x, pt2.y, pointRadius,  paint ); // 繪製第二角點
        canvas.drawCircle( pt1.x, pt2.y, pointRadius,  paint );
        canvas.drawCircle( pt2.x, pt1.y, pointRadius,  paint );

        canvas.drawCircle( pt1.x, ( pt1.y + pt2.y ) / 2.f, pointRadius,  paint ); // 左邊點
        canvas.drawCircle( pt2.x, ( pt1.y + pt2.y ) / 2.f, pointRadius,  paint ); // 右邊點
        canvas.drawCircle( ( pt1.x + pt2.x ) / 2.f, pt1.y, pointRadius,  paint ); // 上邊點
        canvas.drawCircle( ( pt1.x + pt2.x ) / 2.f, pt2.y, pointRadius,  paint ); // 下邊點


        canvas.drawLine( pt1.x, pt1.y, pt1.x, pt2.y, paint );
        canvas.drawLine( pt1.x, pt1.y, pt2.x, pt1.y, paint );
        canvas.drawLine( pt1.x, pt2.y, pt2.x, pt2.y, paint );
        canvas.drawLine( pt2.x, pt1.y, pt2.x, pt2.y, paint );
    }

    /* 將畫布座標點轉換對應之影像原始座標點 */
    private PointF convertImageCoordinate( PointF canvasPoint ) {
        // 獲得當下縮放尺寸
        float curScale = getMatrixValue( Matrix.MSCALE_X, imageMatrix );

        // 獲得當下位移量
        float offsetX = getMatrixValue( Matrix.MTRANS_X, imageMatrix );
        float offsetY = getMatrixValue( Matrix.MTRANS_Y, imageMatrix );

        // 回推對應原始影像上的座標
        float originalX = ( canvasPoint.x - offsetX ) / curScale;
        float originalY = ( canvasPoint.y - offsetY ) / curScale;

        return new PointF( originalX, originalY );
    }

    /* 計算定位框實際範圍並輸出字串 */
    private String cropRegionString( PointF pt1, PointF pt2 ) {
        int w = ( int )( Math.abs( pt2.x - pt1.x + 0.5f ) );
        int h = ( int )( Math.abs( pt2.y - pt1.y + 0.5f ) );
        return  "(" + String.valueOf( w ) + "," + String.valueOf( h ) + ")";
    }

    /* 顯示定位框範圍文字 */
    private void cropRegionTextDisplay() {
        PointF origPoint1 = convertImageCoordinate( cropPoint1 );
        PointF origPoint2 = convertImageCoordinate( cropPoint2 );
//        String regionString = cropRegionString( origPoint1, origPoint2 );
//        float minX = Math.min( cropPoint1.x, cropPoint2.x );
//        float minY = Math.min( cropPoint1.y, cropPoint2.y );
//        canvas.drawText( regionString, minX, minY + textYoffset, paint );

        String strW = "W: " + String.valueOf( ( int )( Math.abs( origPoint2.x - origPoint1.x + 0.5f ) ) );
        String strH = "H: " + String.valueOf( ( int )( Math.abs( origPoint2.y - origPoint1.y + 0.5f ) ) );

        canvas.drawRect( WidthL , WidthT , WidthR , WidthB , paintRectangle );
        canvas.drawRect( HeightL, HeightT, HeightR, HeightB, paintRectangle );
        canvas.drawText( strW, ( WidthR  + WidthL  ) / 2.f, WidthB  - ( WidthB  - WidthT  - textSzie ) / 2.f, paintText );
        canvas.drawText( strH, ( HeightR + HeightL ) / 2.f, HeightB - ( HeightB - HeightT - textSzie ) / 2.f, paintText );
    }

    /* 清除畫布 */
    public void resetCanvas() {
        canvas.drawColor( Color.TRANSPARENT, PorterDuff.Mode.CLEAR ); // 清除畫布
        invalidate(); // 觸發 onDraw 函式繪圖
    }

    /* 重設模式 */
    public void resetGestureMode() {
        mode = GESTURE_MODE.GESTURE_MODE_NONE;
    }

    /* 回傳影像尺寸 */
    public PointF getImageSize() {
        return new PointF( imageSize.x, imageSize.y );
    }

    /* 回傳定位座標 */
    public float[] getCropPosition() {
        PointF origPoint1 = convertImageCoordinate( cropPoint1 );
        PointF origPoint2 = convertImageCoordinate( cropPoint2 );

        float minX = Math.max( 0, Math.min( origPoint1.x, origPoint2.x ) );
        float minY = Math.max( 0, Math.min( origPoint1.y, origPoint2.y ) );
        float maxX = Math.min( imageSize.x, Math.max( origPoint1.x, origPoint2.x ) );
        float maxY = Math.min( imageSize.y, Math.max( origPoint1.y, origPoint2.y ) );

        float[] cropPos = { minX, minY, maxX, maxY };

        return cropPos;
    }

}