package fi.jukkapajarinen.emojidoku.game;

import java.util.Random;

import io.oscr.jsudoku.SudokuFactory;

public class Sudoku {

  private Random mRandom;
  private int mDifficulty;
  private io.oscr.jsudoku.Sudoku mSudokuUnsolved;
  private io.oscr.jsudoku.Sudoku mSudokuSolved;

  public Sudoku(int difficulty) {
    mRandom = new Random();
    mDifficulty = difficulty + 1;
    mSudokuUnsolved = SudokuFactory.makeSudoku(mDifficulty * 20);
    mSudokuSolved = SudokuFactory.copySudoku(mSudokuUnsolved);
    mSudokuSolved.trySolve();
  }

  public String getValueFromSolved(int row, int col) {
    return "" + mSudokuSolved.getPosition(row, col);
  }

  public String getValueFromUnsolved(int row, int col) {
    return mSudokuUnsolved.getPosition(row, col) == 0 ? "" : "" + mSudokuUnsolved.getPosition(row, col);
  }

}
