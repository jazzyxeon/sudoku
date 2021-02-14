package sudoku;

import java.util.Set;

public class Tile {
	private int num;
	public final boolean setupNum;
	private TilePosition<Integer, Integer> tilePos;
	public Set<Integer> possibleValues = null;
	
	public Tile(int num, int row, int col, boolean setupNum){
		validateInput(num, row, col);
		
		setNum(num);
		this.setupNum = setupNum;
		tilePos = new TilePosition<Integer, Integer>(row, col);
	}
	
	public Tile(int row, int col) { //shortcut constructor
		this(0, row, col, false);
	}
	
	void validateInput(int num, int row, int col) {
		if (num < 0) {
			throw new Error("Only non negative integers are valid input, row and col numbers");
		}
		
		if (num > 9) {
			throw new Error("Upper bound violation: the highest number for input is 9");
		}
	}
	
	public TilePosition<Integer, Integer> getPosition() {
		return tilePos;
	}
	
	public boolean isEmpty() {
		return num == 0;
	}
	
	public Integer getNum() {
		return num;
	}

	public void setNum(int num) {
		if (num < 0) {
			throw new Error("Lower bound violation: the lowest input number allowed is 0");
		}
		if (num > 9) {
			throw new Error("Upper bound violation: the highest input number allowed is 9");
		}
		this.num = num;
		possibleValues = null;
	}
	
	@Override
	public String toString() {
		return num+"";// +": "+getPosition();
	}
	
}
