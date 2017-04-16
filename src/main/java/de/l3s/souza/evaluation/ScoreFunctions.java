package de.l3s.souza.evaluation;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;

import com.google.protobuf.TextFormat.ParseException;

import de.l3s.elasticquery.Article;
import de.l3s.souza.date.DateUtils;

public class ScoreFunctions {

	private DateUtils dateObject = new DateUtils ();
	private double alpha;
	
	public ScoreFunctions (double alpha)
	{
		this.alpha = alpha;
		
	}
	
	 public HashMap<Article,Double> urlScoreFunction (String year,HashMap<Article,Double> documents) throws ParseException, java.text.ParseException
	    {
	    	String queryDate = year + "-22-09";
	    	
	    	HashMap<Article,Double> documentsUrlScore = new HashMap<Article,Double>();
	    	Date time = dateObject.convertToDate(queryDate);
	    	Long ts;
	    	String yearStr;
	    	double relevanceScore;
	    	double tempBasedScore;
	    	int timeQ = Integer.parseInt(year);
	    	int timeC = 0;
	    	
	    	for (Entry<Article, Double> key:documents.entrySet())
	    	{
	    		double termBasedScore = key.getValue();
	    		//int urlYear = detectValidYearUrl (key);
	    		//String termsFromdKey = key.replaceAll("[^\\w\\s]", " ").replaceAll("_", " ");
	    		//String[] text2 = getDate (key);
	    		 yearStr = detectValidYearUrl(key.getKey().getUrl());
	    		if (yearStr.contentEquals(" "))
	    		{
	    			
	    			timeC = 0;
	    		/*	StringTokenizer tsToken = new StringTokenizer (finalCaptures.get(key));
					tsToken.nextToken();
					ts = Long.parseLong(tsToken.nextToken());
					String captureDate = parseDate (ts);
					yearStr = captureDate.substring(0,4);
					timeC = 0;
	    			*/
	    		}
	    		
	    		else
	    		{
	    			timeC = Integer.parseInt(yearStr);
	    			//Date timeUrl = convertToDate (text2[0]);
	    			//timeC = new Long(timeUrl.getTime()/1000);
	    		}
	    		
	    		if (timeC == timeQ)
	    		{
	    		//	tempBasedScore = beta;
	    			relevanceScore = alpha * termBasedScore + (1-alpha);
	    		}
	    		else
	    		{
	    			tempBasedScore = (double)1/Math.abs(timeC-timeQ);
	    			relevanceScore = alpha * termBasedScore + (1-alpha)*tempBasedScore;
	    		}
	    		documentsUrlScore.put(key.getKey(), relevanceScore);
	    	}
	    	
	    	return documentsUrlScore;
	    }
	 
	/* 
	 public double calculateTempScore (String year, String timeMl)
	 {
		 String eventDate = year + "-22-09";
		 Date date1, date2;
		 
		 date1 = 
		 
	 }*/
	  private static String detectValidYearUrl (String url)
	  {
	    	String resultUrl = url.replaceAll("[^\\w\\s]", " ").replaceAll("_", " ");
	    	
	    	StringTokenizer token = new StringTokenizer (resultUrl);
	      	
	    	while (token.hasMoreTokens())
	    	{
	    		try {
	    			
	    			int year = Integer.parseInt(token.nextToken());
	    			if (year>1996 && year<=2013)
	    				return Integer.toString(year);
	    		} catch (Exception e)
	    		{
	    			continue;
	    			
	    		}
	    	}
	    	
	    	return " ";
	    	
	  }
}
