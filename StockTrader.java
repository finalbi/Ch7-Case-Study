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
	1. That means that they bought the stock at the highest price possible, which means they can only sell at a loss.
	2. SBUX's low was during the 2008 finacial crisis, which seems like a reasonable reason for a stock to crash.
	   The SP500's 1932 low also makes sense since it was during the great depression.
*/ 
/*
FILE  BuyAndHold  DipBuyer    MovingAverage  RSI   DipBuySellWith7monthAvg
TINY  $12,975.00  $14,975.00  $10,000.00  $19,970.00  $10,000.00 
UP  $24,031.86  $10,000.00  $20,411.92  $10,000.00  $10,000.00 
DOWN  $3,095.64  $3,159.28  $10,000.00  $3,159.28  $3,469.36 
ZIG  $12,101.39  $38,071.99  $9,848.78  $16,459.33  $17,288.85 
AAPL  $85,865.80  $36,610.49  $114,231.31  $32,219.50  $209,532.95 
AMZN  $19,886.04  $14,192.52  $38,170.87  $23,317.03  $43,872.07 
MSFT  $7,237.50  $49,273.15  $7,208.23  $10,141.36  $16,300.90 
IBM  $12,467.97  $27,998.86  $7,549.75  $16,370.40  $14,139.60 
GOOG  $54,398.54  $13,655.41  $18,706.20  $10,000.00  $30,319.29 
SBUX  $36,280.60  $15,759.50  $67,500.23  $18,368.65  $71,507.94 
SP500  $16,362.00  $11,483.45  $37,195.81  $10,374.01  $16,882.35 
SP500  $198,868.74  $40,836.68  $199,113.67  $11,972.37  $207,701.45 
Wins        1            3            2            0            4 
Total  $446,564.22  $251,041.33  $509,524.85  $152,381.93  $631,014.76 
Average  $44,656.42  $25,104.13  $50,952.49  $15,238.19  $63,101.48 


*/
/*
	1. My strategy DipBuySellWith7monthAvg, had the most wins. No strategy was best on every file.
	2. DipBuyer did poorly in AMZN due to the fact that the amazon stock fluxated alot while growing and declining. The fluxuations affected DipBuyer because the condition of the last month being more than this month and the month before last being more than the last month, is rarely fufilled in the AMZN dataset.
	3. MovingAverage beat BuyAndHold in the DOWN file because BuyHold purchased at the beginning and the price declined, on the other hand MovingAverage did not buy at all because 12 months did not pass. All of this allowed MovingAverage to remain at the base amount of $10,000 instead of making a foolish investment like BuyAndHold.
	4. My Strategy did much better than BuyAndHold beating it all but one time.  I had my own system of benchmarking based on average money earned vs total money earned. That system avoided the problem of trying to maximize the income of certain files and helped me focus on the total income instead.
	
	
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
		double totalB = 0;
		double totalD = 0;
		double totalM = 0;
		double totalR = 0;
		double totalS = 0;
		int wonB = 0;
		int wonD = 0;
		int wonM = 0;
		int wonR = 0;
		int wonS = 0;
		for(String file : files) {
			Scanner in = new Scanner(new File(file));
			String symbol = in.next();
			int n = in.nextInt();
			int[] years = new int[n];
			int[] months = new int[n];
			double[] prices = new double[n];
			loadData(in, years,months,prices);
			in.close();
			double[] results = {simulate(years, months, prices, Strategy.BuyAndHold),simulate(years, months, prices, Strategy.DipBuyer),simulate(years, months, prices, Strategy.MovingAverage),simulate(years, months, prices, Strategy.RSI),simulate(years, months, prices, Strategy.DipBuyerAndSeller)};
			totalB += results[0];
			totalD += results[1];
			totalM += results[2];
			totalR += results[3];
			totalS += results[4];
			int winner = 0;
			for (int i = 0;i < 5;i++) {
				winner = results[i] > results[winner] ? i : winner;
			}
			switch(winner) {
				case 0: wonB++; break;
				case 1: wonD++; break;
				case 2: wonM++; break;
				case 3: wonR++; break;
				case 4: wonS++; break;
			}
			System.out.printf("%s  $%,.2f  $%,.2f  $%,.2f  $%,.2f  $%,.2f \n",symbol, results[0], results[1], results[2], results[3], results[4]);
		}
		System.out.printf("Wins        %d            %d            %d            %d            %d \n", wonB, wonD, wonM, wonR, wonS);
		System.out.printf("Total  $%,.2f  $%,.2f  $%,.2f  $%,.2f  $%,.2f \n", totalB, totalD, totalM, totalR, totalS);
		System.out.printf("Average  $%,.2f  $%,.2f  $%,.2f  $%,.2f  $%,.2f \n", totalB/files.length, totalD/files.length, totalM/files.length, totalR/files.length, totalS/files.length);

	}
	//STRATEGIES
	public static String decide( double[] history,  boolean own, double boughtAt, Strategy strategy) {
		switch(strategy) {
			default: return buyAndHold(history,own,boughtAt);
			case Strategy.BuyAndHold: return buyAndHold(history,own,boughtAt);
			case Strategy.DipBuyer: return dipBuyer(history,own,boughtAt);
			case Strategy.MovingAverage: return movingAverage(history,own,boughtAt);
			case Strategy.RSI: return RSI(history,own,boughtAt);
			case Strategy.DipBuyerAndSeller: return dipBuyerAndSeller(history,own,boughtAt);
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
	/*
	 * this is my other much better strategy. it combines the dipBuyer and the 12monthaverage with some modifications.
	 * the logic for buying it is the same as dipBuyer, thus the selling logic is where it gets interesting.
	 * the selling logic checks the 12 month average EXCEPT that it can average a varible number of months, based on the amount of months availible.
	 * After making sure that this price is above the 12-3 month average, it then checks to make sure it is double or more the amount paid.
	 * if both of those conditions are true, then it buys otherwise it holds.
	 */
	public static String dipBuyerAndSeller(double[] history, boolean own, double boughtAt) {
		if (history.length < 7) return "HOLD";
		if (!own) {
			return history[history.length-1] < history[history.length-2] && history[history.length-2] < history[history.length-3] ? "BUY" : "HOLD";
		}
		double avg = 0;
		double sum = 0;
		if (history.length <= 12) {
			for (int i = 0;i < history.length;i++) {
				sum += history[i];
			}
			avg = sum / history.length;
		}
		else {
			for (int i = history.length-12;i < history.length;i++) {
				sum += history[i];
			}
			avg = sum / 12;
		}
		

		return history[history.length-1] > avg && history[history.length-1]/boughtAt >=  2 ? "SELL" : "HOLD";
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
	BuyAndHold,DipBuyer,RSI,MovingAverage,DipBuyerAndSeller
}
