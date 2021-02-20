package sudoku;

import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
	Grid sudoku;
	
	public Main() {
		sudoku = new Grid();
		processInput();
	}
	
	void processInput() {
		sudoku.createSudoku();
		
		String setup = "Pick an action (type number, then press enter):\n";
		setup += "1 -> Insert a number\n";
		setup += "2 -> Remove a number\n";
		setup += "3 -> Forfeit\n";
		
		Scanner in = new Scanner(System.in);
		
		//need to loop this
		while (!sudoku.isComplete(sudoku.grid)) {
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
			
			int tileGroup = prompt("Enter the target tile group:", in, sudoku.validAction);
			int position = prompt("Enter the position of the tile in the group:", in, sudoku.validAction);
			
			
			if (tileGroup != -1 && position != -1) {
				if (action == 1) {
					int num = prompt(numQuestion, in, sudoku.validAction);
					if (num != -1) {
						sudoku.putNumber(num, tileGroup, position, false, true);
					}
					else {
						System.err.println("Fatal Error: the input number resolved to -1 and cannot be processed");
					}
				}
				else {
					sudoku.putNumber(0, tileGroup, position, false, true);
				}
				System.out.println(sudoku);
			}
			else {
				System.err.println("Fatal Error: either the tile group or position resolved to -1 and cannot be processed");
			}
		}
		
		in.close();
		System.out.println("Solved!");
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
		new Main();
	}
}
