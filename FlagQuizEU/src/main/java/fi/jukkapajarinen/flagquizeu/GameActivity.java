package fi.jukkapajarinen.flagquizeu;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.ads.InterstitialAd;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import static fi.jukkapajarinen.flagquizeu.MainActivity.interstitialAd;

public class GameActivity extends AppCompatActivity {

  private InterstitialAd mInterstitialAd;
  private SharedPreferences mPrefs;
  private Handler mHandler;
  private Random mRandom;
  private boolean mAdsRemoved;
  private int mQuestionsAmount;
  private int mSkipsLeft;
  private int mCurrentQuestion;
  private int mRightAnswersAmount;
  private int mWrongAnswerAmount;
  private HashMap<String, Integer> mQuestionsMap;
  private ArrayList<String> mRandomAnswersList;
  private TextView mCurrentQuestionTextView;
  private TextView mPausedTextView;
  private ImageView mGameView;
  private boolean mTimerEnabled;
  private ArrayList<Button> mActionBtns;
  private long mLastPauseTime;
  private Chronometer mChronometer;
  private boolean mPaused;

  private void initQuestionsMap() {
    mQuestionsMap.put("flag_at", R.drawable.flag_at);
    mQuestionsMap.put("flag_be", R.drawable.flag_be);
    mQuestionsMap.put("flag_bg", R.drawable.flag_bg);
    mQuestionsMap.put("flag_cy", R.drawable.flag_cy);
    mQuestionsMap.put("flag_cz", R.drawable.flag_cz);
    mQuestionsMap.put("flag_de", R.drawable.flag_de);
    mQuestionsMap.put("flag_dk", R.drawable.flag_dk);
    mQuestionsMap.put("flag_ee", R.drawable.flag_ee);
    mQuestionsMap.put("flag_es", R.drawable.flag_es);
    mQuestionsMap.put("flag_eu", R.drawable.flag_eu);
    mQuestionsMap.put("flag_fi", R.drawable.flag_fi);
    mQuestionsMap.put("flag_fr", R.drawable.flag_fr);
    mQuestionsMap.put("flag_gb", R.drawable.flag_gb);
    mQuestionsMap.put("flag_gr", R.drawable.flag_gr);
    mQuestionsMap.put("flag_hr", R.drawable.flag_hr);
    mQuestionsMap.put("flag_hu", R.drawable.flag_hu);
    mQuestionsMap.put("flag_ie", R.drawable.flag_ie);
    mQuestionsMap.put("flag_it", R.drawable.flag_it);
    mQuestionsMap.put("flag_lt", R.drawable.flag_lt);
    mQuestionsMap.put("flag_lu", R.drawable.flag_lu);
    mQuestionsMap.put("flag_lv", R.drawable.flag_lv);
    mQuestionsMap.put("flag_mt", R.drawable.flag_mt);
    mQuestionsMap.put("flag_nl", R.drawable.flag_nl);
    mQuestionsMap.put("flag_pl", R.drawable.flag_pl);
    mQuestionsMap.put("flag_pt", R.drawable.flag_pt);
    mQuestionsMap.put("flag_ro", R.drawable.flag_ro);
    mQuestionsMap.put("flag_se", R.drawable.flag_se);
    mQuestionsMap.put("flag_si", R.drawable.flag_si);
    mQuestionsMap.put("flag_sk", R.drawable.flag_sk);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Init ad and check for sharedPreferences and adsRemoved
    mInterstitialAd = interstitialAd();
    mPrefs = getSharedPreferences(getResources().getString(R.string.shared_preferences), Context.MODE_PRIVATE);
    mAdsRemoved = mPrefs.getBoolean("prefAdsRemoved", false);
    mHandler = new Handler();

    // Load all flag strings and ids
    mQuestionsMap = new HashMap<>();
    initQuestionsMap();
    mRandomAnswersList = new ArrayList<>(mQuestionsMap.keySet());

    // Set layout and orientation
    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    setContentView(R.layout.activity_game);
    setSupportActionBar((Toolbar) findViewById(R.id.appToolbar));
    getSupportActionBar().setDisplayShowTitleEnabled(false);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    // Try to set statusBar color
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      getWindow().setStatusBarColor(0xff012a90);
    }

    // Read settings from SharedPreferences
    mQuestionsAmount = (new int[]{5, 10, 20, mQuestionsMap.size()})[mPrefs.getInt("prefQuestionAmountChoice", 0)];
    mSkipsLeft = (new int[]{ 0, 3, 5, 10})[mPrefs.getInt("prefTotalHintsChoice", 1)];
    mTimerEnabled = mPrefs.getBoolean("prefTimerEnabled", true);

    // Initialize other stuff
    mRandom = new Random();
    mActionBtns = new ArrayList<>();
    mCurrentQuestion = 0;
    mRightAnswersAmount = 0;
    mWrongAnswerAmount = 0;

    // Fix the imageView size
    mGameView = (ImageView) findViewById(R.id.gameView);
    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) mGameView.getLayoutParams();
    params.width = params.height = getResources().getDisplayMetrics().widthPixels - Math.round(56 * MainActivity.resources().getDisplayMetrics().density);
    mGameView.setLayoutParams(params);
    int paddingFix = Math.round(56 * MainActivity.resources().getDisplayMetrics().density);
    mGameView.setPadding(0, paddingFix, 0, paddingFix);

    // Read ActionBtns to memory
    for(int i = 0; i < 4 ; i++) {
      Button btn = (Button) findViewById(getResources().getIdentifier("action_"+i, "id", getPackageName()));
      btn.bringToFront();
      mActionBtns.add(btn);
    }

    // Read pausedTextView for pause functions
    mPausedTextView = (TextView) findViewById(R.id.pausedTextView);

    // Read questionTextView
    mCurrentQuestionTextView = (TextView) findViewById(R.id.questionTextView);
    mCurrentQuestionTextView.setText(getResources().getString(R.string.content_question_text) + ": " + mCurrentQuestion + " / " + mQuestionsAmount);

    // Init difficulty text and elapsedTime chronometer
    mLastPauseTime = SystemClock.elapsedRealtime();
    mChronometer = (Chronometer) findViewById(R.id.elapsedTime);
    if(!mTimerEnabled) mChronometer.setVisibility(View.INVISIBLE);
    mChronometer.setBase(mLastPauseTime);
    mChronometer.start();
    mPaused = false;

    // Set event listeners for all layout widgets
    setOnClickListeners();

    // Create first question
    createNextQuestion();
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

  private void createNextQuestion() {

    // Reset button colors
    mActionBtns.get(0).setBackgroundColor(0xff1a2531);
    mActionBtns.get(1).setBackgroundColor(0xff1a2531);
    mActionBtns.get(2).setBackgroundColor(0xff1a2531);
    mActionBtns.get(3).setBackgroundColor(0xff1a2531);

    // Increment currentQuestion counter
    mCurrentQuestion++;
    mCurrentQuestionTextView.setText(getResources().getString(R.string.content_question_text) + ": " + mCurrentQuestion + " / " + mQuestionsAmount);

    ArrayList<String> answers = new ArrayList<>();
    int question;

    List<String> keys = new ArrayList<>(mQuestionsMap.keySet());
    answers.add(0, keys.get(mRandom.nextInt(keys.size())));
    question = mQuestionsMap.get(answers.get(0));
    mQuestionsMap.remove(answers.get(0));

    answers.add(1, mRandomAnswersList.get(mRandom.nextInt(mRandomAnswersList.size())));
    answers.add(2, mRandomAnswersList.get(mRandom.nextInt(mRandomAnswersList.size())));
    answers.add(3, mRandomAnswersList.get(mRandom.nextInt(mRandomAnswersList.size())));

    mGameView.setImageResource(question);
    mGameView.setTag(answers.get(0));

    Collections.shuffle(answers);

    for(int i = 0; i < 4; i++) {
      mActionBtns.get(i).setText(getResources().getIdentifier(answers.get(i), "string", getPackageName()));
      mActionBtns.get(i).setTag(answers.get(i));
    }
  }

  private void setOnClickListeners() {

    // Set ActionBtns listeners
    for(final Button btn : mActionBtns) {
      btn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

          // Check if answer was correct
          if(btn.getTag().toString().equals(mGameView.getTag().toString())) {
            mRightAnswersAmount++;
            btn.setBackgroundColor(0xff5cb85c);
          } else {
            mWrongAnswerAmount++;
            btn.setBackgroundColor(0xffd9534f);
            if(mActionBtns.get(0).getTag().equals(mGameView.getTag().toString())) mActionBtns.get(0).setBackgroundColor(0xff5cb85c);
            if(mActionBtns.get(1).getTag().equals(mGameView.getTag().toString())) mActionBtns.get(1).setBackgroundColor(0xff5cb85c);
            if(mActionBtns.get(2).getTag().equals(mGameView.getTag().toString())) mActionBtns.get(2).setBackgroundColor(0xff5cb85c);
            if(mActionBtns.get(3).getTag().equals(mGameView.getTag().toString())) mActionBtns.get(3).setBackgroundColor(0xff5cb85c);
          }

          // 500ms delay for showing green/red buttons
          mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

              // Check if game is still on
              if (mCurrentQuestion < mQuestionsAmount) {
                createNextQuestion();
              } else {
                // Stop the timer
                mChronometer.stop();

                // First be pessimistic about elapsedmoves >= allowedMoves
                String title = getResources().getString(R.string.dia_title_game_over_2);
                String message = getResources().getString(R.string.content_game_over_2);

                // Check if we came here because of game is completed
                if (mRightAnswersAmount > 2 * mQuestionsAmount / 3) {
                  title = getResources().getString(R.string.dia_title_game_over);
                  message = getResources().getString(R.string.content_game_over);
                }

                // Append score to message
                message += "\n" + getResources().getString(R.string.content_right_text)
                  + ": " + mRightAnswersAmount + ", " + getResources().getString(R.string.content_wrong_text).toLowerCase() + ": " + mWrongAnswerAmount + ".";

                // Create and Show GameOverDialog
                GameOverDialog gameOverDialog = new GameOverDialog(GameActivity.this, title, message);
                gameOverDialog.show();
                gameOverDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(0xff012a90);
                gameOverDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(0xff012a90);
                gameOverDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(0xff012a90);
              }
            }
          }, 500);
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
          mPausedTextView.setVisibility(View.VISIBLE);
          mGameView.setVisibility(View.INVISIBLE);
          mCurrentQuestionTextView.setVisibility(View.INVISIBLE);
          mActionBtns.get(0).setVisibility(View.INVISIBLE);
          mActionBtns.get(1).setVisibility(View.INVISIBLE);
          mActionBtns.get(2).setVisibility(View.INVISIBLE);
          mActionBtns.get(3).setVisibility(View.INVISIBLE);
          Toast.makeText(this, getResources().getText(R.string.toast_paused), Toast.LENGTH_SHORT).show();
        } else {
          mChronometer.setBase(mChronometer.getBase() + SystemClock.elapsedRealtime() - mLastPauseTime);
          mChronometer.start();
          mPaused = false;
          mPausedTextView.setVisibility(View.INVISIBLE);
          mGameView.setVisibility(View.VISIBLE);
          mCurrentQuestionTextView.setVisibility(View.VISIBLE);
          mActionBtns.get(0).setVisibility(View.VISIBLE);
          mActionBtns.get(1).setVisibility(View.VISIBLE);
          mActionBtns.get(2).setVisibility(View.VISIBLE);
          mActionBtns.get(3).setVisibility(View.VISIBLE);
          Toast.makeText(this, getResources().getText(R.string.toast_unpaused), Toast.LENGTH_SHORT).show();
        }
        return true;
      case R.id.menu_item_hint:
        if(!mPaused) {
          if(mSkipsLeft > 0) {
            mSkipsLeft--;
            mCurrentQuestion--;
            createNextQuestion();
            Toast.makeText(this, getResources().getText(R.string.toast_skipped), Toast.LENGTH_SHORT).show();
          } else {
            Toast.makeText(this, getResources().getText(R.string.toast_no_more_skips), Toast.LENGTH_SHORT).show();
          }
        }
        return true;
    }
    return false;
  }
}
