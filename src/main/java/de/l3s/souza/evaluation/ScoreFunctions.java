package de.l3s.souza.evaluation;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.protobuf.TextFormat.ParseException;
import com.twelvemonkeys.util.Time;

import de.l3s.elasticquery.Article;
import de.l3s.elasticquery.LivingKnowledgeSnapshot;
import de.l3s.souza.date.DateUtils;
import de.l3s.souza.preprocess.PreProcess;
import de.l3s.souza.preprocess.XMLParser;
import de.l3s.souza.word2vecseedfinder.deepLearningUtils;
import de.unihd.dbs.heideltime.standalone.HeidelTimeStandalone;
import de.unihd.dbs.heideltime.standalone.exceptions.DocumentCreationTimeMissingException;
import javassist.bytecode.Descriptor.Iterator;

public class ScoreFunctions {

	private DateUtils dateObject = new DateUtils ();
	private HashSet<String> temporalExp = new HashSet<String>();
	private double alpha;
	private PreProcess preprocess = new PreProcess ();
	private XMLParser xmlParser = new XMLParser ();
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
	 
	 public HashMap<LivingKnowledgeSnapshot,Double> urlScoreFunction (HeidelTimeStandalone heideltime, deepLearningUtils deepLearning,String topic,
			 String queryIssueDate, HashMap<LivingKnowledgeSnapshot,Double> documents, String iteration, HashMap<String,String> urls,String initialQuery) throws Exception
	 {
		 
		 double relevanceScore;
	     double tempBasedScore = 0.0f;
	     HashSet<String> timeExpressions = new HashSet<String>();
		 int yearQuery = 0;
		 
		StringTokenizer tokenQueryIssueDate = new StringTokenizer (queryIssueDate);
		
		while (tokenQueryIssueDate.hasMoreTokens())
		{
			String currentToken = tokenQueryIssueDate.nextToken();
			if (currentToken.length()==4)
			{
				yearQuery = Integer.parseInt(currentToken);
				break;
			}
		}
				
		int countDoc = 0;
		String oldest;
		String latest;
		
		for (LivingKnowledgeSnapshot s:documents.keySet())
		{
			
			temporalExp.clear();
			tempBasedScore = 0.0f;
			String url = s.getUrl();
			
		/*	if (urls.containsKey(url))
			{
				String i = urls.get(url);
				if (!i.contentEquals(iteration))
					continue;
			}*/
			String[]timeExpRegex = dateObject.getDate(url);
			
			for (int i=0;i<timeExpRegex.length;i++)
			{
				if (timeExpRegex[i]==null)
					break;
				timeExpressions.add(timeExpRegex[i]);
			}
			
			String tokenizedTerms = preprocess.preProcessUrl(url);
			
			Date date1 = new Date ();
		
			StringTokenizer token = new StringTokenizer (tokenizedTerms);
			
			double termBasedScore = s.getScore();
		
			String textTempExp = s.getTemp();
			
			textTempExp = textTempExp.replaceAll("\n", ",");
			StringTokenizer tokenTextTempExp = new StringTokenizer (textTempExp,",");
			
			/*for (String str:timeExpressions)
			{
				int year;
				long distance;
				
				Date date = new Date ();
				date = DateUtils.parseDate(str);
				Date dateQuery = new Date ();
				SimpleDateFormat df = new SimpleDateFormat ("MMM dd, yyyy",Locale.ENGLISH); //Mar 29, 2013 GMT+0:00
				df.setTimeZone(TimeZone.getTimeZone("GMT"));
				dateQuery = df.parse(queryIssueDate);
				
				//System.out.println(getDateDiff(date,dateQuery,TimeUnit.DAYS));
				
				tempBasedScore = tempBasedScore + calculateCurrentScore (date,dateQuery,termBasedScore,topic);
				
				
			}*/
			
			while (tokenTextTempExp.hasMoreTokens()) {
				String term = tokenTextTempExp.nextToken();
				
				if (!(isNumberString (term)) || (temporalExp.contains(term)))
					continue;
				Date date = new Date ();
				date = DateUtils.parseDate(term);
				Date dateQuery = new Date ();
				SimpleDateFormat df = new SimpleDateFormat ("MMM dd, yyyy", Locale.ENGLISH);
				df.setTimeZone(TimeZone.getTimeZone("GMT"));
				dateQuery = df.parse(queryIssueDate);
				
				if (!temporalExp.contains(term))
					temporalExp.add(term);
				tempBasedScore = tempBasedScore + calculateCurrentScore (date,dateQuery,termBasedScore,topic);
				
				}
			
			int matchingTimesQuery = 0;
			
		/*	while (token.hasMoreTokens()) {
				String term = token.nextToken();
				term = term.toLowerCase();
			/*	if (initialQuery.toLowerCase().contains(term))
					matchingTimesQuery++;*/
				//consider getting the closest time expressions from the current term and calculate the new scores based on the w2vec cos similarity
				//consider getting the closest words from the time expressions in the documents as expanded terms (for the time-aware query expansion)
			//	Collection<String> nearest = deepLearning.getWordsNearest(term, 1);
				
				//java.util.Iterator<String> iter = nearest.iterator();
			//	while (iter.hasNext())
			//	{
			//		double sim = deepLearning.getCosSimilarity(term, iter.next());
			//		termBasedScore += sim;
			//	}
				
			//}
			
			termBasedScore = s.getScore();
			relevanceScore = ( alpha * termBasedScore ) + (1-alpha)*tempBasedScore;
			
			LivingKnowledgeSnapshot lk = new LivingKnowledgeSnapshot ();
			lk = s;
			lk.setScore(relevanceScore);
			documents.put(lk, lk.getScore());
			
		}
		return documents;
		 
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
	  private double calculateCurrentScore (Date currentDate, Date dateQuery, double currentScore, String topic)
	  {
		  
		  double nextScore = currentScore;
		  long diff;
		  
		  if (isPastDate(currentDate,dateQuery))
				diff = getDateDiff (currentDate,dateQuery,TimeUnit.DAYS);
		  else
				diff = getDateDiff(dateQuery,currentDate,TimeUnit.DAYS);
		  
			if (topic.contains("f"))
			{
				
				if (!isPastDate(currentDate,dateQuery))
				{	
					nextScore += diff;
				}
				
			}
		  
			if (topic.contains("p"))
			{
				if (isPastDate(currentDate,dateQuery))
				{
					nextScore += diff;
				}
				
			}
			
			if (topic.contains("r"))
			{
				if (diff <= 200 && diff>0)
					nextScore += 1/diff;
				if (diff==0)
					nextScore += 1;
			}
			
		return nextScore;
	  }
	  
	  public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
		    long diffInMillies = date2.getTime() - date1.getTime();
		    return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
		}
	  
	  public boolean isPastDate (Date date1, Date date2)
	  {
		  
		  if (date1.before(date2))
			  return true;
		  
		  return false;
	  }
	  
	  public boolean isNumberString (String str)
	  {
		  
		  for (int i=0;i<str.length();i++)
		  {
			  
				if  (Character.isDigit(str.charAt(i)))
					continue;
				else
					return false;
			  
		  }
		  return true;
	  }
	  
	  
}
