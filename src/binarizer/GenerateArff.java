package binarizer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class GenerateArff {

	public static void main(String[] args) throws IOException {
		File fout = new File("E:\\HisDoc project\\SCAE\\SCAE_v2\\gae\\weka\\temp.arff");
    	FileOutputStream fos = new FileOutputStream(fout);    
    	BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
    	String arffName = "pz_train";
    	arffName = "@RELATION " + arffName;
    	bw.write(arffName);
    	bw.newLine();
    	bw.newLine();
    	
    	for (int i = 1; i <= 20; i++){
    		bw.write("@ATTRIBUTE feature" + i + " NUMERIC");
    		bw.newLine();
    	}
    	bw.write("@ATTRIBUTE class {0,1,2,3,4}");
    	bw.newLine();
    	bw.newLine();
    	bw.write("@DATA");
    	bw.newLine();
    	
    	BufferedReader br = new BufferedReader(
    			new FileReader("E:\\HisDoc project\\SCAE\\SCAE_v2\\gae\\weka\\pz_train.arff"));
    	try {
    	    StringBuilder sb = new StringBuilder();
    	    String line = br.readLine();

    	    while (line != null) {
    	        bw.write(line);
    	        bw.newLine();
    	        line = br.readLine();
    	    }
    	} finally {
    	    br.close();
    	}
    	bw.close();

	}

}
