package de.l3s.souza.date;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.protobuf.TextFormat.ParseException;

public class DateUtils {

	public Date convertToDate(String input) throws java.text.ParseException, ParseException {
        Date date = null;
        String format;
        
        if(null == input) {
            return null;
        }
        
          ArrayList<SimpleDateFormat>  dateFormats = new ArrayList<SimpleDateFormat>() {/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

		{
            add(new SimpleDateFormat("M/dd/yyyy"));
            add(new SimpleDateFormat("dd/MM/yyyy"));
            add(new SimpleDateFormat("dd.M.yyyy"));
            add(new SimpleDateFormat("M/dd/yyyy hh:mm:ss a"));
            add(new SimpleDateFormat("dd.M.yyyy hh:mm:ss a"));
            add(new SimpleDateFormat("dd.MMM.yyyy"));
            add(new SimpleDateFormat("dd-MMM-yyyy"));
            add(new SimpleDateFormat("dd-MM-yyyy"));
            add(new SimpleDateFormat("yyyyMMdd"));
//            add(new SimpleDateFormat("yyyy-MM"));
            add(new SimpleDateFormat("yyyy-MM-dd"));
            add(new SimpleDateFormat("dd.MM.yyyy"));
            add(new SimpleDateFormat("yyyy/MM/dd"));
            add(new SimpleDateFormat("yyyy.MM.dd"));
          }
          };
        for (SimpleDateFormat form : dateFormats) {
            form.setLenient(false);
            try {
            	date = form.parse(input);	
            	format = form.toPattern().toString();
            } catch (Exception e)
            {
            	continue;
            }
			
        }
        if (date == null)
        	return date;
        
        return date;
    }
	
	public String[] getDate(String desc) {
		  int count=0;
		  String[] allMatches = new String[1];
//		  Matcher m = Pattern.compile("(0[1-9]|[12][0-9]|3[01])[- /.](0[1-9]|[12][0-9]|3[01])[- /.](19)[9]\\d").matcher(desc);
		  Matcher m = Pattern.compile("(0[1-9]|[12][0-9]|3[01])[- /.](0[1-9]|[12][0-9]|3[01])[- /.](19)\\d\\d").matcher(desc);
//		  Matcher m1 = Pattern.compile("(0[1-9]|[12][0-9]|3[01])[- /.](0[1-9]|[12][0-9]|3[01])[- /.](20)[01]\\d").matcher(desc);
		  Matcher m1 = Pattern.compile("(0[1-9]|[12][0-9]|3[01])[- /.](0[1-9]|[12][0-9]|3[01])[- /.](20)\\d\\d").matcher(desc);
//		  Matcher m2 = Pattern.compile("(19)[9]\\d[- /.](0[1-9]|[12][0-9]|3[01])[- /.](0[1-9]|[12][0-9]|3[01])").matcher(desc);
		  Matcher m2 = Pattern.compile("(19)\\d\\d[- /.](0[1-9]|[12][0-9]|3[01])[- /.](0[1-9]|[12][0-9]|3[01])").matcher(desc);
//		  Matcher m3 = Pattern.compile("(20)[01]\\d[- /.](0[1-9]|[12][0-9]|3[01])[- /.](0[1-9]|[12][0-9]|3[01])").matcher(desc);
		  Matcher m3 = Pattern.compile("(20)\\d\\d[- /.](0[1-9]|[12][0-9]|3[01])[- /.](0[1-9]|[12][0-9]|3[01])").matcher(desc);
//		  Matcher m4 = Pattern.compile("(19)[9]\\d(0[1-9]|[12][0-9]|3[01])(0[1-9]|[12][0-9]|3[01])").matcher(desc);
		  Matcher m4 = Pattern.compile("(19)\\d\\d(0[1-9]|[12][0-9]|3[01])(0[1-9]|[12][0-9]|3[01])").matcher(desc);
//		  Matcher m5 = Pattern.compile("(20)[01]\\d(0[1-9]|[12][0-9]|3[01])(0[1-9]|[12][0-9]|3[01])").matcher(desc);
		  Matcher m5 = Pattern.compile("(20)\\d\\d(0[1-9]|[12][0-9]|3[01])(0[1-9]|[12][0-9]|3[01])").matcher(desc);
//		  Matcher m6 = Pattern.compile("(19)[9]\\d[- /.](0[1-9]|[12][0-9])").matcher(desc);
		  Matcher m6 = Pattern.compile("(19)\\d\\d[- /.](0[1-9]|[12][0-9])").matcher(desc);
//		  Matcher m7 = Pattern.compile("(20)[01]\\d[- /.](0[1-9]|[12][0-9])").matcher(desc);
		  Matcher m7 = Pattern.compile("(20)\\d\\d[- /.](0[1-9]|[12][0-9])").matcher(desc);
		  Matcher m8 = Pattern.compile("(20)\\d\\d").matcher(desc);
		  if (m.find()) {
		    allMatches[count] = m.group();
		    return allMatches;
		  }
		  
		  if (m1.find()) {
			    allMatches[count] = m1.group();
			    return allMatches;
		  }
		  
		  if (m2.find()) {
			    allMatches[count] = m2.group();
			    return allMatches;
			    
			  }
		  
		  if (m3.find()) {
			    allMatches[count] = m3.group();
			    return allMatches;
			    
			  }
		  if (m4.find()) {
			  	
			    allMatches[count] = m4.group();
			    return allMatches;
		  }
		  if (m5.find()) {
			  	
			    allMatches[count] = m5.group();
			    return allMatches;
		  }
		  if (m6.find()) {
			  	
			    allMatches[count] = m6.group();
			    return allMatches;
		  }
		  
		  if (m7.find()) {
			  	
			    allMatches[count] = m7.group();
			    return allMatches;
		  }
		  
		  if (m8.find()) {
			    allMatches[count] = m8.group();
			    return allMatches;
			  }
		  
		  return allMatches;
		}
}
