package fi.jukkapajarinen.androidgameloop;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;

public abstract class GameSprite {

  protected Bitmap mImage;
  protected int mTintColor;
  protected float mXPos;
  protected float mYPos;
  protected int mWidth;
  protected int mHeight;
  protected float mXDir;
  protected float mYDir;
  protected float mVelocity;

  public GameSprite(Bitmap image, int tintColor, float x, float y, int width, int height, float xDir, float yDir, float velocity) {
    mImage = Bitmap.createScaledBitmap(image, width, height, true);
    mTintColor = tintColor;
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
    mImage = Bitmap.createScaledBitmap(mImage, mWidth, mHeight, true);
  }

  public int getHeight() {
    return mHeight;
  }

  public void setHeight(int height) {
    mHeight = height;
    mImage = Bitmap.createScaledBitmap(mImage, mWidth, mHeight, true);
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

  public void setImage(Bitmap image) {
    mImage = Bitmap.createScaledBitmap(image, mWidth, mHeight, true);
  }

  public void setTintColor(int tintColor) {
    mTintColor = tintColor;
  }

  public boolean overlap(GameSprite sprite) {
    RectF obj1 = new RectF(mXPos, mYPos, mXPos + mWidth, mYPos + mHeight);
    RectF obj2 = new RectF(sprite.mXPos, sprite.mYPos, sprite.mXPos + sprite.mWidth, sprite.mYPos + sprite.mHeight);
    return obj1.intersect(obj2);
  }

  public abstract void update();

  public void draw(Canvas canvas, Paint paint) {
    if(mTintColor != 0) paint.setColorFilter(new LightingColorFilter(mTintColor, 0));
    canvas.drawBitmap(mImage, mXPos, mYPos, paint);
  }
}
