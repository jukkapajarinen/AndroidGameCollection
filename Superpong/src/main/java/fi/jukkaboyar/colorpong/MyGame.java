package fi.jukkaboyar.colorpong;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.MotionEvent;
import androidx.appcompat.app.AlertDialog;
import fi.jukkaboyar.colorpong.objects.Ball;
import fi.jukkaboyar.colorpong.objects.Item;
import fi.jukkaboyar.colorpong.objects.Paddle;
import fi.jukkaboyar.colorpong.objects.Player;
import fi.jukkaboyar.colorpong.objects.Wall;
import fi.jukkapajarinen.androidgameloop.Game;
import fi.jukkapajarinen.androidgameloop.GameHelpers;
import static fi.jukkapajarinen.androidgameloop.GameHelpers.dp;
import static fi.jukkapajarinen.androidgameloop.GameHelpers.sp;

public class MyGame extends Game {

  private final int BALL_W = dp(21);
  private final int BALL_H = dp(21);
  private final int ITEM_W = dp(26);
  private final int ITEM_H = dp(26);
  private final int PADDLE_W = dp(120);
  private final int PADDLE_S_W = dp(60);
  private final int PADDLE_B_W = dp(200);
  private final int PADDLE_H = dp(25);
  private final int WALL_W = GameHelpers.SCREEN_W;
  private final int WALL_H = dp(6);
  private final int HUD_BALL_W = dp(12);
  private final int HUD_BALL_H = dp(12);
  private final int HUD_PAD_W = dp(6);
  private final int HUD_PAD_H = dp(3);
  private final int BACKGROUND_COLOR = 0xff1f2d3b;
  private final int FOREGROUND_COLOR = 0xffffffff;
  private final int[] BALL_TINTS = new int[] { 0xffd9534f, 0xff209cee, 0xff5cb85c, 0xfff0ad4e, 0xffffffff, 0xffad85d2 };
  private final int[] ITEM_TINTS = new int[] { 0xffd9534f, 0xff209cee, 0xff5cb85c, 0xfff0ad4e, 0xffffffff, 0xffad85d2, 0xff8694a4, 0xff000000, 0xffdf691a };
  private final int[] PADDLE_TINTS = new int[] { 0xffd9534f, 0xff209cee};
  private String[] ITEM_NAMES;
  private Bitmap BALL_BMP;
  private Bitmap ITEM_BMP;
  private Bitmap PADDLE_BMP;
  private Bitmap PADDLE_S_BMP;
  private Bitmap PADDLE_B_BMP;

  private final Context mContext;
  private final SharedPreferences mPrefs;
  private final Random mRandom = new Random();
  private int SCREEN_W = GameHelpers.SCREEN_W;
  private int SCREEN_H = GameHelpers.SCREEN_H;
  private final long mStartTimeInMillis;
  private final boolean mArtificialPlayer;
  private boolean mGameOver;
  private final Paint mBackground;
  private final Paint mForeground;
  private final Paint mTextPaint;
  private final Paint mDefaultPaint;
  private ArrayList<Ball> mBalls;
  private ArrayList<Item> mItems;
  private ArrayList<Integer> mEnabledItems;
  private Paddle mPaddle1;
  private Paddle mPaddle2;
  private Wall mWall1;
  private Wall mWall2;
  private Player mPlayer1;
  private Player mPlayer2;

  public MyGame(Context context) {

    // Provide context to superclass
    super(context);

    // Android related initializations
    mContext = context;
    mPrefs = mContext.getSharedPreferences(mContext.getResources().getString(R.string.shared_preferences), Context.MODE_PRIVATE);

    // Read context related stuff
    ITEM_NAMES = mContext.getResources().getStringArray(R.array.item_names);
    BALL_BMP = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ball);
    ITEM_BMP = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.item);
    PADDLE_BMP = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.paddle);
    PADDLE_S_BMP = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.paddle_small);
    PADDLE_B_BMP = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.paddle_big);

    // Paints for handleDraw
    mBackground = new Paint();
    mBackground.setColor(BACKGROUND_COLOR);
    mForeground = new Paint();
    mForeground.setColor(FOREGROUND_COLOR);
    mForeground.setPathEffect(new DashPathEffect(new float[]{dp(4), dp(4)}, dp(2)));
    mTextPaint = new Paint();
    String fontName = Locale.getDefault().getLanguage().equals("zh") ? "fonts/zcoolhappy.ttf" : "fonts/troika.ttf";
    mTextPaint.setTypeface(Typeface.createFromAsset(mContext.getAssets(), fontName));
    mTextPaint.setColor(FOREGROUND_COLOR);
    mTextPaint.setShadowLayer(dp(2), 0, 0, BACKGROUND_COLOR);
    mTextPaint.setTextSize(sp(12));
    mDefaultPaint = new Paint();

    // Various game mechanics
    if(SCREEN_W > SCREEN_H) {
      SCREEN_W = GameHelpers.SCREEN_H;
      SCREEN_H = GameHelpers.SCREEN_W;
    }
    mStartTimeInMillis = System.currentTimeMillis();
    mGameOver = false;
    mArtificialPlayer = mPrefs.getInt("prefPlayerModeChoice", 0) == 0 ? true : false;

    // Create 10 balls for start
    mBalls = new ArrayList();
    for(int i = 0 ; i < 10 ; i ++) {
      int type = mRandom.nextInt(6);
      mBalls.add(i, new Ball(BALL_BMP, BALL_TINTS[type], mRandom.nextInt(SCREEN_W - BALL_W), SCREEN_H / 2 - BALL_H / 2, BALL_W, BALL_H, mRandom.nextBoolean() ? -1 : 1, mRandom.nextBoolean() ? -1 : 1, 1 + mRandom.nextFloat() * 2, type));
    }

    // Check item settings
    mItems = new ArrayList();
    mEnabledItems = new ArrayList();
    for (int i = 0 ; i < 9 ; i++) {
      if(mPrefs.getBoolean("prefItem" + (i + 1) + "Enabled", true)) mEnabledItems.add(i);
    }

    // Create other game sprites
    mPaddle1 = new Paddle(PADDLE_BMP, PADDLE_TINTS[0], SCREEN_W / 2 - PADDLE_W / 2, 7 * SCREEN_H / 8 - PADDLE_H / 2, PADDLE_W, PADDLE_H, 0, 0, 0);
    mPaddle2 = new Paddle(PADDLE_BMP, PADDLE_TINTS[1], SCREEN_W / 2 - PADDLE_W / 2, SCREEN_H / 8 - PADDLE_H / 2, PADDLE_W, PADDLE_H, 0, 0, 0);
    Bitmap wallBmp = Bitmap.createBitmap(WALL_W, WALL_H, Bitmap.Config.ARGB_8888); wallBmp.eraseColor(0xffffffff);
    mWall1 = new Wall(wallBmp, PADDLE_TINTS[0], 0, 7 * SCREEN_H / 8 - WALL_H / 2, SCREEN_W, WALL_H, 0, 0, 0, false);
    mWall2 = new Wall(wallBmp, PADDLE_TINTS[1], 0, SCREEN_H / 8 - WALL_H / 2, SCREEN_W, WALL_H, 0, 0, 0, false);
    mPlayer1 = new Player(0, mPaddle1, mWall1, 5);
    mPlayer2 = new Player(1, mPaddle2, mWall2, 5);
  }

  public boolean handleTouchEvent(MotionEvent event) {
    for(int i = 0 ; i < event.getPointerCount() ; i++) {
      if (event.getActionMasked() == MotionEvent.ACTION_MOVE) {
        float x = event.getX(i);
        float y = event.getY(i);

        // Player 1 touch area
        if (y > SCREEN_H / 2) {
          x = x < 0 + mPaddle1.getWidth() / 2 ? 0 + mPaddle1.getWidth() / 2 : x;
          x = x > SCREEN_W - mPaddle1.getWidth() / 2 ? SCREEN_W - mPaddle1.getWidth() / 2 : x;
          mPaddle1.move(x - mPaddle1.getWidth() / 2);
        }

        // Player 2 touch area
        if (y < SCREEN_H / 2) {
          x = x < 0 + mPaddle2.getWidth() / 2 ? 0 + mPaddle2.getWidth() / 2 : x;
          x = x > SCREEN_W - mPaddle2.getWidth() / 2 ? SCREEN_W - mPaddle2.getWidth() / 2 : x;
          mPaddle2.move(x - mPaddle2.getWidth() / 2);
        }
      }
    }
    return true;
  }

  private void checkBallCollisions() {
    for (Ball ball : new ArrayList<>(mBalls)) {
      // Horizontal bounds
      if (ball.getXPos() < 0) {
        ball.setXPos(0);
        ball.setXDir(ball.getXDir() * -1);
      } else if (ball.getXPos() > SCREEN_W - ball.getWidth()) {
        ball.setXPos(SCREEN_W - ball.getWidth());
        ball.setXDir(ball.getXDir() * -1);
      }

      // Vertical bounds
      if (ball.getYPos() < 0) {
        mBalls.remove(ball);
        mPlayer2.popBall(ball.getType());
      } else if (ball.getYPos() > SCREEN_H - ball.getHeight()) {
        mBalls.remove(ball);
        mPlayer1.popBall(ball.getType());
      }

      // Paddle of player 1
      if (ball.overlap(mPaddle1)) {
        ball.setYPos(mPaddle1.getYPos() - ball.getHeight());
        ball.setYDir(ball.getYDir() * -1);
        // Right end of paddle
        if(ball.getXPos() + ball.getWidth() / 2 > mPaddle1.getXPos() + 3 * mPaddle1.getWidth() / 4)
          ball.setXDir(Math.abs(ball.getXDir()));
        // Left end of paddle
        if(ball.getXPos() + ball.getWidth() / 2 < mPaddle1.getXPos() + mPaddle1.getWidth() / 4)
          ball.setXDir(Math.abs(ball.getXDir()) * -1);
      }

      // Paddle of player 2
      if (ball.overlap(mPaddle2)) {
        ball.setYPos(mPaddle2.getYPos() + mPaddle2.getHeight());
        ball.setYDir(ball.getYDir() * -1);
        // Right end of paddle
        if(ball.getXPos() + ball.getWidth() / 2 > mPaddle2.getXPos() + 3 * mPaddle2.getWidth() / 4)
          ball.setXDir(Math.abs(ball.getXDir()));
        // Left end of paddle
        if(ball.getXPos() + ball.getWidth() / 2 < mPaddle2.getXPos() + mPaddle2.getWidth() / 4)
          ball.setXDir(Math.abs(ball.getXDir()) * -1);
      }

      // Wall of player 1
      if (mWall1.isVisible() && ball.overlap(mWall1)) {
        ball.setYPos(mWall1.getYPos() - ball.getHeight());
        ball.setYDir(ball.getYDir() * -1);
      }

      // Wall of player 2
      if (mWall2.isVisible() && ball.overlap(mWall2)) {
        ball.setYPos(mWall2.getYPos() + mWall2.getHeight());
        ball.setYDir(ball.getYDir() * -1);
      }
    }
  }

  private void checkItemCollisions() {
    for(Item item : new ArrayList<>(mItems)) {
      // Paddle of player 1
      if (item.overlap(mPaddle1)) {
        mItems.remove(item);
        mPlayer1.setCurrentItem(item);
        useItem(item, mPlayer1);
      }

      // Paddle of player 2
      if (item.overlap(mPaddle2)) {
        mItems.remove(item);
        mPlayer2.setCurrentItem(item);
        useItem(item, mPlayer2);
      }

      // Vertical bounds
      if (item.getYPos() < 0 - item.getHeight() || item.getYPos() > SCREEN_H) {
        mItems.remove(item);
      }
    }
  }

  private void useItem(Item item, Player player) {
    if (item.getType() == 0) {
      // Small paddle
      float oldRealXPos = player.getPaddle().getXPos() + PADDLE_W / 2;
      player.getPaddle().setXPos(oldRealXPos - (PADDLE_S_W / 2));
      player.getPaddle().setWidth(PADDLE_S_W);
      player.getPaddle().setImage(PADDLE_S_BMP);

    } else if (item.getType() == 1) {
      // Big paddle
      float oldRealXPos = player.getPaddle().getXPos() + PADDLE_W / 2;
      float newXPos = oldRealXPos - PADDLE_B_W / 2;
      newXPos = newXPos < 0 ? 0 : newXPos;
      newXPos = newXPos > SCREEN_W - PADDLE_B_W ? SCREEN_W - PADDLE_B_W : newXPos;
      player.getPaddle().setXPos(newXPos);
      player.getPaddle().setWidth(PADDLE_B_W);
      player.getPaddle().setImage(PADDLE_B_BMP);

    } else if (item.getType() == 2) {
      // Color trick
      int type = mRandom.nextInt(6);
      for (Ball ball : new ArrayList<>(mBalls)) {
        ball.setType(type);
        ball.setTintColor(BALL_TINTS[type]);
      }

    } else if (item.getType() == 3) {
      // Extra balls
      for (int i = 0; i < 10; i++) {
        int type = mRandom.nextInt(6);
        mBalls.add(i, new Ball(BALL_BMP, BALL_TINTS[type], mRandom.nextInt(SCREEN_W - BALL_W), SCREEN_H / 2 - BALL_H / 2, BALL_W, BALL_H, mRandom.nextBoolean() ? -1 : 1, mRandom.nextBoolean() ? -1 : 1, 1 + mRandom.nextFloat() * 2, type));
      }

    } else if (item.getType() == 4) {
      // Slow motion
      for (Ball ball : new ArrayList<>(mBalls)) {
        ball.setVelocity(1.0f);
      }

    } else if (item.getType() == 5) {
      // Fast motion
      for (Ball ball : new ArrayList<>(mBalls)) {
        ball.setVelocity(3.0f);
      }

    } else if (item.getType() == 6) {
      // Force push
      for(Ball ball : mBalls) {
        if((player.getId() == 0 && ball.getYDir() > 0) || (player.getId() == 1 && ball.getYDir() < 0))
          ball.setYDir(ball.getYDir() * -1);
      }

    } else if (item.getType() == 7) {
      // Color wall
      player.getWall().setVisible(true);

    } else if (item.getType() == 8) {
      // Tsunami
      for (int i = 0; i < 50; i++) {
        int type = mRandom.nextInt(6);
        mBalls.add(i, new Ball(BALL_BMP, BALL_TINTS[type], mRandom.nextInt(SCREEN_W - BALL_W), SCREEN_H / 2 - BALL_H / 2, BALL_W, BALL_H, mRandom.nextBoolean() ? -1 : 1, mRandom.nextBoolean() ? -1 : 1, 1 + mRandom.nextFloat() * 2, type));
      }
    }
  }

  private void clearItemEffects() {
    Player[] players = new Player[] { mPlayer1, mPlayer2 };
    for (Player player : players) {
      float oldRealXPos = player.getPaddle().getXPos() + player.getPaddle().getWidth() / 2;
      float newXPos = oldRealXPos - PADDLE_W / 2;
      newXPos = newXPos < 0 ? 0 : newXPos;
      newXPos = newXPos > SCREEN_W - PADDLE_W ? SCREEN_W - PADDLE_W : newXPos;
      player.getPaddle().setXPos(newXPos);
      player.getPaddle().setWidth(PADDLE_W);
      player.getPaddle().setImage(PADDLE_BMP);
      player.getWall().setVisible(false);
      player.setCurrentItem(null);
    }
  }

  public void handleUpdate() {
    // Check for winner
    if (!mGameOver && (mPlayer1.noBallsLeft() || mPlayer2.noBallsLeft())) {
      mGameOver = true;
      Activity activity = (Activity) mContext;
      activity.runOnUiThread(new Runnable(){
        @Override
        public void run() {
          GameOverDialog gameOverDialog = new GameOverDialog(mContext, mPlayer1.noBallsLeft() ? mPlayer2 : mPlayer1);
          gameOverDialog.show();
          gameOverDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(0xffd9534f);
          gameOverDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(0xffd9534f);
          gameOverDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(0xffd9534f);
        }
      });
    }

    // Make artificial player move
    if (mArtificialPlayer) {
      float newXPos = mPlayer2.makeAiMove(mBalls, 10);
      newXPos = newXPos < 0 ? 0 : newXPos;
      newXPos = newXPos > SCREEN_W - mPaddle2.getWidth() ? SCREEN_W - mPaddle2.getWidth() : newXPos;
      mPaddle2.move(newXPos);
    }

    // Make 5 more balls if none
    if (mBalls.size() < 1) {
      for (int i = 0; i < 5; i++) {
        int type = mRandom.nextInt(6);
        mBalls.add(i, new Ball(BALL_BMP, BALL_TINTS[type], mRandom.nextInt(SCREEN_W - BALL_W), SCREEN_H / 2 - BALL_H / 2, BALL_W, BALL_H, mRandom.nextBoolean() ? -1 : 1, mRandom.nextBoolean() ? -1 : 1, 1 + mRandom.nextFloat() * 2, type));
      }
    }

    // Create item in every 8 seconds
    if (mEnabledItems.size() > 0 && mItems.size() < 1 && (int) ((System.currentTimeMillis() - mStartTimeInMillis) / 1000) % 8 == 0) {
      int type = mEnabledItems.get(mRandom.nextInt(mEnabledItems.size()));
      mItems.add(0, new Item(ITEM_BMP, ITEM_TINTS[type], mRandom.nextInt(SCREEN_W - ITEM_W), SCREEN_H / 2 - ITEM_H / 2, ITEM_W, ITEM_H, 0, mRandom.nextBoolean() ? -1 : 1, 3.0f, type));
      clearItemEffects();
    }

    // Check collisions for moving sprites
    checkBallCollisions();
    checkItemCollisions();

    // Update balls
    for (Ball ball : mBalls) {
      ball.update();
    }

    // Update items
    for (Item item : mItems) {
      item.update();
    }
  }

  public void handleDraw(Canvas canvas, Paint paint) {
    // Draw background and foreground
    canvas.drawPaint(mBackground);
    canvas.drawLine(0, SCREEN_H / 2, SCREEN_W, SCREEN_H / 2, mForeground);

    // Draw player 1 item name
    if(mPlayer1.getCurrentItem() != null) {
      mTextPaint.setTextSize(sp(18));
      String itemNameText = ITEM_NAMES[mPlayer1.getCurrentItem().getType()].toUpperCase();
      canvas.drawText(itemNameText, SCREEN_W / 2 - mTextPaint.measureText(itemNameText) / 2, 3 * SCREEN_H / 4 + mTextPaint.getTextSize() / 2, mTextPaint);
    }

    // Draw player 2 item name
    if(mPlayer2.getCurrentItem() != null) {
      mTextPaint.setTextSize(sp(18));
      String itemNameText = ITEM_NAMES[mPlayer2.getCurrentItem().getType()].toUpperCase();
      canvas.drawText(itemNameText, SCREEN_W / 2 - mTextPaint.measureText(itemNameText) / 2, SCREEN_H / 4 - mTextPaint.getTextSize() / 2, mTextPaint);
    }

    // Draw player 1 balls left
    for(int i = 0 ; i < 6 ; i++) {
      mTextPaint.setTextSize(sp(12));
      String mBallsLeftText = "" + mPlayer1.getBallsLeftArray()[i];
      mDefaultPaint.setColorFilter(new LightingColorFilter(BALL_TINTS[i], 0));
      canvas.drawBitmap(Bitmap.createScaledBitmap(BALL_BMP, HUD_BALL_W, HUD_BALL_H, true), SCREEN_W / 2 + (i * HUD_BALL_W + i * HUD_PAD_W) - (6 * HUD_BALL_W + 5 * HUD_PAD_W) / 2, 15 * SCREEN_H / 16 + HUD_BALL_H / 2 - HUD_PAD_H, mDefaultPaint);
      canvas.drawText(mBallsLeftText, SCREEN_W / 2 + (i * HUD_BALL_W + i * HUD_PAD_W) - (6 * HUD_BALL_W + 5 * HUD_PAD_W) / 2 + mTextPaint.measureText(mBallsLeftText) / 2, 15 * SCREEN_H / 16 + HUD_BALL_H / 2 - 3 * HUD_PAD_H, mTextPaint);
    }

    // Draw player 2 balls left
    for(int i = 0 ; i < 6 ; i++) {
      mTextPaint.setTextSize(sp(12));
      String mBallsLeftText = "" + mPlayer2.getBallsLeftArray()[i];
      mDefaultPaint.setColorFilter(new LightingColorFilter(BALL_TINTS[i], 0));
      canvas.drawBitmap(Bitmap.createScaledBitmap(BALL_BMP, HUD_BALL_W, HUD_BALL_H, true), SCREEN_W / 2 + (i * HUD_BALL_W + i * HUD_PAD_W) - (6 * HUD_BALL_W + 5 * HUD_PAD_W) / 2, SCREEN_H / 16 - HUD_BALL_H / 2 + HUD_PAD_H, mDefaultPaint);
      canvas.drawText(mBallsLeftText, SCREEN_W / 2 + (i * HUD_BALL_W + i * HUD_PAD_W) - (6 * HUD_BALL_W + 5 * HUD_PAD_W) / 2 + mTextPaint.measureText(mBallsLeftText) / 2, SCREEN_H / 16 - HUD_BALL_H / 2 - HUD_PAD_H, mTextPaint);
    }

    // Draw balls
    for (Ball ball : mBalls) {
      ball.draw(canvas, paint);
    }

    // Draw items
    for (Item item : mItems) {
      item.draw(canvas, paint);
    }

    // Draw walls
    if(mWall1.isVisible()) mWall1.draw(canvas, paint);
    if(mWall2.isVisible()) mWall2.draw(canvas, paint);

    // Draw paddles
    mPaddle1.draw(canvas, paint);
    mPaddle2.draw(canvas, paint);
  }
}
