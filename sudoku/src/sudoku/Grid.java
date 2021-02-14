package sudoku;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Grid {
	
	Integer[] valid = {1,2,3,4,5,6,7,8,9};
	String[] validAction = Stream.of(valid).map(Object::toString).toArray(String[]::new);
	public ArrayList<TileGroup> grid = new ArrayList<TileGroup>();
	
	public Grid(int[][] initValues) {
		for (int i = 1; i <= 9; i++) {
			grid.add(new TileGroup(i));
		}
		
		if (initValues.length != 9) {
			throw new Error("Please ensure the input has 9 rows of numbers");
		}
		
		for (int[] i : initValues) {
			if (i.length != 9) {
				throw new Error("Please ensure the input has 9 columns of numbers");
			}
		}
		
		initialiseValuesByRow(initValues);
		
		System.out.println(this);
	}
	
	int[][] createSudoku() {
		int[][] initValues = {
				//Easy
				{0, 6, 0,  3, 0, 0,  8, 0, 4},
				{5, 3, 7,  0, 9, 0,  0, 0, 0},
				{0, 4, 0,  0, 0, 6,  3, 0, 7},
				
				{0, 9, 0,  0, 5, 1,  2, 3, 8},
				{0, 0, 0,  0, 0, 0,  0, 0, 0},
				{7, 1, 3,  6, 2, 0,  0, 4, 0},
				
				{3, 0, 6,  4, 0, 0,  0, 1, 0},
				{0, 0, 0,  0, 6, 0,  5, 2, 3},
				{1, 0, 2,  0, 0, 9,  0, 8, 0},
		};
		//TODO: Implement method to create a sudoku puzzle randomly  
		return initValues;
	}
	
	void solve() {
		while (!isSolved()) {
			
			boolean exhausted = true;
			boolean finalised = true;
			
			for (int i = 0; i < grid.size(); i++) {
				TileGroup tg = grid.get(i);
				if (tg.isComplete()) continue;
				
				for (Tile t : tg.tiles) {
					if (t.getNum() != 0) continue; //already filled, skip
					
					int emptyPos = t.getPosition().row + t.getPosition().col + (t.getPosition().row * 2);
					
					Stream<Integer> rowStream = checkSurroundings(0, i+1, emptyPos+1).stream().filter(n -> n != 0);
					Stream<Integer> colStream = checkSurroundings(1, i+1, emptyPos+1).stream().filter(n -> n != 0);
					Stream<Integer> tileGroupStream = Stream.of(tg.tiles).filter(n -> n.getNum() != 0).map(nm -> nm.getNum());
					
					Set<Integer> placed = Stream.concat(tileGroupStream, Stream.concat(rowStream, colStream))
							.distinct().collect(Collectors.toSet()); 
					HashSet<Integer> validSet = new HashSet<>(Arrays.asList(valid));
					validSet.removeAll(placed);
					
					t.possibleValues = validSet;
					
					if (validSet.size() > 1) continue;
					
					int val = validSet.stream().findFirst().get();
					putNumber(val, tg.tileGroup, emptyPos+1, false);
					removeValueInTileGroup(val, tg.tileGroup);
				}
				
				exhausted = locateUniqueOccurrence(tg);
				finalised = exhausted;
			}
			
			
			if (exhausted) {
				for (TileGroup tg : grid) {
					if (tg.isComplete()) continue;
					for (int t = 0; t < tg.tiles.length; t++) {
						if (tg.tiles[t].getNum() != 0) continue;
						checkAdjacentUniqueness(tg, t, 0, tg.tiles[t].getPosition().row); 
						finalised = checkAdjacentUniqueness(tg, t, 1, tg.tiles[t].getPosition().col);
					}
				}
				grid.forEach(n -> locateUniqueOccurrence(n));
			}
			
			if (finalised) {
				for (TileGroup tg : grid) {
					if (tg.isComplete()) continue;
					
					for (int t = 0; t < tg.tiles.length; t++) {
						if (tg.tiles[t].getNum() != 0) continue;
						
						Set<Integer> pval = tg.tiles[t].possibleValues;
						
						//check for any similar values in adjacent row
						checkSamePairValueInTileGroup(tg, t, pval, 0);
						
						//check for any similar values in adjacent col
						checkSamePairValueInTileGroup(tg, t, pval, 1);
					}
				}
			}
			//System.out.println(printPossibleValues());
		}
		System.out.println(this);
	}
	
	void checkSamePairValueInTileGroup(TileGroup tg, int t, Set<Integer> pval, int rowOrCol) {
		for (int x = 0; x < 9; x++) {
			if (rowOrCol == 0 
					? tg.tiles[t].getPosition().row != tg.tiles[x].getPosition().row
					: tg.tiles[t].getPosition().col != tg.tiles[x].getPosition().col) continue;
			
			
			if ( x != t && tg.tiles[x].getNum() == 0) {
				if (pval.equals(tg.tiles[x].possibleValues) && pval.size() == 2) {
					removeValuesInTileGroup(pval, tg.tileGroup);
				}
			}
		}
	}
	
	
	boolean checkAdjacentUniqueness(TileGroup tg, int t, int rowOrCol, int rowOrColIndex) {
		boolean finalised = true;
		int rowUpperBound = tg.tileGroup < 4 
				? 2 : (tg.tileGroup > 3 && tg.tileGroup < 7 
						? 5: 8);
		
		int colUpperBound = Arrays.asList(1,4,7).contains(tg.tileGroup) 
				? 6 : (Arrays.asList(2,5,8).contains(tg.tileGroup) 
						? 7 : 8);
		 
		int upperBound = rowOrCol == 0 ? rowUpperBound : colUpperBound;
		
		Tile[] tl = tg.tiles; 
		for (int p : tl[t].possibleValues) {
			if (existInTileGroup(p, rowOrColIndex, t, tl, rowOrCol)) continue;
			
			finalised = rowOrCol == 0 
					? removeFromTileRow(p, rowOrColIndex, tg.tileGroup, upperBound)
							: removeFromTileCol(p, rowOrColIndex, tg.tileGroup, upperBound);
		}
		return finalised;
	}
	
	
	void removeValuesInTileGroup(Set<Integer> pval, int tileGroup) {
		for (Tile t : grid.get(tileGroup-1).tiles) {
			if (t.getNum() != 0 || t.possibleValues.equals(pval)) continue;
			t.possibleValues.removeAll(pval);
			if (t.possibleValues.size() == 1) {
				int val = t.possibleValues.stream().findFirst().get();
				putNumber(val, tileGroup, Arrays.asList(grid.get(tileGroup-1).tiles).indexOf(t)+1, false);
				removeValueInTileGroup(val, tileGroup);
			}
		}
	}
	
	void removeValueInTileGroup(int val, int tileGroup) {
		for (Tile t : grid.get(tileGroup-1).tiles) {
			if (t.possibleValues == null || !t.possibleValues.contains(val)) continue;
			t.possibleValues.remove(val);
		}
	}

	public boolean locateUniqueOccurrence(TileGroup tg) {
		boolean exhausted = true;
		LinkedHashMap<Integer, Integer> countFreq = new LinkedHashMap<>();
		for (Tile t : tg.tiles) {
			if (t.getNum() != 0) continue;
			for (int y : t.possibleValues) {
				countFreq.put(y, (countFreq.containsKey(y) ? countFreq.get(y) : 0) + 1);
			}
		}
		
		for (int key : countFreq.keySet()) {
			if (countFreq.get(key) == 1) {
				for (int t = 0; t < tg.tiles.length; t++) {
					if (tg.tiles[t].getNum() != 0 || !tg.tiles[t].possibleValues.contains(key)) continue;
					exhausted = false;
					removeValueInTileGroup(key, tg.tileGroup);
					putNumber(key, tg.tileGroup, t+1, false);
				}
			}
		}
		return exhausted;
	}
	
	public boolean removeFromTileRow(int num, int row, int tileGroup, int upperBound) {
		boolean finalised = true;
		for (int u = upperBound; u > (upperBound - 3); u--) {
			if (u == (tileGroup-1)) continue;
			for (Tile t : grid.get(u).tiles) {
				if (t.getNum() != 0) continue;
				if (t.getPosition().row == row && t.possibleValues.contains(num)) {
					finalised = false;
					t.possibleValues.remove(num);
				}
			}
		}
		return finalised;
	}
	
	public boolean removeFromTileCol(int num, int col, int tileGroup, int upperBound) {
		boolean finalised = true;
		for (int u = upperBound; u >= (upperBound - 6); u-=3) {
			if (u == (tileGroup-1)) continue;
			for (Tile t : grid.get(u).tiles) {
				if (t.getNum() != 0) continue;
				if (t.getPosition().col == col && t.possibleValues.contains(num)) {
					finalised = false;
					t.possibleValues.remove(num);
				}
			}
		}
		return finalised;
	}
	
	public boolean existInTileGroup(int possibleVal, int rowOrColIndex, int startIndex, Tile[] tiles, int rowOrCol) {
		for (int s = 0; s < 9; s++) {
			if (s == startIndex || tiles[s].getNum() != 0) continue;
			if (tiles[s].possibleValues.contains(possibleVal) && 
					(rowOrCol == 0 ? tiles[s].getPosition().row : tiles[s].getPosition().col) != rowOrColIndex) {
				return true;
			}
		}
		return false;
	}
	
	public void putNumber(int num, int tileGroup, int position, boolean init) {
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
		
		if (init) {
			//initialise value
			TilePosition<Integer, Integer> coord = new TilePosition<>((position-1) /3,(position-1) % 3);
			tg.tiles[position-1] = new Tile(num, coord.row, coord.col, init);
			return;
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
		
		ArrayList<Integer> rowNumbers = checkSurroundings(0, tileGroup, position);
		ArrayList<Integer> colNumbers = checkSurroundings(1, tileGroup, position);

		//check if valid in its own tile group
		//check if setting the number at the position is valid for the given row and col in the entire grid
		if (!tg.isValid(completed) || !(isValid(rowNumbers, completed) && isValid(colNumbers, completed)) ) {
			tile.setNum(originalValue);
			System.err.println("Unable to put in "+num+" in Group "+tileGroup+" at index "+position
					+", as it violates the tile group uniqueness rule.\nReverting to previous value of "+originalValue+"\n\n");
			return;
		}
		
		System.out.println("Successfully placed "+num+" in Group "+tileGroup+" at position "+position+"\n\n");
		
		return;
	}
	
	public ArrayList<Integer> checkSurroundings(int rowOrCol, int tileGroup, int position) {
		int refIndex = rowOrCol == 0 ? (position-1) / 3 : (position-1) % 3;
		int index = rowOrCol == 0 ? (tileGroup-1) % 3 : (tileGroup-1) / 3;
		
		return getAllNumbersInPosition(rowOrCol, refIndex, index, tileGroup, new TileGroup[3]);
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
			for (int t = 0; t < grid.get(i).tiles.length; t++) {
				ArrayList<Integer> rowNumbers = checkSurroundings(0, grid.get(i).tileGroup, t+1);
				ArrayList<Integer> colNumbers = checkSurroundings(1, grid.get(i).tileGroup, t+1);

				//check if valid in its own tile group
				//check if setting the number at the position is valid for the given row and col in the entire grid
				if (!grid.get(i).isValid(true) || !(isValid(rowNumbers, true) && isValid(colNumbers, true)) ) {
					return false;
				}
			}
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
						putNumber(num, tileGroup, position, false);
					}
					else {
						System.err.println("Fatal Error: the input number resolved to -1 and cannot be processed");
					}
				}
				else {
					putNumber(0, tileGroup, position, false);
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
	
	void initialiseValuesByRow(int[][] initValues) {
		for (int i = 0; i < initValues.length; i++) {
			for (int j = 0; j < initValues.length; j++) {
				if (initValues[i][j] == 0) continue;
				int tileGroup = (i/3 + j/3) + (i < 3 ? 1 : (i > 2 && i < 6 ? 3 : 5));
				int initPos = (i%3) * 3 + (j%3) + 1;
				putNumber(initValues[i][j], tileGroup, initPos, true);
			}
		}
	}
	
	void initialiseValuesByTileGroup(int[][] initValues) {
		//Use this if initializing per 9-tile group from top left to right instead of per row of sudoku grid
		for (int i = 0; i < initValues.length; i++) {
			for (int j = 0; j < initValues.length; j++) {
				if (initValues[i][j] == 0) continue;
				putNumber(initValues[i][j], i+1, j+1, true);
			}
		}
	}
	
	
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		int[][] initValues = {
				//Easy
//				{0, 6, 0,  3, 0, 0,  8, 0, 4},
//				{5, 3, 7,  0, 9, 0,  0, 0, 0},
//				{0, 4, 0,  0, 0, 6,  3, 0, 7},
//				
//				{0, 9, 0,  0, 5, 1,  2, 3, 8},
//				{0, 0, 0,  0, 0, 0,  0, 0, 0},
//				{7, 1, 3,  6, 2, 0,  0, 4, 0},
//				
//				{3, 0, 6,  4, 0, 0,  0, 1, 0},
//				{0, 0, 0,  0, 6, 0,  5, 2, 3},
//				{1, 0, 2,  0, 0, 9,  0, 8, 0},
				
				//Medium
//				{0, 3, 0,  7, 0, 8,  0, 0, 0},
//				{0, 0, 0,  0, 0, 0,  2, 0, 0},
//				{0, 0, 4,  0, 0, 0,  0, 7, 1},
//				
//				{6, 0, 1,  0, 0, 0,  0, 0, 2},
//				{7, 0, 0,  0, 0, 5,  0, 0, 0},
//				{0, 0, 0,  0, 0, 0,  8, 5, 0},
//				
//				{0, 0, 3,  0, 8, 0,  0, 0, 0},
//				{9, 2, 0,  0, 6, 0,  0, 0, 5},
//				{1, 0, 0,  0, 0, 0,  9, 0, 6},
				
				//Hard
//				{4, 0, 0,  0, 0, 0,  0, 0, 0},
//				{0, 0, 0,  0, 0, 9,  0, 0, 0},
//				{0, 0, 0,  0, 0, 0,  7, 8, 5},
//				
//				{0, 0, 7,  0, 4, 8,  0, 5, 0},
//				{0, 0, 1,  3, 0, 0,  0, 0, 0},
//				{0, 0, 6,  0, 7, 0,  0, 0, 0},
//				
//				{8, 6, 0,  0, 0, 0,  9, 0, 3},
//				{7, 0, 0,  0, 0, 5,  0, 6, 2},
//				{0, 0, 3,  7, 0, 0,  0, 0, 0},
				
				//Hard - Anti-backtrack
				{0, 0, 0,  0, 0, 0,  0, 0, 0},
				{0, 0, 0,  0, 0, 3,  0, 8, 5},
				{0, 0, 1,  0, 2, 0,  0, 0, 0},
				
				{0, 0, 0,  5, 0, 7,  0, 0, 0},
				{0, 0, 4,  0, 0, 0,  1, 0, 0},
				{0, 9, 0,  0, 0, 0,  0, 0, 0},
				
				{5, 0, 0,  0, 0, 0,  0, 7, 3},
				{0, 0, 2,  0, 1, 0,  0, 0, 0},
				{0, 0, 0,  0, 4, 0,  0, 0, 9},
				
		};
		
		Grid sudoku = new Grid(initValues);
		//sudoku.processInput();
		sudoku.solve();
		long endTime = System.currentTimeMillis();
		long timeElapsed = endTime - startTime;
		System.out.println("Execution time in milliseconds: " + timeElapsed);
	}
	
	public String printPossibleValues() {
		String out = "";
		for (int i = 0; i < 9; i++) {
			out += grid.get(i).tileGroup;
			for (int j = 0; j < 9; j++) {
				Tile t = grid.get(i).tiles[j];
				out += (j % 3 == 0 ? "\n" : "") +(t.possibleValues != null ? t.possibleValues : t.getNum()) + (j == 8 ? "\n\n" : " | ");
			}
		}
		return out;
	}
	
	@Override
	public String toString() {
		String out = "";
		TileGroup[] tgs = new TileGroup[3];
		for (int i = 1; i < 9; i+=3) {
			for (int j = 0; j < 3; j++) {
				ArrayList<Integer> list = getAllNumbersInPosition(0, j, 0, i, tgs);
				out += list.stream().map(Object::toString).collect(Collectors.joining(" | ")) + (j < 2 ? "\n" : "\n\n");
			}
		}
		return out;
	}
}
