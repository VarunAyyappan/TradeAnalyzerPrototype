import java.util.ArrayList;

public class SpreadsheetManager{

  public static final String ANSI_RESET = "\u001B[0m";
  public static final String ANSI_RED = "\u001B[31m";
  public static final String ANSI_GREEN = "\u001B[32m";

  public static int getFirstDigInd(String str) {
      for(int i=0; i<str.length(); i++) {
        if ((str.charAt(i) >= '0') && (str.charAt(i) <= '9'))
          return i;
      }

      return str.length();
    }

    public static int jumpCommas(String str, int numCommas) {
      int index = -1, count = 0;

      while(str.indexOf(",", index+1)!=-1 && count<numCommas) {
        index = str.indexOf(",", index+1);
        count++;
      }

      return index;
    }

    public static void printSpace(int numSpace) {
      for(int i=0; i<numSpace; i++)
        System.out.print(" ");
    }

    public static void printProfitBar(ArrayList<String> ticker, ArrayList<Double> total) {
      System.out.println("Bar Graph of Total Profits (X -> 100 dollars)");
      SpreadsheetManager.printSpace(49);
      System.out.printf("-|%-6s|+%n", "Ticker");

      for(int i = 0; i<ticker.size(); i++) {
        if(total.get(i) > 0) {
          SpreadsheetManager.printSpace(50);
          System.out.printf("|%-6s|", ticker.get(i));
          System.out.print(ANSI_GREEN);

          for(int j=0; j<=total.get(i)+50; j+=100)
            System.out.print("X");

          System.out.println(ANSI_RESET);
        }
        else if(total.get(i) == 0) {
          SpreadsheetManager.printSpace(50);
          System.out.printf("|%-6s|%n", ticker.get(i));
        }
        else {
          System.out.print(ANSI_RED);
          SpreadsheetManager.printSpace(49+(int)((total.get(i)-50)/100));

          for(int j=0; j>=total.get(i)-50; j-=100)
            System.out.print("X");

          System.out.printf(ANSI_RESET+"|%-6s|%n", ticker.get(i));
        }
      }
    }
}
