package com.example.puzzle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.ImageView;
import java.io.IOException;


public class JigsawHelper {
    private static volatile JigsawHelper mInstance;
    private JigsawHelper(){
    }

    public static JigsawHelper getInstance(){
        if (mInstance == null){
            synchronized (JigsawHelper.class){
                if(mInstance == null){
                    mInstance = new JigsawHelper();
                }
            }
        }
        return mInstance;
    }

    /**
     * 获取拼图大图
     */
    public Bitmap getJigsaw(Context context, Uri uri){
        //加载相册里的Bitmap原图，并获取宽高
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int bitmapWidth = bitmap.getWidth();
        int bitmapHight = bitmap.getHeight();

        int screenWidth = getScreenWidth(context);
        float scale = 1.0f;
        if(screenWidth < bitmapWidth) {
            scale = screenWidth *1.0f / bitmapWidth;
        }
        bitmap = Bitmap.createScaledBitmap(bitmap,screenWidth,(int)(bitmapHight * scale),false);
        return bitmap;
    }
    public Bitmap getJigsaw(Context context, Bitmap bitmap){
        int bitmapWidth = bitmap.getWidth();
        int bitmapHight = bitmap.getHeight();

        int screenWidth = getScreenWidth(context);
        float scale = 1.0f;
        if(screenWidth < bitmapWidth) {
            scale = screenWidth *1.0f / bitmapWidth;
        }
        bitmap = Bitmap.createScaledBitmap(bitmap,screenWidth,(int)(bitmapHight * scale),false);
        return bitmap;
    }

//    public Drawable getJisaw_draw()
    public boolean isNearByEmptyView(ImageView imageView, ImageView emptyImageView) {    ////判断点击图片是否在空图片的四周

        Jigsaw emptyJigsaw = (Jigsaw) imageView.getTag();
        Jigsaw jigsaw = (Jigsaw) emptyImageView.getTag();

        if (emptyJigsaw != null && jigsaw != null) {
            //点击拼图在空拼图的左边
            if (jigsaw.getOriginalX() == emptyJigsaw.getOriginalX() && jigsaw.getOriginalY() + 1 == emptyJigsaw.getOriginalY()) {
                return true;
            }
            //点击拼图在空拼图的右边
            if (jigsaw.getOriginalX() == emptyJigsaw.getOriginalX() && jigsaw.getOriginalY() - 1 == emptyJigsaw.getOriginalY()) {
                return true;
            }
            //点击拼图在空拼图的上边
            if (jigsaw.getOriginalY() == emptyJigsaw.getOriginalY() && jigsaw.getOriginalX() + 1 == emptyJigsaw.getOriginalX()) {
                return true;
            }
            //点击拼图在空拼图的下边
            if (jigsaw.getOriginalY() == emptyJigsaw.getOriginalY() && jigsaw.getOriginalX() - 1 == emptyJigsaw.getOriginalX()) {
                return true;
            }
        }
        return false;
    }

    public boolean isFinishGame(ImageView[][] imageViewArray, ImageView emptyImageView) {
        int rightNum = 0;//记录匹配拼图数

        for (int i = 0; i < imageViewArray.length; i++) {
            for (int j = 0; j < imageViewArray[0].length; j++) {
                if (imageViewArray[i][j] != emptyImageView) {
                    Jigsaw jigsaw = (Jigsaw) imageViewArray[i][j].getTag();
                    if (jigsaw != null) {
                        if (jigsaw.getOriginalX() == jigsaw.getCurrentX() && jigsaw.getOriginalY() == jigsaw.getCurrentY()) {
                            rightNum++;
                        }
                    }
                }
            }
        }
        if (rightNum == (imageViewArray.length * imageViewArray[0].length) - 1) {
            return true;
        }
        return false;
    }

    /**
     * 获取屏幕宽度
     */
    public int getScreenWidth(Context context){
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

}
