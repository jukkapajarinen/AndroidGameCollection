package fi.jukkapajarinen.flappyblock.objects;

import android.graphics.Bitmap;

import fi.jukkapajarinen.androidgameloop.GameSprite;

public class Glider extends GameSprite {

    private float mGravity;
    private float mLift;

    public Glider(Bitmap image, int tintColor, float x, float y, int width, int height, float xDir, float yDir, float velocity, float gravity, float lift) {
        super(image, tintColor, x, y, width, height, xDir, yDir, velocity);
        mGravity = gravity;
        mLift = lift;
    }

    public float getGravity() {
        return mGravity;
    }

    public void setGravity(float gravity) {
        mGravity = gravity;
    }

    public float getLift() {
        return mLift;
    }

    public void setLift(float lift) {
        mLift = lift;
    }

    public void up() {
        mVelocity -= mLift;
    }

    @Override
    public void update() {
        mVelocity += mGravity;
        mYPos += mYDir * mVelocity;
    }
}
