package phaseone;
import java.util.List;
import java.util.Set;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class IRSystem {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String queryFile = "query.txt";
		QueryProcessor qp = new QueryProcessor(queryFile);
		List < List<String> > query = qp.getQuery();
		
		VocabProcessor vp = new VocabProcessor("gramlist.txt");
		InvertedIndexProcessor iip = new InvertedIndexProcessor("inverseindex.txt"); 
		
		DocumentProcessor dp = new DocumentProcessor("news_random_id_unlabeled_new");
		int queryId = 1;
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File("output_bm_feedback.txt")));
		for (int i = 0;i < qp.getQuery().size();i++){
			List <String> grams = query.get(i);
			List <DocumentVector> dvList = BM25.getDocumentRank(grams, vp, iip, dp);
			
			List <String> expandedGrams = null;
			for (int f = 0;f < 3;f++){
				expandedGrams = QueryProcessor.queryExpand(dvList, queryId-1, grams);
				dvList = BM25.getDocumentRank(expandedGrams, vp, iip, dp);
			}			
			
			
			writer.write(queryId + "\n");
			for (int j = 0;j < 100 & j < dvList.size();j++){
				writer.write(dvList.get(j).id+" ");
			}
			writer.write("\n");
			queryId++;
//			if (dvList.size() > 0)System.out.println(dvList.get(0).id);
			
			
		}
		
		writer.close();
		


	}

}
