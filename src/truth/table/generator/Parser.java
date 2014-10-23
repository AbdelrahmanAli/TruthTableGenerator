package truth.table.generator;

import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.JOptionPane;

public class Parser 
{
	private String infixExpression;
	private ArrayList<String> parsedInfix;
	private ArrayList<String> infixVariables; // contain all propositional symbols in the statement
	private HashSet dublicateRemover;
	private String statmentNumber;
	
	public Parser() 
	{
		parsedInfix = new ArrayList();
		infixVariables = new ArrayList();
		dublicateRemover = new HashSet();
	}

	public void setInfix(String infix,String statmentNumber)
	{
		parsedInfix.clear(); // remove the previously parsed infix expression
		infixVariables.clear(); // remove the previously parsed infix expression
		dublicateRemover.clear(); // remove all from hash set 
		infixExpression = infix; 
		this.statmentNumber = statmentNumber+": ";
	}
	
	public PostfixExpression generatePostfix()
	{
		PostfixExpression postfix = new PostfixExpression(infixExpression,parsedInfix,infixVariables);
		postfix.createPostfix();
		return postfix;
	}
	
	public boolean checkValidationAndParseInfix() 
	{
		// remove all spaces 
		infixExpression = infixExpression.replace(" ", "");
		int bracket = 0,lastOperatorIndex = -2,lastVariableIndex = -2;
		boolean variableDetected = false,tMentioned = false,fMentioned = false;
		for(int i = 0 ; i < infixExpression.length() ; i++)
		{
			if(infixExpression.charAt(i) == '(')
			{
				bracket++;
				parsedInfix.add("(");
			}
			else if(infixExpression.charAt(i) == ')')
			{
				if(i == lastOperatorIndex+1)
				{
					JOptionPane.showMessageDialog(null,statmentNumber+"Operator before closed bracket.","Invalid Input",JOptionPane.ERROR_MESSAGE );
					return false; 
				}
				
				bracket--;
				if(bracket < 0)
				{
					JOptionPane.showMessageDialog(null,statmentNumber+"Unmatched Bracket >>> )","Invalid Input",JOptionPane.ERROR_MESSAGE );
					return false; 
				}
				else if(infixExpression.charAt(i-1) == '(')
				{
					JOptionPane.showMessageDialog(null,statmentNumber+"Empty Brackets >>> ()","Invalid Input",JOptionPane.ERROR_MESSAGE );
					return false;
				}
				parsedInfix.add(")");
			}
			else if( isCharButNotOR( infixExpression.charAt(i) ) ) // propositional variable with T for true and F for False
			{
				if(i == lastVariableIndex+1)
				{
					JOptionPane.showMessageDialog(null,statmentNumber+"Multiple variables without operator between them.","Invalid Input",JOptionPane.ERROR_MESSAGE );
					return false;  
				}
				
				String propositionalVariable = (infixExpression.charAt(i)+"").toUpperCase();
				if( i+1 != infixExpression.length() && isCharButNotOR( infixExpression.charAt(i+1) ) )
				{
					i++;
					propositionalVariable+= (infixExpression.charAt(i)+"").toLowerCase();
				}
				
				if(propositionalVariable.equals("T")&&!tMentioned)
				{
					JOptionPane.showMessageDialog(null,statmentNumber+"Please note that the letter "+propositionalVariable+" stands for TRUE.","Notification",JOptionPane.INFORMATION_MESSAGE );
					tMentioned = true;
				}
				else if(propositionalVariable.equals("F")&&!fMentioned)
				{
					JOptionPane.showMessageDialog(null,statmentNumber+"Please note that the letter "+propositionalVariable+" stands for False.","Notification",JOptionPane.INFORMATION_MESSAGE );
					fMentioned = true;
				}
				
				lastVariableIndex = i;
				parsedInfix.add(propositionalVariable);
				variableDetected = true;
				
				if(!dublicateRemover.contains(propositionalVariable)) // check if the variable name already exist or not
				{
					infixVariables.add(propositionalVariable);
					dublicateRemover.add(propositionalVariable);
				}
			}
			else if( infixExpression.charAt(i)=='v' || infixExpression.charAt(i)=='^' || infixExpression.charAt(i)=='~') // and , not , or
			{
				if(i == lastOperatorIndex+1 && (infixExpression.charAt(i)!='~') )
				{
					JOptionPane.showMessageDialog(null,statmentNumber+"Multiple operators.","Invalid Input",JOptionPane.ERROR_MESSAGE );
					return false; 
				}
				else if(i == infixExpression.length()-1)
				{
					JOptionPane.showMessageDialog(null,statmentNumber+"Operator at the end of the expression.","Invalid Input",JOptionPane.ERROR_MESSAGE );
					return false; 
				}
				else if(infixExpression.charAt(i)!='~' && (i==0 || (i-1 >=0 && infixExpression.charAt(i-1)=='(')))
				{
					JOptionPane.showMessageDialog(null,statmentNumber+"Operator at start or after and openning bracket.","Invalid Input",JOptionPane.ERROR_MESSAGE );
					return false; 
				}
				
				if(infixExpression.charAt(i)=='~' && variableDetected) 
				{
					JOptionPane.showMessageDialog(null,statmentNumber+"The Operator ~ (Not) isn't a binary operator.","Invalid Input",JOptionPane.ERROR_MESSAGE );
					return false;
				}
				else variableDetected = false;
				
				lastOperatorIndex = i;
				parsedInfix.add(infixExpression.charAt(i)+"");
			}
			else if(infixExpression.charAt(i)=='-') // Imply
			{
				if(i == lastOperatorIndex+1)
				{
					JOptionPane.showMessageDialog(null,statmentNumber+"Multiple operators.","Invalid Input",JOptionPane.ERROR_MESSAGE );
					return false; 
				}
				
				if(i+1 != infixExpression.length() && infixExpression.charAt(i+1)=='>')
				{
					i++;
					lastOperatorIndex = i;
					if(i == infixExpression.length()-1)
					{
						JOptionPane.showMessageDialog(null,statmentNumber+"Operator at the end.","Invalid Input",JOptionPane.ERROR_MESSAGE );
						return false; 
					}
					parsedInfix.add("->");
				}
				else
				{
					JOptionPane.showMessageDialog(null,statmentNumber+"Invalid Operator.","Invalid Input",JOptionPane.ERROR_MESSAGE );
					return false; // invalid operator
				}
			}
			else if(infixExpression.charAt(i)=='<') // Bi-condition
			{
				if(i == lastOperatorIndex+1)
				{
					JOptionPane.showMessageDialog(null,statmentNumber+"Multiple operators.","Invalid Input",JOptionPane.ERROR_MESSAGE );
					return false; 
				}
				if((i+1 != infixExpression.length() && infixExpression.charAt(i+1)=='-') && (i+2 != infixExpression.length() && infixExpression.charAt(i+2)=='>') )
				{
					i+=2;
					lastOperatorIndex = i;
					if(i == infixExpression.length()-1) 
					{
						JOptionPane.showMessageDialog(null,statmentNumber+"Operator at the end.","Invalid Input",JOptionPane.ERROR_MESSAGE );
						return false; // operator at the end
					}
					parsedInfix.add("<->");
				}
				else
				{
					JOptionPane.showMessageDialog(null,statmentNumber+"Invalid Operator.","Invalid Input",JOptionPane.ERROR_MESSAGE );
					return false; // invalid operator
				}
			}
			else
			{
				JOptionPane.showMessageDialog(null,statmentNumber+"Invalid Statment.","Invalid Input",JOptionPane.ERROR_MESSAGE );
				return false; // invalid char at i
			}  
		}
		
		if(bracket!=0)
		{
			JOptionPane.showMessageDialog(null,statmentNumber+"Unmatched Bracket >>> (","Invalid Input",JOptionPane.ERROR_MESSAGE );
			return false; //  unmatched bracket
		}
		
		handleRepeatedNot();
		return true;
	}
	
	private boolean isCharButNotOR(char character)
	{
		return (character >= 'A' && character <= 'Z') || ((character >= 'a' && character <= 'z') && character != 'v');
	}
	
	private void handleRepeatedNot()
	{
		String tempInfix = "";
		for (int i = 0; i < parsedInfix.size(); i++) 
		{
			if(parsedInfix.get(i).equals("~"))
			{
				
				int startIndex = i;
				int lastIndex = i;
				
				while(lastIndex+1 != parsedInfix.size() && parsedInfix.get(lastIndex+1).equals("~"))
				{	lastIndex++;	}
				
				if((lastIndex-startIndex+1)%2 == 0) //even , all NOTSs eliminated
				{
					for(int j = startIndex ; j <= lastIndex ; j++)
					{
						parsedInfix.remove(i);
					}
					
					tempInfix+=parsedInfix.get(i);
				}
				else // odd , only 1 not left
				{
					for(int j = startIndex+1 ; j <= lastIndex ; j++)
					{
						parsedInfix.remove(i);
					}
					tempInfix+=parsedInfix.get(i);
				}
			}
			else
			{
				if(parsedInfix.get(i).equals("<->") || parsedInfix.get(i).equals("->") || parsedInfix.get(i).equals("v") || parsedInfix.get(i).equals("^"))
				tempInfix+=" "+parsedInfix.get(i)+" ";
				else tempInfix+= parsedInfix.get(i); 
			}
		}
		infixExpression = tempInfix;
	}
	
	public String getInfix()
	{
		return infixExpression;
	}
	
}
