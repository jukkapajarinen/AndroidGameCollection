package fi.jukkaboyar.colorpong.objects;

import android.graphics.Bitmap;
import fi.jukkapajarinen.androidgameloop.GameSprite;

public class Ball extends GameSprite {

  private int mType;

  public Ball(Bitmap image, int tintColor, float x, float y, int width, int height, float xDir, float yDir, float velocity, int type) {
    super(image, tintColor, x, y, width, height, xDir, yDir, velocity);
    mType = type;
  }

  public int getType() {
    return mType;
  }

  public void setType(int type) {
    mType = type;
  }

  @Override
  public void update() {
    mXPos = mXPos +  mXDir * mVelocity;
    mYPos = mYPos +  mYDir * mVelocity;
  }
}
