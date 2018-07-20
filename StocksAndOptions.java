import java.util.Scanner;
import java.util.ArrayList;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileNotFoundException;

public class StocksAndOptions
{
  private Scanner fileIn;
  private PrintWriter fileOut;

  public StocksAndOptions() {
    fileIn = null;
    fileOut = null;
    System.out.println("Ammends your excel file for Stocks and Options.");
  }

  public static void main(String[] args) {
    StocksAndOptions sap = new StocksAndOptions();
    String fileName = "";

    fileName = sap.getFileName();
    sap.openInput(fileName);
    fileName = sap.getNewFile(fileName);
    sap.openPrint(fileName);
    sap.readAndWrite();
    sap.openInput(fileName);
    sap.profits();
  }

  public String getFileName() {
    Scanner console = new Scanner(System.in);
    System.out.print("Name of file -> ");
    return console.nextLine();
  }

  public String getNewFile(String fileString) {
    if (fileString.indexOf(".") != -1)
      return (fileString.substring(0, fileString.indexOf(".")) + "_NEW" + fileString.substring(fileString.indexOf(".")));
    else
      return fileString;
  }

  public void openInput(String fileString) {
    try {
      fileIn = new Scanner(new File(fileString));
    }
    catch (FileNotFoundException e) {
      System.out.println("\n\nSorry, but the " + fileString +  " file was found.\n\n");
      System.exit(1);
    }

    System.out.println("Input file opened!");
  }

  public void openPrint(String fileString) {
		try {
			fileOut = new PrintWriter(fileString);
		}
		catch (FileNotFoundException e) {
			System.out.println("\n\nSorry, but the " + fileString +  " file was found.\n\n");
			System.exit(1);
		}

    System.out.println("Output file opened!");
  }

  public void readAndWrite() {
    String cur = "", edited = "", parsed = "", date = "";
    boolean started = false;

    while(fileIn.hasNext() && !started) {
      cur = fileIn.nextLine();

      if(cur.indexOf("Symbol")!=-1 && cur.indexOf("Security Description")!=-1) {
        edited = cur.substring(0, cur.indexOf(",")+1) + "\"Type\",\"Unerlying\",\"Exp Date\"" + cur.substring(cur.indexOf(","));
        fileOut.println(edited);
        started = true;
      }
    }

    while(fileIn.hasNext()) {
      cur = fileIn.nextLine();

      if(cur.indexOf("CALL")==-1 && cur.indexOf("PUT")==-1) {
        edited = cur.substring(0, cur.indexOf(",")+1) + "\"Stock\",\"" + cur.substring(1, cur.indexOf(",")-1) + "\",\"\"" + cur.substring(cur.indexOf(","));
      }
      else {
        if(cur.indexOf("PUT") !=-1)
          edited = cur.substring(0, cur.indexOf(",")+1) + "\"Put\",\"";
        else
          edited = cur.substring(0, cur.indexOf(",")+1) + "\"Call\",\"";

          parsed = cur.substring(getFirstDigInd(cur), cur.indexOf(",")-1);
          date = parsed.substring(2, 4) + "/" + parsed.substring(0,2) + "/" + parsed.substring(4, 6);
          edited += (cur.substring(1, getFirstDigInd(cur)) + "\",\"" + date + "\"" + cur.substring(cur.indexOf(",")));
      }

      fileOut.println(edited);
    }

    fileIn.close();
    fileOut.close();
    System.out.println("Done Writing in Output File.");
  }

  public int getFirstDigInd(String str) {
    for(int i=0; i<str.length(); i++) {
      if ((str.charAt(i) >= '0') && (str.charAt(i) <= '9'))
        return i;
    }

    return -1;
  }

  public void profits() {
      String cur = "", parsed = "";
      int arrayIndex = -1, strIndex = -1;
      double profits = -1.0;
      boolean started = false, isShortTerm = false;
      ArrayList<String> name = new ArrayList<String>();
      ArrayList<Double> shortTerm = new ArrayList<Double>(), longTerm = new ArrayList<Double>();

      while(fileIn.hasNext() && !started) {
        cur = fileIn.nextLine();

        if(cur.indexOf("Symbol")!=-1 && cur.indexOf("Security Description")!=-1)
          started = true;
      }

      while(fileIn.hasNext()) {
        cur = fileIn.nextLine();
        strIndex = cur.indexOf("\"", jumpCommas(cur, 2)) + 1;
        parsed = cur.substring(strIndex, cur.indexOf("\"", strIndex));
        arrayIndex = name.indexOf(parsed);

        if(arrayIndex == -1) {
          name.add(parsed);
          arrayIndex = name.indexOf(parsed);
        }

        strIndex = cur.indexOf("\"", jumpCommas(cur, 6)) +1;
        parsed = cur.substring(strIndex, cur.indexOf("\"", strIndex));

        if(!parsed.isEmpty()) {
          profits = Double.parseDouble(parsed);
          addToArray(shortTerm, profits, arrayIndex);
        }

        strIndex = cur.indexOf("\"", jumpCommas(cur, 7)) +1;
        parsed = cur.substring(strIndex, cur.indexOf("\"", strIndex));

        if(!parsed.isEmpty()) {
          profits = Double.parseDouble(parsed);
          addToArray(longTerm, profits, arrayIndex);
        }
      }

      System.out.println("\nShort Term Profits:");
      System.out.printf("%10s          %-10s%n", "Ticker", "Profit");

      for(int i = 0; i<shortTerm.size(); i++) 
        System.out.printf("%10s          %-+,10.2f%n", name.get(i), shortTerm.get(i));

      System.out.println("\nLong Term Profits:");
      System.out.printf("%10s          %-10s%n", "Ticker", "Profit");

      for(int i = 0; i<longTerm.size(); i++)
          System.out.printf("%10s          %-+,10.2f%n", name.get(i), longTerm.get(i));

      System.out.println("\n");
      fileIn.close();
  }

  public int jumpCommas(String str, int numCommas) {
    int index = -1, count = 0;

    while(str.indexOf(",", index+1)!=-1 && count<numCommas) {
      index = str.indexOf(",", index+1);
      count++;
    }

    return index;
  }

  public void addToArray(ArrayList<Double> arr, double element, int index) {
    while(index >= arr.size())
      arr.add(0.0);

    arr.set(index, element + arr.get(index));
  }

}
