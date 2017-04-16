package de.l3s.souza.word2vecseedfinder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.StringTokenizer;

import de.l3s.elasticquery.Article;
import de.l3s.elasticquery.ElasticMain;
import de.l3s.elasticquery.LivingKnowledgeSnapshot;
import de.l3s.souza.evaluation.PairDocumentSimilarity;
import de.l3s.souza.evaluation.ScoreFunctions;
import de.l3s.souza.preprocess.PreProcess;
import de.unihd.dbs.heideltime.standalone.DocumentType;
import de.unihd.dbs.heideltime.standalone.HeidelTimeStandalone;
import de.unihd.dbs.heideltime.standalone.OutputType;
import de.unihd.dbs.heideltime.standalone.POSTagger;
import de.unihd.dbs.heideltime.standalone.exceptions.DocumentCreationTimeMissingException;
import de.unihd.dbs.uima.annotator.heideltime.resources.Language;

public class Query 
{
	private static final boolean localmode = false;
	private ScoreFunctions urlScoreObject;
	private int terms;
	private BufferedWriter bw;
	private int maxSimTerms;
	private static HeidelTimeStandalone heidelTime;
	private int maxDoc;
	private static ArrayList<String> vowels;
	private String currentQuery;
	private static StringBuilder sb = new StringBuilder();
	private String nextQuery;
	private HashMap<String, Article> articlesWithoutDuplicates;
	private HashMap<LivingKnowledgeSnapshot, Double> articles;
	private HashMap<String,Integer> queryTerms;
	private deepLearningUtils deepLearning;
	private QueryExpansion queryExpansion;
	private HashMap<String,Article> retrievedDocuments;
	private static HashSet<String> domains;
	private HashSet<String> similarTermsLinks;
	private double beta;
	private HashMap<Article,Double> finalDocSet;
	private HashSet<String> entitiesInLinks;
	private HashMap<String,Double> entitiesCandidates;
	private ArrayList<String> entitiesFromBabelFy;
	public HashMap<String, Article> getArticlesWithoutDuplicates() {
		return articlesWithoutDuplicates;
	}

	public void setArticlesWithoutDuplicates(
			HashMap<String, Article> articlesWithoutDuplicates) {
		this.articlesWithoutDuplicates = articlesWithoutDuplicates;
	}

	public String getCurrentQuery() {
		return currentQuery;
	}
	
	public void setCurrentQuery(String currentQuery) {
		this.currentQuery = currentQuery;
	}
	
	public int getTerms() {
		return terms;
	}

	public void setTerms(int terms) {
		this.terms = terms;
	}

	public int getMaxSimTerms() {
		return maxSimTerms;
	}

	public void setMaxSimTerms(int maxSimTerms) {
		this.maxSimTerms = maxSimTerms;
	}

	public int getMaxDoc() {
		return maxDoc;
	}

	public void setMaxDoc(int maxDoc) {
		this.maxDoc = maxDoc;
	}

	
	public String buildNextQuery () throws IOException
	{
		
		//extractSimilarTermsCurrentQuery (maxSimTerms);
	
		//Map<String, Double> ordered = sortByComparator (queryCandidatesScores,DESC);
		
		return nextQuery;
	}

	public Query(String topicID,String runname, String initialQuery,int limit,String field,int terms, int maxSimTerms,
			String eventDate, int maxDoc, int maxIter, double alpha,double beta,double gama,double scoreParam) throws Exception {
		
		super();
		this.beta = beta;
		urlScoreObject = new ScoreFunctions (scoreParam);
		entitiesCandidates = new HashMap<String,Double>();
	
		BufferedWriter res = new BufferedWriter(new FileWriter("/home/souza/NTCIR-eval/ntcir12_Temporalia_taskdata/Evaluation Data/"+topicID+"/"+topicID+"."+runname+".res", true));
		
		bw = new BufferedWriter(new FileWriter("output.txt", true));
		BufferedWriter out = new BufferedWriter
    		    (new OutputStreamWriter(new FileOutputStream("SeedFinderDeepLearning.html"),"UTF-8"));
		
		  sb.append("<html>");
		    sb.append("<head>");
		    
		    sb.append("<title> SeedFinder"+ field +" search Results");
		    sb.append("</title>");
		    sb.append("<style>");
		    sb.append("table, th, td {");
		    sb.append("border: 1px solid black;");
		    sb.append("border-collapse: collapse;");
		    sb.append("}");				    
		    sb.append("th, td {");
		    sb.append(" padding: 5px;");
		    sb.append("}");
		    sb.append("</style>");
		    sb.append("</head>");
		    sb.append("<body><center><b>SeedFinder " + field +"-search</b></center>");
		    sb.append("<center><b>Parameters {alpha: "+ alpha + " beta: "+ beta +" gama: "+ gama+ " scoreFunction: "+scoreParam+"}</b></center>");
		  //  sb.append("<body><center><b>URL search</b></center>");
		    sb.append("<script>");
		    sb.append("var e = document.getElementById('parent')");
		    sb.append ("e.onmouseover = function() {");
		    sb.append("document.getElementById('popup').style.display = 'block'");
		    sb.append ("}");
		    sb.append("e.onmouseout = function() {");
		    sb.append("document.getElementById('popup').style.display = 'none';");
		    sb.append("}");
		    sb.append("</script>");
		vowels = new ArrayList<String>();
		   vowels.add("a");
	       vowels.add("e");
	       vowels.add("i");
	       vowels.add("o");
	       vowels.add("u");
		domains = new HashSet<String>();
		readDomains ();
	/*	heidelTime = new HeidelTimeStandalone(Language.GERMAN,
                DocumentType.NEWS,
                OutputType.TIMEML,
                "src/main/resources/config.props",
                POSTagger.TREETAGGER, true);*/
		this.terms = terms;
		this.maxSimTerms = maxSimTerms;
		this.maxDoc = maxDoc;
		this.currentQuery = initialQuery;
		queryTerms = new HashMap<String,Integer>();
		retrievedDocuments = new HashMap<String,Article>();
		similarTermsLinks = new HashSet<String>();
		new ElasticMain (initialQuery,limit,field,"souza_livingknowledge");
		articlesWithoutDuplicates = new HashMap<String,Article>();
		articles = new HashMap<LivingKnowledgeSnapshot,Double>();
		finalDocSet = new HashMap<Article,Double>();
		addQueryTerms(initialQuery);
		processQuery(initialQuery,field);
	//	handleDuplicates(articles);
		
		deepLearning = new deepLearningUtils ("articles.txt");
		deepLearning.loadModel("pathToSaveModel.txt");
	/*	Collection<String> nearest = deepLearning.getWordsNearest("merkel",10);
		
		  for (Iterator iterator = nearest.iterator(); iterator.hasNext();) 
		  {
		        String element = (String) iterator.next();
		        System.out.print (element +" ");

		  }*/
		//deepLearning.trainRetrievedDocuments(articlesWithoutDuplicates, "/home/souza/workspace/deepLearningSeedFinder/articles.txt");
		queryExpansion = new QueryExpansion(initialQuery, articlesWithoutDuplicates, articles, maxSimTerms, terms,eventDate, alpha, beta);
		//deepLearning.loadModel("pathToSaveModel.txt");

		populateRetrivedDocuments();
	/*	if (localmode)
			deepLearning.trainRetrievedDocuments(articlesWithoutDuplicates, "/Users/tarcisio/Documents/workspace/deepLearningSeedFinder/articles.txt");
		else
			deepLearning.trainRetrievedDocuments(articlesWithoutDuplicates, "/home/souza/workspace/deepLearningSeedFinder/articles.txt"); */
	//	deepLearning.loadModel("pathToSaveModel.txt");
	//	extractEntitiesFromDocuments();
		//queryExpansion.extractSimilarTermsQuery(deepLearning, annotations,entitiesCandidates);
		
		queryExpansion.extractSimilarTermsUrls(deepLearning,heidelTime,gama);
		HashSet<String> nextQuery;
		nextQuery = queryExpansion.getNextQuery();
		//testBabelFy(articlesWithoutDuplicates);
		//extractEntitiesFromDocuments();
		int iter = 1;
		String currentQueryString;
	
		while (iter <= maxIter)
		{
			
			currentQueryString = "";
			Iterator<String> iterator = nextQuery.iterator();
			int size = nextQuery.size();
			int position = 0;
			while (iterator.hasNext()) 
			{
			    
				if (position==size)
				{
					currentQueryString = currentQueryString + iterator.next().toString();
				}
				else
					currentQueryString = currentQueryString + iterator.next().toString() + " ";
				
				position++;
				
			}
	
			/*currentQueryString = currentQueryString.replaceAll("ue", "ü");
			currentQueryString = currentQueryString.replaceAll("oe", "ö");
			currentQueryString = currentQueryString.replaceAll("ae", "ä");*/
			System.out.println ("Processing query: "+currentQueryString+" "+"iter: "+iter);
			addQueryTerms(currentQueryString);
			processQuery(currentQueryString,"url");
		//	handleDuplicates(articles);
			populateRetrivedDocuments();
		
	/*	if (localmode)
			deepLearning.trainRetrievedDocuments(articlesWithoutDuplicates, "/Users/tarcisio/Documents/workspace/deepLearningSeedFinder/articles.txt");
		else
			deepLearning.train(articlesWithoutDuplicates, "/home/souza/workspace/deepLearningSeedFinder/articles.txt");
		*/
			
			queryExpansion.setCurrentQuery(currentQueryString);
			queryExpansion.setArticlesWithoutDup(articlesWithoutDuplicates);
		//	extractEntitiesFromDocuments();
	//		queryExpansion.extractSimilarTermsUrls();
			
			queryExpansion.extractSimilarTermsUrls(deepLearning,heidelTime,gama);
			//queryExpansion.extractSimilarTermsQuery(deepLearning, annotations,entitiesCandidates);
			nextQuery = queryExpansion.getNextQuery();
			
			//evaluateDocuments();
			iter ++;
			
		}
		
		fitFinalDoc();
		finalDocSet = urlScoreObject.urlScoreFunction("2002", finalDocSet);
		sortFinalDoc();
	//	evaluateDocuments();
	//	sortFinalDoc();
		deepLearning.closeFiles();
		int currentDoc = 0;
		
		sb.append("<table style=\"width:100%\">");
		sb.append("<tr>");
		sb.append("<th>Timestamp</th>");
		sb.append("<th>score</th>");
		sb.append("<th>article</th>");
		sb.append("<th>relevance</th>");
		sb.append("<th>URL</th>");
		sb.append("</tr>");
		
		PreProcess preprocess = new PreProcess ();
		PairDocumentSimilarity parser = new PairDocumentSimilarity ();
		HashSet <String> cs = new HashSet<String>();
		cs = queryExpansion.getCollectionSpecification();
		int relevance;
		
		int articleNumber = 1;
		for(Entry<Article, Double> s : finalDocSet.entrySet())
		{
			
			String article = preprocess.removeStopWords(s.getKey().getText());
			double sim = parser.getHigherScoreSimilarity(article, cs);
			
			if (sim < 0.4)
				relevance = 0;
			else
				relevance = 1;
			
			if (articleNumber > maxDoc)
				break;
			String snippet;
			if (s.getKey().getText().length() <= 100)
				snippet = s.getKey().getText();
			else
				snippet = s.getKey().getText().substring(0, 100);
			BufferedWriter page = new BufferedWriter
	    		    (new OutputStreamWriter(new FileOutputStream(articleNumber+".html"),"UTF-8"));
			sb.append("<tr>");
			sb.append("<td>" + s.getKey().getTimestamp() + "</td>");
			sb.append("<td>" + s.getKey().getScore() + "</td>");
			sb.append("<td>" + snippet + "</td>");
			sb.append("<td>" + relevance + "</td>");
			sb.append("<td><a href=\"" + articleNumber + ".html"+"\">" + s.getKey().getUrl() + "</a>" + "</td>");
			articleNumber++;
			page.write(s.getKey().getHtml());
			page.close();
		}
		sb.append("</table>");
		sb.append("</body></html>");
		out.write(sb.toString());
		out.close();
		
		for(Entry<String, Integer> s : queryTerms.entrySet())
		{
			System.out.println (s.getKey() + " " + s.getValue());
			
		}
	}

	public void readDomains () throws IOException
	{
		File f;
		if (localmode)			
			 f = new File ("/Users/tarcisio/complete.txt");
		else
			 f = new File ("/home/souza/complete.txt");
		FileReader fr = new FileReader (f);
		BufferedReader br = new BufferedReader (fr);
		
		String line;
		
		while ((line=br.readLine())!=null)
		{
			domains.add(line);
		}
		
		
	}
	
	public void addQueryTerms (String q)
	{
		StringTokenizer token = new StringTokenizer (q);
		
		while (token.hasMoreTokens())
		{
			String current = token.nextToken();
			if (queryTerms.containsKey(current))
			{
				int currentFreq = queryTerms.get(current);
				queryTerms.put(current, currentFreq+1);
			}
			else
				queryTerms.put(current, 1);
		}	
	}
	
	private void handleDuplicates(HashMap<String, Article> art) throws MalformedURLException {
		
		articlesWithoutDuplicates.clear();
		articlesWithoutDuplicates = new  HashMap<String,Article>();
		
		for(Entry<String, Article> s : art.entrySet()) {		
			URL url = new URL (s.getValue().getUrl());
			articlesWithoutDuplicates.put(url.getPath(),s.getValue());
		}
	
	}
	
	@SuppressWarnings("unchecked")
	public void processQuery (String query,String field) throws Exception
	{
			 ElasticMain.setKeywords(query);
			 ElasticMain.setField(field);
			 ElasticMain.run();
			 articles = (HashMap<LivingKnowledgeSnapshot, Double>) ElasticMain.getResults();
	}	

	public void fitFinalDoc ()
	{
		for(Entry<String, Article> s : retrievedDocuments.entrySet())
		{
			
			finalDocSet.put(s.getValue(), s.getValue().getScore());
		}
	}
	
	public void sortFinalDoc ()
	{
		
		Map<Article, Double> ordered = sortByComparator (finalDocSet,false);
		finalDocSet.clear();
		finalDocSet = (HashMap<Article, Double>) ordered;
	
	}
	public void populateRetrivedDocuments () throws IOException, DocumentCreationTimeMissingException
	{
		
		for(Entry<String, Article> s : articlesWithoutDuplicates.entrySet())
		{
			retrievedDocuments.put(s.getValue().getUrl(), s.getValue());
		}
		
	}
	
	private static Map<Article, Double> sortByComparator(Map<Article, Double> unsortMap, final boolean order)
	{

	            List<Entry<Article, Double>> list = new LinkedList<Entry<Article, Double>>(unsortMap.entrySet());

	            // Sorting the list based on values
	            Collections.sort(list, new Comparator<Entry<Article, Double>>()
	            {
	                public int compare(Entry<Article, Double> o1,
	                        Entry<Article, Double> o2)
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
	            Map<Article, Double> sortedMap = new LinkedHashMap<Article, Double>();
	            for (Entry<Article, Double> entry : list)
	            {
	                sortedMap.put(entry.getKey(), entry.getValue());
	            }

	            return sortedMap;
	}
	
	
}
