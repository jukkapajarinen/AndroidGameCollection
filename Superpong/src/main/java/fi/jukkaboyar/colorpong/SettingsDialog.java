package fi.jukkaboyar.colorpong;

import java.util.ArrayList;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.LightingColorFilter;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;

public class SettingsDialog extends AlertDialog {

  private final int[] ITEM_TINTS = new int[] { 0xffd9534f, 0xff209cee, 0xff5cb85c, 0xfff0ad4e, 0xffffffff, 0xffad85d2, 0xff8694a4, 0xff000000, 0xffdf691a };
  private final SharedPreferences mPrefs;
  private final SharedPreferences.Editor mEditor;
  private final boolean mAdsRemoved;
  private ArrayList<CheckBox> mPrefItemXEnabledList;
  private final Spinner mPlayerModeSpinner;
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
    ArrayList<CheckBox> checkBoxes = new ArrayList<>();
    checkBoxes.add(0, (CheckBox) dialogLayout.findViewById(R.id.item1CheckBox));
    checkBoxes.add(1, (CheckBox) dialogLayout.findViewById(R.id.item2CheckBox));
    checkBoxes.add(2, (CheckBox) dialogLayout.findViewById(R.id.item3CheckBox));
    checkBoxes.add(3, (CheckBox) dialogLayout.findViewById(R.id.item4CheckBox));
    checkBoxes.add(4, (CheckBox) dialogLayout.findViewById(R.id.item5CheckBox));
    checkBoxes.add(5, (CheckBox) dialogLayout.findViewById(R.id.item6CheckBox));
    checkBoxes.add(6, (CheckBox) dialogLayout.findViewById(R.id.item7CheckBox));
    checkBoxes.add(7, (CheckBox) dialogLayout.findViewById(R.id.item8CheckBox));
    checkBoxes.add(8, (CheckBox) dialogLayout.findViewById(R.id.item9CheckBox));
    mPlayerModeSpinner = (Spinner) dialogLayout.findViewById(R.id.playersModeSpinner);
    mMysteryCodeEditText = (EditText) dialogLayout.findViewById(R.id.mysteryCodeEditText);

    // Loop through checkBoxes and change enabled status by preferences
    mPrefItemXEnabledList = new ArrayList<>();
    for (int i = 0 ; i < checkBoxes.size() ; i++) {
      checkBoxes.get(i).setText(context.getResources().getStringArray(R.array.item_names)[i]);
      checkBoxes.get(i).getCompoundDrawables()[0].mutate().setColorFilter(new LightingColorFilter(ITEM_TINTS[i], 0));
      checkBoxes.get(i).setChecked(mPrefs.getBoolean("prefItem" + (i + 1) + "Enabled", true));
      mPrefItemXEnabledList.add(i, checkBoxes.get(i));
    }
    mPlayerModeSpinner.setSelection(mPrefs.getInt("prefPlayerModeChoice", 0));

    // Positive button commits all settings
    setButton(AlertDialog.BUTTON_POSITIVE, context.getResources().getText(R.string.dia_btn_save), new OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {

        // Edit normal SharedPreferences
        for (int i = 0 ; i < mPrefItemXEnabledList.size() ; i++) mEditor.putBoolean("prefItem" + (i + 1) + "Enabled", mPrefItemXEnabledList.get(i).isChecked());
        mEditor.putInt("prefPlayerModeChoice", mPlayerModeSpinner.getSelectedItemPosition());

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
