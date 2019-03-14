/**
*Program class for creating pseudorandom number used by the SRPN calculator.
*/
public class RandomSRPN
{
	private int[] random;
	private int rIndex;
	
	public RandomSRPN()
	{
		//set the values of the pseudorandom numbers that are used
		random= new int[]{1804289383,846930886,1681692777,1714636915,1957747793,424238335,719885186,
		1649760492,59651649,1189641421,1025202362,1350490027,783368690,1102520059,2044897763,
		1967513926,1365180540,1540383426,304089172,1303455736,35005211,521595368};
		rIndex = -1;
	}
	
	/**
	* Returns the next pseudorandom number
	* @return  random
	*      The next pseudorandom number
	*/
	public int getNextRandom()
	{
		//if the last number in the array is reached start from the beginning
		rIndex = rIndex == 21 ? 0 : rIndex + 1;
		return random[rIndex];
	}
	
	/**
	* Decrease rIndex by 1(set the next pseudorandom number to be the same as last)
	*/
	public void decreaseIndex()
	{
		rIndex -= 1;
	}
}