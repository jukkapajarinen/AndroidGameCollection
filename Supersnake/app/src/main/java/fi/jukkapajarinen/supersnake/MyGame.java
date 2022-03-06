package fi.jukkapajarinen.supersnake;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.Random;

import fi.jukkapajarinen.androidgameloop.Game;
import fi.jukkapajarinen.androidgameloop.GameHelpers;
import fi.jukkapajarinen.supersnake.objects.Food;
import fi.jukkapajarinen.supersnake.objects.Snake;


public class MyGame extends Game {

  private final Context mContext;
  private final SharedPreferences mPrefs;
  private final Handler handler = new Handler();
  private int SCREEN_W = GameHelpers.SCREEN_W;
  private int SCREEN_H = GameHelpers.SCREEN_H;
  private Random mRandom = new Random();
  private final Paint mDefaultPaint;
  private int mScale;
  private Snake mSnake;
  private ArrayList<Food> mFoods = new ArrayList<>();

  public MyGame(Context context) {

    // Provide context to superclass
    super(context);

    // Android related initializations
    mContext = context;
    mPrefs = mContext.getSharedPreferences(mContext.getResources().getString(R.string.shared_preferences), Context.MODE_PRIVATE);

    // Paints for handleDraw
    mDefaultPaint = new Paint();
    mDefaultPaint.setColor(0xff141e30);

    // Screen orientation fix
    if(SCREEN_W > SCREEN_H) {
      SCREEN_W = GameHelpers.SCREEN_H;
      SCREEN_H = GameHelpers.SCREEN_W;
    }

    // Various game mechanics
    mScale = 40;
    mSnake = new Snake(0xfff5f5f5, 10, 10, 1, 0, mScale);
    mFoods.add(makeFood());

  }

  private Food makeFood() {
      int cols = (int) Math.floor(SCREEN_W / mScale);
      int rows = (int) Math.floor(SCREEN_H / mScale);
      int randX = (int) Math.floor(mRandom.nextInt(cols)) * mScale;
      int randY =(int) Math.floor(mRandom.nextInt(rows)) * mScale ;
      return new Food(0xffc653ff, randX, randY, mScale);
  }

  private boolean checkCollision() {
    if(mSnake.getXPos() <= 0){
      mSnake.setXPos(0);
      return true;
    } else if(mSnake.getXPos() >= SCREEN_W){
      mSnake.setXPos(SCREEN_W - mSnake.getScl());
      return true;
    } else if(mSnake.getYPos() <= 0){
      mSnake.setYPos(0);
      return true;
    } else if(mSnake.getYPos() >= SCREEN_H){
      mSnake.setYPos(SCREEN_H - mSnake.getScl());
      return true;
    } else {
      return false;
    }
  }

  public boolean handleTouchEvent(MotionEvent event) {
    for(int i = 0 ; i < event.getPointerCount() ; i++) {
      if (event.getActionMasked() == MotionEvent.ACTION_UP) {
        float x = event.getX(i);
        float y = event.getY(i);

        // handling user input
        if(mSnake.getDirection() == 1 || mSnake.getDirection() == 3) { // top or bottom
          if(x < mSnake.getXPos() && mSnake.getXPos() != -1) mSnake.setDirection(4);
          if(x > mSnake.getXPos() && mSnake.getXPos() != -1) mSnake.setDirection(2);
        } else if(mSnake.getDirection() == 2 || mSnake.getDirection() == 4) { //right or left
          if(y < mSnake.getYPos() && mSnake.getYPos() != -1) mSnake.setDirection(1);
          if(y > mSnake.getYPos() && mSnake.getYPos() != -1) mSnake.setDirection(3);
        }
      }
    }
    return true;
  }

  public void handleUpdate() {
    mSnake.update();

    for(int i = 0; i < mFoods.size() ; i++) {
        if(mSnake.eat(mFoods.get(i))){
            mFoods.add(makeFood());
        }
    }

    if(checkCollision()) {
        mSnake.setDirection(-1);
        System.out.println("gameover");
    }
  }

  public void handleDraw(Canvas canvas, Paint paint) {
    canvas.drawPaint(mDefaultPaint);

    paint.setColor(0xff00ffff);
    for(int i = 0; i < mFoods.size() ; i++) {
      mFoods.get(i).draw(canvas, paint);
    }

    paint.setColor(0xff00ff00);
    mSnake.draw(canvas, paint);
  }
}
