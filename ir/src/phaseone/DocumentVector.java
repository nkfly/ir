package phaseone;
import java.util.HashMap;
import java.util.Map;


public class DocumentVector implements Comparable<DocumentVector>{
	int id;
	Map<Integer, Double> dimension2value = new HashMap<Integer, Double>();
	double value = 0;
	Double length = null;
	String time;
	public void setTime(String t){
		this.time = t;
	}
	
	public String getTime(){
		return this.time;
	}
	public DocumentVector(int id){
		this.id = id;
	}
	
	public int getId(){
		return id;
	}
	
	public void setValue(int dimension, double value){
		dimension2value.put(dimension, value);
	}
	
	public double length(){
		if (length == null){
			length = 0.0;
			for (Integer dimension : dimension2value.keySet()){
				length += dimension2value.get(dimension)*dimension2value.get(dimension);
			}
			length = Math.sqrt(length);
		}
		return length;
	}

	@Override
	public int compareTo(DocumentVector arg0) {
		if (this.value < arg0.value)return 1;
		else if (this.value > arg0.value)return -1;
		return 0;
	}
	
}