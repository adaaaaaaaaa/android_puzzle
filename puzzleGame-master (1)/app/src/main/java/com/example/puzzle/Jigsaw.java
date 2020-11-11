package com.example.puzzle;

import android.graphics.Bitmap;

public class Jigsaw {/***
 * 滑块起始点位置（originX，originY）
 * 滑块当前位置 (currentX，currentY)
 * 当前显示的图像 (bitmap)
 ***/

    private int originalX;
    private int originalY;
    private Bitmap bitmap;
    private int currentX;
    private int currentY;

    public Jigsaw(int originalX, int originalY, Bitmap bitmap){
        this.originalX = originalX;
        this.originalY = originalY;
        this.bitmap = bitmap;
        this.currentX = originalX;
        this.currentY = originalY;
    }

    public int getOriginalX() {
        return originalX;
    }

    public void setOriginalX(int originalX) {
        this.originalX = originalX;
    }

    public int getOriginalY() {
        return originalY;
    }

    public void setOriginalY(int originalY) {
        this.originalY = originalY;
    }

    public Bitmap getBitmap() {

        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getCurrentX() {
        return currentX;
    }

    public void setCurrentX(int currentX) {
        this.currentX = currentX;
    }

    public int getCurrentY() {
        return currentY;
    }

    public void setCurrentY(int currentY) {
        this.currentY = currentY;
    }

    @Override
    public String toString() {
        return "Jigsaw{"+
                "originalX" + originalX +
                "originalY" + originalY +
                "currentX" + currentX +
                "currentY" + currentY +
                "}";
    }
}
