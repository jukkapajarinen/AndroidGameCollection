package fi.jukkapajarinen.emojidoku.game;

import android.widget.Button;

public class Move {

  private Button mButton;
  private String mOldValue;
  private String mNewValue;

  public Move(Button button, String oldValue, String newValue) {
    mButton = button;
    mOldValue = oldValue;
    mNewValue = newValue;
  }

  public Button getButton() {
    return mButton;
  }

  public void setButton(Button button) {
    mButton = button;
  }

  public String getOldValue() {
    return mOldValue;
  }

  public void setOldValue(String oldValue) {
    mOldValue = oldValue;
  }

  public String getNewValue() {
    return mNewValue;
  }

  public void setNewValue(String newValue) {
    mNewValue = newValue;
  }
}
