import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;
/*
File  Lowest  LowestDate  Highest  HighestDate
TINY  $30.00  Mar 2020  $70.00  Jul 2020 
UP  $20.00  Jan 2010  $48.14  Dec 2014 
DOWN  $30.36  Dec 2014  $100.00  Jan 2010 
ZIG  $35.00  Jul 2010  $65.00  Mar 2010 
AAPL  $7.07  Mar 2003  $223.02  Mar 2010 
AMZN  $5.97  Sep 2001  $135.91  Nov 2009 
MSFT  $15.81  Feb 2009  $43.22  Mar 2000 
IBM  $53.01  Sep 2002  $130.32  Dec 2009 
GOOG  $102.37  Aug 2004  $707.00  Oct 2007 
SBUX  $4.09  Nov 2008  $62.16  Oct 2015 
SP500  $4.77  Jun 1932  $31.30  Sep 1929 
SP500  $307.12  Oct 1990  $6853.03  Dec 2025 
*/

public class StockTrader {

public static final double START_CASH = 10000.0;
public static final double FEE = 10.0;

public static double simulate(int[] years, int[] months, double[] prices) {
    double cash = START_CASH;
    int shares = 0;
    double boughtAt = 0.0;

    for (int i = 0; i < prices.length; i++) {
        double[] history = Arrays.copyOf(prices, i + 1);
        String action = decide(history, shares > 0, boughtAt);

        if (action.equals("BUY") && shares == 0) {
            int canBuy = (int) ((cash - FEE) / prices[i]);
            if (canBuy > 0) {
                shares = canBuy;
                cash = cash - shares * prices[i] - FEE;
                boughtAt = prices[i];
                System.out.printf("%s %d: BUY %d shares at $%.2f%n",
                        monthName(months[i]), years[i], shares, prices[i]);
            }
        } else if (action.equals("SELL") && shares > 0) {
            cash = cash + shares * prices[i] - FEE;
            System.out.printf("%s %d: SELL %d shares at $%.2f%n",
                    monthName(months[i]), years[i], shares, prices[i]);
            shares = 0;
            boughtAt = 0.0;
        }
    }
    return cash + shares * prices[prices.length - 1];
}

    public static void main(String[] args) throws FileNotFoundException {
		String[] files = {"tiny.txt","rising.txt","falling.txt","zigzag.txt","aapl_2000_2010.txt", "amzn_2000_2010.txt","msft_2000_2010.txt","ibm_2000_2010.txt","goog_2004_2010.txt","sbux_2007_2016.txt","sp500_1925_1945.txt","sp500_1990_2025.txt"};
		Scanner in = new Scanner(new File(files[1]));
		String symbol = in.next();
		int n = in.nextInt();
		int[] years = new int[n];
		int[] months = new int[n];
		double[] prices = new double[n];
		loadData(in, years,months,prices);
		in.close();
        double result = simulate(years, months, prices);
		System.out.printf("Final value: $%,.2f%n", result);
	}
	//STRATEGIES
	public static String decide( double[] history,  boolean own, double boughtAt) {
		return myStrategy(history,own,boughtAt);
		
	}
	public static String buyAndHold(double[] history,boolean own,double boughtAt) {
		return "BUY";
	}
	public static String dipBuyer(double[] history,boolean own,double boughtAt) {
		if (history.length < 3) return "HOLD";
		if (!own) {
			return history[history.length-1] < history[history.length-2] && history[history.length-2] < history[history.length-3] ? "BUY" : "HOLD";
		}
		return history[history.length-1]/boughtAt >= 1.1 ? "SELL" : "HOLD";
	}
	public static String movingAverage(double[] history,boolean own,double boughtAt) {
		if (history.length < 12) return "HOLD";
		double avg = 0;
		double sum = 0;
		for (int i = history.length-12;i < history.length;i++) {
			sum += history[i];
		}
		avg = sum / 12.0;
		if (!own && history[history.length-1] > avg) return "BUY";
		return own && history[history.length-1] < avg ? "SELL" : "HOLD";
	}
	//DATA RETRIEVERS
	public static int indexOfBiggestDrop(double[] prices) {
		int index = 1;
		for(int i = 1;i < prices.length;i++){
			index = (prices[i-1] - prices[i])/prices[i-1] > (prices[index-1] - prices[index])/prices[index-1] ? i : index;
		}
		return index;
	}
	public static int indexOfLowest(double[] prices) {
		int low = 0;
		for (int i = 0;i < prices.length;i++) {
			low = prices[i] < prices[low] ? i : low;
		}
		return low;
	}
	public static int indexOfHighest(double[] prices){
		int high = 0;
		for (int i = 0;i < prices.length;i++) {
			high = prices[i] > prices[high] ? i : high;
		}
		return high;
	}
	//MISC
	public static void printData(int[] years, int[] months, double[] prices) {
		for(int i = 0;i < years.length;i++) {
			System.out.printf("%s %d: $%.2f \n", monthName(months[i]), years[i], prices[i]);
		}
	}
	public static void loadData(Scanner in, int[] years, int[] months, double[] prices) {
		for(int i = 0;i < years.length;i++) {
			years[i] = in.nextInt();
			months[i] = in.nextInt();
			prices[i] = in.nextDouble();
		}
	}
	public static String monthName(int month) {
		String[] monthNames = {"Jan", "Feb", "Mar", "Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
		return monthNames[month-1];
	}
}
