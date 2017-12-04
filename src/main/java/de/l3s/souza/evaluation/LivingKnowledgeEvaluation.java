package de.l3s.souza.evaluation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.StringTokenizer;

import de.l3s.elasticquery.LivingKnowledgeSnapshot;

public class LivingKnowledgeEvaluation {

	private static String NTCIR_VERSION;
	private static String propFileName = "seedfinder.properties";
	private  Properties config;
	private String root = "";
	private static Map<String,String> documents = new HashMap<String,String>();
	private static int totalRelevant;
	private int totalRelevantPRF;
	private double avPrecision;
	private ArrayList<Point> BestprecRecall;
	private double BestAvPrecision;
	
	public double getAvPrecision() {
		return avPrecision;
	}

	public LivingKnowledgeEvaluation (String topic) throws IOException
	{		
		documents.clear();
		totalRelevant = 0;
		avPrecision = 0.0f;
		BestAvPrecision = 0.0f;
		setBestprecRecall(new ArrayList<Point>());
	//	InputStream inputStream = LivingKnowledgeEvaluation.class.getClassLoader().getResourceAsStream(propFileName);
		config = new Properties ();
		
	/*	if (inputStream != null) {
			config.load(inputStream);
		} else {
			throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
		}
	*/
		//NTCIR_VERSION = config.getProperty("ntcir_task");
		NTCIR_VERSION = "ntcir11_Temporalia_taskdata";
	//	root = "/home/souza/NTCIR-eval/"+NTCIR_VERSION+"/Evaluation Data/"+topic;
		root = "/home/souza/NTCIR-eval/"+NTCIR_VERSION+"/TaskData/TIR/"+topic;	
		walk (root);
	}
	
	public Map<String,String> getEvaluatedDocuments ()
	{
		return documents;
	}
	
	public static void walk(String path) throws IOException
	{

		File root = new File(path);
		File[] list = root.listFiles();

		for (File f : list) {
			if (f.isDirectory()) {
				walk(f.getAbsolutePath());
		
			} else {
				
				if (!f.getAbsolutePath().contains(".rel"))
					continue;
				FileReader fr = new FileReader (f);
				BufferedReader br = new BufferedReader (fr);
				
				String line;
				while ((line = br.readLine()) != null)
				{
					StringTokenizer token = new StringTokenizer (line);
				 
					while (token.hasMoreTokens())
					{
						String rel;
						String id;
						
						id = token.nextToken();
						rel = token.nextToken();
						
						documents.put(id,rel);
						
						if (!rel.contentEquals("L0")) {
							totalRelevant++;
						}
					}
				 
				}
				br.close();
			}
		}
	}
	
	public HashMap<String,Double> classifyDocuments (Map<LivingKnowledgeSnapshot,Double> incomingDocuments)
	{
		
		int total = 20;
		double sum = 0.0f;
		int i = 0;
		int relevant = 0;
		double precision;
		double recall;
		
		totalRelevantPRF = 0;
		ArrayList<Point> precRecall = new ArrayList<Point>();
		
		HashMap<String,Double> classified = new HashMap<String,Double>();
		//HashMap<String,Double>
		for (LivingKnowledgeSnapshot article : incomingDocuments.keySet())
		{
			if (i<=total)
				i++;
			if (documents.containsKey(article.getDocId()))
			{
				String id = article.getDocId();
				String relevance = documents.get(article.getDocId());
				
				double numberRelevance;
				
				
				if (relevance.contentEquals("L0"))
				{
					numberRelevance = 0;
					if (i<=total)
					{
						precision = (double) relevant / i;
						recall = (double) relevant / total;
						Point point = new Point ();
						point.setPrecision(precision);
						point.setRecall(recall);
						precRecall.add(point);
						sum +=precision;
						
					}
				}
				else
				{
					numberRelevance = 1;
					if (i<=total)
					{
						relevant++;
						precision = (double) relevant/i;
						recall = (double) relevant / total;
						Point point = new Point ();
						point.setPrecision(precision);
						point.setRecall(recall);
						precRecall.add(point);
						sum = sum + precision;
						
					}
				}
				
				classified.put(id,numberRelevance);
			}
			else
			{
				classified.put(article.getDocId(), (double)0);
				if (i<=total)
				{
					precision = (double) relevant / i;
					recall = (double) relevant / total;
					Point point = new Point ();
					point.setPrecision(precision);
					point.setRecall(recall);
					precRecall.add(point);
					sum +=precision;
				}
			}
			
		}
		totalRelevantPRF = relevant;
		if (relevant==0)
			 avPrecision = 0.0f;
		else
			 avPrecision = (double) sum/total;
		
		if (BestAvPrecision <= avPrecision)
		{
			BestAvPrecision = avPrecision;
			setBestprecRecall(precRecall);
		}
		return classified;
	}
	
	public int getTotalRelevantPRF() {
		return totalRelevantPRF;
	}

	public double getPrecisionAtn (StringBuilder results, int total)
	{
		double precision = 0;
		
		int i = 0;
		int relevant = 0;
		StringTokenizer token = new StringTokenizer (results.toString());
		
		while (token.hasMoreTokens())
		{
			i++;
			
			if (i > total)
				break;
			
			String current = token.nextToken();
			String relevance = getArticleRelevance (current);
			if (relevance.contentEquals("L1") || (relevance.contentEquals("L2")))
				relevant++;
			
			precision = (double) relevant/total;
			
		}
	
		return precision;
	}
	
	public String getArticleRelevance (String docID)
	{
		if (documents.containsKey(docID))
			return documents.get(docID);
		else
			return "--";
	}
	
	private static Map<String, Double> sortByComparator(HashMap<String, Double> unsortMap, final boolean order)
	{

	            List<Entry<String, Double>> list = new LinkedList<Entry<String, Double>>(unsortMap.entrySet());

	            // Sorting the list based on values
	            Collections.sort(list, new Comparator<Entry<String, Double>>()
	            {
	                public int compare(Entry<String, Double> o1,
	                        Entry<String, Double> o2)
	                {
	                    if (order)
	                    {
	                        return o1.getValue().compareTo(o2.getValue());
	                    }
	                    else
	                    {
	                        return o2.getValue().compareTo(o1.getValue());

	                    }
	                }
	            });

	            // Maintaining insertion order with the help of LinkedList
	            Map<String, Double> sortedMap = new LinkedHashMap<String, Double>();
	            for (Entry<String, Double> entry : list)
	            {
	                sortedMap.put(entry.getKey(), entry.getValue());
	            }

	            return sortedMap;
	}

	public ArrayList<Point> getBestprecRecall() {
		return BestprecRecall;
	}

	private void setBestprecRecall(ArrayList<Point> bestprecRecall) {
		BestprecRecall = bestprecRecall;
	}
}
