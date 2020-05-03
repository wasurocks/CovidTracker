import java.util.Scanner;
import java.io.*;

public class Proj2190101_1_5 {
  public static void main(String[] args) throws FileNotFoundException, IOException {
    String csvName = "data.csv";
    CountryInfected[] countries = getInfected(csvName);
    double[][] vals = new double[6][countries[0].getInfected().length];
    int k = 0;

    for (int i = 0; i < countries.length; i++) {
      if (countries[i].getCountry().equals("Germany")) {
        vals[k++] = getSCurve(countries[i].getInfected(), 90, new double[] { 0, 71, 0.148, 160110 },
            new double[] { 0, 71, 0.15, 160120 });
        vals[k++] = getDoNothingCurve(countries[i].getInfected(), 90);
      }
    }
    for (int i = 0; i < countries.length; i++) {
      if (countries[i].getCountry().equals("Panama")) {
        vals[k++] = getSCurve(countries[i].getInfected(), 90, new double[] { 0, 84, 0.119, 7292 },
            new double[] { 0, 100, 0.2, 7293 });
        vals[k++] = getDoNothingCurve(countries[i].getInfected(), 90);
      }
    }
    for (int i = 0; i < countries.length; i++) {
      if (countries[i].getCountry().equals("Thailand")) {
        vals[k++] = getSCurve(countries[i].getInfected(), 90, new double[] { 2.9, 67, 0.17, 2870 },
            new double[] { 3.0, 69, 0.19, 2880 });
        vals[k++] = getDoNothingCurve(countries[i].getInfected(), 90);
      }
    }

    csvName = "1_5.csv";
    BufferedWriter br = new BufferedWriter(new FileWriter(csvName));

    for (int i = 0; i < 6; i++) {
      for (int j = 0; j < 10; j++) {
        br.write(Double.toString(vals[i][j]));
        if (j < 9)
          br.write(",");
      }
      br.newLine();
    }
    br.close();

  }

  // MODEL FUNCTIONS

  // Generate S-curve prediction
  public static double[] getSCurve(int[] pastData, int numFutureDays, double[] paramLowerBounds,
      double[] paramUpperBounds) {

    // Train
    double[] fitParams = slice(paramLowerBounds, 0, paramLowerBounds.length);

    double minError = MSE(pastData, paramLowerBounds);

    double error = 0;
    for (double i = paramLowerBounds[0]; i <= paramUpperBounds[0]; i += 0.1) {
      for (double j = paramLowerBounds[1]; j <= paramUpperBounds[1]; j += 1) {
        for (double k = paramLowerBounds[2]; k <= paramUpperBounds[2]; k += 0.01) {
          for (double l = paramLowerBounds[3]; l < paramUpperBounds[3]; l += 1) {
            error = MSE(pastData, new double[] { i, j, k, l });
            if (error < minError) {
              minError = error;
              for (int m = 0; m < 4; m++) {
                switch (m) {
                  case 0:
                    fitParams[m] = i;
                    break;
                  case 1:
                    fitParams[m] = j;
                    break;
                  case 2:
                    fitParams[m] = k;
                    break;
                  case 3:
                    fitParams[m] = l;
                    break;
                }
              }
            }
          }
        }
      }
    }

    // Predict
    double[] predicted = new double[numFutureDays + 1];
    double S = fitParams[0];
    double D = fitParams[1];
    double L = fitParams[2];
    double M = fitParams[3];

    for (int i = 1; i <= numFutureDays; i++)
      predicted[i - 1] = (S + (M / (1.0 + Math.exp(-L * ((pastData.length + i) - D)))));

    // Summarize parameters
    System.out.print("The fitted S-curve model has S=[");
    System.out.print(fitParams[0]);
    System.out.print("], D=[");
    System.out.print(fitParams[1]);
    System.out.print("], L=[");
    System.out.print(fitParams[2]);
    System.out.print("], M=[");
    System.out.print(fitParams[3]);
    System.out.print("], with the first projected day being d=[");
    System.out.print(pastData.length + 1);
    System.out.print("].");
    System.out.println();

    return predicted;
  }

  // Find MSE
  public static double MSE(int[] daysData, double[] params) {

    double sum = 0;
    for (int i = 0; i < daysData.length; i++)
      sum += Math.pow((sigmoid(i, params) - daysData[i]), 2);
    return sum / daysData.length;
  }

  // Calculate sigmoid
  public static double sigmoid(int day, double[] params) {
    return params[0] + (params[3] / (1 + Math.exp((-1 * params[2]) * (day - params[1]))));
  }

  // Generate do-nothing prediction
  public static double[] getDoNothingCurve(int[] pastData, int numFutureDays) {

    // Initializing values
    double[] predicted = new double[4 + numFutureDays];

    // Filling predictions
    for (int i = 0; i < predicted.length; i++) {
      if (i < 4)
        predicted[i] = pastData[pastData.length - 4 + i];
      else
        predicted[i] = predicted[i - 1] * (((predicted[i - 1] / predicted[i - 2])
            + (predicted[i - 2] / predicted[i - 3]) + (predicted[i - 3] / predicted[i - 4])) / 3);
    }

    // Returning slice of predictions with only generated values
    return slice(predicted, 4, predicted.length);
  }

  // PROCESSING FUNCTIONS

  // Function to get the infected array
  public static CountryInfected[] getInfected(String csvName) throws FileNotFoundException {
    Scanner sc = new Scanner(new File(csvName));
    // Columns are dynamic, based on data size
    int columns = sc.nextLine().split(",").length;
    int rows = 266, i = 0;

    // Parsing CSV into 2D array
    String[][] lines = new String[rows][columns];

    while (sc.hasNextLine() && i < lines.length) {
      lines[i] = sc.nextLine().split(",");
      i++;
    }
    sc.close();

    // Process the 2D array from specifications
    String[][] filtered = filterCaseNumbers(aggregateCountry(lines));
    changeNames(filtered);

    // Obtain array to return
    CountryInfected[] countries = new CountryInfected[filtered.length];

    for (i = 0; i < countries.length; i++) {
      countries[i] = new CountryInfected(filtered[i][1], intArrayOf(slice(filtered[i], 4, filtered[0].length)));
    }

    return countries;
  }

  // Function to process repeated values data array
  public static String[][] aggregateCountry(String[][] data) {
    // Inializing values based from data size
    String[] filtered = new String[data.length];
    String[][] filteredCountries = new String[data.length][data[0].length];
    int k = 0;

    // Aggregating repeat countries
    for (int i = 0, res = 0; i < data.length; i++) {
      res = checkInArray(data[i][1], filtered, k);
      if (res < 0) {
        filtered[k] = data[i][1];
        filteredCountries[k] = data[i];
        k++;
      } else {
        for (int j = 4; j < data[0].length; j++) {
          filteredCountries[res][j] = String
              .valueOf(Integer.parseInt(filteredCountries[res][j]) + Integer.parseInt(data[i][j]));
        }
      }
    }

    // Decreasing array size
    String[][] filteredCountriesSized = new String[k][data[0].length - 1];
    for (int i = 0; i < k; i++)
      filteredCountriesSized[i] = filteredCountries[i];
    return filteredCountriesSized;
  }

  // Function to remove countries with confirmed cases < 100
  public static String[][] filterCaseNumbers(String[][] data) {
    // Inializing values based from data size
    String[][] filteredCountries = new String[data.length + 1][data[0].length];
    int k = 0;

    // Filtering only countries matching condition
    for (int i = 0; i < data.length; i++) {
      if (Integer.parseInt(data[i][data[0].length - 1]) > 100) {
        filteredCountries[k] = data[i];
        k++;
      }
    }

    // Decreasing array size
    String[][] filteredCountriesSized = new String[k][data[0].length];
    for (int i = 0; i < k; i++) {
      filteredCountriesSized[i] = filteredCountries[i];
    }

    return filteredCountriesSized;

  }

  // Function to replace invalid names
  public static void changeNames(String[][] data) {
    String[] toChange = new String[] { "Holy See", "Korea South", "Taiwan*" };
    String[] changeTo = new String[] { "Vatican City", "South Korea", "Taiwan" };
    for (int i = 0, pos; i < data.length; i++) {
      pos = checkInArray(data[i][1], toChange, toChange.length);
      if (pos >= 0)
        data[i][1] = changeTo[pos];
    }
  }

  // UTILITY FUNCTIONS

  // Function to check if a string exists in an array. If it does, return its
  // position
  public static int checkInArray(String str, String[] arr, int k) {
    for (int i = 0; i < k; i++) {
      if ((arr[i] != null) && (arr[i].equals(str)))
        return i;
    }
    return -1;
  }

  // Function to slice array of strings (not including end index)
  public static String[] slice(String[] arr, int start, int end) {
    String[] sliced = new String[end - start];
    for (int i = start, k = 0; i < end; i++, k++) {
      sliced[k] = arr[i];
    }
    return sliced;
  }

  // Function to slice array of doubles (not including end index)
  public static double[] slice(double[] arr, int start, int end) {
    double[] sliced = new double[end - start];
    for (int i = start, k = 0; i < end; i++, k++) {
      sliced[k] = arr[i];
    }
    return sliced;
  }

  // Function to convert array of string into array of int
  public static int[] intArrayOf(String[] arr) {
    int[] converted = new int[arr.length];
    for (int i = 0; i < arr.length; i++)
      converted[i] = Integer.parseInt(arr[i]);
    return converted;
  }

  // Function to sum array of doubles
  public static double sum(double[] arr) {
    double sum = 0;
    for (int i = 0; i < arr.length; i++)
      sum += arr[i];
    return sum;
  }
}