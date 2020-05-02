import java.util.Scanner;
import java.io.*;

public class CovidTracker {
	public static void main(String[] args) throws FileNotFoundException {
		String csvName = "data.csv";
		getInfected(csvName);
	}
	public static void getInfected(String csvName) throws FileNotFoundException {
		Scanner sc = new Scanner(new File(csvName));

		int i = 0;

		while (sc.hasNextLine()) i++;
		sc.close();

		sc = new Scanner(new File(csvName));

		String[][] lines = new String[]

		while (sc.hasNextLine()) {
			String[] line = sc.nextLine().split(",");
			i++;
		}
		sc.close();




/*
		for(i = 0; i < lines.length - 1; i++) {
			for(j = 0; j < lines[0].length - 1; j++) System.out.print(lines[i][j]);
			System.out.println();
		}*/

	}
	public static void AggregrateCountry(String[][] data) {

	}
}
