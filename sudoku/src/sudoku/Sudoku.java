package sudoku;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class Sudoku {
	static ArrayList<ArrayList<Integer>> sudokuGrid = new ArrayList<>();
	static ArrayList<ArrayList<Integer>> boxes = new ArrayList<>();
	static ArrayList<ArrayList<Integer>> indexedBoxes = new ArrayList<>();
	
	static ArrayList<Integer> flattenedGrid = new ArrayList<>();
	static ArrayList<Integer> validNumbers = new ArrayList<>();
	
	static int gridSize;
	static final int EVEN_DIVISOR = 2;
	static final int STANDARD_DIVISOR = 3;
	static int boundary;
	
	public static void makeFourByFourGrid() {
		gridSize = 4;
		
		for (int i = 0; i < gridSize; i++) {
			validNumbers.add(i+1);
		}
		
		sudokuGrid.add(new ArrayList<Integer>(Arrays.asList(1, 0, 0, 0)));
		sudokuGrid.add(new ArrayList<Integer>(Arrays.asList(0, 0, 1, 4)));
		sudokuGrid.add(new ArrayList<Integer>(Arrays.asList(0, 1, 2, 0)));
		sudokuGrid.add(new ArrayList<Integer>(Arrays.asList(0, 3, 0, 0)));
		
//		sudokuGrid.add(new ArrayList<Integer>(Arrays.asList(3, 0, 4, 0)));
//		sudokuGrid.add(new ArrayList<Integer>(Arrays.asList(0, 1, 0, 2)));
//		sudokuGrid.add(new ArrayList<Integer>(Arrays.asList(0, 4, 0, 3)));
//		sudokuGrid.add(new ArrayList<Integer>(Arrays.asList(2, 0, 1, 0)));
		
//		sudokuGrid.add(new ArrayList<Integer>(Arrays.asList(3, 1, 0, 0)));
//		sudokuGrid.add(new ArrayList<Integer>(Arrays.asList(0, 2, 0, 0)));
//		sudokuGrid.add(new ArrayList<Integer>(Arrays.asList(0, 0, 2, 0)));
//		sudokuGrid.add(new ArrayList<Integer>(Arrays.asList(0, 0, 1, 3)));
	}
	
	public static void makeNineByNineGrid() {
		gridSize = 9;
		
		for (int i = 0; i < gridSize; i++) {
			validNumbers.add(i+1);
		}
		
		sudokuGrid.add(new ArrayList<Integer>(Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0, 0)));
		sudokuGrid.add(new ArrayList<Integer>(Arrays.asList(0, 1, 2, 0, 3, 4, 5, 6, 7)));
		sudokuGrid.add(new ArrayList<Integer>(Arrays.asList(0, 3, 4, 5, 0, 6, 1, 8, 2)));
		sudokuGrid.add(new ArrayList<Integer>(Arrays.asList(0, 0, 1, 0, 5, 8, 2, 0, 6)));
		sudokuGrid.add(new ArrayList<Integer>(Arrays.asList(0, 0, 8, 6, 0, 0, 0, 0, 1)));
		sudokuGrid.add(new ArrayList<Integer>(Arrays.asList(0, 2, 0, 0, 0, 7, 0, 5, 0)));
		sudokuGrid.add(new ArrayList<Integer>(Arrays.asList(0, 0, 3, 7, 0, 5, 0, 2, 8)));
		sudokuGrid.add(new ArrayList<Integer>(Arrays.asList(0, 8, 0, 0, 6, 0, 7, 0, 0)));
		sudokuGrid.add(new ArrayList<Integer>(Arrays.asList(2, 0, 7, 0, 8, 3, 6, 1, 5)));
	}
	
	public static boolean isValid(ArrayList<Integer> box, int gridSize) {
		ArrayList<Integer> copy = new ArrayList<>();
		for (Integer number : box) {
			if (!copy.contains(number)) copy.add(number);
			else return false;
		}
		return !copy.contains(0);
	}
	
	public static void flattenGrid() {
		flattenedGrid.clear();
		for (ArrayList<Integer> grid : sudokuGrid) {
			flattenedGrid.addAll(grid);
		}
	}
	
	public static void createBoxes() {
		ArrayList<ArrayList<Integer>> boxes = new ArrayList<>();
		
		for (int a = 0; a < gridSize; a++) {
			if (a % boundary != 0) continue;
			for (int b = gridSize * a; b < ((a * gridSize) + gridSize); b++) {
				if (b % boundary != 0) continue;
				ArrayList<Integer> box = new ArrayList<Integer>(Arrays.asList(new Integer[gridSize]));
				for (int i = 0; i < box.size(); i++) {
					box.set(i, 0);
				}
				for (int i = 0; i < boundary; i++) {
					box.set(i, flattenedGrid.get(b+i));
					box.set(i+boundary, flattenedGrid.get(b+i+gridSize));
				}
				boxes.add(box);
			}
		}
		Sudoku.boxes = boxes;
	}
	
	public static ArrayList<ArrayList<Integer>> createGrid(ArrayList<ArrayList<Integer>> boxes) {
		ArrayList<ArrayList<Integer>> copy  = new ArrayList<>(boxes);
		
		ArrayList<ArrayList<Integer>> result  = new ArrayList<>();
		for(int a = 0; a < gridSize; a += 2) {
			ArrayList<Integer> grid = new ArrayList<Integer>();
			
			while (copy.get(a).size() > 0) {
				for (int b = a; b < a+boundary; b++) {
					for (int c = 0; c < boundary; c++) {
						grid.add(copy.get(b).remove(0));
					}
					
					if (grid.size() < 4) continue;
					result.add(grid);
					grid = new ArrayList<Integer>();
				}
			}
		}
//		System.out.println(result);
		return result;
	}
	
	public static void indexBox() {		
		ArrayList<ArrayList<Integer>> indexedBoxes = new ArrayList<>();
		for (int i = 0; i < gridSize; i++) {
			if (i % boundary != 0) continue;
			ArrayList<Integer> index = new ArrayList<>();
			for (int j = 0; j < gridSize; j++) {
				int ind = j + gridSize * i; 
				index.add(ind);
				index.add(ind + gridSize);
				
				if (index.size() < gridSize) continue;
				indexedBoxes.add(index);
				index = new ArrayList<>();
			}
		}
		Sudoku.indexedBoxes = indexedBoxes;
	}
	
	public static void traverse() {
		for(int x = flattenedGrid.indexOf(0); x < flattenedGrid.size(); x++) {
			ArrayList<Integer> numbers = new ArrayList<>();
			
			if (flattenedGrid.get(x) != 0) continue;
			
			//grab every number in row
			int rowIndex = x/gridSize;
			ArrayList<Integer> row = new ArrayList<>(sudokuGrid.get(rowIndex));
			row.remove((Object) 0);
			numbers.addAll(row);
			
			//grab every number in box
			ArrayList<Integer> box = new ArrayList<>();
			int indexing = -1;
			for(ArrayList<Integer> indexedBox : indexedBoxes) {
				if (indexedBox.contains(x)) {
					indexing = indexedBoxes.indexOf(indexedBox);
					box = new ArrayList<>(boxes.get(indexing));
					break;
				}
			}
			
			//grab every number in the column
			ArrayList<Integer> col = new ArrayList<>();
			col = lookupColumn(col, x);
			numbers.addAll(col);
			
			
			HashSet<Integer> possibility = new HashSet<Integer>();
			for (int num : numbers) {
				if (num != 0) {
					possibility.add(num);
				}
			}
			
			if (possibility.size() == gridSize - 1) {
				for (int num : validNumbers) {
					if (!possibility.contains(num)) {
						ArrayList<Integer> ab = sudokuGrid.get(rowIndex);
						ab.set(x%gridSize, num);
						flattenedGrid.set(x, num);
						createBoxes();
						break;
					}
				}
			}
			
			for(ArrayList<Integer> indexedBox : indexedBoxes) {
				if (indexedBox.contains(x)) {
					indexing = indexedBoxes.indexOf(indexedBox);
					box = new ArrayList<>(boxes.get(indexing));
					break;
				}
			}
			
			if (count(0, box) == 1) {
				for (int num : validNumbers) {
					if (!box.contains(num)) {
						box.set(box.indexOf(0), num);
						boxes.set(indexing, box);
						sudokuGrid = createGrid(boxes);
						flattenGrid();
						createBoxes();
						break;
					}
				}
			}
		}
	}
	
	public static int count(Object obj, ArrayList<Integer> enumerable) {
		int counter = 0;
		for (Integer num : enumerable) {
			counter += num == 0 ? 1 : 0;
		}
		return counter;
	}
	
	public static ArrayList<Integer> lookupColumn(ArrayList<Integer> col, int index) {
		int back = index - gridSize;
		while (back >= 0) {
			col.add(flattenedGrid.get(back));
			back-= gridSize;
		}
		
		int forward = index + gridSize;
		while (forward < flattenedGrid.size()) {
			col.add(flattenedGrid.get(forward));
			forward += gridSize;
		}
		return col;
	}
	
	public static void main(String[] args) {
//		makeFourByFourGrid();
		makeNineByNineGrid();
		boundary = (gridSize % 2 == 0) ? EVEN_DIVISOR : STANDARD_DIVISOR;
		flattenGrid();
		createBoxes();
		indexBox();
		
		System.out.println("Unsolved:");
		System.out.println(sudokuGrid);
		System.out.println(flattenedGrid);
		System.out.println(boxes);
		
		while (flattenedGrid.contains(0)) {
			traverse();
		}
		System.out.println("Solved:");
		System.out.println(sudokuGrid);
		System.out.println(flattenedGrid);
		System.out.println(boxes);
	}
}
