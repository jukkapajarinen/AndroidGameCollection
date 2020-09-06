/*
The MIT License (MIT)

Copyright (c) 2013 Oscar Utbult

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
 */
package io.oscr.jsudoku;


import android.graphics.Point;

import java.util.Arrays;
import java.util.Stack;

final class SudokuModel implements Sudoku {
	private static final long serialVersionUID = 7823359376980607527L;
	private int[][] board;

	SudokuModel() {
		this(Constants.AMOUNT_UNSOLVED);
	}
	
	SudokuModel(int boxesEmpty) {
		SudokuModel tmp = new SudokuModel(new int[Constants.BOARDSIZE][Constants.BOARDSIZE]);
		tmp.trySolve();
		removeParts(boxesEmpty, tmp.board);
		this.board = tmp.board;

	}

	SudokuModel(int[][] board) {
		this.board = copyBoard(board);

	}

	SudokuModel(Sudoku sudoku) {
		SudokuModel sudo = (SudokuModel) sudoku;
		this.board = copyBoard(sudo.board);

	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isSolved() {
		// Check that each row is correctly solved
		for (int[] row : board) {
			if (!isSolvedRow(row))
				return false;
		}

		// Each column is correctly solved
		for (int[] row : getColumns()) {
			if (!isSolvedRow(row))
				return false;
		}

		// Each box is correctly solved
		for (int[] row : getBoxes()) {
			if (!isSolvedRow(row))
				return false;
		}

		// If none of the above are false then the sudoku is correctly solved
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isValid() {
		// Check that each row is correctly solved
		for (int[] row : board) {
			if (!isValidRow(row))
				return false;
		}

		// Each column is correctly solved
		for (int[] row : getColumns()) {
			if (!isValidRow(row))
				return false;
		}

		// Each box is correcly solved
		for (int[] row : getBoxes()) {
			if (!isValidRow(row))
				return false;
		}

		// If none of the above are false then the sudoku is correctly solved
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getPosition(int x, int y) {
		return board[x][y];

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPosition(int x, int y, int value) {
		board[x][y] = value;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean trySolve() {
		if (!isValid()) {
			return false;
		}

		SudokuModel sudoku = solve(new SudokuModel(board));
		if(sudoku != null){
			board = sudoku.board;
			return true;
		}

		return false;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(board);
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null)
			return false;

		if (getClass() != obj.getClass())
			return false;

		SudokuModel other = (SudokuModel) obj;
		if (!Arrays.deepEquals(board, other.board))
			return false;

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();

		// Create build the sudoku string
		for (int[] i : board) {
			for (int j = 0; j < board.length; j++) {
				s.append(i[j] + " ");
			}
			s.append("\n");
		}
		return s.toString();
	}

	/*
	 * Returns deep copy of board matrix.
	 * 
	 * @param board to be copied.
	 * 
	 * @return reference to board copy.
	 */
	private int[][] copyBoard(int[][] board) {
		int[][] result = new int[Constants.BOARDSIZE][Constants.BOARDSIZE];
		for (int i = 0; i < Constants.BOARDSIZE; i++)
			result[i] = Arrays.copyOf(board[i], board[i].length);

		return result;
	}

	/*
	 * Removes as many numbers as given by amount from random position on board.
	 * This to that the Sudoku won't be solved when returned to caller.
	 */
	private void removeParts(int emptySpaces, int[][] board) {
		do {
			int y = (int) (Math.random() * Constants.BOARDSIZE);
			int x = (int) (Math.random() * Constants.BOARDSIZE);

			if (board[x][y] != Constants.EMPTY_POSITION) {
				board[x][y] = Constants.EMPTY_POSITION;
				emptySpaces--;
			}
		} while (emptySpaces > 0);

	}

	/*
	 * Randomizes the order of the content of an int array. Will not change the
	 * elements contained.
	 */
	private void shuffle(int[] values) {
		for (int i = 0; i < values.length; i++) {
			int position = (int) (Math.random() * values.length);
			int tmp = values[position];
			values[position] = values[i];
			values[i] = tmp;
		}

	}

	/*
	 * Returns first empty coordinate of the Sudoku as a Point.
	 * 
	 * Finds first empty square on the Sudoku board. Will return either the
	 * first empty position or null if no empty position exists.
	 * 
	 * @return Point with (x,y) values of the first empty position.
	 */
	private Point getFirstEmpty() {
		for (int i = 0; i < Constants.BOARDSIZE; i++) {
			for (int j = 0; j < Constants.BOARDSIZE; j++) {
				if (board[i][j] == Constants.EMPTY_POSITION) {
					return new Point(i, j);
				}
			}
		}

		return null;
	}

	/*
	 * Checks if a row is valid.
	 * 
	 * A row is valid if it doesn't contain the same digit twice.
	 * 
	 * @param row to be checked.
	 * 
	 * @return true if valid, otherwise false.
	 */
	private boolean isValidRow(int[] row) {
		// Make a copy so that original isn't affected
		int[] copy = Arrays.copyOf(row, row.length);
		Arrays.sort(copy);

		// Only counts to 8 since it uses i+1 further down.
		for (int i = 0; i < 8; i++) {
			// Check if it's not filled
			if (copy[i] == Constants.EMPTY_POSITION)
				continue;

			// Check if the row has duplicates
			if (copy[i] == copy[i + 1])
				return false;
		}

		// If none of the above checks fail it's a valid row
		return true;
	}

	/*
	 * Controls if a row is a solved Sudoku row.
	 * 
	 * Checks that a row doesn't contain any -1 values (unused points), any
	 * duplicate or other illegal values.
	 * 
	 * @param row to check
	 * 
	 * @return true if it's a valid row, otherwise false
	 */
	private boolean isSolvedRow(int[] row) {
		// Make a copy so that original isn't affected
		int[] copy = Arrays.copyOf(row, row.length);
		Arrays.sort(copy);

		// Check if there is an empty squre
		if (copy[0] == Constants.EMPTY_POSITION)
			return false;

		int startValue = 1;
		for (int i = 0; i < Constants.BOARDSIZE; i++) {

			// Check if the row has duplicates
			if (copy[i] != startValue)
				return false;

			// Increase for next check
			startValue++;
		}

		// If none of the above checks fail it's a valid row
		return true;
	}

	/*
	 * Returns an int[][] with all columns as rows.
	 * 
	 * @return int[][] columns as rows.
	 */
	private int[][] getColumns() {
		int[][] columns = new int[Constants.BOARDSIZE][Constants.BOARDSIZE];

		for (int i = 0; i < Constants.BOARDSIZE; i++)
			for (int j = 0; j < Constants.BOARDSIZE; j++)
				columns[j][i] = board[i][j];

		return columns;
	}

	/*
	 * Returns an int[][] that represents the Sudoku boards boxes.
	 * 
	 * The whole Sudoku board is divided into 3x3 boxes. This method creates an
	 * int[][] where each nested array represents a box on the Sudoku board.
	 * 
	 * @return int[][] the boxes as rows.
	 */
	private int[][] getBoxes() {
		int[][] boxes = new int[Constants.BOARDSIZE][Constants.BOARDSIZE];

		int row = 0;
		// The whole Sudoku is 3 x 3 boxes
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				// For each box there is 3x3 values we want to save.
				int index = 0;
				for (int k = j * 3; k < (j + 1) * 3; k++) {
					for (int n = i * 3; n < (i + 1) * 3; n++) {
						boxes[row][index] = board[k][n];
						index++;
					}
				}
				row++;
			}
		}
		return boxes;
	}

	/*
	 * Will attempt to solve Sudoku by using brute force.
	 * 
	 * @param Sudoku to be solved.
	 * @return solved Sudoku or null if not solvable.
	 */
	private SudokuModel solve(SudokuModel sudoku){
		Stack<SudokuModel> stack = new Stack<>();
		stack.push(sudoku);
		
		while(!stack.isEmpty()){
			sudoku = stack.pop();
			
			if(sudoku.isSolved())
				return sudoku;
			
			Point p = sudoku.getFirstEmpty();
			
			// This allows it to be used to generate Sudokus!
			int[] values = new int[Constants.BOARDSIZE];
			for (int i = 0; i < 9; i++) {
				values[i] = i + 1;
			}
			shuffle(values);

			// Try to fill the empty point with each digit from 1-9
			for (int i : values) {
				SudokuModel tmp = new SudokuModel(sudoku.board);
				tmp.setPosition(p.x, p.y, i);
				
				// If it's valid continue to try to solve it.
				if (tmp.isValid()) {
					stack.push(tmp);
				}
			}
		}
		return null;
	}
}