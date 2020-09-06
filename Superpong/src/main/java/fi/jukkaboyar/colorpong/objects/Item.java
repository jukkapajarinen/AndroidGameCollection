package fi.jukkaboyar.colorpong.objects;

import android.graphics.Bitmap;
import fi.jukkapajarinen.androidgameloop.GameSprite;

public class Item extends GameSprite {

  private int mType;

  public Item(Bitmap image, int tintColor, float x, float y, int width, int height, float xDir, float yDir, float velocity, int type) {
    super(image, tintColor, x, y, width, height, xDir, yDir, velocity);
    mType = type;
  }

  public int getType() {
    return mType;
  }

  @Override
  public void update() {
    mYPos = mYPos + mYDir * mVelocity;
  }
}
