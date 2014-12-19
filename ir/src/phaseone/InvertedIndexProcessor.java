package phaseone;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


class PosAndDF{
	public long pos;
	public int df;
}

public class InvertedIndexProcessor {
	
	Map<Integer, PosAndDF> index2pos = new HashMap<Integer, PosAndDF>();
	String invertedIndexFile;
	Set <String> documentIdSet = new HashSet<String>();
	RandomAccessFile reader;
	
	public List <DocumentIdAndRawFreq> getDocumentIdAndRawFreqList(int vocab) throws IOException{
		List <DocumentIdAndRawFreq> documentIdAndRawFreqList= new ArrayList<DocumentIdAndRawFreq>();
		PosAndDF p = index2pos.get(vocab);
		if (p != null){
			reader.seek(p.pos);
			for (int i = 0;i < p.df;i++){
				String line = reader.readLine();
				String [] entries = line.split("\\s+");						
				DocumentIdAndRawFreq d= new DocumentIdAndRawFreq();
				d.docuemntId = Integer.parseInt(entries[0]);
				d.frequency = Integer.parseInt(entries[1]);
				documentIdAndRawFreqList.add(d);
			}
			
		}
		return documentIdAndRawFreqList;
		
	}
	
	public double getIdf(int vocabId){
		if (index2pos.get(vocabId) == null){
			return 1;
		}
		return 1+Math.log(((double)getDocumentNum())/index2pos.get(vocabId).df);
	}
	
	public double getDf(int vocabId){
		return index2pos.get(vocabId).df;
	}
	
	public int getDocumentNum(){
		return documentIdSet.size();
	}
	public InvertedIndexProcessor(String invertedIndexFile) throws IOException{
		this.invertedIndexFile = invertedIndexFile;
		reader = new RandomAccessFile(invertedIndexFile, "r");
		String line = null;
		while((line = reader.readLine()) != null){
			String [] idAndDf = line.split("\\s+");
			PosAndDF p = new PosAndDF();
			p.pos = reader.getFilePointer();
			p.df = Integer.parseInt(idAndDf[1]);
			index2pos.put(Integer.parseInt(idAndDf[0]), p);
			
			for (int i = 0;i < p.df;i++){
				line = reader.readLine();
				String [] entries = line.split("\\s+");
				documentIdSet.add(entries[0]);
			}
			
		}
		
	}
	
	
	
	
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
