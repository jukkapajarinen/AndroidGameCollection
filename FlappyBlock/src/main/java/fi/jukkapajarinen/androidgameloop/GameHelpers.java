package fi.jukkapajarinen.androidgameloop;

public final class GameHelpers {

  public static final int dp(int dp) { return Math.round(dp * Game.context().getResources().getDisplayMetrics().density); }
  public static final float dp(float dp) { return dp * Game.context().getResources().getDisplayMetrics().density; }
  public static final int sp(int sp) { return Math.round(sp * Game.context().getResources().getDisplayMetrics().scaledDensity); }
  public static final float sp(float sp) { return sp * Game.context().getResources().getDisplayMetrics().scaledDensity; }
  public static final int px(int px) { return Math.round(px / Game.context().getResources().getDisplayMetrics().scaledDensity); }
  public static final float px(float px) { return px / Game.context().getResources().getDisplayMetrics().scaledDensity; }

  public static final int SCREEN_W = Game.context().getResources().getDisplayMetrics().widthPixels;
  public static final int SCREEN_H = Game.context().getResources().getDisplayMetrics().heightPixels;
}
