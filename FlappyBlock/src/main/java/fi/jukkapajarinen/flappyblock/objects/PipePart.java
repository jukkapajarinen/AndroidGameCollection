package fi.jukkapajarinen.flappyblock.objects;

import android.graphics.Bitmap;

import fi.jukkapajarinen.androidgameloop.GameSprite;

public class PipePart extends GameSprite {

    public PipePart(Bitmap image, int tintColor, float x, float y, int width, int height, float xDir, float yDir, float velocity) {
        super(image, tintColor, x, y, width, height, xDir, yDir, velocity);
    }

    @Override
    public void update() {
        mXPos += mXDir * mVelocity;
    }
}
