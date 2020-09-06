package fi.jukkapajarinen.supersudoku;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.ads.InterstitialAd;
import java.util.ArrayList;
import java.util.Collections;
import fi.jukkapajarinen.supersudoku.game.Move;
import fi.jukkapajarinen.supersudoku.game.Sudoku;
import static fi.jukkapajarinen.supersudoku.MainActivity.interstitialAd;

public class GameActivity extends AppCompatActivity {

  private InterstitialAd mInterstitialAd;
  private SharedPreferences mPrefs;
  private boolean mAdsRemoved;
  private Sudoku mSudoku;
  private int mDifficultyChoice;
  private int mHintsLeft;
  private boolean mTimerEnabled;
  private ArrayList<Button> mSudokuGridBtns;
  private ArrayList<Button> mActionBtns;
  private ImageButton mActionBtnErase;
  private ArrayList<Move> mAllMoves;
  private int mCurrentActionBtnId;
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
      getWindow().setStatusBarColor(0xffdf691a);
    }

    // Read settings from SharedPreferences
    mDifficultyChoice = mPrefs.getInt("prefDifficultyChoice", 0);
    mHintsLeft = (new int[]{ 0, 3, 5, 10})[mPrefs.getInt("prefTotalHintsChoice", 1)];
    mTimerEnabled = mPrefs.getBoolean("prefTimerEnabled", true);

    // Initialize all ArrayLists
    mSudokuGridBtns = new ArrayList<>();
    mActionBtns = new ArrayList<>();
    mAllMoves = new ArrayList<>();

    // Create Sudoku and read SudokuGrid to memory
    mSudoku = new Sudoku(mDifficultyChoice);
    for(int i = 0 ; i < 9 ; i++) {
      for(int j = 0 ; j < 9 ; j++) {
        Button btn = (Button) findViewById(getResources().getIdentifier("button"+i+"_"+j, "id", getPackageName()));
        btn.setTag(i+";"+j);
        GridLayout.LayoutParams params = (GridLayout.LayoutParams) btn.getLayoutParams();
        params.width = params.height = (getResources().getDisplayMetrics().widthPixels - Math.round(56 * MainActivity.resources().getDisplayMetrics().density)) / 9;
        btn.setLayoutParams(params);
        String populatedVal = mSudoku.getValueFromUnsolved(i, j);
        if(!populatedVal.equals("")) btn.setEnabled(false);
        btn.setText(populatedVal);
        mSudokuGridBtns.add(btn);
      }
    }

    // Read ActionBtns to memory
    for(int i = 0; i < 9 ; i++) {
      Button btn = (Button) findViewById(getResources().getIdentifier("action_"+i, "id", getPackageName()));
      mActionBtns.add(btn);
    }

    // Read eraseActionBtn to memory
    mActionBtnErase = (ImageButton) findViewById(R.id.action_9);

    // Init difficulty text and elapsedTime chronometer
    TextView difficultyTextView = (TextView) findViewById(R.id.modeTextView);
    switch(mDifficultyChoice) {
      case 2: difficultyTextView.setText(getResources().getString(R.string.pref_difficulty_hard)); break;
      case 1: difficultyTextView.setText(getResources().getString(R.string.pref_difficulty_intermediate)); break;
      case 0: default: difficultyTextView.setText(getResources().getString(R.string.pref_difficulty_easy)); break;
    }
    mLastPauseTime = SystemClock.elapsedRealtime();
    mChronometer = (Chronometer) findViewById(R.id.elapsedTime);
    if(!mTimerEnabled) mChronometer.setVisibility(View.INVISIBLE);
    mChronometer.setBase(mLastPauseTime);
    mChronometer.start();
    mPaused = false;

    // Set mCurrentActionBtnId to match id for number 1
    mCurrentActionBtnId = 0;

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

  private void setOnClickListeners() {

    // Set SudokuGrid button listeners
    for(final Button btn : mSudokuGridBtns) {
      btn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

          // Make the move and update the button
          String oldValue = btn.getText().toString();
          String newValue = mCurrentActionBtnId == 9 ? "" : "" + (mCurrentActionBtnId + 1);
          mAllMoves.add(new Move(btn, oldValue, newValue));
          btn.setText(newValue);
          btn.setTextColor(0xff000000);
          btn.setTypeface(Typeface.DEFAULT_BOLD);

          // Check for winning move or failed puzzle
          if(checkForWinningMove() || checkForFailedPuzzle()) {
            mChronometer.stop();
            GameOverDialog gameOverDialog = new GameOverDialog(GameActivity.this, checkForWinningMove() ? 0 : 1);
            gameOverDialog.show();
            gameOverDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(0xffdf691a);
            gameOverDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(0xffdf691a);
            gameOverDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(0xffdf691a);

          }
        }
      });
    }

    // Set ActionBtns listeners
    for(final Button btn : mActionBtns) {
      btn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

          // Change currentActionId
          String resourceIdName = getResources().getResourceName(btn.getId());
          resourceIdName = resourceIdName.substring(resourceIdName.indexOf("action_"));
          mCurrentActionBtnId = Integer.parseInt(resourceIdName.substring(7));

          // Change background colors
          for(Button action : mActionBtns) action.setBackgroundColor(Color.TRANSPARENT);
          mActionBtnErase.setBackgroundColor(Color.TRANSPARENT);
          btn.setBackgroundColor(0xff1a2531);
        }
      });
    }

    // Set EraseBtn listener
    mActionBtnErase.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {

        // Change currentActionId
        mCurrentActionBtnId = 9;

        // Change background colors
        for(Button action : mActionBtns) action.setBackgroundColor(Color.TRANSPARENT);
        mActionBtnErase.setBackgroundColor(0xff1a2531);
      }
    });
  }

  private boolean checkForWinningMove() {
    for(Button btn : mSudokuGridBtns) {
      String tag = (String) btn.getTag();
      String[] rowCol = tag.split(";");
      if(!btn.getText().equals(mSudoku.getValueFromSolved(Integer.parseInt(rowCol[0]), Integer.parseInt(rowCol[1]))))
        return false;
    }
    return true;
  }

  private boolean checkForFailedPuzzle() {
    for(Button btn : mSudokuGridBtns) {
      if(btn.getText().toString().equals(""))
        return false;
    }
    return true;
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
          for(Button btn : mSudokuGridBtns) btn.setTextColor(Color.TRANSPARENT);
          mLastPauseTime = SystemClock.elapsedRealtime();
          mChronometer.stop();
          mPaused = true;
          Toast.makeText(this, getResources().getText(R.string.toast_paused), Toast.LENGTH_SHORT).show();
        } else {
          for(Button btn : mSudokuGridBtns) btn.setTextColor(0xff000000);
          mChronometer.setBase(mChronometer.getBase() + SystemClock.elapsedRealtime() - mLastPauseTime);
          mChronometer.start();
          mPaused = false;
        }
        return true;
      case R.id.menu_item_undo:
        if(!mPaused) {
          if (!mAllMoves.isEmpty()) {
            Move lastMove = mAllMoves.remove(mAllMoves.size() - 1);
            lastMove.getButton().setText(lastMove.getOldValue());
          } else {
            Toast.makeText(this, getResources().getText(R.string.toast_no_moves_to_undo), Toast.LENGTH_SHORT).show();
          }
        }
        return true;
      case R.id.menu_item_hint:
        if(!mPaused) {
          if (mHintsLeft > 0) {
            ArrayList<Button> sudokuGridBtns = new ArrayList<>(mSudokuGridBtns);
            for (Button btn : mSudokuGridBtns)
              if (!btn.getText().equals("")) sudokuGridBtns.remove(btn);
            if(sudokuGridBtns.size() > 0) {
              Collections.shuffle(sudokuGridBtns);
              String tag = (String) sudokuGridBtns.get(0).getTag();
              String[] rowCol = tag.split(";");
              for (Button btn : mSudokuGridBtns) {
                if (btn.getTag().equals(tag)) {
                  btn.setText(mSudoku.getValueFromSolved(Integer.parseInt(rowCol[0]), Integer.parseInt(rowCol[1])));
                  break;
                }
              }
              mHintsLeft--;
            }
          } else {
            Toast.makeText(this, getResources().getString(R.string.toast_no_more_hints), Toast.LENGTH_SHORT).show();
          }
        }
        return true;
    }
    return false;
  }
}
