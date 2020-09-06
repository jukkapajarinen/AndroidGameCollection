package fi.jukkapajarinen.flappyblock;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import androidx.appcompat.app.AlertDialog;
import android.view.MotionEvent;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import fi.jukkapajarinen.androidgameloop.Game;
import fi.jukkapajarinen.androidgameloop.GameHelpers;
import fi.jukkapajarinen.flappyblock.objects.Glider;
import fi.jukkapajarinen.flappyblock.objects.Pipe;
import static fi.jukkapajarinen.androidgameloop.GameHelpers.dp;
import static fi.jukkapajarinen.androidgameloop.GameHelpers.sp;


public class MyGame extends Game {

  private final int GLIDER_W = dp(32);
  private final int GLIDER_H = dp(32);
  private final int PIPE_W = dp(32);
  private final int PAUSE_W = dp(24);
  private final int PAUSE_H = dp(24);
  private final int TAP_W = dp(24);
  private final int TAP_H = dp(24);
  private final int HUD_CAP = dp(16);
  private final int CAP_H = dp(128);
  private final int OFFSET_W = dp(256);
  private final int[] COLORS = { 0xfffe3e85, 0xfff5f5f5 };

  private final Context mContext;
  private final SharedPreferences mPrefs;
  private int SCREEN_W = GameHelpers.SCREEN_W;
  private int SCREEN_H = GameHelpers.SCREEN_H;

  private float mPipeSpeed = 4.0f;
  private float mGravity = 1.8f;
  private float mLift = 24.0f;

  private boolean mGameOver = false;
  private boolean mPaused = true;
  private int mScore = 0;
  private Paint mTextPaint = new Paint();
  private Random mRandom = new Random();
  private Bitmap mBackgroundBMP;
  private Bitmap mPauseBtn;
  private Bitmap mPipeBMP;
  private Glider mGlider;
  private ArrayList<Pipe> mPipes = new ArrayList<>();

  public MyGame(Context context) {

    // Provide context to superclass
    super(context);

    // Android related initializations
    mContext = context;
    mPrefs = mContext.getSharedPreferences(mContext.getResources().getString(R.string.shared_preferences), Context.MODE_PRIVATE);

    // Screen orientation fix
    if(SCREEN_W > SCREEN_H) {
      SCREEN_W = GameHelpers.SCREEN_H;
      SCREEN_H = GameHelpers.SCREEN_W;
    }

    // Various game mechanics
    mTextPaint.setColor(COLORS[1]);
    mTextPaint.setTextSize(sp(20));
    mTextPaint.setTypeface(Typeface.createFromAsset(mContext.getAssets(), Locale.getDefault().getLanguage().equals("zh") ? "fonts/zcoolhappy.ttf" : "fonts/troika.ttf"));
    mBackgroundBMP = Bitmap.createBitmap(SCREEN_W, SCREEN_H, Bitmap.Config.ARGB_8888);
    mPauseBtn = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_play_pause), PAUSE_W, PAUSE_H, true);
    Bitmap gliderBMP = Bitmap.createBitmap(GLIDER_W, GLIDER_H, Bitmap.Config.ARGB_8888);
    mPipeBMP = Bitmap.createBitmap(PIPE_W, SCREEN_H, Bitmap.Config.ARGB_8888);
    mBackgroundBMP.eraseColor(COLORS[0]);
    gliderBMP.eraseColor(COLORS[1]);
    mPipeBMP.eraseColor(COLORS[1]);
    mGlider = new Glider(gliderBMP, COLORS[1], SCREEN_W / 6, SCREEN_H / 2, GLIDER_W, GLIDER_H, 0, 1, 1, mGravity, mLift);
    mPipes.add(new Pipe(mPipeBMP, COLORS[1], SCREEN_W, (float) mRandom.nextInt(SCREEN_H), PIPE_W, SCREEN_H, CAP_H, -1, 0, mPipeSpeed));

    // Set timer for starting game
    final Handler handler = new Handler();
    handler.postDelayed(new Runnable() {
      @Override
      public void run() {
          mPaused = false;
        }
      }, 500);
  }

  private void checkCollision() {
    // Top and bottom boundaries
    if(mGlider.getYPos() < 0 || mGlider.getYPos() > SCREEN_H - mGlider.getHeight()) {
      launchGameOverDialog();
    }

    // Pipes and glider
    for(int i = 0 ; i < mPipes.size() ; i++) {
      if(mPipes.get(i).obj().overlap(mGlider) || mPipes.get(i).obj2().overlap(mGlider)) {
        launchGameOverDialog();
        break;
      }
    }

    // Check if cleared pipe
    for(int i = 0 ; i < mPipes.size() ; i++) {
      if(!mPipes.get(i).isCleared() && mPipes.get(i).obj().getXPos()+mPipes.get(i).obj().getWidth() <= mGlider.getXPos()) {
        mPipes.get(i).setCleared(true);
        mScore++;
      }
    }
  }

  private void launchGameOverDialog() {
    mGameOver = true;
    mPaused = true;
    Activity activity = (Activity) mContext;
    activity.runOnUiThread(new Runnable(){
      @Override
      public void run() {
        String title = mContext.getResources().getString(R.string.dia_title_game_over);
        String message = mContext.getResources().getString(R.string.content_game_over, mScore);
        GameOverDialog gameOverDialog = new GameOverDialog(mContext, title, message);
        gameOverDialog.show();
        gameOverDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(0xfffe3e85);
        gameOverDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(0xfffe3e85);
        gameOverDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(0xfffe3e85);
      }
    });
  }

  private void managePipes() {
    // remove offscreen pipes
    for(int i = 0 ; i < mPipes.size() ; i++) {
      if(mPipes.get(i).obj().getXPos() < 0 - mPipes.get(i).obj().getWidth()) {
        mPipes.remove(i);
        break;
      }
    }

    // make new pipes if needed
    if(mPipes.get(mPipes.size() - 1).obj().getXPos() < SCREEN_W - OFFSET_W) {
      mPipes.add(new Pipe(mPipeBMP, COLORS[1], SCREEN_W, (float) mRandom.nextInt(SCREEN_H), PIPE_W, SCREEN_H, CAP_H, -1, 0, mPipeSpeed));
    }
  }

  private boolean isPauseTouched(float x, float y) {
    float pauseBtnX = SCREEN_W-PAUSE_W-HUD_CAP;
    float pauseBtnY = HUD_CAP;
    return (x >= pauseBtnX && x <= pauseBtnX + PAUSE_W) && (y >= pauseBtnY && y <= pauseBtnY + PAUSE_H);
  }

  public boolean handleTouchEvent(MotionEvent event) {
    for(int i = 0 ; i < event.getPointerCount() ; i++) {
      if (event.getActionMasked() == MotionEvent.ACTION_UP) {
        float x = event.getX(i);
        float y = event.getY(i);
        if(isPauseTouched(x, y)) {
          Toast.makeText(mContext, mContext.getResources().getText(mPaused ? R.string.toast_unpaused : R.string.toast_paused), Toast.LENGTH_SHORT).show();
          mPaused = !mPaused;
        } else {
          if(!mPaused) mGlider.up();
        }
      }
    }
    return true;
  }

  public void handleUpdate() {
    if(!mGameOver && !mPaused) {
      for (int i = 0; i < mPipes.size(); i++) {
        mPipes.get(i).update();
      }
      mGlider.update();
      checkCollision();
      managePipes();
    }
  }

  public void handleDraw(Canvas canvas, Paint paint) {
    canvas.drawBitmap(mBackgroundBMP, 0, 0, paint);
    for (int i = 0; i < mPipes.size(); i++) {
        mPipes.get(i).draw(canvas, paint);
    }
    mGlider.draw(canvas, paint);
    canvas.drawText("" + mScore, HUD_CAP+dp(4), HUD_CAP+dp(4)-mTextPaint.descent()-mTextPaint.ascent(), mTextPaint);
    canvas.drawBitmap(mPauseBtn, SCREEN_W - PAUSE_W - HUD_CAP, HUD_CAP, paint);
  }
}
