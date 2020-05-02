import java.util.Scanner;
import java.io.File;

public class CovidTracker {
	public static void main(String[] args) {
		String csvName = "data.csv";

	}
	public static String[] getInfected(String csvName) {
		Scanner sc = new Scanner(new File(csvName));
		sc.useDelimiter(",");
		while (sc.hasNext()) System.out.print(sc.nextLine());
		sc.close();
	}
}
