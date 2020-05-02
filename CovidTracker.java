import java.util.Scanner;
import java.io.*;

public class CovidTracker {
	public static void main(String[] args) throws FileNotFoundException {
		String csvName = "data.csv";
		getInfected(csvName);
	}
	public static void getInfected(String csvName) throws FileNotFoundException {
		Scanner sc = new Scanner(new File(csvName));
		sc.useDelimiter(",");
		while (sc.hasNextLine()) {
			System.out.println(sc.nextLine());
			System.out.println();
		}
		sc.close();
	}
}
