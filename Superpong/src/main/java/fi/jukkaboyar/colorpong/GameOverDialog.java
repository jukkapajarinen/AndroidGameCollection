package fi.jukkaboyar.colorpong;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import com.google.android.gms.ads.InterstitialAd;
import fi.jukkaboyar.colorpong.objects.Player;
import static fi.jukkaboyar.colorpong.MainActivity.interstitialAd;

public class GameOverDialog extends AlertDialog {

  private final Context mContext;
  private final InterstitialAd mInterstitialAd;
  private final SharedPreferences mPrefs;
  private final boolean mAdsRemoved;

  public GameOverDialog(final Context context, Player winner) {
    super(context);

    // Initialize contextual things
    mContext = context;
    mInterstitialAd = interstitialAd();
    mPrefs = context.getSharedPreferences(mContext.getResources().getString(R.string.shared_preferences), Context.MODE_PRIVATE);
    mAdsRemoved = mPrefs.getBoolean("prefAdsRemoved", false);

    // Set dialog title and it's content
    setTitle(mContext.getResources().getString(R.string.dia_title_game_over));
    setMessage(winner.getId() == 0 ? mContext.getResources().getText(R.string.content_player1_won) : mContext.getResources().getText(R.string.content_player2_won));
    setCancelable(false);
    setCanceledOnTouchOutside(false);

    // Positive button calls a rematch
    setButton(Dialog.BUTTON_POSITIVE, mContext.getResources().getString(R.string.dia_btn_rematch), new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        Activity activity = (Activity) mContext;
        activity.startActivity(new Intent(activity, MyGameActivity.class));
        activity.finish();
      }
    });

    // Negative button exits and shows Interstitial
    setButton(Dialog.BUTTON_NEGATIVE, mContext.getResources().getString(R.string.dia_btn_exit), new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialogInterface, int which) {
        if(!mAdsRemoved && mInterstitialAd.isLoaded()) mInterstitialAd.show();
        Activity activity = (Activity) mContext;
        activity.finish();
      }
    });

  }

}
