package phaseone;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class VocabProcessor {
	
	Map <String, Integer> vocab2index;
	Map <Integer, String> index2vocab;
	public Integer getIndex(String vocab){
		return vocab2index.get(vocab);
	}
	public VocabProcessor(String vocabFile) throws IOException{
		vocab2index = new HashMap<String, Integer>();
		index2vocab = new HashMap<Integer, String>();
		
		BufferedReader reader = new BufferedReader(new FileReader(new File(vocabFile)));
		String line = null;
		int index = 0;
		while((line = reader.readLine()) != null){
			vocab2index.put(line, index);
			index2vocab.put(index, line);
			index++;
		}
		reader.close();
		
		
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
