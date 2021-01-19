package sudoku;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Grid {
	
	Integer[] valid = {1,2,3,4,5,6,7,8,9};
	String[] validAction = Stream.of(valid).map(Object::toString).toArray(String[]::new);
	public ArrayList<TileGroup> grid = new ArrayList<TileGroup>();
	
	public Grid() {
		for (int i = 0; i < 9; i++) {
			grid.add(new TileGroup(i+1));
		}
		
		initialiseValues(0, 6, 2);
		initialiseValues(0, 5, 4);
		initialiseValues(0, 3, 5);
		initialiseValues(0, 7, 6);
		initialiseValues(0, 4, 8);
		
		
		initialiseValues(1, 3, 1);
		initialiseValues(1, 9, 5);
		initialiseValues(1, 6, 9);
		
		
		initialiseValues(2, 8, 1);
		initialiseValues(2, 4, 3);
		initialiseValues(2, 3, 7);
		initialiseValues(2, 7, 9);
		
		
		initialiseValues(3, 9, 2);
		initialiseValues(3, 7, 7);
		initialiseValues(3, 1, 8);
		initialiseValues(3, 3, 9);
		

		initialiseValues(4, 5, 2);
		initialiseValues(4, 1, 3);
		initialiseValues(4, 6, 7);
		initialiseValues(4, 2, 8);
		
		
		initialiseValues(5, 2, 1);
		initialiseValues(5, 3, 2);
		initialiseValues(5, 8, 3);
		initialiseValues(5, 4, 8);
	
		
		initialiseValues(6, 3, 1);
		initialiseValues(6, 6, 3);
		initialiseValues(6, 1, 7);
		initialiseValues(6, 2, 9);
		
		
		initialiseValues(7, 4, 1);
		initialiseValues(7, 6, 5);
		initialiseValues(7, 9, 9);
	
		
		initialiseValues(8, 1, 2);
		initialiseValues(8, 5, 4);
		initialiseValues(8, 2, 5);
		initialiseValues(8, 3, 6);
		initialiseValues(8, 8, 8);
		
		System.out.println(this);
	}
	
	void solve() {
		while (!isComplete(grid)) {
//			for (int i = 0; i < grid.size(); i++) {
//				if (grid.get(i).isComplete()) continue;
//				
//				if (Arrays.asList(grid.get(i).tiles).indexOf(0) != -1) {
//					
//				}
//			}
			System.out.println('a');
			break;
		}
	}
	
	void initialiseValues(int tileGroupIndex, int num, int position) {
		TilePosition<Integer, Integer> coord = positionToCoord(position);
		grid.get(tileGroupIndex).tiles[position-1] = new Tile(num, coord.row, coord.col, true);
		if (!checkSurroundings(tileGroupIndex+1, position, false)) {
			throw new Error("Unable to put in "+num+" in Group "+(tileGroupIndex+1)+" at index "+position+", as it violates the tile group uniqueness rule.");
		}
	}
	
	TilePosition<Integer, Integer> positionToCoord(int position) {
		return new TilePosition<>((position-1) /3,(position-1) % 3);
	}
	
	public void putNumber(int num, int tileGroup, int position) {
		if (tileGroup < 0 || position < 0 ) {
			throw new Error("Only non-negative integers are valid tile group and position");
		}
		
		if (tileGroup > 9 || position > 9) {
			throw new Error("Upper bound violation: valid numbers for tile group and position are between 1 and 9");
		}
			
		TileGroup tg = grid.get(tileGroup-1);
		
		//Sanity check
		if (tg.tileGroup != tileGroup) {
			throw new Error("Fatal Error: tile group mismatch between the given input and grid tile groups");
		}
		
		Tile tile = tg.tiles[position-1];
		if (tile.setupNum) {
			System.err.println("Unable to replace a game config value");
			return;
		}
		int originalValue = tile.getNum();
		tile.setNum(num);
		
		boolean completed = true;
		for (int i = 0; i < grid.size(); i++) {
			completed &= grid.get(i).isEmpty();
		}
		
		//check if valid in its own tilegroup
		//check if setting the number at the position is valid for the given row and col in the entire grid
		if (!tg.isValid(completed) || !checkSurroundings(tileGroup, position, completed)) {
			tile.setNum(originalValue);
			System.err.println("Unable to put in "+num+" in Group "+tileGroup+" at index "+position+", as it violates the tile group uniqueness rule.\nReverting to previous value of "+originalValue+"\n\n");
			return;
		}
		
		System.out.println("Successfully placed "+num+" in Group "+tileGroup+" at index "+position+"\n\n");
	}
	
	public boolean checkSurroundings(int tileGroup, int position, boolean completed) {
		int rowRefIndex = (position-1) / 3;
		int rowIndex = (tileGroup-1) % 3;
		
		int colRefIndex = (position-1) % 3;
		int colIndex = (tileGroup-1) / 3;
		
		TileGroup[] toCheck = new TileGroup[3];
		ArrayList<Integer> rowNumbers = getAllNumbersInPosition(0, rowRefIndex, rowIndex, tileGroup, toCheck);
		ArrayList<Integer> colNumbers = getAllNumbersInPosition(1, colRefIndex, colIndex, tileGroup, toCheck);
		
		return isValid(rowNumbers, completed) && isValid(colNumbers, completed);
	}
	
	private ArrayList<Integer> getAllNumbersInPosition(int ref, int refIndex, int index, int tileGroup, TileGroup[] toCheck) {
		ArrayList<Integer> numbers = new ArrayList<Integer>();
		
		if (ref == 0) {//row
			switch (index) {
			case 0:
				toCheck[0] = grid.get(tileGroup-1);
				toCheck[1] = grid.get(tileGroup);
				toCheck[2] = grid.get(tileGroup+1);
				break;
			case 1:
				toCheck[0] = grid.get(tileGroup-2);
				toCheck[1] = grid.get(tileGroup-1);
				toCheck[2] = grid.get(tileGroup);
				break;
			case 2:
				toCheck[0] = grid.get(tileGroup-3);
				toCheck[1] = grid.get(tileGroup-2);
				toCheck[2] = grid.get(tileGroup-1);
			}
		}
		else if (ref == 1) {//col
			switch (index) {
			case 0:
				toCheck[0] = grid.get(tileGroup-1);
				toCheck[1] = grid.get(tileGroup+2);
				toCheck[2] = grid.get(tileGroup+5);
				break;
			case 1:
				toCheck[0] = grid.get(tileGroup-4);
				toCheck[1] = grid.get(tileGroup-1);
				toCheck[2] = grid.get(tileGroup+2);
				break;
			case 2:
				toCheck[0] = grid.get(tileGroup-7);
				toCheck[1] = grid.get(tileGroup-4);
				toCheck[2] = grid.get(tileGroup-1);
			}
		}
		
		for (TileGroup tg : toCheck) {
			for (Tile t : tg.tiles) {
				if (ref == 0) {					
					if (t.getPosition().row == refIndex) numbers.add(t.getNum());
					
					if (t.getPosition().row > refIndex) break;
				}
				else if (ref == 1) {
					if (t.getPosition().col == refIndex) numbers.add(t.getNum());
				}
			}
		}
		
		return numbers;
	}
	
	public boolean isValid(ArrayList<Integer> numbers, boolean checkSolved) {
		if (checkSolved && !isFilled(numbers)) {
			return false;
		}
		
		for (int i : valid) {
			int count = 0;
			for (int n : numbers) {
				count += n == i ? 1 : 0;
			}
			if (count > 1) return false;
		}
		
		return true;
	}
	
	public boolean isFilled(ArrayList<Integer> numbers) {
		for (int n : numbers) {
			if (n == 0) return false;
		}
		return true;
	}
	
	boolean isComplete(ArrayList<TileGroup> sudokuTiles) {
		for (int i = 0; i < sudokuTiles.size(); i++) {
			if (!sudokuTiles.get(i).isComplete()) return false;
		}
		return true;
	}
	
	public boolean isSolved() {
		boolean solved = true;
		for (int i = 0; i < grid.size(); i++) {
			solved &= grid.get(i).isValid(true);
			//TODO: need to check for uniqueness per row and col too
		}
		return solved;
	}
	
	void processInput() {
		String setup = "Pick an action (type number, then press enter):\n";
		setup += "1 -> Insert a number\n";
		setup += "2 -> Remove a number\n";
		setup += "3 -> Forfeit\n";
		
		Scanner in = new Scanner(System.in);
		
		//need to loop this
		while (!isComplete(grid)) {
			int action = prompt(setup, in, new String[]{"1","2","3"});
			System.out.println(action);
			if (action != 1 && action != 2) {
				System.out.println("Bye for now!");
				System.exit(0);
			}
			
			String numQuestion = "Enter the number to ";
			if (action == 1) {
				numQuestion += "insert into the tile group";
			}
			else {
				numQuestion += "remove from the tile group";
			}
			
			int tileGroup = prompt("Enter the target tile group:", in, validAction);
			int position = prompt("Enter the position of the tile in the group:", in, validAction);
			
			
			if (tileGroup != -1 && position != -1) {
				if (action == 1) {
					int num = prompt(numQuestion, in, validAction);
					if (num != -1) {
						putNumber(num, tileGroup, position);
					}
					else {
						System.err.println("Fatal Error: the input number resolved to -1 and cannot be processed");
					}
				}
				else {
					putNumber(0, tileGroup, position);
				}
				System.out.println(this);
			}
			else {
				System.err.println("Fatal Error: either the tile group or position resolved to -1 and cannot be processed");
			}
		}
		
		in.close();
		System.out.println("Solved!");
		//end of loop
	}
	
	int prompt(String question, Scanner scanner, String[] validActions) {
		System.out.println(question);
		int out = -1;
		while (scanner.hasNext()) {
			String a = scanner.next();
			
			if (!Arrays.asList(validActions).contains(a)) {
				System.err.print("Please enter a valid action in the form of number from the following options:\n");
				System.err.print(Stream.of(validActions).collect(Collectors.joining(", ")));
				continue;
			}
			out = Integer.parseInt(a);
			break;
		}
		return out;
	}
	
	public static void main(String[] args) {
		Grid sudoku = new Grid();
		sudoku.processInput();
	}
	
	@Override
	public String toString() {
		String out = "";
		TileGroup[] tgs = new TileGroup[3];
		for (int i = 1; i < 9; i+=3) {
			for (int j = 0; j < 3; j++) {
				ArrayList<Integer> list = this.getAllNumbersInPosition(0, j, 0, i, tgs);
				out += list.stream().map(Object::toString).collect(Collectors.joining(" | ")) + (j < 2 ? "\n" : "\n\n");
			}
		}
		return out;
	}
}
