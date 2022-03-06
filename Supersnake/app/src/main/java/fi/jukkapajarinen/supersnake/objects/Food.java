package fi.jukkapajarinen.supersnake.objects;

import android.graphics.Bitmap;

import com.google.android.gms.common.util.ArrayUtils;

import java.util.Collections;

import fi.jukkapajarinen.androidgameloop.GameBlock;
import fi.jukkapajarinen.androidgameloop.GameSprite;

public class Food extends GameBlock {

    public Food(int color, float x, float y, int scale) {
        super(color, x, y, scale, scale, 0, 0, 0);
    }

    @Override
    public void update() {

    }
}
