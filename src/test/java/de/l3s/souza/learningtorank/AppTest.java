package de.l3s.souza.learningtorank;

import java.io.File;

import ciir.umass.edu.eval.Evaluator;
import ciir.umass.edu.learning.RANKER_TYPE;
import ciir.umass.edu.learning.RankList;
import ciir.umass.edu.learning.Ranker;
import ciir.umass.edu.learning.RankerFactory;
import ciir.umass.edu.metric.METRIC;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     * @throws Exception 
     */
    public void testApp() throws Exception
    {
    	Evaluator ev = new Evaluator (RANKER_TYPE.LAMBDAMART,METRIC.MAP,METRIC.MAP);
    	
    	//ev.score("/home/souza/RankLib/models_shuffled/f5.cas", "/home/souza/RankLib/score_testing.txt", "output.txt");
    	ev.rank("/home/souza/RankLib/models_shuffled/f5.cas", "/home/souza/RankLib/score_testing.txt", "output.txt");
    }
}
