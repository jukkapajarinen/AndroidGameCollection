package fi.jukkapajarinen.flappyblock.objects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public class Pipe {

    private PipePart mTopPipe;
    private PipePart mBtmPipe;
    private boolean mCleared;

    public Pipe(Bitmap image, int tintColor, float x, float yCap, int width, int totalHeight, int capHeight, float xDir, float yDir, float velocity) {
        float yCapTop = yCap - capHeight / 2;
        if(yCapTop < 0f) yCapTop = 0f;
        if(yCapTop > totalHeight - capHeight) yCapTop = totalHeight - capHeight;
        float yCapBtm = yCapTop + capHeight;

        float hPipeTop = yCapTop - 0;
        float hPipeBtm = totalHeight - yCapBtm;

        if(hPipeTop > 0) mTopPipe = new PipePart(image, tintColor, x, 0, width, (int) hPipeTop, xDir, yDir, velocity);
        if(hPipeBtm > 0) mBtmPipe = new PipePart(image, tintColor, x, yCapBtm, width, (int) hPipeBtm, xDir, yDir, velocity);
        mCleared = false;
    }

    public PipePart obj() {
        if(mTopPipe == null) return mBtmPipe;
        return mTopPipe;
    }

    public PipePart obj2() {
        if(mBtmPipe == null) return mTopPipe;
        return mBtmPipe;
    }

    public boolean isCleared() {
        return mCleared;
    }

    public void setCleared(boolean cleared) {
        mCleared = cleared;
    }

    public void update() {
        if(mTopPipe != null) mTopPipe.update();
        if(mBtmPipe != null) mBtmPipe.update();
    }

    public void draw(Canvas canvas, Paint paint) {
        if(mTopPipe != null) mTopPipe.draw(canvas, paint);
        if(mBtmPipe != null) mBtmPipe.draw(canvas, paint);
    }
}
