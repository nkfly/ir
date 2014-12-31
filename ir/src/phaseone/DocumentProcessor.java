package phaseone;
import java.io.File;


public class DocumentProcessor {
	String newsDirectory;
	double averageDocumentLength;
	public double getAverageDocumentLength() {
		return averageDocumentLength;
	}

	public void setAverageDocumentLength(double averageDocumentLength) {
		this.averageDocumentLength = averageDocumentLength;
	}
	
	public long getDocumentLength(int id){
		
		File document = new File(newsDirectory+"/"+id);
		if(document.exists()){
			return document.length();
		}
		return 0;
		
	}

	public DocumentProcessor(String newsDirectory){
		this.newsDirectory = newsDirectory;
		int documentNumber = 0;
		File folder = new File(newsDirectory);
		if (folder.isDirectory()){
			for (final File fileEntry : folder.listFiles()) {
				averageDocumentLength += fileEntry.length();
				documentNumber++;
		    }
			
		}
		
		
		averageDocumentLength = averageDocumentLength/documentNumber;
		
	}
	
	public File[] getAllDocs(){
		File folder = new File(newsDirectory);
		return folder.listFiles();

	}
	
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
