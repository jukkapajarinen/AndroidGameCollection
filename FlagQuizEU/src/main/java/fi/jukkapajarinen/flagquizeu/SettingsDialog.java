package fi.jukkapajarinen.flagquizeu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.core.app.ActivityCompat;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class SettingsDialog extends AlertDialog {

  private final SharedPreferences mPrefs;
  private final SharedPreferences.Editor mEditor;
  private final boolean mAdsRemoved;
  private final Spinner mQuestionAmountSpinner;
  private final Spinner mTotalSkipsSpinner;
  private final CheckBox mTimerCheckBox;
  private final EditText mMysteryCodeEditText;

  protected SettingsDialog(final Context context) {
    super(context);

    // Initialize contextual things
    mPrefs = context.getSharedPreferences(context.getResources().getString(R.string.shared_preferences), Context.MODE_PRIVATE);
    mEditor = mPrefs.edit();
    mAdsRemoved = mPrefs.getBoolean("prefAdsRemoved", false);

    // Set dialog title and layout
    setTitle(context.getResources().getText(R.string.dia_title_settings));
    View dialogLayout = getLayoutInflater().inflate(R.layout.dialog_settings, null);
    setView(dialogLayout);

    // Initialize widgets from layout
    mQuestionAmountSpinner = (Spinner) dialogLayout.findViewById(R.id.questionsAmountSpinner);
    mTotalSkipsSpinner = (Spinner) dialogLayout.findViewById(R.id.totalSkipsSpinner);
    mTimerCheckBox = (CheckBox) dialogLayout.findViewById(R.id.timerCheckBox);
    mMysteryCodeEditText = (EditText) dialogLayout.findViewById(R.id.mysteryCodeEditText);

    // Initialize widget values from SharedPreferences
    mQuestionAmountSpinner.setSelection(mPrefs.getInt("prefQuestionAmountChoice", 0));
    mTotalSkipsSpinner.setSelection(mPrefs.getInt("prefTotalHintsChoice", 1));
    mTimerCheckBox.setChecked(mPrefs.getBoolean("prefTimerEnabled", true));

    // Positive button commits all settings
    setButton(AlertDialog.BUTTON_POSITIVE, context.getResources().getText(R.string.dia_btn_save), new OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {

        // Edit normal SharedPreferences
        mEditor.putInt("prefQuestionAmountChoice", mQuestionAmountSpinner.getSelectedItemPosition());
        mEditor.putInt("prefTotalHintsChoice", mTotalSkipsSpinner.getSelectedItemPosition());
        mEditor.putBoolean("prefTimerEnabled", mTimerCheckBox.isChecked());

        // Edit adsRemoved if mysteryCode is correct
        if(mMysteryCodeEditText.getText().toString().equals(context.getResources().getString(R.string.mystery_code_md5))) {
          mEditor.putBoolean("prefAdsRemoved", true);
          Activity previousActivity = (Activity) context;
          ActivityCompat.finishAffinity(previousActivity);
          previousActivity.startActivity(new Intent(previousActivity, MainActivity.class));
          Toast.makeText(context, context.getResources().getString(R.string.toast_ads_now_removed), Toast.LENGTH_SHORT).show();
        }

        // Commit and dismiss
        mEditor.commit();
        dismiss();
      }
    });

    // Negative button resets all settings
    setButton(AlertDialog.BUTTON_NEGATIVE, context.getResources().getText(R.string.dia_btn_reset), new OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        mEditor.clear();
        mEditor.putBoolean("prefAdsRemoved", mAdsRemoved);
        mEditor.commit();
        dismiss();
      }
    });

    // Neutral button cancels the possible changes
    setButton(AlertDialog.BUTTON_NEUTRAL, context.getResources().getText(R.string.dia_btn_cancel), new OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dismiss();
      }
    });
  }
}
