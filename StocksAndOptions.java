import java.util.Scanner;
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
    StocksAndOptions test = new StocksAndOptions();
    String fileName = "";

    fileName = test.getFileName();
    test.openInput(fileName);
    fileName = test.getNewFile(fileName);
    test.openPrint(fileName);
    test.readAndWrite();
  }

  public String getFileName() {
    Scanner console = new Scanner(System.in);
    System.out.print("Name of file -> ");
    return console.nextLine();
  }

  public String getNewFile(String fileString) {
    if (fileString.indexOf(".") != -1) {
      return (fileString.substring(0, fileString.indexOf(".")) + "_NEW" + fileString.substring(fileString.indexOf(".")));
    }
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

    if(fileIn.hasNext()) {
      cur = fileIn.nextLine();
      edited = cur.substring(0, cur.indexOf(",")+1) + "\"Type\",\"Unerlying\",\"Exp Date\"" + cur.substring(cur.indexOf(","));
      fileOut.println(edited);
    }

    while(fileIn.hasNext()) {
      cur = fileIn.nextLine();

      if(cur.indexOf("CALL")==-1 && cur.indexOf("PUT")==-1) {
        edited = cur.substring(0, cur.indexOf(",")+1) + "\"Stock\",\"" + cur.substring(1, cur.indexOf(",")-1) + "\",\"\"" + cur.substring(cur.indexOf(","));
      }
      else {
        if(cur.indexOf("PUT") !=-1)
          edited = cur.substring(0, cur.indexOf(",")+1) + "\"PUT\",\"";
        else
          edited = cur.substring(0, cur.indexOf(",")+1) + "\"CALL\",\"";

          parsed = cur.substring(getFirstDigInd(cur), cur.indexOf(",")-1);
          date = parsed.substring(2, 4) + "/" + parsed.substring(0,2) + "/" + parsed.substring(4, 6);
          edited += (cur.substring(1, getFirstDigInd(cur)) + "\",\"" + date + "\"" + cur.substring(cur.indexOf(",")));
      }

      fileOut.println(edited);
    }

    fileIn.close();
    fileOut.close();
    System.out.println("Done");
  }

  public int getFirstDigInd(String str) {
    for(int i=0; i<str.length(); i++) {
      if ((str.charAt(i) >= '0') && (str.charAt(i) <= '9'))
        return i;
    }

    return -1;
  }

}
