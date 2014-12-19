package phaseone;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;


public class QueryProcessor {
	
	private List <List <String> > query;
	
	public QueryProcessor(String fileName) throws IOException{
		BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)));
		String line = null;
		setQuery(new ArrayList <List <String> >());
		while((line = reader.readLine()) != null) {
			
			String [] words = line.split("\\s+");
//			System.out.print(words);			
			this.query.add(chopGrams(words));
			
			
			
		}
		
		
		reader.close();
		
		
	}

	public static List <String> chopGrams(String [] words){
		List <String> grams = new ArrayList<String>();
		for (int k = 0;k < words.length;k++){
			String word = words[k];
			for (int i = 0;i < word.length()-1;i++){
				if (Character.isLowerCase(word.charAt(i)) || Character.isUpperCase(word.charAt(i)) 
						|| Character.isDigit(word.charAt(i))){
					int j = i+1;
					boolean isDigit = Character.isDigit(word.charAt(i));
					if (isDigit){
						while(j < word.length() 
								&& ( Character.isDigit(word.charAt(j)))){
							j++;
						}
						
					}else {
						while(j < word.length() 
								&& (Character.isLowerCase(word.charAt(j)) || Character.isUpperCase(word.charAt(j)) 
										)){
							j++;
						}
						
					}
					
					grams.add(word.substring(i, j));
					i = j;
					continue;
				}else {
					grams.add(word.substring(i, i+2));
				}
			}
			
		}
			return grams;
	}

	public List <List <String> > getQuery() {
		return query;
	}

	public void setQuery(List <List <String> > query) {
		this.query = query;
	}
	
	public static List <String> queryExpand(List <DocumentVector> dvList, int queryIndex, List<String> q){
		List <String> rq = new ArrayList<String>();
		for (String gram : q){
			rq.add(gram);
		}
		for (int k = 0;k < 5 && k < dvList.size();k++){
			try { 
				File f = new File("news_random_id_unlabeled_new/"+dvList.get(k).id); 
				SAXReader reader = new SAXReader(); 
				Document doc = reader.read(f); 
				Element root = doc.getRootElement(); 
				Element foo; 
				for (Iterator i = root.elementIterator("doc"); i.hasNext();) { 
					foo = (Element) i.next(); 
					List <String> grams = chopGrams(foo.elementText("title").split("\\s+"));
					for (String gram : grams){
						rq.add(gram);
					}
				
				} 
				} catch (Exception e) { 
				e.printStackTrace(); 
				} 
			
		}
		return rq;
		
	}
	
	public static void main(String[] argv){
		try { 
			File f = new File("news_random_id_unlabeled_new/"+0); 
			SAXReader reader = new SAXReader(); 
			Document doc = reader.read(f); 
			Element root = doc.getRootElement(); 
			Element foo; 
			for (Iterator i = root.elementIterator("doc"); i.hasNext();) { 
			foo = (Element) i.next(); 
			System.out.print("title:" + foo.elementText("title")); 
			} 
			} catch (Exception e) { 
			e.printStackTrace(); 
			} 

		
		
	}

	
	

}
