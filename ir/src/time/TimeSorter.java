package time;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import phaseone.BM25;
import phaseone.DocumentProcessor;
import phaseone.DocumentVector;
import phaseone.InvertedIndexProcessor;
import phaseone.QueryProcessor;
import phaseone.VocabProcessor;

public class TimeSorter {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws DocumentException 
	 */
	public static void main(String[] args) throws IOException, DocumentException {
		VocabProcessor vp = new VocabProcessor("gramlist.txt");
		InvertedIndexProcessor iip = new InvertedIndexProcessor("inverseindex.txt"); 
		
		DocumentProcessor dp = new DocumentProcessor("C:/Users/user/news_random_id_unlabeled_new");
		
		AnswerKeeper ak = new AnswerKeeper("news_to_news_random_id_unlabeled_table_groundtruth_no_original_id.txt");
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File("timeline.txt")));
		
		
		BufferedReader test = new BufferedReader(new FileReader(new File("phase1_pooling_result.txt")));
		String line;
		while((line = test.readLine()) != null){
			List <DocumentVector> timelineList = new ArrayList<DocumentVector>();
			for (String documentId : line.split("\\s+")){
				File f = new File("news_random_id_unlabeled_new/"+documentId); 
				SAXReader reader = new SAXReader(); 
				Document doc = reader.read(f); 
				Element root = doc.getRootElement(); 
				Element foo;
				List <String> gramList = new ArrayList<String>();
				for (Iterator i = root.elementIterator("doc"); i.hasNext();) { 
					foo = (Element) i.next(); 
					List <String> grams = QueryProcessor.chopGrams(foo.elementText("title").split("\\s+"));
					for (String gram : grams){
						gramList.add(gram);
					}
				}
				
				
				List <DocumentVector> dvList = BM25.getDocumentRank(gramList, vp, iip, dp);
				for (int i = 0;i < dvList.size();i++){
					if(!documentId.equals(dvList.get(i).getId())){
						DocumentVector dv = dvList.get(i);
						dv.setTime(ak.getTime(String.valueOf(dvList.get(i).getId())));
						timelineList.add(dv);
						break;
					}
				}
			}
			
			Collections.sort(timelineList, new Comparator<DocumentVector>(){
				@Override
				public int compare(DocumentVector arg0, DocumentVector arg1) {
					return arg0.getTime().compareTo(arg1.getTime());
				}
			});
			
			for (DocumentVector dv: timelineList){
				writer.write(dv.getId()+":"+dv.getTime()+"\t");
			}
			writer.write("\n");
		}
		test.close();
		writer.close();
		
		// TODO Auto-generated method stub

	}

}
