package fi.jukkapajarinen.flagquiz;

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
import static fi.jukkapajarinen.flagquiz.MainActivity.interstitialAd;

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
    mQuestionsMap.put("flag_ad", R.drawable.flag_ad);
    mQuestionsMap.put("flag_ae", R.drawable.flag_ae);
    mQuestionsMap.put("flag_af", R.drawable.flag_af);
    mQuestionsMap.put("flag_ag", R.drawable.flag_ag);
    mQuestionsMap.put("flag_ai", R.drawable.flag_ai);
    mQuestionsMap.put("flag_al", R.drawable.flag_al);
    mQuestionsMap.put("flag_am", R.drawable.flag_am);
    mQuestionsMap.put("flag_ao", R.drawable.flag_ao);
    mQuestionsMap.put("flag_aq", R.drawable.flag_aq);
    mQuestionsMap.put("flag_ar", R.drawable.flag_ar);
    mQuestionsMap.put("flag_as", R.drawable.flag_as);
    mQuestionsMap.put("flag_at", R.drawable.flag_at);
    mQuestionsMap.put("flag_au", R.drawable.flag_au);
    mQuestionsMap.put("flag_aw", R.drawable.flag_aw);
    mQuestionsMap.put("flag_ax", R.drawable.flag_ax);
    mQuestionsMap.put("flag_az", R.drawable.flag_az);
    mQuestionsMap.put("flag_ba", R.drawable.flag_ba);
    mQuestionsMap.put("flag_bb", R.drawable.flag_bb);
    mQuestionsMap.put("flag_bd", R.drawable.flag_bd);
    mQuestionsMap.put("flag_be", R.drawable.flag_be);
    mQuestionsMap.put("flag_bf", R.drawable.flag_bf);
    mQuestionsMap.put("flag_bg", R.drawable.flag_bg);
    mQuestionsMap.put("flag_bh", R.drawable.flag_bh);
    mQuestionsMap.put("flag_bi", R.drawable.flag_bi);
    mQuestionsMap.put("flag_bj", R.drawable.flag_bj);
    mQuestionsMap.put("flag_bl", R.drawable.flag_bl);
    mQuestionsMap.put("flag_bm", R.drawable.flag_bm);
    mQuestionsMap.put("flag_bn", R.drawable.flag_bn);
    mQuestionsMap.put("flag_bo", R.drawable.flag_bo);
    mQuestionsMap.put("flag_bq", R.drawable.flag_bq);
    mQuestionsMap.put("flag_br", R.drawable.flag_br);
    mQuestionsMap.put("flag_bs", R.drawable.flag_bs);
    mQuestionsMap.put("flag_bt", R.drawable.flag_bt);
    mQuestionsMap.put("flag_bv", R.drawable.flag_bv);
    mQuestionsMap.put("flag_bw", R.drawable.flag_bw);
    mQuestionsMap.put("flag_by", R.drawable.flag_by);
    mQuestionsMap.put("flag_bz", R.drawable.flag_bz);
    mQuestionsMap.put("flag_ca", R.drawable.flag_ca);
    mQuestionsMap.put("flag_cc", R.drawable.flag_cc);
    mQuestionsMap.put("flag_cd", R.drawable.flag_cd);
    mQuestionsMap.put("flag_cf", R.drawable.flag_cf);
    mQuestionsMap.put("flag_cg", R.drawable.flag_cg);
    mQuestionsMap.put("flag_ch", R.drawable.flag_ch);
    mQuestionsMap.put("flag_ci", R.drawable.flag_ci);
    mQuestionsMap.put("flag_ck", R.drawable.flag_ck);
    mQuestionsMap.put("flag_cl", R.drawable.flag_cl);
    mQuestionsMap.put("flag_cm", R.drawable.flag_cm);
    mQuestionsMap.put("flag_cn", R.drawable.flag_cn);
    mQuestionsMap.put("flag_co", R.drawable.flag_co);
    mQuestionsMap.put("flag_cr", R.drawable.flag_cr);
    mQuestionsMap.put("flag_cu", R.drawable.flag_cu);
    mQuestionsMap.put("flag_cv", R.drawable.flag_cv);
    mQuestionsMap.put("flag_cw", R.drawable.flag_cw);
    mQuestionsMap.put("flag_cx", R.drawable.flag_cx);
    mQuestionsMap.put("flag_cy", R.drawable.flag_cy);
    mQuestionsMap.put("flag_cz", R.drawable.flag_cz);
    mQuestionsMap.put("flag_de", R.drawable.flag_de);
    mQuestionsMap.put("flag_dj", R.drawable.flag_dj);
    mQuestionsMap.put("flag_dk", R.drawable.flag_dk);
    mQuestionsMap.put("flag_dm", R.drawable.flag_dm);
    mQuestionsMap.put("flag_do", R.drawable.flag_do);
    mQuestionsMap.put("flag_dz", R.drawable.flag_dz);
    mQuestionsMap.put("flag_ec", R.drawable.flag_ec);
    mQuestionsMap.put("flag_ee", R.drawable.flag_ee);
    mQuestionsMap.put("flag_eg", R.drawable.flag_eg);
    mQuestionsMap.put("flag_eh", R.drawable.flag_eh);
    mQuestionsMap.put("flag_er", R.drawable.flag_er);
    mQuestionsMap.put("flag_es", R.drawable.flag_es);
    mQuestionsMap.put("flag_et", R.drawable.flag_et);
    mQuestionsMap.put("flag_fi", R.drawable.flag_fi);
    mQuestionsMap.put("flag_fj", R.drawable.flag_fj);
    mQuestionsMap.put("flag_fk", R.drawable.flag_fk);
    mQuestionsMap.put("flag_fm", R.drawable.flag_fm);
    mQuestionsMap.put("flag_fo", R.drawable.flag_fo);
    mQuestionsMap.put("flag_fr", R.drawable.flag_fr);
    mQuestionsMap.put("flag_ga", R.drawable.flag_ga);
    mQuestionsMap.put("flag_gb", R.drawable.flag_gb);
    mQuestionsMap.put("flag_gd", R.drawable.flag_gd);
    mQuestionsMap.put("flag_ge", R.drawable.flag_ge);
    mQuestionsMap.put("flag_gf", R.drawable.flag_gf);
    mQuestionsMap.put("flag_gg", R.drawable.flag_gg);
    mQuestionsMap.put("flag_gh", R.drawable.flag_gh);
    mQuestionsMap.put("flag_gi", R.drawable.flag_gi);
    mQuestionsMap.put("flag_gl", R.drawable.flag_gl);
    mQuestionsMap.put("flag_gm", R.drawable.flag_gm);
    mQuestionsMap.put("flag_gn", R.drawable.flag_gn);
    mQuestionsMap.put("flag_gp", R.drawable.flag_gp);
    mQuestionsMap.put("flag_gq", R.drawable.flag_gq);
    mQuestionsMap.put("flag_gr", R.drawable.flag_gr);
    mQuestionsMap.put("flag_gs", R.drawable.flag_gs);
    mQuestionsMap.put("flag_gt", R.drawable.flag_gt);
    mQuestionsMap.put("flag_gu", R.drawable.flag_gu);
    mQuestionsMap.put("flag_gw", R.drawable.flag_gw);
    mQuestionsMap.put("flag_gy", R.drawable.flag_gy);
    mQuestionsMap.put("flag_hk", R.drawable.flag_hk);
    mQuestionsMap.put("flag_hm", R.drawable.flag_hm);
    mQuestionsMap.put("flag_hn", R.drawable.flag_hn);
    mQuestionsMap.put("flag_hr", R.drawable.flag_hr);
    mQuestionsMap.put("flag_ht", R.drawable.flag_ht);
    mQuestionsMap.put("flag_hu", R.drawable.flag_hu);
    mQuestionsMap.put("flag_id", R.drawable.flag_id);
    mQuestionsMap.put("flag_ie", R.drawable.flag_ie);
    mQuestionsMap.put("flag_il", R.drawable.flag_il);
    mQuestionsMap.put("flag_im", R.drawable.flag_im);
    mQuestionsMap.put("flag_in", R.drawable.flag_in);
    mQuestionsMap.put("flag_io", R.drawable.flag_io);
    mQuestionsMap.put("flag_iq", R.drawable.flag_iq);
    mQuestionsMap.put("flag_ir", R.drawable.flag_ir);
    mQuestionsMap.put("flag_is", R.drawable.flag_is);
    mQuestionsMap.put("flag_it", R.drawable.flag_it);
    mQuestionsMap.put("flag_je", R.drawable.flag_je);
    mQuestionsMap.put("flag_jm", R.drawable.flag_jm);
    mQuestionsMap.put("flag_jo", R.drawable.flag_jo);
    mQuestionsMap.put("flag_jp", R.drawable.flag_jp);
    mQuestionsMap.put("flag_ke", R.drawable.flag_ke);
    mQuestionsMap.put("flag_kg", R.drawable.flag_kg);
    mQuestionsMap.put("flag_kh", R.drawable.flag_kh);
    mQuestionsMap.put("flag_ki", R.drawable.flag_ki);
    mQuestionsMap.put("flag_km", R.drawable.flag_km);
    mQuestionsMap.put("flag_kn", R.drawable.flag_kn);
    mQuestionsMap.put("flag_kp", R.drawable.flag_kp);
    mQuestionsMap.put("flag_kr", R.drawable.flag_kr);
    mQuestionsMap.put("flag_kw", R.drawable.flag_kw);
    mQuestionsMap.put("flag_ky", R.drawable.flag_ky);
    mQuestionsMap.put("flag_kz", R.drawable.flag_kz);
    mQuestionsMap.put("flag_la", R.drawable.flag_la);
    mQuestionsMap.put("flag_lb", R.drawable.flag_lb);
    mQuestionsMap.put("flag_lc", R.drawable.flag_lc);
    mQuestionsMap.put("flag_li", R.drawable.flag_li);
    mQuestionsMap.put("flag_lk", R.drawable.flag_lk);
    mQuestionsMap.put("flag_lr", R.drawable.flag_lr);
    mQuestionsMap.put("flag_ls", R.drawable.flag_ls);
    mQuestionsMap.put("flag_lt", R.drawable.flag_lt);
    mQuestionsMap.put("flag_lu", R.drawable.flag_lu);
    mQuestionsMap.put("flag_lv", R.drawable.flag_lv);
    mQuestionsMap.put("flag_ly", R.drawable.flag_ly);
    mQuestionsMap.put("flag_ma", R.drawable.flag_ma);
    mQuestionsMap.put("flag_mc", R.drawable.flag_mc);
    mQuestionsMap.put("flag_md", R.drawable.flag_md);
    mQuestionsMap.put("flag_me", R.drawable.flag_me);
    mQuestionsMap.put("flag_mf", R.drawable.flag_mf);
    mQuestionsMap.put("flag_mg", R.drawable.flag_mg);
    mQuestionsMap.put("flag_mh", R.drawable.flag_mh);
    mQuestionsMap.put("flag_mk", R.drawable.flag_mk);
    mQuestionsMap.put("flag_ml", R.drawable.flag_ml);
    mQuestionsMap.put("flag_mm", R.drawable.flag_mm);
    mQuestionsMap.put("flag_mn", R.drawable.flag_mn);
    mQuestionsMap.put("flag_mo", R.drawable.flag_mo);
    mQuestionsMap.put("flag_mp", R.drawable.flag_mp);
    mQuestionsMap.put("flag_mq", R.drawable.flag_mq);
    mQuestionsMap.put("flag_mr", R.drawable.flag_mr);
    mQuestionsMap.put("flag_ms", R.drawable.flag_ms);
    mQuestionsMap.put("flag_mt", R.drawable.flag_mt);
    mQuestionsMap.put("flag_mu", R.drawable.flag_mu);
    mQuestionsMap.put("flag_mv", R.drawable.flag_mv);
    mQuestionsMap.put("flag_mw", R.drawable.flag_mw);
    mQuestionsMap.put("flag_mx", R.drawable.flag_mx);
    mQuestionsMap.put("flag_my", R.drawable.flag_my);
    mQuestionsMap.put("flag_mz", R.drawable.flag_mz);
    mQuestionsMap.put("flag_na", R.drawable.flag_na);
    mQuestionsMap.put("flag_nc", R.drawable.flag_nc);
    mQuestionsMap.put("flag_ne", R.drawable.flag_ne);
    mQuestionsMap.put("flag_nf", R.drawable.flag_nf);
    mQuestionsMap.put("flag_ng", R.drawable.flag_ng);
    mQuestionsMap.put("flag_ni", R.drawable.flag_ni);
    mQuestionsMap.put("flag_nl", R.drawable.flag_nl);
    mQuestionsMap.put("flag_no", R.drawable.flag_no);
    mQuestionsMap.put("flag_np", R.drawable.flag_np);
    mQuestionsMap.put("flag_nr", R.drawable.flag_nr);
    mQuestionsMap.put("flag_nu", R.drawable.flag_nu);
    mQuestionsMap.put("flag_nz", R.drawable.flag_nz);
    mQuestionsMap.put("flag_om", R.drawable.flag_om);
    mQuestionsMap.put("flag_pa", R.drawable.flag_pa);
    mQuestionsMap.put("flag_pe", R.drawable.flag_pe);
    mQuestionsMap.put("flag_pf", R.drawable.flag_pf);
    mQuestionsMap.put("flag_pg", R.drawable.flag_pg);
    mQuestionsMap.put("flag_ph", R.drawable.flag_ph);
    mQuestionsMap.put("flag_pk", R.drawable.flag_pk);
    mQuestionsMap.put("flag_pl", R.drawable.flag_pl);
    mQuestionsMap.put("flag_pm", R.drawable.flag_pm);
    mQuestionsMap.put("flag_pn", R.drawable.flag_pn);
    mQuestionsMap.put("flag_pr", R.drawable.flag_pr);
    mQuestionsMap.put("flag_ps", R.drawable.flag_ps);
    mQuestionsMap.put("flag_pt", R.drawable.flag_pt);
    mQuestionsMap.put("flag_pw", R.drawable.flag_pw);
    mQuestionsMap.put("flag_py", R.drawable.flag_py);
    mQuestionsMap.put("flag_qa", R.drawable.flag_qa);
    mQuestionsMap.put("flag_re", R.drawable.flag_re);
    mQuestionsMap.put("flag_ro", R.drawable.flag_ro);
    mQuestionsMap.put("flag_rs", R.drawable.flag_rs);
    mQuestionsMap.put("flag_ru", R.drawable.flag_ru);
    mQuestionsMap.put("flag_rw", R.drawable.flag_rw);
    mQuestionsMap.put("flag_sa", R.drawable.flag_sa);
    mQuestionsMap.put("flag_sb", R.drawable.flag_sb);
    mQuestionsMap.put("flag_sc", R.drawable.flag_sc);
    mQuestionsMap.put("flag_sd", R.drawable.flag_sd);
    mQuestionsMap.put("flag_se", R.drawable.flag_se);
    mQuestionsMap.put("flag_sg", R.drawable.flag_sg);
    mQuestionsMap.put("flag_sh", R.drawable.flag_sh);
    mQuestionsMap.put("flag_si", R.drawable.flag_si);
    mQuestionsMap.put("flag_sj", R.drawable.flag_sj);
    mQuestionsMap.put("flag_sk", R.drawable.flag_sk);
    mQuestionsMap.put("flag_sl", R.drawable.flag_sl);
    mQuestionsMap.put("flag_sm", R.drawable.flag_sm);
    mQuestionsMap.put("flag_sn", R.drawable.flag_sn);
    mQuestionsMap.put("flag_so", R.drawable.flag_so);
    mQuestionsMap.put("flag_sr", R.drawable.flag_sr);
    mQuestionsMap.put("flag_ss", R.drawable.flag_ss);
    mQuestionsMap.put("flag_st", R.drawable.flag_st);
    mQuestionsMap.put("flag_sv", R.drawable.flag_sv);
    mQuestionsMap.put("flag_sx", R.drawable.flag_sx);
    mQuestionsMap.put("flag_sy", R.drawable.flag_sy);
    mQuestionsMap.put("flag_sz", R.drawable.flag_sz);
    mQuestionsMap.put("flag_tc", R.drawable.flag_tc);
    mQuestionsMap.put("flag_td", R.drawable.flag_td);
    mQuestionsMap.put("flag_tf", R.drawable.flag_tf);
    mQuestionsMap.put("flag_tg", R.drawable.flag_tg);
    mQuestionsMap.put("flag_th", R.drawable.flag_th);
    mQuestionsMap.put("flag_tj", R.drawable.flag_tj);
    mQuestionsMap.put("flag_tk", R.drawable.flag_tk);
    mQuestionsMap.put("flag_tl", R.drawable.flag_tl);
    mQuestionsMap.put("flag_tm", R.drawable.flag_tm);
    mQuestionsMap.put("flag_tn", R.drawable.flag_tn);
    mQuestionsMap.put("flag_to", R.drawable.flag_to);
    mQuestionsMap.put("flag_tr", R.drawable.flag_tr);
    mQuestionsMap.put("flag_tt", R.drawable.flag_tt);
    mQuestionsMap.put("flag_tv", R.drawable.flag_tv);
    mQuestionsMap.put("flag_tw", R.drawable.flag_tw);
    mQuestionsMap.put("flag_tz", R.drawable.flag_tz);
    mQuestionsMap.put("flag_ua", R.drawable.flag_ua);
    mQuestionsMap.put("flag_ug", R.drawable.flag_ug);
    mQuestionsMap.put("flag_um", R.drawable.flag_um);
    mQuestionsMap.put("flag_us", R.drawable.flag_us);
    mQuestionsMap.put("flag_uy", R.drawable.flag_uy);
    mQuestionsMap.put("flag_uz", R.drawable.flag_uz);
    mQuestionsMap.put("flag_va", R.drawable.flag_va);
    mQuestionsMap.put("flag_vc", R.drawable.flag_vc);
    mQuestionsMap.put("flag_ve", R.drawable.flag_ve);
    mQuestionsMap.put("flag_vg", R.drawable.flag_vg);
    mQuestionsMap.put("flag_vi", R.drawable.flag_vi);
    mQuestionsMap.put("flag_vn", R.drawable.flag_vn);
    mQuestionsMap.put("flag_vu", R.drawable.flag_vu);
    mQuestionsMap.put("flag_wf", R.drawable.flag_wf);
    mQuestionsMap.put("flag_ws", R.drawable.flag_ws);
    mQuestionsMap.put("flag_ye", R.drawable.flag_ye);
    mQuestionsMap.put("flag_yt", R.drawable.flag_yt);
    mQuestionsMap.put("flag_za", R.drawable.flag_za);
    mQuestionsMap.put("flag_zm", R.drawable.flag_zm);
    mQuestionsMap.put("flag_zw", R.drawable.flag_zw);
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
      getWindow().setStatusBarColor(0xff9c27b0);
    }

    // Read settings from SharedPreferences
    mQuestionsAmount = (new int[]{5, 10, 20, 50, mQuestionsMap.size()})[mPrefs.getInt("prefQuestionAmountChoice", 0)];
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
              if(mCurrentQuestion < mQuestionsAmount) {
                createNextQuestion();
              } else {
                // Stop the timer
                mChronometer.stop();

                // First be pessimistic about elapsedMoves >= allowedMoves
                String title = getResources().getString(R.string.dia_title_game_over_2);
                String message = getResources().getString(R.string.content_game_over_2);

                // Check if we came here because of game is completed
                if(mRightAnswersAmount > 2 * mQuestionsAmount / 3) {
                  title = getResources().getString(R.string.dia_title_game_over);
                  message = getResources().getString(R.string.content_game_over);
                }

                // Append score to message
                message += "\n" + getResources().getString(R.string.content_right_text)
                  + ": " + mRightAnswersAmount + ", " + getResources().getString(R.string.content_wrong_text).toLowerCase() + ": " + mWrongAnswerAmount +".";

                // Create and Show GameOverDialog
                GameOverDialog gameOverDialog = new GameOverDialog(GameActivity.this, title, message);
                gameOverDialog.show();
                gameOverDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(0xff9c27b0);
                gameOverDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(0xff9c27b0);
                gameOverDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(0xff9c27b0);
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
