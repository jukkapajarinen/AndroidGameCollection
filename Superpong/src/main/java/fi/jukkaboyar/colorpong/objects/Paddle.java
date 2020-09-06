package fi.jukkaboyar.colorpong.objects;

import android.graphics.Bitmap;
import fi.jukkapajarinen.androidgameloop.GameSprite;

public class Paddle extends GameSprite {

  public Paddle(Bitmap image, int tintColor, float x, float y, int width, int height, float xDir, float yDir, float velocity) {
    super(image, tintColor, x, y, width, height, xDir, yDir, velocity);
  }

  public void move(float x) {
    setXPos(x);
  }

  @Override
  public void update() {}
}
