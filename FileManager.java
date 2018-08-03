import java.util.Scanner;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileNotFoundException;

public class FileManager {
  private static Scanner console;

  static {
    console = new Scanner(System.in);
  }

  public static String getFileName() {
    //Scanner console = new Scanner(System.in);
    System.out.print("Name of file -> ");
    String input = console.nextLine();
    //console.close();
    return input;
  }

  public static String getNewFile(String fileString, String insert) {
    if (fileString.indexOf(".") != -1)
      return (fileString.substring(0, fileString.indexOf(".")) + insert + fileString.substring(fileString.indexOf(".")));
    else
      return fileString;
  }

  public static Scanner openInput(String fileString) {
    Scanner fileIn = null;

    try {
      fileIn = new Scanner(new File(fileString));
    }
    catch (FileNotFoundException e) {
      System.out.println("\n\nSorry, but the " + fileString +  " file wasn't found.\n\n");
      System.exit(1);
    }

    System.out.println("Input file opened!");
    return fileIn;
  }


  public static PrintWriter openPrint(String fileString) {
    PrintWriter fileOut = null;

    try {
        fileOut = new PrintWriter(fileString);
    }
    catch (FileNotFoundException e) {
        System.out.println("\n\nSorry, but the " + fileString +  " file wasn't found.\n\n");
        System.exit(1);
    }

    System.out.println("Output file opened!");
    return fileOut;
    }
}
