package fi.jukkaboyar.colorpong.objects;

import android.graphics.Bitmap;
import fi.jukkapajarinen.androidgameloop.GameSprite;

public class Wall extends GameSprite {

  private boolean mVisible;

  public Wall(Bitmap image, int tintColor, float x, float y, int width, int height, float xDir, float yDir, float velocity, boolean visible) {
    super(image, tintColor, x, y, width, height, xDir, yDir, velocity);
    mVisible = visible;
  }

  public boolean isVisible() {
    return mVisible;
  }

  public void setVisible(boolean visible) {
    this.mVisible = visible;
  }

  @Override
  public void update() {}
}
