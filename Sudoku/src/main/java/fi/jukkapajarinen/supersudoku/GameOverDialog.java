package fi.jukkapajarinen.supersudoku;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import com.google.android.gms.ads.InterstitialAd;
import static fi.jukkapajarinen.supersudoku.MainActivity.interstitialAd;

public class GameOverDialog extends AlertDialog {

  private final Context mContext;
  private final InterstitialAd mInterstitialAd;
  private final SharedPreferences mPrefs;
  private final boolean mAdsRemoved;

  public GameOverDialog(final Context context, final int type) {
    super(context);

    // Initialize contextual things
    mContext = context;
    mInterstitialAd = interstitialAd();
    mPrefs = context.getSharedPreferences(mContext.getResources().getString(R.string.shared_preferences), Context.MODE_PRIVATE);
    mAdsRemoved = mPrefs.getBoolean("prefAdsRemoved", false);

    // Set dialog title and it's content
    setTitle(type == 0 ? mContext.getResources().getString(R.string.dia_title_game_over) : mContext.getResources().getString(R.string.dia_title_game_over_2));
    setMessage(type == 0 ? context.getResources().getString(R.string.content_game_over) : context.getResources().getString(R.string.content_game_over_2));
    setCancelable(false);
    setCanceledOnTouchOutside(false);

    // Positive button calls a rematch
    setButton(Dialog.BUTTON_POSITIVE, type == 0 ? mContext.getResources().getString(R.string.dia_btn_rematch) : mContext.getResources().getString(R.string.dia_btn_continue), new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        if(type == 0) {
          Activity activity = (Activity) mContext;
          activity.startActivity(new Intent(activity, GameActivity.class));
          activity.finish();
        } else {
          dismiss();
        }
      }
    });

    // Negative button exits and shows Interstitial
    setButton(Dialog.BUTTON_NEGATIVE, mContext.getResources().getString(R.string.dia_btn_exit), new OnClickListener() {
      @Override
      public void onClick(DialogInterface dialogInterface, int which) {
        if(!mAdsRemoved && mInterstitialAd.isLoaded()) mInterstitialAd.show();
        Activity activity = (Activity) mContext;
        activity.finish();
      }
    });
  }

}
