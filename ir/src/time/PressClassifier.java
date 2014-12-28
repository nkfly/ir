package time;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nlp.DependencyPair;
import nlp.RuleManager2;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import edu.fudan.nlp.cn.tag.POSTagger;
import edu.fudan.nlp.parser.dep.DependencyTree;
import edu.fudan.nlp.parser.dep.JointParser;
import edu.fudan.util.exception.LoadModelException;
import phaseone.BM25;
import phaseone.DocumentProcessor;
import phaseone.DocumentVector;
import phaseone.InvertedIndexProcessor;
import phaseone.QueryProcessor;
import phaseone.VocabProcessor;

public class PressClassifier {
	public static void main(String [] argv)throws Exception{
		String queryFile = "query.txt";
		QueryProcessor qp = new QueryProcessor(queryFile);
		List < List<String> > query = qp.getQuery();
		
		VocabProcessor vp = new VocabProcessor("gramlist.txt");
		InvertedIndexProcessor iip = new InvertedIndexProcessor("inverseindex.txt"); 
		
		String dir = "/home/nkfly/news_random_id_unlabeled_new/";
		DocumentProcessor dp = new DocumentProcessor(dir);
		
		AnswerKeeper ak = new AnswerKeeper("news_to_news_random_id_unlabeled_table_groundtruth_no_original_id.txt");
		
		
		BufferedReader test = new BufferedReader(new FileReader(new File("phase1_pooling_result.txt")));
		String line;
		int i = 0;
		
		JointParser parser = new JointParser("models/dep.m");
		POSTagger tag = new POSTagger("models/seg.m","models/pos.m");
		
		
		while((line = test.readLine()) != null){
			BufferedWriter trainingWriter = new BufferedWriter(new FileWriter(new File("training"+i+".txt")));
			BufferedWriter testWriter = new BufferedWriter(new FileWriter(new File("test"+i+".txt")));
			List <String> grams = query.get(i);
			i++;
			List <DocumentVector> dvList = BM25.getDocumentRank(grams, vp, iip, dp);
			Map <String, Integer> adj2index = new HashMap<String, Integer>();
			Map <String, Integer> adj2df = new HashMap<String, Integer>();
			String regex = "[、？，。\\s+]";
			List <ClassifiedInstance> listOfCi= new ArrayList <ClassifiedInstance>();
			for (DocumentVector dv : dvList){
				try {
					File f = new File(dir+dv.getId()); 
					replaceBr(f);
					SAXReader reader = new SAXReader();
					Document doc = reader.read(f); 
					Element root = doc.getRootElement(); 
					Element foo;				
					
					ClassifiedInstance ci = new ClassifiedInstance();
					ci.id = dv.getId();
					for (Iterator iter = root.elementIterator("doc"); iter.hasNext();) { 
						foo = (Element) iter.next(); 
						for (String sentence : foo.elementText("title").split(regex)){
							processNlpInfo(sentence, tag,parser,adj2index,adj2df,ci);
						}
						for (String sentence : foo.elementText("text").split(regex)){
							processNlpInfo(sentence, tag,parser,adj2index,adj2df,ci);
						}					
					}
					listOfCi.add(ci);
					
				}catch (Exception e){
					e.printStackTrace();
				}
				
				
			}
			
			
//			String [] testDocuments = line.split("\\s+");
//			Set <String> testDocumentSet = new HashSet<String>();
//			for (String td : testDocuments){
//				testDocumentSet.add(td);
//			}
			for (ClassifiedInstance ci : listOfCi){
				if (ak.getNewsFirm(String.valueOf(ci.id)) == null){
					ci.press = 4;
					testWriter.write(ci.press + " ");
					for (String d : ci.dimension2value.keySet()){
						testWriter.write(adj2index.get(d) + ":" + 1+Math.log(ci.dimension2value.get(d))*Math.log(1+((double)listOfCi.size())/adj2df.get(d)) + " ");
					}
					testWriter.write("\n");
				}else {
					ci.press = Integer.parseInt(ak.getNewsFirm(String.valueOf(ci.id)));
					trainingWriter.write(ci.press + " ");
					for (String d : ci.dimension2value.keySet()){
						trainingWriter.write(adj2index.get(d) + ":" + 1+Math.log(ci.dimension2value.get(d))*Math.log(1+((double)listOfCi.size())/adj2df.get(d)) + " ");
					}
					trainingWriter.write("\n");
				}
			}
			
			
			trainingWriter.close();
			testWriter.close();
			
		}
		
		test.close();

		
	}
	
	private static void processNlpInfo(String sentence, POSTagger tag, JointParser parser, Map <String, Integer> adj2index, Map <String, Integer> adj2df,ClassifiedInstance ci){
		String[][] s = tag.tag2Array(sentence);
		if (s == null)return;
		DependencyTree tree = parser.parse2T(s[0],s[1]);
		//System.out.println(tree);
		List <List <String> > wordPropertyMatrix = tree.toList();
		//DependencyPair dp = RuleManager.checkDependencyPair(wordPropertyMatrix);
		DependencyPair dependencyPair = RuleManager2.checkDependencyPair(wordPropertyMatrix);
		if (dependencyPair == null) return;
		
		if (adj2index.get(dependencyPair.getAdjective()) == null){
			adj2index.put(dependencyPair.getAdjective(), adj2index.size());
		}
		
		if (adj2df.get(dependencyPair.getAdjective()) == null){
			adj2df.put(dependencyPair.getAdjective(), 1);
		}else {
			adj2df.put(dependencyPair.getAdjective(), adj2df.get(dependencyPair.getAdjective())+1);
		}
		
		if (ci.dimension2value.get(dependencyPair.getAdjective()) == null){
			ci.dimension2value.put(dependencyPair.getAdjective(), 1.0);
		}else {
			ci.dimension2value.put(dependencyPair.getAdjective(), ci.dimension2value.get(dependencyPair.getAdjective())+1.0);
		}
	}
	
	private static void replaceBr(File f) throws IOException{
		BufferedReader reader = new BufferedReader(new FileReader(f));
		String line;
		String content = "";
		while((line = reader.readLine()) != null){
			content += (line.replace("<BR>", "") + "\n");
		}
		reader.close();
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(f));
		writer.write(content);
		writer.close();		
		
	}
		
	

}

class ClassifiedInstance {
	public Integer id;
	public Integer press;
	public Map <String, Double> dimension2value = new HashMap<String, Double>();
	
}