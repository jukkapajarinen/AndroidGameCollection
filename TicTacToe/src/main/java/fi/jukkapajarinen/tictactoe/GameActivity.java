package fi.jukkapajarinen.tictactoe;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.ads.InterstitialAd;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import static fi.jukkapajarinen.tictactoe.MainActivity.interstitialAd;

public class GameActivity extends AppCompatActivity {

  private InterstitialAd mInterstitialAd;
  private SharedPreferences mPrefs;
  private boolean mAdsRemoved;
  private final Random mRandom = new Random();
  private int mBoardSize;
  private boolean mArtificialPlayer;
  private boolean mMovesEnabled;
  private boolean mTimerEnabled;
  private int mElapsedMoves;
  private int mAllowedMoves;
  private TextView mElapsedMovesTextView;
  private TextView mTurnTextView;
  private ArrayList<Button> mGameGridContainer;
  private int mCurrentPlayerId;
  private long mLastPauseTime;
  private Chronometer mChronometer;
  private boolean mPaused;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Init ad and check for sharedPreferences and adsRemoved
    mInterstitialAd = interstitialAd();
    mPrefs = getSharedPreferences(getResources().getString(R.string.shared_preferences), Context.MODE_PRIVATE);
    mAdsRemoved = mPrefs.getBoolean("prefAdsRemoved", false);

    // Set layout and orientation
    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    setContentView(R.layout.activity_game);
    setSupportActionBar((Toolbar) findViewById(R.id.appToolbar));
    getSupportActionBar().setDisplayShowTitleEnabled(false);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    // Try to set statusBar color
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      getWindow().setStatusBarColor(0xff5cb85c);
    }

    // Read settings from SharedPreferences
    mArtificialPlayer = mPrefs.getInt("prefPlayerModeChoice", 1) == 0;
    mMovesEnabled = mPrefs.getBoolean("prefMovesEnabled", false);
    mTimerEnabled = mPrefs.getBoolean("prefTimerEnabled", true);
    mBoardSize = 3;
    mAllowedMoves = mBoardSize * mBoardSize;

    // Fix ticTacToeGrid button sizes
    Typeface typeface = Typeface.createFromAsset(getAssets(), Locale.getDefault().getLanguage().equals("zh") ? "fonts/zcoolhappy.ttf" : "fonts/troika.ttf");
    mGameGridContainer = new ArrayList<>();
    for(int i = 0 ; i < mBoardSize ; i++) {
      for(int j = 0 ; j < mBoardSize ; j++) {
        Button btn = (Button) findViewById(getResources().getIdentifier("button"+i+"_"+j, "id", getPackageName()));
        btn.setTag(i + ";" + j);
        btn.setText("");
        btn.setTypeface(typeface);
        GridLayout.LayoutParams params = (GridLayout.LayoutParams) btn.getLayoutParams();
        params.width = params.height = (getResources().getDisplayMetrics().widthPixels - Math.round(56 * MainActivity.resources().getDisplayMetrics().density)) / mBoardSize;
        btn.setLayoutParams(params);
        mGameGridContainer.add(btn);
      }
    }

    // Init boardSizeText and elapsedMoves
    mElapsedMoves = 0;
    mElapsedMovesTextView = (TextView) findViewById(R.id.elapsedMovesTextView);
    mElapsedMovesTextView.setText(getResources().getString(R.string.content_moves_text) + ": " + mElapsedMoves + " / " + mAllowedMoves);
    if(!mMovesEnabled) mElapsedMovesTextView.setVisibility(View.INVISIBLE);
    mCurrentPlayerId = 0;
    mTurnTextView = (TextView) findViewById(R.id.turnValueTextView);
    mTurnTextView.setText(mCurrentPlayerId == 0 ? "X" : "O");
    mTurnTextView.setTextColor(mCurrentPlayerId == 0 ? 0xffd9534f : 0xff5cb85c);

    // Timer related stuff
    mLastPauseTime = SystemClock.elapsedRealtime();
    mChronometer = (Chronometer) findViewById(R.id.elapsedTime);
    if(!mTimerEnabled) mChronometer.setVisibility(View.INVISIBLE);
    mChronometer.setBase(mLastPauseTime);
    mChronometer.start();
    mPaused = false;

    // Set event listeners for all layout widgets
    setOnClickListeners();
  }

  @Override
  protected void onPause() {
    super.onPause();
    finish();
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    if(!mAdsRemoved && mInterstitialAd.isLoaded()) mInterstitialAd.show();
  }

  private int checkWinner() {
    String b0 = mGameGridContainer.get(0).getText().toString();
    String b1 = mGameGridContainer.get(1).getText().toString();
    String b2 = mGameGridContainer.get(2).getText().toString();
    String b3 = mGameGridContainer.get(3).getText().toString();
    String b4 = mGameGridContainer.get(4).getText().toString();
    String b5 = mGameGridContainer.get(5).getText().toString();
    String b6 = mGameGridContainer.get(6).getText().toString();
    String b7 = mGameGridContainer.get(7).getText().toString();
    String b8 = mGameGridContainer.get(8).getText().toString();

    // 0,1,2 first row
    if(b0.equals("X") && b1.equals("X") && b2.equals("X")) return 0;
    if(b0.equals("O") && b1.equals("O") && b2.equals("O")) return 1;

    // 3,4,5 second row
    if(b3.equals("X") && b4.equals("X") && b5.equals("X")) return 0;
    if(b3.equals("O") && b4.equals("O") && b5.equals("O")) return 1;

    // 6,7,8 third row
    if(b6.equals("X") && b7.equals("X") && b8.equals("X")) return 0;
    if(b6.equals("O") && b7.equals("O") && b8.equals("O")) return 1;

    // 0,3,6 first col
    if(b0.equals("X") && b3.equals("X") && b6.equals("X")) return 0;
    if(b0.equals("O") && b3.equals("O") && b6.equals("O")) return 1;

    // 1,4,7 second col
    if(b1.equals("X") && b4.equals("X") && b7.equals("X")) return 0;
    if(b1.equals("O") && b4.equals("O") && b7.equals("O")) return 1;

    // 2,5,8 third row
    if(b2.equals("X") && b5.equals("X") && b8.equals("X")) return 0;
    if(b2.equals("O") && b5.equals("O") && b8.equals("O")) return 1;

    // 0,4,8 first diagonal
    if(b0.equals("X") && b4.equals("X") && b8.equals("X")) return 0;
    if(b0.equals("O") && b4.equals("O") && b8.equals("O")) return 1;

    // 2,4,6 second diagonal
    if(b2.equals("X") && b4.equals("X") && b6.equals("X")) return 0;
    if(b2.equals("O") && b4.equals("O") && b6.equals("O")) return 1;

    // return 2 if it's a tie
    int filledButtonsAmount = 0;
    for(Button btn: mGameGridContainer) if(!btn.getText().toString().equals("")) filledButtonsAmount++;
    if(filledButtonsAmount >= mBoardSize * mBoardSize) return 2;

    // return -1 for no winner
    return -1;
  }

  private Button selectAiMove() {
    String b0 = mGameGridContainer.get(0).getText().toString();
    String b1 = mGameGridContainer.get(1).getText().toString();
    String b2 = mGameGridContainer.get(2).getText().toString();
    String b3 = mGameGridContainer.get(3).getText().toString();
    String b4 = mGameGridContainer.get(4).getText().toString();
    String b5 = mGameGridContainer.get(5).getText().toString();
    String b6 = mGameGridContainer.get(6).getText().toString();
    String b7 = mGameGridContainer.get(7).getText().toString();
    String b8 = mGameGridContainer.get(8).getText().toString();

    // 0,1,2 first row
    if(b0.equals("X") && b1.equals("X") && b2.equals("")) return mGameGridContainer.get(2);
    if(b0.equals("X") && b1.equals("") && b2.equals("X")) return mGameGridContainer.get(1);
    if(b0.equals("") && b1.equals("X") && b2.equals("X")) return mGameGridContainer.get(0);

    // 3,4,5 second row
    if(b3.equals("X") && b4.equals("X") && b5.equals("")) return mGameGridContainer.get(5);
    if(b3.equals("X") && b4.equals("") && b5.equals("X")) return mGameGridContainer.get(4);
    if(b3.equals("") && b4.equals("X") && b5.equals("X")) return mGameGridContainer.get(3);

    // 6,7,8 third row
    if(b6.equals("X") && b7.equals("X") && b8.equals("")) return mGameGridContainer.get(8);
    if(b6.equals("X") && b7.equals("") && b8.equals("X")) return mGameGridContainer.get(7);
    if(b6.equals("") && b7.equals("X") && b8.equals("X")) return mGameGridContainer.get(6);

    // 0,3,6 first col
    if(b0.equals("X") && b3.equals("X") && b6.equals("")) return mGameGridContainer.get(6);
    if(b0.equals("X") && b3.equals("") && b6.equals("X")) return mGameGridContainer.get(3);
    if(b0.equals("") && b3.equals("X") && b6.equals("X")) return mGameGridContainer.get(0);

    // 1,4,7 second col
    if(b1.equals("X") && b4.equals("X") && b7.equals("")) return mGameGridContainer.get(7);
    if(b1.equals("X") && b4.equals("") && b7.equals("X")) return mGameGridContainer.get(4);
    if(b1.equals("") && b4.equals("X") && b7.equals("X")) return mGameGridContainer.get(1);

    // 2,5,8 third row
    if(b2.equals("X") && b5.equals("X") && b8.equals("")) return mGameGridContainer.get(8);
    if(b2.equals("X") && b5.equals("") && b8.equals("X")) return mGameGridContainer.get(5);
    if(b2.equals("") && b5.equals("X") && b8.equals("X")) return mGameGridContainer.get(2);

    // 0,4,8 first diagonal
    if(b0.equals("X") && b4.equals("X") && b8.equals("")) return mGameGridContainer.get(8);
    if(b0.equals("X") && b4.equals("") && b8.equals("X")) return mGameGridContainer.get(4);
    if(b0.equals("") && b4.equals("X") && b8.equals("X")) return mGameGridContainer.get(0);

    // 2,4,6 second diagonal
    if(b2.equals("X") && b4.equals("X") && b6.equals("")) return mGameGridContainer.get(6);
    if(b2.equals("X") && b4.equals("") && b6.equals("X")) return mGameGridContainer.get(4);
    if(b2.equals("") && b4.equals("X") && b6.equals("X")) return mGameGridContainer.get(2);

    // return random if above doesn't match
    Button aiMoveBtn = mGameGridContainer.get(mRandom.nextInt(mBoardSize * mBoardSize));
    while(!aiMoveBtn.getText().toString().equals(""))
      aiMoveBtn = mGameGridContainer.get(mRandom.nextInt(mBoardSize * mBoardSize));
    return aiMoveBtn;
  }

  private void makeMove(Button btn) {
    // Update text and color
    btn.setText(mCurrentPlayerId == 0 ? "X" : "O");
    btn.setTextColor(mCurrentPlayerId == 0 ? 0xffd9534f : 0xff5cb85c);
    btn.setEnabled(false);

    // Update mElapsedMoves and TextView
    mElapsedMoves++;
    mElapsedMovesTextView.setText(getResources().getString(R.string.content_moves_text) + ": " + mElapsedMoves + " / " + mAllowedMoves);

    // Change currentPlayerId
    mCurrentPlayerId = mCurrentPlayerId == 0 ? 1 : 0;
    mTurnTextView.setText(mCurrentPlayerId == 0 ? "X" : "O");
    mTurnTextView.setTextColor(mCurrentPlayerId == 0 ? 0xffd9534f : 0xff5cb85c);

    // Check for winning move
    int winner = checkWinner();
    if(winner == 0 || winner == 1 || winner == 2) {

      // Assume it was a tie
      String title = getResources().getString(R.string.dia_title_game_over_2);
      String message = getResources().getString(R.string.content_game_over_2);

      // Fix values if it was not a tie
      if(winner == 0) {
        title = getResources().getString(R.string.dia_title_game_over);
        message = "X " + getResources().getString(R.string.content_game_over);
      } else if(winner == 1) {
        title = getResources().getString(R.string.dia_title_game_over);
        message = "O " + getResources().getString(R.string.content_game_over);
      }

      // Create and Show GameOverDialog
      GameOverDialog gameOverDialog = new GameOverDialog(GameActivity.this, title, message);
      gameOverDialog.show();
      gameOverDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(0xff5cb85c);
      gameOverDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(0xff5cb85c);
      gameOverDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(0xff5cb85c);
    }
  }

  private void setOnClickListeners() {

    // Set GameArea Button listeners
    ArrayList<Button> buttons = new ArrayList<>(mGameGridContainer);
    for(final Button btn : buttons) {
      btn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          if(mPaused) {
            Toast.makeText(GameActivity.this, GameActivity.this.getResources().getText(R.string.toast_paused), Toast.LENGTH_SHORT).show();
          } else {

            // Make human move
            makeMove(btn);

            // Make AI move if necessary
            if(mArtificialPlayer && checkWinner() == -1) {
              makeMove(selectAiMove());
            }
          }
        }
      });
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);
    getMenuInflater().inflate(R.menu.menu_game, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        finish();
        if(!mAdsRemoved && mInterstitialAd.isLoaded()) mInterstitialAd.show();
        return true;
      case R.id.menu_item_pause:
        if(!mPaused) {
          mLastPauseTime = SystemClock.elapsedRealtime();
          mChronometer.stop();
          mPaused = true;
          Toast.makeText(this, getResources().getText(R.string.toast_paused), Toast.LENGTH_SHORT).show();
        } else {
          mChronometer.setBase(mChronometer.getBase() + SystemClock.elapsedRealtime() - mLastPauseTime);
          mChronometer.start();
          mPaused = false;
          Toast.makeText(this, getResources().getText(R.string.toast_unpaused), Toast.LENGTH_SHORT).show();
        }
        return true;
      case R.id.menu_item_help:
        if(!mPaused) {
          Toast.makeText(this, getResources().getText(R.string.toast_help), Toast.LENGTH_SHORT).show();
        }
        return true;
    }
    return false;
  }
}
