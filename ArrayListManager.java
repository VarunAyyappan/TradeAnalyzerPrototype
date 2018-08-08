import java.util.ArrayList;

public class ArrayListManager {
    public static void addToArray(ArrayList<Integer> arr, int element, int index) {
        while(index >= arr.size())
          arr.add(0);

        arr.set(index, element + arr.get(index));
    }

    public static void addToArray(ArrayList<Double> arr, double element, int index) {
        while(index >= arr.size())
          arr.add(0.0);

        arr.set(index, element + arr.get(index));
    }

    public static void sortProfitArrayLists(ArrayList<String> ticker, ArrayList<Double> shortTerm, ArrayList<Double> longTerm, ArrayList<Integer> numTrades, ArrayList<Double> total) {
      ArrayList<ArrayList<Double>> decimals = new ArrayList<ArrayList<Double>>();
      decimals.add(shortTerm);
      decimals.add(longTerm);

      ArrayList<ArrayList<Integer>> integers = new ArrayList<ArrayList<Integer>>();
      integers.add(numTrades);

      quickSort(ticker, total, decimals, integers, 0, total.size()-1);
    }

    public static void sortProfitArrayLists(ArrayList<String> ticker, ArrayList<Double> shortTerm, ArrayList<Double> longTerm, ArrayList<Integer> numTrades, ArrayList<Double> returnOfCapital, ArrayList<Double> dividendReceived, ArrayList<Integer> numDistributions, ArrayList<Double> total) {
      ArrayList<ArrayList<Double>> decimals = new ArrayList<ArrayList<Double>>();
      decimals.add(shortTerm);
      decimals.add(longTerm);
      decimals.add(returnOfCapital);
      decimals.add(dividendReceived);

      ArrayList<ArrayList<Integer>> integers = new ArrayList<ArrayList<Integer>>();
      integers.add(numTrades);
      integers.add(numDistributions);

      quickSort(ticker, total, decimals, integers, 0, total.size()-1);
    }

    public static void sortProfitArrayLists(ArrayList<String> ticker, ArrayList<Double> shortTerm, ArrayList<Double> longTerm, ArrayList<Integer> numTrades, ArrayList<Double> returnOfCapital, ArrayList<Double> dividendReceived, ArrayList<Integer> numDistributions, ArrayList<Double> unrealizedGains, ArrayList<Double> total) {
      ArrayList<ArrayList<Double>> decimals = new ArrayList<ArrayList<Double>>();
      decimals.add(shortTerm);
      decimals.add(longTerm);
      decimals.add(returnOfCapital);
      decimals.add(dividendReceived);
      decimals.add(unrealizedGains);

      ArrayList<ArrayList<Integer>> integers = new ArrayList<ArrayList<Integer>>();
      integers.add(numTrades);
      integers.add(numDistributions);

      quickSort(ticker, total, decimals, integers, 0, total.size()-1);
    }

  public static void quickSort(ArrayList<String> ticker, ArrayList<Double> total, ArrayList<ArrayList<Double>> decimals, ArrayList<ArrayList<Integer>> integers, int low, int high) {
      if(low>=high) return;
      String tempName = "";
      double pivot = total.get((low + high)/ 2), tempTotal = 0.0;
      int i=low, j=high;
      double[] tempDecimals = new double[decimals.size()];
      int[] tempIntegers = new int[integers.size()];

      while (i<=j) {
        while (total.get(i)<pivot)
          i++;

        while (total.get(j)>pivot)
          j--;

        if(i<=j) {
          tempTotal = total.get(i);
          tempName = ticker.get(i);

          for(int k=0; k<decimals.size(); k++)
            tempDecimals[k] = decimals.get(k).get(i);

          for(int k=0; k<integers.size(); k++)
            tempIntegers[k] = integers.get(k).get(i);

          total.set(i, total.get(j));
          ticker.set(i, ticker.get(j));

          for(int k=0; k<decimals.size(); k++)
            decimals.get(k).set(i, decimals.get(k).get(j));

          for(int k=0; k<integers.size(); k++)
            integers.get(k).set(i, integers.get(k).get(j));

          total.set(j, tempTotal);
          ticker.set(j, tempName);

          for(int k=0; k<decimals.size(); k++)
            decimals.get(k).set(j, tempDecimals[k]);

          for(int k=0; k<integers.size(); k++)
            integers.get(k).set(j, tempIntegers[k]);

          i++;
          j--;
        }
      }

  quickSort(ticker, total, decimals, integers, low, j);
  quickSort(ticker, total, decimals, integers, i, high);
}

    public static void quickSort(int[] values, int low, int high) {
		    if(low>=high) return;
        int pivot = values[(low + high)/2];
        int i=low, j=high, temp;

        while (i<=j) {
          while (values[i]<pivot)
            i++;

          while (values[j]>pivot)
            j--;

          if(i<=j) {
            temp = values[i];
            values[i] = values[j];
            values[j] = temp;
            i++;
            j--;
          }
        }

		quickSort(values, low, j);
		quickSort(values, i, high);
	}
}
