package fi.jukkapajarinen.supersnake.objects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Pair;

import com.google.android.gms.common.util.ArrayUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import fi.jukkapajarinen.androidgameloop.GameBlock;
import fi.jukkapajarinen.androidgameloop.GameSprite;

public class Snake extends GameBlock {

    private int mScale;
    private int mTotal;
    private Point[] mTail;

    public Snake(int color, float x, float y, float xDir, float yDir, int scale) {
        super(color, x, y, scale, scale, xDir, yDir, 1);
        mScale = scale;
        mTotal = 1;
        mTail = new Point[10];
    }

    public boolean eat(Food food) {
        if(food.overlap(this)) {
            mTotal++;
            return true;
        } else {
            return false;
        }
    }

    public int getTotal() {
        return mTotal;
    }

    public Point[] getTail() {
        return mTail;
    }

    public int getDirection() {
        if(getXDir() == 0 && getYDir() == -1) {
            return 1; // top
        } else if(getXDir() == 1 && getYDir() == 0) {
            return 2; // right
        } else if(getXDir() == 0 && getYDir() == 1) {
            return 3; // bottom
        } else if(getXDir() == -1 && getYDir() == 0) {
            return 4; // left
        } else {
            return -1; // still
        }
    }

    public void setDirection(int direction) {
        if(direction == 1) {
            setXDir(0); // top
            setYDir(-1);
        } else if(direction == 2) {
            setXDir(1); // right
            setYDir(0);
        } else if(direction == 3) {
            setXDir(0); // bottom
            setYDir(1);
        } else if(direction == 4) {
            setXDir(-1); // left
            setYDir(0);
        } else if(direction == -1) {
            setXDir(0); // still
            setYDir(0);
        }
    }

    public int getScl() {
        return mScale;
    }

    public void setScl(int scale) {
        mScale = scale;
    }

    @Override
    public void update() {
        for(int i = 0; i < mTail.length - 1 ; i++) {
            mTail[i] = mTail[i + 1];
        }
        if(mTotal >= 1) {
            mTail[mTotal - 1] = new Point((int) mXPos, (int) mYPos);
        }

        mXPos = mXPos + mXDir * mScale;
        mYPos = mYPos + mYDir * mScale;
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        for(int i = 0; i < mTotal ; i++) {
            canvas.drawRect(mTail[i].x, mTail[i].y, mTail[i].x + mScale, mTail[i].y + mScale, paint);
        }

        canvas.drawRect(mXPos, mYPos, mXPos + mScale, mYPos + mScale, paint);
    }
}
