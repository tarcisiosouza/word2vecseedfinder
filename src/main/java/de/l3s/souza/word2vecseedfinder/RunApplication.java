package de.l3s.souza.word2vecseedfinder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.l3s.souza.learningtorank.Term;
import de.l3s.souza.learningtorank.TermUtils;

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
	private static int maxUsedFreqTerm;
	private static String features;
	private static double lambda;
	private static int windowsize;
	private static deepLearningUtils deepLearning;
	private static int candidateTerms;
	private static boolean L2r;
	
	public static void main (String args[]) throws Exception
	{
		
		InputStream inputStream = RunApplication.class.getClassLoader().getResourceAsStream(propFileName);
		config = new Properties ();
		double totalMap = 0.0f;
		int total = 0;
		
		if (inputStream != null) {
			config.load(inputStream);
		} else {
			throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
		}
		
		L2r = Boolean.parseBoolean(config.getProperty("L2r"));
		candidateTerms = Integer.parseInt(config.getProperty("candidateTerms"));
		features = config.getProperty("features");
		lambda = Double.parseDouble(config.getProperty("lambda"));
		windowsize=Integer.parseInt(config.getProperty("windowsize"));
		limit = Integer.parseInt(config.getProperty("limit"));
		maxUsedFreqTerm = Integer.parseInt(config.getProperty("maxUsedFreqTerm"));
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
		Term term = new Term ("");
		TermUtils termUtils = new TermUtils ("","/home/souza/NTCIR-eval/ntcir11_Temporalia_taskdata/TaskData/TIR/",term,windowsize,lambda,features);
		//File file = new File ("/home/souza/NTCIR-eval/ntcir12_Temporalia_taskdata/Test Collection/NTCIR12_Temporalia2_FormalRun_TDR_En.txt");
		File file = new File ("/home/souza/NTCIR-eval/ntcir11_Temporalia_taskdata/TaskData/TIR/NTCIR-11TIRTopicsFormalRun.txt");

		FileReader fr = new FileReader (file);
		BufferedReader br = new BufferedReader (fr);
		String id;
		String description = null;
		String query_time = null;
		String title = null;
		String line;
		String topic;
		String atempQuery="";
		
		Query query = new Query (maxUsedFreqTerm,runname,limit,field,terms,maxSimTerms,candidateTerms,maxDoc,
				maxIter,alpha,beta,gama,scoreParam,L2r);
		while ((line=br.readLine())!=null)
		{
			total++;
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
				
				if (topic.contains("a"))
					atempQuery = initialQuery;
				
				System.out.println("Current topic: "+topic);
				int number = Integer.parseInt(topic.substring(0,3));
				
				/*if (!topic.contentEquals("026r") )
					continue;
				/*if (number > 49)
					continue;
			/*	if (topic.contentEquals("001p"))
				{*/
				//public TermUtils (String topic, String path, Term term, int windowSize,double lambda, String features)
				termUtils.setTopic(topic);
				query.run(termUtils, topic, title, title+" "+description, title, query_time);
				totalMap = totalMap + query.getBestMAP();
				double currentMap = totalMap / total;
				
				System.out.println("Current MAP:" + currentMap);
			/*	break;	
				}*/
					
			}
			
			
		}

	}
}

	
