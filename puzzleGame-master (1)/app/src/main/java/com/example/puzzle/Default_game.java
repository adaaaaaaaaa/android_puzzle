package com.example.puzzle;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class Default_game extends AppCompatActivity {
    private TextView textView;
    private int x;
    private int y;
    private int move_step = 0 ;
    private int record_min;
    private String mode;
    private GridLayout mGridLayout2;
    private ImageView img_place2;
    private TextView text_move;
    private TextView text_move_min;
    Bitmap bitmap_game;
    //拼图碎片合集
    private int[][] mJigsawArray ;
    private ImageView[][] mImageViewArray ;

    //空白碎片
    private  ImageView mEmptyImageView;
    private GestureDetector mGestureDerector;
    private boolean isAnimated;

    //记录文件
    SharedPreferences share;
    SharedPreferences setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_AppCompat_DayNight_NoActionBar);
        setContentView(R.layout.activity_default_game);
        initView();
        mGestureDerector = new GestureDetector(this,new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {  //判断手势方向
                int getstureDirection = GestureHelper.getInstance().getGestureDirection(e1,e2);
                handleFlingGesture(getstureDirection,true);
                return true;
            }
        });
    }



    private void initView(){
        textView = findViewById(R.id.text_game_num);
        img_place2 = findViewById(R.id.img_place2);
        mGridLayout2 = findViewById(R.id.gl_layout2);
        text_move = findViewById(R.id.text_move);
        text_move_min = findViewById(R.id.text_move_min);
        text_move.setText("目前移动步数 :  " + move_step);
        Intent intent = getIntent();
        x = intent.getIntExtra("game_num",1);
        y = intent.getIntExtra("xy_default2",3);
        switch (y){
            case 3:
                mode = "简单模式";
                break;
            case 6:
                mode = "中等模式";
                break;
            case 9:
                mode = "困难模式";
                break;
        }
        textView.setText( mode +"第"+ x +"关");
        switch (x){
            case 1:
                bitmap_game = BitmapFactory.decodeResource(getResources(),R.drawable.img1);
                break;
            case 2:
                bitmap_game = BitmapFactory.decodeResource(getResources(),R.drawable.img2);
                break;
            case 3:
                bitmap_game = BitmapFactory.decodeResource(getResources(),R.drawable.img3);
                break;
            case 4:
                bitmap_game = BitmapFactory.decodeResource(getResources(),R.drawable.img4);
                break;
            case 5:
                bitmap_game = BitmapFactory.decodeResource(getResources(),R.drawable.img5);
                break;
            case 6:
                bitmap_game = BitmapFactory.decodeResource(getResources(),R.drawable.img6);
                break;
            case 7:
                bitmap_game = BitmapFactory.decodeResource(getResources(),R.drawable.img7);
                break;
            case 8:
                bitmap_game = BitmapFactory.decodeResource(getResources(),R.drawable.img8);
                break;
            case 9:
                bitmap_game = BitmapFactory.decodeResource(getResources(),R.drawable.img9);
                break;
        }
        img_place2.setImageBitmap(bitmap_game);
        mJigsawArray = new int[y][y];
        mImageViewArray = new ImageView[y][y];
        mGridLayout2.setColumnCount(y);
        mGridLayout2.setRowCount(y);
        initJigsaw(JigsawHelper.getInstance().getJigsaw(this,bitmap_game));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                randomJigsaw();
            }
        },200);

        set_record();
    }

    private void set_record(){
        setting = getSharedPreferences("name",0);
        record_min = setting.getInt(mode+x,100000);
        if (record_min ==100000){
            text_move_min.setText("记录最短步数 :  无记录");
        }
        else text_move_min.setText("记录最短步数 :  "+ record_min);
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
    private void randomJigsaw(){
        for (int i = 0; i < 100; i++) {
            int gestureDirection = (int) ((Math.random() * 4) + 1);
            handleFlingGesture(gestureDirection, false);
        }
    }

    //游戏初始化
    private void initJigsaw(Bitmap jigsawBitmap){

        int itemWidth = jigsawBitmap.getWidth()/y;
        int itemHight = jigsawBitmap.getHeight()/y;
        img_place2.setImageBitmap(jigsawBitmap);

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
                mGridLayout2.addView(imageView);
            }
        }
        //设置空白碎片
        ImageView imageView = (ImageView)mGridLayout2.getChildAt(mGridLayout2.getChildCount() - 1);
        imageView.setImageBitmap(null);
        mEmptyImageView = imageView;
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

            //步数+1
            move_step += 1;
            text_move.setText("目前移动步数 :  " + move_step);

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
                            //数据写入文件
                            if(move_step < record_min){
                                share = getSharedPreferences("name", 0);
                                SharedPreferences.Editor edit = share.edit();
                                edit.putInt(mode + x , move_step);
                                edit.commit();
                                Toast.makeText(Default_game.this, "拼图成功，打破记录，游戏结束！", Toast.LENGTH_LONG).show();
                            }
                            else Toast.makeText(Default_game.this, "拼图成功，游戏结束！", Toast.LENGTH_LONG).show();
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
                if (emptyJigsaw.getOriginalY() + 1 <= mGridLayout2.getColumnCount() - 1) {
                    imageView = mImageViewArray[emptyJigsaw.getOriginalX()][emptyJigsaw.getOriginalY() + 1];
                }
                break;
            case GestureHelper.RIGHT:
                if (emptyJigsaw.getOriginalY() - 1 >= 0) {
                    imageView = mImageViewArray[emptyJigsaw.getOriginalX()][emptyJigsaw.getOriginalY() - 1];
                }
                break;
            case GestureHelper.UP:
                if (emptyJigsaw.getOriginalX() + 1 <= mGridLayout2.getRowCount() - 1) {
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

}

