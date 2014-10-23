package truth.table.generator;


import java.util.ArrayList;
import java.util.Stack;

import javax.swing.JOptionPane;

public class PostfixExpression 
{
	private ArrayList<String> postfix;
	private ArrayList<String> variables;
	private ArrayList<String> parsedInfix;
	
	protected  Stack<String> post ;
	protected Stack<boolean[]> post1;
	private String postfixExpression ;
	
	private int z;
	
	boolean [][]truthTable;
	boolean []results;// truth table results array of size (2^numberOfVariables)
	
	private ArrayList<String> resultsEncoded; // encode truth table result example 0000 -> 4,0 means 4 zeros
	
	private String infixExpression;
	private boolean checkEvaluation ; // Tautology = true , contradiction = false ,,, initially = true
	
	
	public PostfixExpression(String infixExpression,ArrayList<String> parsedInfixFromParser,ArrayList<String> variablesFromParser) 
	{
		// clone object
		variables = new ArrayList<String>();
		for(int i = 0 ; i < variablesFromParser.size() ; i++)	variables.add(variablesFromParser.get(i));
		
		this.results= new boolean[(int) Math.pow(2,variables.size())];
		this.infixExpression = infixExpression;
		//clone object
		parsedInfix = new  ArrayList<String>();
		for(int i = 0 ; i < parsedInfixFromParser.size() ; i++)	parsedInfix.add(parsedInfixFromParser.get(i));
		
		postfixExpression = "";
		post = new Stack<String>();
		post1 = new Stack<boolean[]>();
		resultsEncoded =new ArrayList<String>();
		
		checkEvaluation = true;
		postfix = new ArrayList();
		resultsEncoded = new ArrayList();
	}
	
	public void createPostfix() 
	{	
		// create postfix arraylist
		post.add(" ");
		
		for (int i = 0; i < parsedInfix.size(); i++) {
			
			if (parsedInfix.get(i).equalsIgnoreCase("^") || parsedInfix.get(i).equalsIgnoreCase("v") || parsedInfix.get(i).equalsIgnoreCase("(") ||parsedInfix.get(i).equalsIgnoreCase(")")||
					parsedInfix.get(i).equalsIgnoreCase("~")|| parsedInfix.get(i).equalsIgnoreCase("->") ||parsedInfix.get(i).equalsIgnoreCase("<->") 
					){
			 switch (parsedInfix.get(i)) {
			 case "(": post.push("(");
			 			break; 
			 case ")": 
				 while(!post.peek().equalsIgnoreCase("(") ){
					 postfix.add(post.peek());
					 post.pop();
				 }
				 post.pop();
				 break;
			 case "~": post.push("~");
			 			break;
			 case "^": 
			 		while(post.peek().equalsIgnoreCase("~")|| post.peek().equalsIgnoreCase("^") )
			 		{
						postfix.add(post.peek());
			 			post.pop();
			 			
			 		}
			 		post.push("^");
			 		break;
			 case "v": 
					while(post.peek().equalsIgnoreCase("~") || post.peek().equalsIgnoreCase("^")|| post.peek().equalsIgnoreCase("v"))
			 		{
						postfix.add(post.peek());
			 			post.pop();
			 		}
			 		post.push("v");
			 		break;
			 case "->": 	
					 while(post.peek().equalsIgnoreCase("~") || post.peek().equalsIgnoreCase("^")|| post.peek().equalsIgnoreCase("v")|| post.peek().equalsIgnoreCase("->") )
			 			{
							postfix.add(post.peek());
				 			post.pop();
			 			}
			 		post.push("->");
			 		break;
			 case "<->":	
				 while(post.peek().equalsIgnoreCase("~") || post.peek().equalsIgnoreCase("^") || post.peek().equalsIgnoreCase("v")|| post.peek().equalsIgnoreCase("->"))
		 			{
					 	postfix.add(post.peek());
					 	post.pop();
		 			}
				 post.push("<->");
				 break;
				
			 }
			 
			}else postfix.add(parsedInfix.get(i));	
			
			 
		}
		
		while (post.size()>1)
		{
			postfix.add(post.peek());
		 	post.pop();
		}
		post.pop();
		

	}
	
	public void evaluateTruthTable()
	{	
		z =  (int) Math.pow(2, variables.size());
		truthTable = new boolean[z][variables.size()];
		
		for (int k = 0; k < z; k++) 
		{
			for (int k2 = 0; k2 < variables.size(); k2++){
				    
				    if(variables.get(variables.size()-k2-1).equalsIgnoreCase("T") )
					{
				    	truthTable[k][variables.size()-k2-1] = true ;
					}else if(variables.get(variables.size()-k2-1).equalsIgnoreCase("F"))
						truthTable[k][variables.size()-k2-1] = false ;
					else truthTable[k][variables.size()-k2-1] = (((1<<k2)&k) != 0) ;
	
			}
		}

		int m =0  , m1 = 0;
		boolean[] tempResult = new boolean[z];
	
		
			for (int i = 0; i < postfix.size(); i++) {
				
				postfixExpression += postfix.get(i) ;

				results =new boolean[z];
				for (int k = 0; k < z; k++) 
					results[k] = truthTable[k][0]  ;

				if (postfix.get(i).equalsIgnoreCase("^") || postfix.get(i).equalsIgnoreCase("v") ||postfix.get(i).equalsIgnoreCase("~")
					|| postfix.get(i).equalsIgnoreCase("->") ||postfix.get(i).equalsIgnoreCase("<->") ){
					

				switch (postfix.get(i)) {
					
						case "~": 	

							if (post.size()!=0)
							{ 				
								for (int k = 0; k < variables.size(); k++) {
								if(post.peek().equalsIgnoreCase(variables.get(k))){
									m = k ;
									break ;
								}
								}
								post.pop(); 
								for (int k = 0; k < z; k++) 
								{
										results[k] = !truthTable[k][m]  ;
								}
							}else {
								
								results=post1.pop();
								for (int k = 0; k < z; k++)							
								results[k] = !results[k]  ;
							}
							
						 	post1.push(results);
					 		break;
					 			
				 case "^": 
					 if(post1.size()==0)
					 	{

							for (int k = 0; k < variables.size(); k++) {
							if(post.peek().equalsIgnoreCase(variables.get(k))){
								m = k ;
								break ;
							}
							}
							post.pop();
							
						 for (int k = 0; k < variables.size() && post.size() != 0; k++) {
								if(post.peek().equalsIgnoreCase(variables.get(k)))
								{
									m1 = k ;	
									break;
								}
						    }
						 	post.pop();
					    
						 	for (int k = 0; k < z ; k++) 
							results[k] = truthTable[k][m1]&truthTable[k][m]  ;
					    
					 	}else if(post1.size()==1 ){
					 		for (int k = 0; k < variables.size(); k++) {
								if(post.peek().equalsIgnoreCase(variables.get(k)))
								{
									m = k ;
									break;
								}
						    }
						 	post.pop();
					 		results = post1.pop();
					    	for (int k = 0; k < z; k++) 								
							results[k] = results[k] &truthTable[k][m] ;
					 	}else{
							tempResult=post1.pop();
							results=post1.pop();
							for (int k = 0; k < z; k++) 								
							results[k] = results[k] &tempResult[k]  ;
					 	}
					    post1.push(results);
				 		break;
				 		
				 case "v": 
					 if(post1.size()==0)
					 	{

							for (int k = 0; k < variables.size(); k++) {
							if(post.peek().equalsIgnoreCase(variables.get(k))){
								m = k ;
								break ;
							}
							}
							post.pop();
							
						 for (int k = 0; k < variables.size() && post.size() != 0; k++) {
								if(post.peek().equalsIgnoreCase(variables.get(k)))
								{
									m1 = k ;	
									break;
								}
						    }
						 	post.pop();
					    
						 	for (int k = 0; k < z ; k++) 
							results[k] = truthTable[k][m1]|truthTable[k][m]  ;
					    
					 	}else if(post1.size()==1 ){
					 		for (int k = 0; k < variables.size(); k++) {
								if(post.peek().equalsIgnoreCase(variables.get(k)))
								{
									m = k ;
									break;
								}
						    }
						 	post.pop();
					 		results = post1.pop();
					    	for (int k = 0; k < z; k++) 								
							results[k] = results[k] | truthTable[k][m] ;
					 	}else{
							tempResult=post1.pop();
							results=post1.pop();
							for (int k = 0; k < z; k++) 								
							results[k] = results[k] |tempResult[k]  ;
					 	}
					    post1.push(results);
				 		break;
				 		
				 case "->": 	
					 if(post1.size()==0)
					 	{

							for (int k = 0; k < variables.size(); k++) {
							if(post.peek().equalsIgnoreCase(variables.get(k))){
								m = k ;
								break ;
							}
							}
							post.pop();
							
						 for (int k = 0; k < variables.size() && post.size() != 0; k++) {
								if(post.peek().equalsIgnoreCase(variables.get(k)))
								{
									m1 = k ;	
									break;
								}
						    }
						 	post.pop();
					    
						 	for (int k = 0; k < z ; k++) 
							results[k] = !truthTable[k][m1]|truthTable[k][m]  ;
					    
					 	}else if(post1.size()==1 ){
					 		for (int k = 0; k < variables.size(); k++) {
								if(post.peek().equalsIgnoreCase(variables.get(k)))
								{
									m = k ;
									break;
								}
						    }
						 	post.pop();
					 		results = post1.pop();
					    	for (int k = 0; k < z; k++) 								
							results[k] = !results[k] |truthTable[k][m] ;
					 	}else{
							tempResult=post1.pop();
							results=post1.pop();
							for (int k = 0; k < z; k++) 								
							results[k] = !results[k] |tempResult[k]  ;
					 	}
					    post1.push(results);
				 		break;
				 		
				 case "<->":	
					 if(post1.size()==0)
					 	{

							for (int k = 0; k < variables.size(); k++) {
							if(post.peek().equalsIgnoreCase(variables.get(k))){
								m = k ;
								break ;
							}
							}
							post.pop();
							
						 for (int k = 0; k < variables.size() && post.size() != 0; k++) {
								if(post.peek().equalsIgnoreCase(variables.get(k)))
								{
									m1 = k ;	
									break;
								}
						    }
						 	post.pop();
					    
						 	for (int k = 0; k < z ; k++) 
							results[k] = (truthTable[k][m1]&truthTable[k][m]) |(!truthTable[k][m1]&!truthTable[k][m]) ;
					    
					 	}else if(post1.size()==1 ){
					 		for (int k = 0; k < variables.size(); k++) {
								if(post.peek().equalsIgnoreCase(variables.get(k)))
								{
									m = k ;
									break;
								}
						    }
						 	post.pop();
					 		results = post1.pop();
					    	for (int k = 0; k < z; k++) 								
							results[k] = (results[k] & truthTable[k][m])|(!results[k] &!truthTable[k][m]) ;
					 	}else{
							tempResult=post1.pop();
							results=post1.pop();
							for (int k = 0; k < z; k++) 								
							results[k] = (results[k] &tempResult[k])|(!results[k] &!tempResult[k])  ;
					 	}
					    post1.push(results);
				 		break;
				 		
				 }
				
			}else 	post.push(postfix.get(i));
			
		
		}
	}
	
	public boolean compareWith(PostfixExpression comparedPostfix)
	{
		ArrayList<String> comparedVariables=comparedPostfix.getVariablesNames();

		int size1=variables.size();
		int size2=comparedVariables.size();
		boolean [] comparedOutput =comparedPostfix.getResultArray();
		
		if (size1!=size2)   return false ;
		else // v1 = v2 
		{
		
		    // if they have the same variables name or not
			for(int i = 0 ; i < variables.size() ; i++)
			{   if(!variables.contains(comparedVariables.get(i)))   return false;   }
			
		    // if they have the same output or not
			for (int i = 0; i < results.length; i++)  // outputs 
		    {	if(results[i]!=comparedOutput[i]) return false;  }
		    
			// handle imply variables
			if(parsedInfix.contains("->"))
			{
			    String infix1 , infix2;
			    infix1 = infixExpression.replace("(", ""); // infix 1
			    infix1 = infixExpression.replace(")", ""); // infix 1
			    
			    infix2 = comparedPostfix.getInfix().replace("(", ""); // infix 2
			    infix2 = comparedPostfix.getInfix().replace(")", ""); // infix 2
			    
			    for (int i = 0; i < infix1.length(); i++) 
    			    if((infix1.charAt(i)=='-' && infix1.charAt(i-1)!='<') && (infix2.charAt(i)=='-' && infix2.charAt(i-1)!='<'))
			        {
			            if(infix1.charAt(i-2)==infix2.charAt(i-2) && infix1.charAt(i+3)==infix2.charAt(i+3) )return true ;
			            else return false ;
			        }
			}
		}
		return true;
	}
	
	public ArrayList<String> getVariablesNames()
	 {
	
	  return variables;
	  
	 }
	public ArrayList<String> getPostFix()
	 {
	
	  return postfix;
	  
	 }
	public String checkTautology() 
	{   
	    int falseNumber =0 ;
	    int trueNumber = 0;
		for (int i = 0; i < results.length; i++) {
			if(results[i])	trueNumber++;
			else falseNumber++;
		}
		
		if(falseNumber==results.length)	return "Contradiction" ;
		else if(trueNumber==results.length)	return "Tautology" ;
        else return "Neither";
	}
	public String getInfix()
	 {
	  return infixExpression;
	 }
	
	public String getPostfixExpression()
	 {
	  return postfixExpression;
	 }
	public boolean[][] getTruthTableArray()
	 {
	  return truthTable;
	 }
	public boolean[] getResultArray()
	 {
	  return results;
	 }


}