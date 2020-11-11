package com.example.puzzle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;


public class MapStorage extends AppCompatActivity {
    private GridLayout mGridLayout;
    private ImageView img_place;

    private int x=3,y=3;

    //拼图碎片合集
    private int[][] mJigsawArray ;
    private ImageView[][] mImageViewArray ;

    //空白碎片
    private  ImageView mEmptyImageView;
    private GestureDetector mGestureDerector;
    private boolean isAnimated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_AppCompat_DayNight_NoActionBar);
        setContentView(R.layout.activity_map_storage);
        mGestureDerector = new GestureDetector(this,new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {  //判断手势方向
                int getstureDirection = GestureHelper.getInstance().getGestureDirection(e1,e2);
                handleFlingGesture(getstureDirection,true);
                return true;
            }
        });

        if (ContextCompat.checkSelfPermission(MapStorage.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!=       //判断是否有打开图库的权限
                PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MapStorage.this,new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            },2);
        }

        mGridLayout = findViewById(R.id.gl_layout);
        img_place = findViewById(R.id.img_place);
        //初始化拼图碎片
        Intent intent = getIntent();
        x = intent.getIntExtra("xy_album",3);
        y = intent.getIntExtra("xy_album",3);
        mJigsawArray = new int[x][y];
        mImageViewArray = new ImageView[x][y];
        mGridLayout.setColumnCount(x);
        mGridLayout.setRowCount(y);
        openAlbum();
    }


    private void randomJigsaw(){
        for (int i = 0; i < 100; i++) {
            int gestureDirection = (int) ((Math.random() * 4) + 1);
            handleFlingGesture(gestureDirection, false);
        }
    }

    //游戏初始化
    private void initJigsaw(Bitmap jigsawBitmap){
        int itemWidth = jigsawBitmap.getWidth()/x;
        int itemHight = jigsawBitmap.getHeight()/y;
        img_place.setImageBitmap(jigsawBitmap);

        for (int i = 0; i < mJigsawArray.length;i++){
            for (int j = 0;j < mJigsawArray[0].length; j++){
                Bitmap bitmap = Bitmap.createBitmap(jigsawBitmap,j*itemWidth,i*itemHight,itemWidth,itemHight);
                ImageView imageView = new ImageView(this);
                imageView.setImageBitmap(bitmap);
                imageView.setPadding(2,2,2,2);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isNearBy = JigsawHelper.getInstance().isNearByEmptyView((ImageView) v, mEmptyImageView);  //判断点击图片是否在空图片的四周
                        if (isNearBy) {
                            //处理移动
                            handleClickItem((ImageView) v, true);
                        }
                    }
                });
                //绑定数据
                imageView.setTag(new Jigsaw(i,j,bitmap));
                mImageViewArray[i][j] = imageView;
                mGridLayout.addView(imageView);
            }
        }
        //设置空白碎片
        ImageView imageView = (ImageView)mGridLayout.getChildAt(mGridLayout.getChildCount() - 1);
        imageView.setImageBitmap(null);
        mEmptyImageView = imageView;
    }
    protected void onResume() {
        super.onResume();
        Music.play(this, R.raw.music);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Music.stop(this);
    }
    private void  handleClickItem(final  ImageView imageView,boolean animated){
        if(animated){
            handleClickItem(imageView);
        }else{
            changeJigsawData(imageView);
        }
    }

    private void handleClickItem(final ImageView imageView) {
        if (!isAnimated) {
            TranslateAnimation translateAnimation = null;
            if (imageView.getX() < mEmptyImageView.getX()) {
                //左往右
                translateAnimation = new TranslateAnimation(0, imageView.getWidth(), 0, 0);
            }

            if (imageView.getX() > mEmptyImageView.getX()) {
                //右往左
                translateAnimation = new TranslateAnimation(0, -imageView.getWidth(), 0, 0);
            }

            if (imageView.getY() > mEmptyImageView.getY()) {
                //下往上
                translateAnimation = new TranslateAnimation(0, 0, 0, -imageView.getHeight());
            }

            if (imageView.getY() < mEmptyImageView.getY()) {
                //上往下
                translateAnimation = new TranslateAnimation(0, 0, 0, imageView.getHeight());
            }

            if (translateAnimation != null) {
                translateAnimation.setDuration(180);
                translateAnimation.setFillAfter(true);
                translateAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        isAnimated = true;
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        //清除动画
                        isAnimated = false;
                        imageView.clearAnimation();
                        //交换拼图数据
                        changeJigsawData(imageView);
                        //判断游戏是否结束
                        boolean isFinish = JigsawHelper.getInstance().isFinishGame(mImageViewArray, mEmptyImageView);
                        if (isFinish) {
                            Toast.makeText(MapStorage.this, "拼图成功，游戏结束！", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                imageView.startAnimation(translateAnimation);
            }
        }
    }

    public void changeJigsawData(ImageView imageView) {
        Jigsaw emptyJigsaw = (Jigsaw) mEmptyImageView.getTag();
        Jigsaw jigsaw = (Jigsaw) imageView.getTag();

        //更新imageView的显示内容
        mEmptyImageView.setImageBitmap(jigsaw.getBitmap());
        imageView.setImageBitmap(null);
        //交换数据
        emptyJigsaw.setCurrentX(jigsaw.getCurrentX());
        emptyJigsaw.setCurrentY(jigsaw.getCurrentY());
        emptyJigsaw.setBitmap(jigsaw.getBitmap());

        //更新空拼图引用
        mEmptyImageView = imageView;
    }
    private void handleFlingGesture(int gestureDirection, boolean animation) {
        ImageView imageView = null;
        Jigsaw emptyJigsaw = (Jigsaw) mEmptyImageView.getTag();
        switch (gestureDirection) {
            case GestureHelper.LEFT:
                if (emptyJigsaw.getOriginalY() + 1 <= mGridLayout.getColumnCount() - 1) {
                    imageView = mImageViewArray[emptyJigsaw.getOriginalX()][emptyJigsaw.getOriginalY() + 1];
                }
                break;
            case GestureHelper.RIGHT:
                if (emptyJigsaw.getOriginalY() - 1 >= 0) {
                    imageView = mImageViewArray[emptyJigsaw.getOriginalX()][emptyJigsaw.getOriginalY() - 1];
                }
                break;
            case GestureHelper.UP:
                if (emptyJigsaw.getOriginalX() + 1 <= mGridLayout.getRowCount() - 1) {
                    imageView = mImageViewArray[emptyJigsaw.getOriginalX() + 1][emptyJigsaw.getOriginalY()];
                }
                break;
            case GestureHelper.DOWN:
                if (emptyJigsaw.getOriginalX() - 1 >= 0) {
                    imageView = mImageViewArray[emptyJigsaw.getOriginalX() - 1][emptyJigsaw.getOriginalY()];
                }
                break;
            default:
                break;
        }
        if (imageView != null) {
            handleClickItem(imageView, animation);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDerector.onTouchEvent(event);
    }

    public void openAlbum(){
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 2);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                }
                break;
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        } else {
            switch (requestCode) {
                case 2:
                    Bitmap jigsaw = JigsawHelper.getInstance().getJigsaw(this, data.getData());  //图库获取Bitmap
                    initJigsaw(jigsaw);  //初始化jigsaw
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            randomJigsaw();   //延迟200ms调用
                        }
                    },200);
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
