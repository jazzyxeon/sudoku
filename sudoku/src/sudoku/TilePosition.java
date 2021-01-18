package sudoku;

public class TilePosition<Row, Col> {
	public final int row;
	public final int col;
	
	public TilePosition(int row, int col) {
		if (row < 0 || row > 2) {
			throw new Error("Row index number can only be between 0 and 2");
		}
		
		if (col < 0 || col > 2) {
			throw new Error("Col index number can only be between 0 and 2");
		}
		
		this.row = row;
		this.col = col;
	}
	
	@Override
	public String toString() {
		return "("+row+", "+col+")";
	}
}
