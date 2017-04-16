package de.l3s.souza.word2vecseedfinder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RunApplication {

	private static String propFileName = "seedfinderparam.properties";
	private static  Properties config;
	private static int limit;
	private static int maxSimTerms;
	private static int maxDoc;
	private static int maxIter;
	private static String field;
	private static int terms;
	private static String initialQuery;
	private static double alpha;
	private static double beta;
	private static double gama;
	private static double scoreParam;
	private static String runname;
	
	public static void main (String args[]) throws Exception
	{
		
		InputStream inputStream = RunApplication.class.getClassLoader().getResourceAsStream(propFileName);
		config = new Properties ();
		
		if (inputStream != null) {
			config.load(inputStream);
		} else {
			throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
		}
		
		limit = Integer.parseInt(config.getProperty("limit"));
		maxSimTerms = Integer.parseInt(config.getProperty("maxSimTerms"));
		maxDoc = Integer.parseInt(config.getProperty("maxDoc"));
		field =config.getProperty("field");
		terms = Integer.parseInt(config.getProperty("terms"));
		maxIter = Integer.parseInt(config.getProperty("maxIter"));
		alpha = Double.parseDouble(config.getProperty("alpha"));
		beta = Double.parseDouble(config.getProperty("beta"));
		gama = Double.parseDouble(config.getProperty("gama"));
		scoreParam = Double.parseDouble(config.getProperty("scoreParam"));
		runname = config.getProperty("runname");
		
		File file = new File ("/home/souza/NTCIR-eval/ntcir12_Temporalia_taskdata/Test Collection/NTCIR12_Temporalia2_FormalRun_TDR_En.txt");
		FileReader fr = new FileReader (file);
		BufferedReader br = new BufferedReader (fr);
		String id;
		String description;
		String query_time = null;
		String title;
		String line;
		String topic;
		
		while ((line=br.readLine())!=null)
		{
			if (line.contains("<id>"))
			{
				final Pattern pattern = Pattern.compile("<id>(.+?)</id>");
				final Matcher m = pattern.matcher(line);
				m.find();
				id = m.group(1);
			}
			
			if (line.contains("<title>"))
			{
				final Pattern pattern = Pattern.compile("<title>(.+?)</title>");
				final Matcher m = pattern.matcher(line);
				m.find();
				title = m.group(1);
			}
			if (line.contains("<description>"))
			{
				Pattern pattern = Pattern.compile("<description>(.+?)</description>");
				Matcher m = pattern.matcher(line);
				m.find();
				description = m.group(1);
			}
			
			if (line.contains("<query"))
			{
				Pattern pattern = Pattern.compile("<query_issue_time>(.+?)</query_issue_time>");
				Matcher m = pattern.matcher(line);
				m.find();
				query_time = m.group(1);
			}
			
			if (line.contains("<subtopic id"))
			{
				Pattern pattern = Pattern.compile("<subtopic id=\"(.+?)\"");
				Matcher m = pattern.matcher(line);
				m.find();
				topic = m.group(1);
				line = line.replaceAll("<[^>]+>", "");
								
				int i= 0;
				for (i=0;;i++)
				{
					
					if (Character.isAlphabetic(line.charAt(i)))
						break;
				}
				
				line = line.substring(i, line.length());
				initialQuery = line;
				
				
				Query query = new Query (topic,runname,initialQuery,limit,field,terms,maxSimTerms,query_time,maxDoc,
						maxIter,alpha,beta,gama,scoreParam);
			}
			
			
		}

	}
}

	
