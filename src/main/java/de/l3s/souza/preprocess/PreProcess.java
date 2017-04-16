package de.l3s.souza.preprocess;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.StringTokenizer;

public class PreProcess {
	
	 private static HashSet<String> stopwords = new HashSet<String>(); 
	 private static ArrayList<String> vowels = new ArrayList<String>();
	 
	 public PreProcess () {
		   vowels.add("a");
	       vowels.add("e");
	       vowels.add("i");
	       vowels.add("o");
	       vowels.add("u");
		 
		 try 
  	   	  {
  	   	   File fl = new File("/home/souza/stopwords_de.txt");
     	//	File fl = new File("/home/souza/stopwords_de.txt");
  	   	   BufferedReader br = new BufferedReader(new FileReader(fl)) ;
  	   	   String string;
  	   	   while ((string=br.readLine())!=null)
  	   	   {
  	   		   stopwords.add(string);
  	   	   }
  	   	   br.close();
  	   	   
  	   	  }
  	   	  catch (IOException  e)
  	   	  { e.printStackTrace(); }
		 
	 }
	 public String removeStopWords (String str)
		{
			String newStr="";
			//String input = str.replaceAll("[-+.^:,]","");
			
			StringTokenizer token = new StringTokenizer (str);
			while (token.hasMoreTokens())
			{
				String term = token.nextToken().toLowerCase();
				if (!stopwords.contains(term) && term.length()>1)
				{
					newStr = newStr + " " +term;
				}
			}
			return newStr;
		}
	 
	 public boolean isStopWord (String str)
	 {
		 
		 if (stopwords.contains(str))
		 	return true;
		 
		 return false;
	 }
	 
	
	//Clean the URL, removing stopwords and other unused fields
		public String preProcessUrl (String u) throws MalformedURLException
		{
			String path;
			String final_url = null;
			URL url = new URL (u);
			path = url.getPath();
			path =path.replaceAll("[^\\w\\s]", " ").replaceAll("_", " ");
			StringTokenizer token = new StringTokenizer (path);
			
			while (token.hasMoreElements())
	     	{
				String str = token.nextToken();
			
				if (isValidToken(str))
				{		
					if (final_url != null)
						final_url = final_url + " " +str;
					else
						final_url = str;
				}
	     	}
			
			if (final_url == null)
				return "";
			else
				return final_url;
		}
		
		public static boolean isValidToken (String str)
		{
			int position = 0;
			String currentChar;
			
			if (stopwords.contains(str.toLowerCase()) || str.length()<=2 || isNumberConcatChar(str))
				return false;
				while (position < str.length())
				{
					
					try {
						Integer.parseInt(String.valueOf(str.charAt(position)));
						return false;
					} catch (NumberFormatException ex) {
						
						currentChar = String.valueOf(str.charAt(position));
						
						if (vowels.contains(currentChar.toLowerCase()))
							return true;
					}
					position++;
				}
				return false;
		}
		
		
		public static boolean isNumberConcatChar (String str)
		{
			int size = str.length();
			
			for (int i=0;i<str.length();i++)
			{
				if (Character.isDigit(str.charAt(i)))
				{
					return true;
				}
				
			}
			
			return false;
			
		}
		public static boolean isNumber (String str)
		{
		
		    try{  
	            Integer.parseInt(str);  
	            return true;  
	        }catch(Exception e){
	        try {	
	        	Float.parseFloat(str);
	        	return true;
	        }catch (Exception f) {
	        	return false;
	        }  
	        }  
		
		}
		

}
