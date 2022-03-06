package fi.jukkapajarinen.androidgameloop;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;

public abstract class Game {

  public static Context context() { return mContext; }
  private static Context mContext;

  public Game(Context context) {
    mContext = context;
  }

  public abstract boolean handleTouchEvent(MotionEvent event);

  public abstract void handleUpdate();

  public abstract void handleDraw(Canvas canvas, Paint paint);
}
