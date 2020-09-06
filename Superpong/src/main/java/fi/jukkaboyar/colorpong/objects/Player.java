package fi.jukkaboyar.colorpong.objects;

import java.util.ArrayList;
import java.util.Arrays;

public class Player {

  private final int mId;
  private final Paddle mPaddle;
  private final Wall mWall;
  private int[] mBallsLeft;
  private Item mCurrentItem;

  public Player(int id, Paddle paddle, Wall wall, int startBalls) {
    mId = id;
    mPaddle = paddle;
    mWall = wall;
    mBallsLeft = new int[6];
    Arrays.fill(mBallsLeft, startBalls);
    mCurrentItem = null;
  }

  public int getId() {
    return mId;
  }

  public Paddle getPaddle() {
    return mPaddle;
  }

  public Wall getWall() {
    return mWall;
  }

  public int[] getBallsLeftArray() {
    return mBallsLeft;
  }

  public void popBall(int type) {
    if(mBallsLeft[type] > 0) mBallsLeft[type]--;
  }

  public boolean noBallsLeft() {
    for (int value : mBallsLeft) {
      if (value > 0) return false;
    }
    return true;
  }

  public Item getCurrentItem() {
    return mCurrentItem;
  }

  public void setCurrentItem(Item currentItem) {
    mCurrentItem = currentItem;
  }

  public float makeAiMove(ArrayList<Ball> balls, float speed) {
    float newXPos = mPaddle.getXPos();
    float oldXPos = newXPos;
    float targetXPos;
    if (balls.size() > 0) {
      Ball closestBall = balls.get(0);
      for (Ball ball : balls) {
        if (ball.getYPos() < closestBall.getYPos() && ball.getYDir() < 0) closestBall = ball;
      }

      if(closestBall.getYDir() < 0) {
        targetXPos = closestBall.getXPos() + closestBall.getWidth() / 2 - mPaddle.getWidth() / 2;
        if(targetXPos - oldXPos > 0) {
          newXPos += speed;
          if(newXPos > targetXPos)
            newXPos = targetXPos;
        } else if(targetXPos - oldXPos < 0) {
          newXPos -= speed;
          if(newXPos < targetXPos)
            newXPos = targetXPos;
        }
      }
    }
    return newXPos;
  }
}
