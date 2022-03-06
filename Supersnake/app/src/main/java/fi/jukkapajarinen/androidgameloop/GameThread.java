package fi.jukkapajarinen.androidgameloop;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.SurfaceHolder;

class GameThread extends Thread {

  private final SurfaceHolder mSurfaceHolder;
  private final Game mGameObject;
  private final Paint mPaint;
  private boolean mRunning;
  private Canvas mCanvas;

  public GameThread(SurfaceHolder surfaceHolder, Game gameObject) {
    super();
    mSurfaceHolder = surfaceHolder;
    mGameObject = gameObject;
    mPaint = new Paint();
  }

  @Override
  public synchronized void start() {
    mRunning = true;
    super.start();
  }

  public void kill() {
    mRunning = false;
    boolean retry = true;
    while (retry) {
      try {
        join();
      } catch (Exception e) {
        e.printStackTrace();
      }
      retry = false;
    }
  }

  @Override
  public void run() {
    long startTimeInMillis;
    long loopTimeInMillis;
    long waitTimeInMillis;
    long targetTimeInMillis = 1000 / 60; // 1000ms / 60fps = 16.666msf

    while(mRunning) {
      startTimeInMillis = System.currentTimeMillis();

      try {
        mCanvas = mSurfaceHolder.lockCanvas();
        synchronized (mSurfaceHolder) {
          mGameObject.handleUpdate();
          mGameObject.handleDraw(mCanvas, mPaint);
        }
        mSurfaceHolder.unlockCanvasAndPost(mCanvas);
      } catch (Exception e) {
        e.printStackTrace();
      }

      loopTimeInMillis = System.currentTimeMillis() - startTimeInMillis;
      waitTimeInMillis = targetTimeInMillis - loopTimeInMillis;

      if(waitTimeInMillis > 0) {
        try {
          sleep(waitTimeInMillis);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }
}
