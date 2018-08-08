import java.util.Scanner;
import java.util.ArrayList;
import java.io.PrintWriter;

public class StocksAndOptions {
  private String fileName;
  private Scanner fileIn;
  private PrintWriter fileOut;
  ArrayList<String> name;
  ArrayList<Double> shortTerm, longTerm, returnOfCapital, dividendReceived, unrealizedGains, total;
  ArrayList<Integer> numTrades, numDistributions;

  public static final String ANSI_RESET = "\u001B[0m";
  public static final String ANSI_RED = "\u001B[31m";
  public static final String ANSI_GREEN = "\u001B[32m";

  public StocksAndOptions() {
    fileName = "";
    fileIn = null;
    fileOut = null;
    name = new ArrayList<String>();
    shortTerm = new ArrayList<Double>();
    longTerm = new ArrayList<Double>();
    returnOfCapital = new ArrayList<Double>();
    dividendReceived = new ArrayList<Double>();
    unrealizedGains = new ArrayList<Double>();
    total = new ArrayList<Double>();
    numTrades = new ArrayList<Integer>();
    numDistributions = new ArrayList<Integer>();
  }

  public static void main(String[] args) {
    StocksAndOptions sap = new StocksAndOptions();
    sap.process();
  }

  public void process() {
    System.out.println("\n\n\n");

    System.out.println("Ammends your excel file for Stocks and Options.\n");
    fileName = FileManager.getFileName();
    System.out.println();
    processTrades();
    calculateProfits();
    ArrayListManager.sortProfitArrayLists(name, shortTerm, longTerm, numTrades, total);
    printProfitTable();
    SpreadsheetManager.printProfitBar(name, total);
    printProfitFile();

    System.out.println();
    fileName = FileManager.getFileName();
    System.out.println();
    processAllActivity();
    profitsAllActivity();
    ArrayListManager.sortProfitArrayLists(name, shortTerm, longTerm, numTrades, returnOfCapital, dividendReceived, numDistributions, total);
    printProfitsAllActivity();
    SpreadsheetManager.printProfitBar(name, total);

    fileName = FileManager.getFileName();
    System.out.println();
    processPosition();
    profitsPosition();
    ArrayListManager.sortProfitArrayLists(name, shortTerm, longTerm, numTrades, returnOfCapital, dividendReceived, numDistributions, unrealizedGains, total);
    printPositions();
    SpreadsheetManager.printProfitBar(name, total);
    printFinalFile();

    System.out.println("\n\n\n");
  }

  public void processTrades() {
    String cur = "", edited = "", parsed = "", date = "";
    boolean started = false;

    fileIn =  FileManager.openInput(fileName);
    fileOut =  FileManager.openPrint(FileManager.getNewFile(fileName, "_NEW"));

    while(fileIn.hasNext() && !started) {
      cur = fileIn.nextLine();
      cur = SpreadsheetManager.removeQM(cur);

      if(cur.indexOf("Symbol")!=-1 && cur.indexOf("Security Description")!=-1) {
        edited = cur.substring(0, cur.indexOf(",")+1) + "Type,Unerlying,Exp Date" + cur.substring(cur.indexOf(","));
        fileOut.println(edited);
        started = true;
      }
    }

    while(fileIn.hasNext()) {
      cur = fileIn.nextLine();
      cur = SpreadsheetManager.removeQM(cur);

      if(cur.indexOf("CALL")==-1 && cur.indexOf("PUT")==-1) {
        edited = cur.substring(0, cur.indexOf(",")+1) + "Stock," + cur.substring(0, cur.indexOf(",")+1) + cur.substring(cur.indexOf(","));
      }
      else {
        if(cur.indexOf("PUT") !=-1)
          edited = cur.substring(0, cur.indexOf(",")+1) + "Put,";
        else
          edited = cur.substring(0, cur.indexOf(",")+1) + "Call,";

          parsed = cur.substring(SpreadsheetManager.getFirstDigInd(cur), cur.indexOf(","));
          date = parsed.substring(2, 4) + "/" + parsed.substring(0,2) + "/" + parsed.substring(4, 6);
          edited += (cur.substring(0, SpreadsheetManager.getFirstDigInd(cur)) + "," + date + cur.substring(cur.indexOf(",")));
      }

      fileOut.println(edited);
    }

    fileIn.close();
    fileOut.close();
    System.out.println("Done Writing in Output File.");
  }

  public void calculateProfits() {
      String cur = "", parsed = "";
      int arrayIndex = -1, strIndex = -1;
      double profits = -1.0;
      boolean started = false;
      fileIn =  FileManager.openInput(FileManager.getNewFile(fileName, "_NEW"));

      while(fileIn.hasNext() && !started) {
        cur = fileIn.nextLine();
        cur = SpreadsheetManager.removeQM(cur);

        if(cur.indexOf("Symbol")!=-1 && cur.indexOf("Security Description")!=-1)
          started = true;
      }

      while(fileIn.hasNext()) {
        cur = fileIn.nextLine();
        cur = SpreadsheetManager.removeQM(cur);
        strIndex = cur.indexOf(",", SpreadsheetManager.jumpCommas(cur, 2)) + 1;
        parsed = cur.substring(strIndex, cur.indexOf(",", strIndex));
        arrayIndex = name.indexOf(parsed);

        if(arrayIndex == -1) {
          name.add(parsed);
          arrayIndex = name.indexOf(parsed);
          ArrayListManager.addToArray(shortTerm, 0.0, arrayIndex);
          ArrayListManager.addToArray(longTerm, 0.0, arrayIndex);
          ArrayListManager.addToArray(total, 0.0, arrayIndex);
          ArrayListManager.addToArray(numTrades, 0, arrayIndex);
        }

        strIndex = cur.indexOf(",", SpreadsheetManager.jumpCommas(cur, 6)) +1;
        parsed = cur.substring(strIndex, cur.indexOf(",", strIndex));

        if(!parsed.isEmpty()) {
          profits = Double.parseDouble(parsed);
          ArrayListManager.addToArray(shortTerm, profits, arrayIndex);
          ArrayListManager.addToArray(total, profits, arrayIndex);
          ArrayListManager.addToArray(numTrades, 1, arrayIndex);
        }

        strIndex = cur.indexOf(",", SpreadsheetManager.jumpCommas(cur, 7)) +1;
        parsed = cur.substring(strIndex, cur.indexOf(",", strIndex));

        if(!parsed.isEmpty()) {
          profits = Double.parseDouble(parsed);
          ArrayListManager.addToArray(longTerm, profits, arrayIndex);
          ArrayListManager.addToArray(total, profits, arrayIndex);
          ArrayListManager.addToArray(numTrades, 1, arrayIndex);
        }
      }
  }

  public void printProfitTable() {
    System.out.println("\nProfits:");
    System.out.printf("%-10s          %-15s          %-15s          %-20s          %-20s%n", "Ticker", "Short Term Profit", "Long Term Profit", "Number of Trades", "Total");

    for(int i = 0; i<name.size(); i++) {
        System.out.printf("%-10s          ", name.get(i));

        if(shortTerm.get(i) > 0)
          System.out.printf(ANSI_GREEN + "%-+,15.2f            " + ANSI_RESET, shortTerm.get(i));
        else if(shortTerm.get(i) == 0)
          System.out.printf("%-+,15.2f            ", shortTerm.get(i));
        else
          System.out.printf(ANSI_RED + "%-+,15.2f            " + ANSI_RESET, shortTerm.get(i));

        if(longTerm.get(i) > 0)
          System.out.printf(ANSI_GREEN + "%-+,15.2f           " + ANSI_RESET, longTerm.get(i));
        else if(longTerm.get(i) == 0)
          System.out.printf("%-+,15.2f           ", longTerm.get(i));
        else
          System.out.printf(ANSI_RED + "%-+,15.2f           " + ANSI_RESET, longTerm.get(i));

        System.out.printf("%-20d          ", numTrades.get(i));

        if(total.get(i) > 0)
          System.out.printf(ANSI_GREEN + "%-+,20.2f           " + ANSI_RESET, total.get(i));
        else if(total.get(i) == 0)
          System.out.printf("%-+,20.2f           ", total.get(i));
        else
          System.out.printf(ANSI_RED + "%-+,20.2f           " + ANSI_RESET, total.get(i));

        System.out.println();
    }

    System.out.println();
  }

  public void printProfitFile() {
      fileOut = FileManager.openPrint(FileManager.getNewFile(fileName, "_P"));

      String cur = "Symbol,Short Term Profit,Long Term Profit,Number of Trades";
      fileOut.println(cur);

      for(int i=0; i<name.size(); i++) {
        cur = name.get(i) + "," + shortTerm.get(i) + "," + longTerm.get(i) + "," + numTrades.get(i);
        fileOut.println(cur);
      }

      fileOut.close();
  }

  public void processAllActivity() {
    String cur = "", edited = "", parsed = "";
    boolean started = false;

    fileIn =  FileManager.openInput(fileName);
    fileOut =  FileManager.openPrint(FileManager.getNewFile(fileName, "_NEW"));

    while(fileIn.hasNext() && !started) {
      cur = fileIn.nextLine();
      cur = SpreadsheetManager.removeQM(cur);

      if(cur.indexOf("Symbol")!=-1 && cur.indexOf("Description")!=-1) {
        edited = cur.substring(0, SpreadsheetManager.jumpCommas(cur, 3)+1) + "Underlying" + cur.substring(SpreadsheetManager.jumpCommas(cur, 3));
        fileOut.println(edited);
        started = true;
      }
    }

    while(fileIn.hasNext()) {
      cur = fileIn.nextLine();
      cur = SpreadsheetManager.removeQM(cur);
      edited = cur.substring(0, SpreadsheetManager.jumpCommas(cur, 3)+1);
      parsed = cur.substring(SpreadsheetManager.jumpCommas(cur, 2)+1, SpreadsheetManager.jumpCommas(cur, 3));
      edited += (parsed.substring(0, SpreadsheetManager.getFirstDigInd(parsed)) + cur.substring(SpreadsheetManager.jumpCommas(cur, 3)));
      fileOut.println(edited);
    }

    fileIn.close();
    fileOut.close();
    System.out.println("Done Writing in Output File.\n");
  }

  public void profitsAllActivity() {
    String cur = "", ticker = "", value = "";
    int arrayIndex = -1;
    boolean started = false;
    fileIn = FileManager.openInput(FileManager.getNewFile(fileName, "_NEW"));

    while(fileIn.hasNext() && !started) {
      cur = fileIn.nextLine();
      cur = SpreadsheetManager.removeQM(cur);

      if(cur.indexOf("Symbol")!=-1 && cur.indexOf("Description")!=-1 && cur.indexOf("Underlying")!=-1) {
        started = true;
        ArrayListManager.addToArray(returnOfCapital, 0.0, name.size()-1);
        ArrayListManager.addToArray(dividendReceived, 0.0, name.size()-1);
        ArrayListManager.addToArray(numDistributions, 0, name.size()-1);
      }
    }

    while(fileIn.hasNext() && started) {
      cur = fileIn.nextLine();
      cur = SpreadsheetManager.removeQM(cur);
      ticker = cur.substring(SpreadsheetManager.jumpCommas(cur, 3)+1, SpreadsheetManager.jumpCommas(cur, 4));
      arrayIndex = name.indexOf(ticker);

      if(arrayIndex == -1) {
        name.add(ticker);
        arrayIndex = name.indexOf(ticker);
        ArrayListManager.addToArray(shortTerm, 0.0, arrayIndex);
        ArrayListManager.addToArray(longTerm, 0.0, arrayIndex);
        ArrayListManager.addToArray(numTrades, 0, arrayIndex);
        ArrayListManager.addToArray(returnOfCapital, 0.0, arrayIndex);
        ArrayListManager.addToArray(dividendReceived, 0.0, arrayIndex);
        ArrayListManager.addToArray(numDistributions, 0, name.size()-1);
        ArrayListManager.addToArray(total, 0.0, arrayIndex);
      }

      if(cur.indexOf("RETURN OF CAPITAL") != -1) {
        value = cur.substring(SpreadsheetManager.jumpCommas(cur, 6)+2, SpreadsheetManager.jumpCommas(cur, 7));
        ArrayListManager.addToArray(returnOfCapital, Double.parseDouble(value), arrayIndex);
        ArrayListManager.addToArray(total, Double.parseDouble(value), arrayIndex);
        ArrayListManager.addToArray(numDistributions, 1, name.size()-1);
      }
      else if(cur.indexOf("DIVIDEND RECEIVED") != -1) {
        value = cur.substring(SpreadsheetManager.jumpCommas(cur, 6)+2, SpreadsheetManager.jumpCommas(cur, 7));
        ArrayListManager.addToArray(dividendReceived, Double.parseDouble(value), arrayIndex);
        ArrayListManager.addToArray(total, Double.parseDouble(value), arrayIndex);
        ArrayListManager.addToArray(numDistributions, 1, name.size()-1);
      }
    }

    fileIn.close();
  }

  public void printProfitsAllActivity() {
    System.out.println("\nAll Activities:");
    System.out.printf("%-10s          %-15s          %-15s          %-20s          %-20s            %-20s         %15s          %-10s%n", "Ticker", "Short Term Profit", "Long Term Profit", "Number of Trades", "Retrurn of Capital", "Dividend Received", "Distributions", "Total");

    for(int i = 0; i<name.size(); i++) {
        System.out.printf("%-10s          ", name.get(i));

        if(shortTerm.get(i) > 0)
          System.out.printf(ANSI_GREEN + "%-+,15.2f            " + ANSI_RESET, shortTerm.get(i));
        else if(shortTerm.get(i) == 0)
          System.out.printf("%-+,15.2f            ", shortTerm.get(i));
        else
          System.out.printf(ANSI_RED + "%-+,15.2f            " + ANSI_RESET, shortTerm.get(i));

        if(longTerm.get(i) > 0)
          System.out.printf(ANSI_GREEN + "%-+,15.2f           " + ANSI_RESET, longTerm.get(i));
        else if(longTerm.get(i) == 0)
          System.out.printf("%-+,15.2f           ", longTerm.get(i));
        else
          System.out.printf(ANSI_RED + "%-+,15.2f           " + ANSI_RESET, longTerm.get(i));

        System.out.printf("%-20d          ", numTrades.get(i));

        if(returnOfCapital.get(i) > 0)
          System.out.printf(ANSI_GREEN + "%-+,20.2f            " + ANSI_RESET, returnOfCapital.get(i));
        else if(returnOfCapital.get(i) == 0)
          System.out.printf("%-+,20.2f            ", returnOfCapital.get(i));
        else
          System.out.printf(ANSI_RED + "%-+,20.2f            " + ANSI_RESET, returnOfCapital.get(i));

        if(dividendReceived.get(i) > 0)
          System.out.printf(ANSI_GREEN + "%-+,20.2f           " + ANSI_RESET, dividendReceived.get(i));
        else if(dividendReceived.get(i) == 0)
          System.out.printf("%-+,20.2f           ", dividendReceived.get(i));
        else
          System.out.printf(ANSI_RED + "%-+,20.2f           " + ANSI_RESET, dividendReceived.get(i));

        System.out.printf("%-15d", numDistributions.get(i));

        if(total.get(i) > 0)
          System.out.printf(ANSI_GREEN + "        %-+,10.2f%n" + ANSI_RESET, total.get(i));
        else if(total.get(i) == 0)
          System.out.printf("        %-+,10.2f%n", total.get(i));
        else
          System.out.printf(ANSI_RED + "        %-+,10.2f%n" + ANSI_RESET, total.get(i));

    }

    System.out.println();
  }

  public void processPosition() {
    String cur = "", parsed = "", edited = "";
    boolean started = false;

    fileIn =  FileManager.openInput(fileName);
    fileOut =  FileManager.openPrint(FileManager.getNewFile(fileName, "_NEW"));

    while(fileIn.hasNext() && !started) {
      cur = fileIn.nextLine();
      cur = SpreadsheetManager.removeQM(cur);

      if(cur.indexOf("Symbol")!=-1 && cur.indexOf("G/L")!=-1) {
        edited = cur.substring(0, cur.indexOf(",")+1) + "Unerlying" + cur.substring(cur.indexOf(","));
        fileOut.println(edited);
        started = true;
      }
    }

    while(fileIn.hasNext()) {
      cur = fileIn.nextLine();
      cur = SpreadsheetManager.removeQM(cur);

      parsed = cur.substring(0, cur.indexOf(","));
      edited = cur.substring(0, cur.indexOf(",")+1) + parsed.substring(0, SpreadsheetManager.getFirstDigInd(parsed)) + cur.substring(cur.indexOf(","));

      fileOut.println(edited);
    }

    fileIn.close();
    fileOut.close();
    System.out.println("Done Writing in Output File.");
  }


  public void profitsPosition() {
    String cur = "", parsed = "";
    int arrayIndex = -1, strIndex = -1;
    double profits = -1.0;
    boolean started = false;
    fileIn =  FileManager.openInput(FileManager.getNewFile(fileName, "_NEW"));

    while(fileIn.hasNext() && !started) {
      cur = fileIn.nextLine();
      cur = SpreadsheetManager.removeQM(cur);

      if(cur.indexOf("Symbol")!=-1 && cur.indexOf("Unerlying")!=-1 && cur.indexOf("G/L")!=-1) {
        started = true;
        ArrayListManager.addToArray(unrealizedGains, 0.0, name.size()-1);
      }
    }

    while(fileIn.hasNext()) {
      cur = fileIn.nextLine();
      cur = SpreadsheetManager.removeQM(cur);
      strIndex = cur.indexOf(",", SpreadsheetManager.jumpCommas(cur, 1)) + 1;
      parsed = cur.substring(strIndex, cur.indexOf(",", strIndex));
      arrayIndex = name.indexOf(parsed);

      if(arrayIndex == -1) {
        name.add(parsed);
        arrayIndex = name.indexOf(parsed);
        ArrayListManager.addToArray(shortTerm, 0.0, arrayIndex);
        ArrayListManager.addToArray(longTerm, 0.0, arrayIndex);
        ArrayListManager.addToArray(returnOfCapital, 0.0, arrayIndex);
        ArrayListManager.addToArray(dividendReceived, 0.0, arrayIndex);
        ArrayListManager.addToArray(unrealizedGains, 0.0, arrayIndex);
        ArrayListManager.addToArray(total, 0.0, arrayIndex);
        ArrayListManager.addToArray(numTrades, 0, arrayIndex);
        ArrayListManager.addToArray(numDistributions, 0, arrayIndex);
      }

      strIndex = cur.indexOf(",", SpreadsheetManager.jumpCommas(cur, 15)) +1;
      parsed = cur.substring(strIndex, cur.indexOf(",", strIndex));

      if(!parsed.isEmpty() && parsed.indexOf("--")==-1) {
        profits = Double.parseDouble(parsed);
        ArrayListManager.addToArray(unrealizedGains, profits, arrayIndex);
        ArrayListManager.addToArray(total, profits, arrayIndex);
        //ArrayListManager.addToArray(numTrades, 1, arrayIndex);
        //System.out.println(name.get(arrayIndex) + "     " + unrealizedGains.get(arrayIndex));
      }
    }
  }

  public void printPositions() {
    System.out.println("\nAll Activities Including Positions:");
    System.out.printf("%-10s          %-15s          %-15s          %-20s          %-20s            %-20s         %15s          %-10s     %-10s%n", "Ticker", "Short Term Profit", "Long Term Profit", "Number of Trades", "Retrurn of Capital", "Dividend Received", "Distributions", "Positions", "Total");

    for(int i = 0; i<name.size(); i++) {
        System.out.printf("%-10s          ", name.get(i));

        if(shortTerm.get(i) > 0)
          System.out.printf(ANSI_GREEN + "%-+,15.2f            " + ANSI_RESET, shortTerm.get(i));
        else if(shortTerm.get(i) == 0)
          System.out.printf("%-+,15.2f            ", shortTerm.get(i));
        else
          System.out.printf(ANSI_RED + "%-+,15.2f            " + ANSI_RESET, shortTerm.get(i));

        if(longTerm.get(i) > 0)
          System.out.printf(ANSI_GREEN + "%-+,15.2f           " + ANSI_RESET, longTerm.get(i));
        else if(longTerm.get(i) == 0)
          System.out.printf("%-+,15.2f           ", longTerm.get(i));
        else
          System.out.printf(ANSI_RED + "%-+,15.2f           " + ANSI_RESET, longTerm.get(i));

        System.out.printf("%-20d          ", numTrades.get(i));

        if(returnOfCapital.get(i) > 0)
          System.out.printf(ANSI_GREEN + "%-+,20.2f            " + ANSI_RESET, returnOfCapital.get(i));
        else if(returnOfCapital.get(i) == 0)
          System.out.printf("%-+,20.2f            ", returnOfCapital.get(i));
        else
          System.out.printf(ANSI_RED + "%-+,20.2f            " + ANSI_RESET, returnOfCapital.get(i));

        if(dividendReceived.get(i) > 0)
          System.out.printf(ANSI_GREEN + "%-+,20.2f           " + ANSI_RESET, dividendReceived.get(i));
        else if(dividendReceived.get(i) == 0)
          System.out.printf("%-+,20.2f           ", dividendReceived.get(i));
        else
          System.out.printf(ANSI_RED + "%-+,20.2f           " + ANSI_RESET, dividendReceived.get(i));

        System.out.printf("%-15d", numDistributions.get(i));

        if(unrealizedGains.get(i) > 0)
          System.out.printf(ANSI_GREEN + "        %-+,10.2f" + ANSI_RESET, unrealizedGains.get(i));
        else if(unrealizedGains.get(i) == 0)
          System.out.printf("        %-+,10.2f", unrealizedGains.get(i));
        else
          System.out.printf(ANSI_RED + "        %-+,10.2f" + ANSI_RESET, unrealizedGains.get(i));

        if(total.get(i) > 0)
          System.out.printf(ANSI_GREEN + "        %-+,10.2f%n" + ANSI_RESET, total.get(i));
        else if(total.get(i) == 0)
          System.out.printf("        %-+,10.2f%n", total.get(i));
        else
          System.out.printf(ANSI_RED + "        %-+,10.2f%n" + ANSI_RESET, total.get(i));

    }

    System.out.println();
  }


  public void printFinalFile() {
    fileOut = FileManager.openPrint("StocksAndOptionsOutput.csv");

    String cur = "Symbol,Short Term Profit,Long Term Profit,Number of Trades,Return of Capital,Dividend Received,Number of Distributions,Unrealized Gains,Total Profit";
    fileOut.println(cur);

    for(int i=0; i<name.size(); i++) {
      cur = name.get(i) + "," + shortTerm.get(i) + "," + longTerm.get(i) + "," + numTrades.get(i) + "," + returnOfCapital.get(i);
      cur += ("," + dividendReceived.get(i) + "," + numDistributions.get(i) + "," + unrealizedGains.get(i) + "," + total.get(i));
      fileOut.println(cur);
    }

    fileOut.close();
  }
}
