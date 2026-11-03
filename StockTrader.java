import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class StockTrader {

    public static void main(String[] args) throws FileNotFoundException {
        Scanner in = new Scanner(new File("tiny.txt"));
        String symbol = in.next();
        int n = in.nextInt();
        int[] years = new int[n];
        int[] months = new int[n];
        double[] prices = new double[n];
        loadData(in, years,months,prices);
        int low = indexOfLowest(prices);
        int high = indexOfHighest(prices);
        System.out.printf("%s lowest: $%.2f in %s %d \n", symbol, prices[low], monthName(months[low]), years[low]);
        System.out.printf("%s highest: $%.2f in %s %d", symbol, prices[high], monthName(months[high]), years[high]);
	}
	public static int indexOfBiggestDrop(double[] prices) {
		int index = 1;
		for(int i = 1;i < prices.length;i++){
			index = (prices[i-1] - prices[i])/prices[i-1] > (prices[index-1] - prices[index])/prices[index-1] ? i : index;
		}
		return index;
		
	}
    public static void loadData(Scanner in, int[] years, int[] months, double[] prices) {
		for(int i = 0;i < years.length;i++) {
			years[i] = in.nextInt();
			months[i] = in.nextInt();
			prices[i] = in.nextDouble();
		}
	}
	public static int indexOfLowest(double[] prices) {
		int low = 0;
		for (int i = 0;i < prices.length;i++) {
			low = prices[i] < prices[low] ? i : low;
		}
		return low;
	}
	public static int indexOfHighest(double[] prices){
		int low = 0;
		for (int i = 0;i < prices.length;i++) {
			low = prices[i] > prices[low] ? i : low;
		}
		return low;
	}
	 
	public static void printData(int[] years, int[] months, double[] prices) {
		for(int i = 0;i < years.length;i++) {
			System.out.printf("%s %d: $%.2f \n", monthName(months[i]), years[i], prices[i]);
		}
	}
	public static String monthName(int month) {
		String[] monthNames = {"Jan", "Feb", "Mar", "Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
		return monthNames[month-1];
	}
}
