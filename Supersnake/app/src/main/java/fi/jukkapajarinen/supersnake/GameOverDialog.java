package fi.jukkapajarinen.supersnake;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.android.gms.ads.InterstitialAd;

import static fi.jukkapajarinen.supersnake.MainActivity.interstitialAd;

public class GameOverDialog extends AlertDialog {

  private final Context mContext;
  private final InterstitialAd mInterstitialAd;
  private final SharedPreferences mPrefs;
  private final boolean mAdsRemoved;

  public GameOverDialog(final Context context, String title, String message) {
    super(context);

    // Initialize contextual things
    mContext = context;
    mInterstitialAd = interstitialAd();
    mPrefs = context.getSharedPreferences(mContext.getResources().getString(R.string.shared_preferences), Context.MODE_PRIVATE);
    mAdsRemoved = mPrefs.getBoolean("prefAdsRemoved", false);

    // Set dialog title and it's content
    setTitle(title);
    setMessage(message);
    setCancelable(false);
    setCanceledOnTouchOutside(false);

    // Positive button calls a rematch
    setButton(Dialog.BUTTON_POSITIVE, mContext.getResources().getString(R.string.dia_btn_rematch), new OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        Activity activity = (Activity) mContext;
        activity.startActivity(new Intent(activity, MyGameActivity.class));
        activity.finish();
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
