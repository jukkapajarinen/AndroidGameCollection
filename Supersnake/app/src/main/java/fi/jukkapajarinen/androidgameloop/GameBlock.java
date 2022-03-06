package fi.jukkapajarinen.androidgameloop;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;

import com.google.android.gms.common.util.ArrayUtils;

import java.util.Collections;

public abstract class GameBlock {

  protected int mColor;
  protected float mXPos;
  protected float mYPos;
  protected int mWidth;
  protected int mHeight;
  protected float mXDir;
  protected float mYDir;
  protected float mVelocity;

  public GameBlock(int color, float x, float y, int width, int height, float xDir, float yDir, float velocity) {
    mColor = color;
    mWidth = width;
    mHeight = height;
    mXPos = x;
    mYPos = y;
    mWidth = width;
    mHeight = height;
    mXDir = xDir;
    mYDir = yDir;
    mVelocity = GameHelpers.dp(velocity);
  }

  public float getXPos() {
    return mXPos;
  }

  public void setXPos(float xPos) {
    mXPos = xPos;
  }

  public float getYPos() {
    return mYPos;
  }

  public void setYPos(float yPos) {
    mYPos = yPos;
  }

  public int getWidth() {
    return mWidth;
  }

  public void setWidth(int width) {
    mWidth = width;
  }

  public int getHeight() {
    return mHeight;
  }

  public void setHeight(int height) {
    mHeight = height;
  }

  public float getXDir() {
    return mXDir;
  }

  public void setXDir(float xDir) {
    mXDir = xDir;
  }

  public float getYDir() {
    return mYDir;
  }

  public void setYDir(float yDir) {
    mYDir = yDir;
  }

  public float getVelocity() {
    return mVelocity;
  }

  public void setVelocity(float velocity) {
    mVelocity = GameHelpers.dp(velocity);
  }

  public void setColor(int color) {
    mColor = color;
  }

  public boolean overlap(GameBlock sprite) {
    RectF obj1 = new RectF(mXPos, mYPos, mXPos + mWidth, mYPos + mHeight);
    RectF obj2 = new RectF(sprite.mXPos, sprite.mYPos, sprite.mXPos + sprite.mWidth, sprite.mYPos + sprite.mHeight);
    return obj1.intersect(obj2);
  }

  public abstract void update();

  public void draw(Canvas canvas, Paint paint) {
    canvas.drawRect(mXPos, mYPos, mXPos + mWidth, mYPos + mHeight, paint);
  }
}
