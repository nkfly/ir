package time;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AnswerKeeper {
	Map <String, String> document2news;
	Map <String, String> document2time;
	
	public AnswerKeeper(String answer) throws IOException{
		BufferedReader reader = new BufferedReader(new FileReader(new File(answer)));
		document2news = new HashMap<String, String>();
		document2time = new HashMap<String, String>();
		String line;
		while((line = reader.readLine()) != null){
			String [] entries =  line.split("\\s+");
			document2news.put(entries[0], entries[1]);
			document2time.put(entries[0], entries[2]);
		}
		
		reader.close();
		
	}
	
	public String getNewsFirm(String documentId){
		return document2news.get(documentId);		
	}
	
	public String getTime(String documentId){
		return document2time.get(documentId);		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
