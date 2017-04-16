package de.l3s.souza.evaluation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

public class LivingKnowledgeEvaluation {

	private static String NTCIR_VERSION;
	private static String propFileName = "seedfinder.properties";
	private  Properties config;
	private String root = "";
	
	private static Map<String,Double> documents = new HashMap<String,Double>();
	
	public LivingKnowledgeEvaluation (String topic) throws IOException
	{		
		documents.clear();
		InputStream inputStream = LivingKnowledgeEvaluation.class.getClassLoader().getResourceAsStream(propFileName);
		config = new Properties ();
		
		if (inputStream != null) {
			config.load(inputStream);
		} else {
			throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
		}
		
		NTCIR_VERSION = config.getProperty("ntcir_task");
		root = "/home/souza/NTCIR-eval/"+NTCIR_VERSION+"/Evaluation Data/"+topic;
		
		walk (root);
	}
	
	public Map<String,Double> getEvaluatedDocuments ()
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
						double relevance;
						
						if (rel.contentEquals("L0"))
							relevance = 0;
						else
							relevance = 1;
						
						documents.put(id,relevance);
					}
				}
			}
		}
	}
}
