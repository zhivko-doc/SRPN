/**
* Program class for SRPN Stack. Contains the stack(array) of numbers, 
* its size and the stack(string) of operators.
* Features of the class are methods for addition, subtraction, multiplication,
* division, remainder and power involving the last two numbers in the stack.
*/
public class Stack
{
	private double[] stack;
	private int size;
	//string to hold the operators that are yet to be used
	private String operators;
	
	
	public Stack()
	{
		//set default values
		stack = new double[23];
		size = 0;
		operators = "";
	}
 
	/**
	* Get a specific number from the stack
	* @param  index
	*	   The index of the number
	* @return  stack[index]
	*      A specific number of the stack
	*/
	public double peek(int index)
	{
		return stack[index];
	}
 
	/**
	* Get the current size of the stack
	* @return  size
	*      The size of the stack
	*/
	public int getSize()
	{
		return size;
	}
 
	/**
	* Get the stack(string) of operators
	* @return  operators
	*      The stack(string) of operators
	*/
	public String getOperators()
	{
		return operators;
	}
	
	/**
	* Set new value of operators
	* @param  newOperators
	*      The new operators
	*/
	public void setOperators(String newOperators)
	{
		operators=newOperators;
	}
	
	/**
	* Add a new number to the stack(if possible)
	* @param  num
	*      The new number
	*/
	public void push(int num)
	{
		if(size < 23)
		{
			stack[size] = (double)num;
			size ++;
		}
		else
		{
			System.out.println("Stack overflow.");
		}
	}
	
	/*
	* The methods below handle saturation the same way:
	* For example if we want to see whether num2+num1>max  
	* we can check max-num2<num1 without performing the operation that will go beyond max(or min) integer value.
	*/
	
	/**
	* Performs addition on the last 2 numbers in the stack and handles saturation.
	*/
	public void add()
	{
		if(stack[size-2] >= 0)
		{
			stack[size-2] = Integer.MAX_VALUE - stack[size-2] < stack[size-1] ? (double)Integer.MAX_VALUE : stack[size-2] + stack[size-1];
		}
		else
		{
			stack[size-2] = Integer.MIN_VALUE - stack[size-2] > stack[size-1] ? (double)Integer.MIN_VALUE : stack[size-2] + stack[size-1];
		}
		size--;
	}
	
	//s2-s1<min <=> min+s1>s2
	//s2-s1>max <=> max+s1<s2
	/**
	* Performs subtraction on the last 2 numbers in the stack(penultimate - last) and handles saturation.
	*/
	public void subtract()
	{
		if(stack[size-2] <= 0)
		{
			stack[size-2] = Integer.MIN_VALUE + stack[size-1] > stack[size-2] ? (double)Integer.MIN_VALUE : stack[size-2] - stack[size-1];
		}
		else
		{
			stack[size-2] = Integer.MAX_VALUE + stack[size-1] < stack[size-2] ? (double)Integer.MAX_VALUE : stack[size-2] - stack[size-1];
		}
		size--;
	}
	
	//s2*s1>max <=> max/s1<s2
	//s2*s1<min <=> min/s1>s2
	/**
	* Performs multiplication on the last 2 numbers in the stack and handles saturation.
	*/
	public void multiply()
	{
		if(stack[size-1] < 0)
		{
			//make stack[size-1] positive so that we don't have to worry about the sign of the inequality
			stack[size-1] *= -1;
			stack[size-2] *= -1;
		}
		if(stack[size-1] == 0||stack[size-1] == 0)
		{
			stack[size-2] = 0.0;
		}
		else if(stack[size-2] > 1)
		{//if the result is positive
			stack[size-2] = Integer.MAX_VALUE / stack[size-1] < stack[size-2] ? (double)Integer.MAX_VALUE : stack[size-2] * stack[size-1];
		}
		else if(stack[size-2] < -1)
		{//if the result is negative
			stack[size-2] = Integer.MIN_VALUE / stack[size-1] > stack[size-2] ? (double)Integer.MIN_VALUE : stack[size-2] * stack[size-1];
		}
		else
		{//if either number is between -1 and 1 no saturation is possible
			stack[size-2] *= stack[size-1];
		}
		size--;
	}
	
	//s2/s1>max <=> max*s1<s2
	//s2/s1<min <=> min*s1>s2
	/**
	* Performs division on the last 2 numbers in the stack(penultimate / last) and handles saturation.
	*/
	public void divide()
	{
		if(stack[size-1] == 0)
		{
			System.out.println("Divide by 0.");
		}
		else 
		{
			if(stack[size-1] < 0)
			{
				//make stack[size-1] positive so that we don't have to worry about the sign of the inequality
				stack[size-1] *= -1;
				stack[size-2] *= -1;
			}
			if(stack[size-1] < 1)
			{
				if(stack[size-2] > 0)
				{
					stack[size-2] = Integer.MAX_VALUE * stack[size-1] < stack[size-2] ? (double)Integer.MAX_VALUE : stack[size-2] / stack[size-1];
				}
				else
				{
					stack[size-2] = Integer.MIN_VALUE * stack[size-1] > stack[size-2] ? (double)Integer.MIN_VALUE : stack[size-2] / stack[size-1];
				}
			}
			else
			{
				stack[size-2] /= stack[size-1];
			}
			size--;
		}
	}
	
	/**
	* Calculates the remainder of penultimate / last numbers in the stack.
	*/
	public void remainder()
	{
		if(stack[size-1] < 1 && stack[size-1] > -1)
		{
			System.out.println("Floating point exception");
			System.exit(1);
		}
		stack[size-2] %= stack[size-1];
		size--;
	}
	
	//pow(s2,s1)>max <=> pow(max,1/s1)<s2
	//pow(s2,s1)<min <=> pow(min,1/s1)>s2
	/**
	* Calculates the penultimate number to the power of the last number in the stack and handles saturation.
	*/
	public void power()
	{
		if(stack[size-1] < 0)
		{
			System.out.println("Negative power.");
		}
		else
		{
			//if the base is negative
			if(stack[size-2] < 0)
			{
				//if the exponent isn't an integer the result is the minimum int value
				if(stack[size-1] != (int)stack[size-1])
				{
					stack[size-2] = (double)Integer.MIN_VALUE;
				}
				else if(stack[size-1] % 2 == 0)
				{
					//if the exponent is divisible by 2 the result is positive
					stack[size-2] = Math.pow(Integer.MAX_VALUE,1.0 / stack[size-1]) < stack[size-2] ? (double)Integer.MAX_VALUE : Math.pow(stack[size-2],stack[size-1]);
				}
				else
				{
					//if the exponent isn't divisible by 2 the result is negative
					stack[size-2] = Math.pow(Integer.MIN_VALUE,1.0 / stack[size-1]) > stack[size-2] ? (double)Integer.MAX_VALUE : Math.pow(stack[size-2],stack[size-1]);
				}
			}
			else if(stack[size-1] > 1)
			{
				stack[size-2] = Math.pow(Integer.MAX_VALUE,1.0 / stack[size-1]) < stack[size-2] ? (double)Integer.MAX_VALUE : Math.pow(stack[size-2],stack[size-1]);
			}
			else
			{
				stack[size-2] = Math.pow(stack[size-2],stack[size-1]);
			}
			size--;
		}
	}
}