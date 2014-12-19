package phaseone;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class BM25 {
	
	public static List <DocumentVector> getDocumentRank(List<String> query,VocabProcessor vp, InvertedIndexProcessor iip, DocumentProcessor dp) throws IOException{
//		DocumentVector queryVector = new DocumentVector(-1);// -1 for query
		Map <Integer, DocumentVector> id2dv = new HashMap<Integer, DocumentVector>();
		for (String vocab : query){
			Integer index = vp.getIndex(vocab);
			if (index == null){
				System.out.println(vocab);
				continue;
			}
			
//			queryVector.dimension2value.put(index, iip.getIdf(index));
			
			double b = 0.75;
			double k1 = 1;
			List <DocumentIdAndRawFreq> dList = iip.getDocumentIdAndRawFreqList(index);
			for (DocumentIdAndRawFreq d : dList){
				DocumentVector dv = id2dv.get(d.docuemntId);
				if (dv == null) {
					dv = new DocumentVector(d.docuemntId);
				}
				dv.dimension2value.put(index, Math.log(iip.getDocumentNum()-iip.getDf(index)+0.5/iip.getDf(index)+0.5)*(k1+1)*d.frequency/(k1*(1-b+b*dp.getDocumentLength(d.docuemntId)/dp.getAverageDocumentLength())+d.frequency ));
				id2dv.put(d.docuemntId, dv);
				
			}
		}
		
		List <DocumentVector> documentVectorList = new ArrayList<DocumentVector>();
		for (Integer documentId : id2dv.keySet()){
			DocumentVector dv = id2dv.get(documentId);
			for (Integer dimension : dv.dimension2value.keySet()){
				dv.value += dv.dimension2value.get(dimension);
			}
			documentVectorList.add(dv);
		}
		
		
		Collections.sort(documentVectorList);
		return documentVectorList;
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
