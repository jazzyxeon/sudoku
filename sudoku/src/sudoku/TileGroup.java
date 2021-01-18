package sudoku;

public class TileGroup {
	
	public final int tileGroup;
	public Tile[] tiles = new Tile[9];
	int[] valid = {1,2,3,4,5,6,7,8,9};
	
	public TileGroup(int tileGroup) {
		if (tileGroup < 1) {
			throw new Error("Input error: the allowed number for tile group is between 1 and 9 inclusive");
		}
		
		this.tileGroup = tileGroup;
		for (int col = 0; col < 9; col++) {
			int row = col / 3;
			tiles[col] = new Tile(row,(col % 3));
		}
	}
	
	
	
	public boolean isValid(boolean checkSolved) {
		if (checkSolved && !isComplete()) {
			return false;
		}
		
		for (int i : valid) {
			int count = 0;
			for (Tile t : tiles) {
				count += t.getNum() == i ? 1 : 0;
			}
			if (count > 1) return false;
		}
		
		return true;
	}
	
	public boolean isComplete() {
		for (Tile t : tiles) {
			if (t.isEmpty()) return false;
		}
		return true;
	}
	
	public boolean isEmpty() {
		for (Tile t : tiles) {
			if (!t.isEmpty()) return false;
		}
		return true;
	}
	
	@Override
	public String toString() {
		String out = "Group: "+ tileGroup +"\n";
		for (Tile t : tiles) {
			out += t + ((t.getPosition().col == 2) ? "\n" : " | ");
		}
		return out;
	}
}
