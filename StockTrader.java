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
SP500  $307.12  Oct 1990  $6,853.03  Dec 2025 
*/
/*
FILE  BuyAndSell  DipBuyer    MovingAverage  RSI
TINY  $12,975.00  $14,975.00  $10,000.00  $19,970.00 
UP  $24,031.86  $10,000.00  $20,411.92  $10,000.00 
DOWN  $3,095.64  $3,159.28  $10,000.00  $3,159.28 
ZIG  $12,101.39  $38,071.99  $9,848.78  $16,459.33 
AAPL  $85,865.80  $36,610.49  $114,231.31  $32,219.50 
AMZN  $19,886.04  $14,192.52  $38,170.87  $23,317.03 
MSFT  $7,237.50  $49,273.15  $7,208.23  $10,141.36 
IBM  $12,467.97  $27,998.86  $7,549.75  $16,370.40 
GOOG  $54,398.54  $13,655.41  $18,706.20  $10,000.00 
SBUX  $36,280.60  $15,759.50  $67,500.23  $18,368.65 
SP500  $16,362.00  $11,483.45  $37,195.81  $10,374.01 
SP500  $198,868.74  $40,836.68  $199,113.67  $11,972.37
*/


public class StockTrader {

public static final double START_CASH = 10000.0;
public static final double FEE = 10.0;

public static double simulate(int[] years, int[] months, double[] prices, Strategy strategy) {
    double cash = START_CASH;
    int shares = 0;
    double boughtAt = 0.0;

    for (int i = 0; i < prices.length; i++) {
        double[] history = Arrays.copyOf(prices, i + 1);
        String action = decide(history, shares > 0, boughtAt, strategy);

        if (action.equals("BUY") && shares == 0) {
            int canBuy = (int) ((cash - FEE) / prices[i]);
            if (canBuy > 0) {
                shares = canBuy;
                cash = cash - shares * prices[i] - FEE;
                boughtAt = prices[i];
               // System.out.printf("%s %d: BUY %d shares at $%.2f%n",
                      //  monthName(months[i]), years[i], shares, prices[i]);
            }
        } else if (action.equals("SELL") && shares > 0) {
            cash = cash + shares * prices[i] - FEE;
           // System.out.printf("%s %d: SELL %d shares at $%.2f%n",
                   // monthName(months[i]), years[i], shares, prices[i]);
            shares = 0;
            boughtAt = 0.0;
        }
    }
    return cash + shares * prices[prices.length - 1];
}

    public static void main(String[] args) throws FileNotFoundException {
		String[] files = {"tiny.txt","rising.txt","falling.txt","zigzag.txt","aapl_2000_2010.txt", "amzn_2000_2010.txt","msft_2000_2010.txt","ibm_2000_2010.txt","goog_2004_2010.txt","sbux_2007_2016.txt","sp500_1925_1945.txt","sp500_1990_2025.txt"};
		for(String file : files) {
			Scanner in = new Scanner(new File(file));
			String symbol = in.next();
			int n = in.nextInt();
			int[] years = new int[n];
			int[] months = new int[n];
			double[] prices = new double[n];
			loadData(in, years,months,prices);
			in.close();
			double resultB = simulate(years, months, prices, Strategy.BuyAndHold);
			double resultD = simulate(years, months, prices, Strategy.DipBuyer);
			double resultM = simulate(years, months, prices, Strategy.MovingAverage);
			double resultR = simulate(years, months, prices, Strategy.RSI);
			System.out.printf("%s  $%,.2f  $%,.2f  $%,.2f  $%,.2f \n",symbol, resultB, resultD, resultM, resultR);
		}

	}
	//STRATEGIES
	public static String decide( double[] history,  boolean own, double boughtAt, Strategy strategy) {
		switch(strategy) {
			default: return buyAndHold(history,own,boughtAt);
			case Strategy.BuyAndHold: return buyAndHold(history,own,boughtAt);
			case Strategy.DipBuyer: return dipBuyer(history,own,boughtAt);
			case Strategy.MovingAverage: return movingAverage(history,own,boughtAt);
			case Strategy.RSI: return RSI(history,own,boughtAt);
		}
		
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
	/*
	 * This right here is my strategy. It uses RSI; which is used by many investors around the world.
	 * RSI calculates the average momentum of the price and scales it from 1-100.
	 * Based on the average momentum you can predict if the trend is bullish, bearish or range and make a informed deiscion based on that.
	*/
	public static String RSI(double[] history,boolean own,double boughtAt) {
		if (history.length < 3) return "HOLD";
		double[] Up = new double[history.length-1];
		double[] Down = new double[history.length-1];
		double avgG = 0;
		double avgL = 0;
		double RS = 0;
		double RSI = 0;
		for (int i = 0;i < history.length-1;i++) {
			if(history[i+1] - history[i] > 0) {
				Up[i] = history[i+1] - history[i];
			}
			else {
				Down[i] = history[i] - history[i+1];
			}
			
		}

		for (int i = 0;i < history.length-1;i++) {
			avgG += Up[i];
			avgL += Down[i];
		}
		avgG = avgG/(history.length-1);
		avgL = avgL/(history.length-1);
		RS = avgG/avgL;
		RSI = 100 - (100/(1+RS)); 
		
		if (RSI >= 60) return "SELL";
		if (RSI <= 40) return "BUY";
		
		return "HOLD";
		
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
enum Strategy {
	BuyAndHold,DipBuyer,RSI,MovingAverage
}
