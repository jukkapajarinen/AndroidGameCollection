package fi.jukkapajarinen.androidgameloop;

import android.content.Context;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

  private final Game mGameObject;
  private final GameThread mGameThread;

  public GameView(Context context, Game gameObject) {
    super(context);
    getHolder().addCallback(this);
    setFocusable(true);
    mGameObject = gameObject;
    mGameThread = new GameThread(getHolder(), mGameObject);
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    return mGameObject.handleTouchEvent(event);
  }

  @Override
  public void surfaceCreated(SurfaceHolder surfaceHolder) {
    mGameThread.start();
  }

  @Override
  public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {}

  @Override
  public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
    mGameThread.kill();
  }
}
