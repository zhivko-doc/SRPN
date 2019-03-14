import java.io.*;

/**
 * Program class for an SRPN calculator. Its main purpose is to take numbers(operands) 
 * and operators as input and produce the corresponding output.
 * Operators for addition, subtraction etc. usually come after the operands. 
 * However, this program supports infix notation(eg. 3+5).
 * It also supports negative and floating-point numbers(eg. 1/3).
 * All the numbers are saturated.
 * Operands in octal numeric system can be inputted when there is a 0 before them.
 * Symbols:
 * Operands: 0,1,2,3,4,5,6,7,8,9
 * Operators: -,+,*,/,%,^,d,=,' ','\t'(or tab),r
 * All others are unrecognised
 * # (for commenting) can be both operator and unrecognised.
 */
 
public class SRPN {
	
	private Stack stack = new Stack();
	private RandomSRPN rand = new RandomSRPN();
	private char operands[] = {'0','1','2','3','4','5','6','7','8','9'};
	//bool to indicate whether the operand must be added to the last
	private boolean isOperand = false;
	//bool to indicate negative number as input
	private boolean isNegative =  false;
	//bool to indicates whether the input is a comment
	private boolean isComment = false;
	//bool to indicate the number is in octal numeric system
	private boolean isOctal = false;
	//bool to show invalid octal input(if there is 8 or 9 in it)
	private boolean wrongOctal = false;
	
    public void processCommand(String s) 
	{
		//loop to go through every symbol
        for(int i = 0; i < s.length(); i++)
		{
			//First checks for the special symbol #
			//A comment is activated or deactivated if there is either ' ', '\t' or nothing around it.
			if(s.charAt(i) == '#')
			{
				//if there is nothing around #
				if(s.length() == 1)
				{
					isComment = !isComment;
					return;
				}
				//if # is the last symbol on the line
				else if(i + 1 == s.length())
				{
					if(s.charAt(i-1) == ' ' || s.charAt(i-1) == '\t')
					{
						isComment = !isComment;
						return;
					}
				}
				//if # is the first symbol on the line
				else if(i - 1 < 0)
				{
					if(s.charAt(i + 1) == ' ' || s.charAt(i + 1) == '\t')
					{
						isComment = !isComment;
						i++;
					}
				}
				//if # is surrounded by space or tab
				else if((s.charAt(i-1) == ' ' || s.charAt(i-1) == '\t') && (s.charAt(i+1) == ' ' || s.charAt(i+1) == '\t'))
				{
					isComment = !isComment;
					i++;
				}
			}
			
			
			if(!isComment)
			{
				int num = 0;// variable to temporarily store a number
				for(int j = 0; j < 10; j++)
				{
					//if the current symbol is an operand
					if(s.charAt(i) == operands[j])
					{
						//Here the index j corresponds to the operand itself
						int value = j;
						//makes value negative if indicated as such
						value *= isNegative ? -1 : 1;
						if(isOperand)
						{
							if(isOctal)
							{
								if(j == 9 || j == 8 || wrongOctal)
								{
									num = 0;
									wrongOctal = true;
								}
								else
								{
									//handle saturatation of the number
									if(!isNegative)
									{
										num = (Integer.MAX_VALUE - value) / 8 < num ? Integer.MAX_VALUE : num * 8 + value;
									}
									else
									{
										num = (Integer.MAX_VALUE - value) / 8 < num ? Integer.MAX_VALUE : num * 8 + value;
									}
								}
							}
							else
							{
								//handle saturatation of the number
								if(!isNegative)
								{
									num = (Integer.MAX_VALUE - value) / 10 < num ? Integer.MAX_VALUE : num * 10 + value;
								}
								else
								{
									num = (Integer.MIN_VALUE - value) / 10 > num ? Integer.MIN_VALUE : num * 10 + value;
								}
							}
						}
						else//if it's the first digit in the number
						{
							if(value == 0)
							{
								isOctal = true;
							}
							num = value;
						}
						isOperand = true;
						
						//if the current symbol isnt the last
						if(i + 1 < s.length())
						{
							//start processing the next symbol 
							i++;
							j = -1;
						}
						else
						{
							/*
							* if the current symbol is the last:
							* push the number if valid
							* reset bool values for the next line
							*/
							if(!wrongOctal)
							{
								stack.push(num);
							}
							wrongOctal = false;
							isNegative = false;
							isOperand = false;
							isOctal = false;
							calculate();
							return;
						}
					}
				}
				
				//if there is a number that is not an invalid octal
				if(isOperand && !wrongOctal)
				{
					stack.push(num);
				}
				//reset the bool values in case of another number on the same line
				wrongOctal = false;
				isOctal = false;
				isOperand = false;
				isNegative = false;
				
				String operators = stack.getOperators();
				/*
				*The method calculate() is called when:
				*1)Symbols d, ' ', '\t' are reached
				*2)end of line
				*3)the current operator is of higher priority than the last in the stack of operators(if there is one)
				*The priority ranking is is: -,+,*,/,%,^ . That is checked in each case of these operators.
				*/
				switch(s.charAt(i))
				{
					case ' ':
					calculate();break;
					case '\t':
					calculate();break;
					case '=':
					equals();break;
					case 'd':
					calculate();
					display();break;
					case 'r':
					int stackSize = stack.getSize();
					stack.push(rand.getNextRandom());
					if(stackSize == stack.getSize())
					{
						rand.decreaseIndex();
					}break;
					case '-':
					//first check whether '-' indicates a negative number
					//that happens only when there is an operand after it and the symbol before it isn't an operand
					if(i + 1 < s.length())
					{
						for(int j = 0; j < 10; j++)
						{
							if(i - 1 < 0)
							{
								if(s.charAt(i+1)==operands[j])
								{
									isNegative=true;
									break;
								}
							}
							else
							{
								if(s.charAt(i-1)==operands[j])
								{
									isNegative=false;
									break;
								}
								else if(s.charAt(i+1)==operands[j])
								{
									isNegative=true;
								}
							}
						}
					}
					//if '-' is an operator for subtraction
					if(!isNegative)
					{
						if(operators.length()>0)
						{
							char c = operators.charAt(operators.length()-1);
							if(!(c=='-'))
							{
								calculate();
							}
						}
					operators=stack.getOperators() + "-";
					stack.setOperators(operators);
					}
					break;
					case '+':
					if(operators.length()>0)
					{
						char c = operators.charAt(operators.length()-1);
						if(!(c=='-'||c=='+'))
						{
							calculate();
						}
					}
					operators=stack.getOperators() + "+";
					stack.setOperators(operators);break;
					case '*':
					if(operators.length() > 0)
					{
						char c = operators.charAt(operators.length()-1);
						if(!(c == '-' || c == '+' || c == '*'))
						{
							calculate();
						}
					}
					operators=stack.getOperators() + "*";
					stack.setOperators(operators);break;
					case '/':
					if(operators.length()>0)
					{
						char c = operators.charAt(operators.length()-1);
						if(c == '%' || c == '^')
						{
							calculate();
						}
					}
					operators=stack.getOperators() + "/";
					stack.setOperators(operators);break;
					case '%':
					if(operators.length() > 0)
					{
						char c = operators.charAt(operators.length()-1);
						if(c == '^')
						{
							calculate();
						}
					}
					operators=stack.getOperators() + "%";
					stack.setOperators(operators);break;
					case '^':
					operators=stack.getOperators() + "^";
					stack.setOperators(operators);break;
					default: System.out.println("Unrecognised operator or operand \"" + s.charAt(i) + "\".");break;
				}
			}
		}
		calculate();
    }
	
	/**
	* Perform the available operations for calculation on the stack.
	*/
	private void calculate()
	{
		//go through all the operators in the string stack
		for(int i = stack.getOperators().length()-1; i >= 0; i--)
		{
			//if there are enough operands in the stack
			if(stack.getSize() > 1)
			{
				//perform the corresponding operation
				switch(stack.getOperators().charAt(i))
				{
					case '-':
					stack.subtract();break;
					case '+':
					stack.add();break;
					case '*':
					stack.multiply();break;
					case '/':
					stack.divide();break;
					case '%':
					stack.remainder();break;
					case '^':
					stack.power();break;
					default: 
					System.out.println("Error");break;
				}
			}
			else
			{
				//show the error if there aren't enough operands in the stack
				System.out.println("Stack underflow.");
			}
		}
		//reset the operator stack for future use
		stack.setOperators("");
	}
	
	/**
	* Displays all the numbers in the stack(from first to last).
	*/
	private void display()
	{
		if(stack.getSize()==0)
		{
			//if the stack is empty print the minimum integer
			System.out.println(Integer.MIN_VALUE); 
		}
		else
		{
			for(int i = 0;i<stack.getSize();i++)
			{
				System.out.println((int)stack.peek(i));
			}
		}
	}
	
	/**
	* Prints the last number of the stack to the screen.
	*/
	private void equals()
	{
		if(stack.getSize() == 0)
		{
			//if the stack is empty print it.
			System.out.println("Stack empty.");
		}
		else
		{
			System.out.println((int)stack.peek(stack.getSize()-1));
		}
	}
	
    public static void main(String[] args) {
        SRPN srpn = new SRPN();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        
        try 
		{
            //Keep on accepting input from the command-line
            while(true) 
			{
                String command = reader.readLine();
                
                //Close on an End-of-file (EOF) (Ctrl-D on the terminal)
                if(command == null)
                {
                  //Exit code 0 for a graceful exit
                  System.exit(0);
                }
                
                //Otherwise, (attempt to) process the character
				srpn.processCommand(command);    
            }
        } 
		catch(IOException e) 
		{
          System.err.println(e.getMessage());
          System.exit(1);
        }
    }
}
