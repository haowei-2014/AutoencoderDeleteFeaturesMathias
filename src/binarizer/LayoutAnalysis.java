/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package binarizer;

import diuf.diva.dia.ms.ml.ae.SCAE;
//import diuf.diva.dia.ms.ml.ae.classifier.FFCNN;
import diuf.diva.dia.ms.util.DataBlock;
import diuf.diva.dia.ms.util.DataBlockDisplay;
import diuf.diva.dia.ms.util.Image;
import diuf.diva.dia.ms.util.ImageDataBlock;
import diuf.diva.dia.ms.util.Tracer;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import Hao.wp_NB_Disc_SFS;

/**
 *
 * @author ms
 */
public class LayoutAnalysis {
	
	String trainingImPath = null;
    String trainingGtPath = null;
    SCAE scae = null;
    Dataset dataset = null;
    int[] selectedFeatures = null;
    
    public static int[] colorList = {
        Color.RED.getRGB(),
        Color.GREEN.getRGB(),
        Color.BLUE.getRGB(),
        Color.BLACK.getRGB(),
        Color.WHITE.getRGB(),
        Color.GRAY.getRGB()
    };
    
    public void setup(String scaedat) throws ClassNotFoundException, IOException{
        trainingImPath = "data/parzival/training";
        trainingGtPath = "data/parzival-gt/training";
        scae = SCAE.load(scaedat);
        System.out.println("Number of features: " + scae.getFeatureLength());
        dataset = new Dataset(trainingImPath, trainingGtPath, 2); // max 2 images
    }
    
    public void getFeaturesAndLables(String txtFile) throws IOException{
    	
    	File fout = new File(txtFile);
    	FileOutputStream fos = new FileOutputStream(fout);    
    	BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

    	int fractionKept = 1234; // skip 1233 pixels, get features+label for one, skips 1233 ...
        
        int hSize = scae.getInputWidth()/2 + 1;
        int vSize = scae.getInputHeight()/2 + 1;
        
        for (GTImage gti : dataset) {
            int n = 0;
            for (int px=hSize; px<gti.pixels.getWidth()-hSize; px++) {
                for (int py=vSize; py<gti.pixels.getHeight()-vSize; py++) {
                    if ((++n)%fractionKept != 0) {
                        continue;
                    }
                    
                    scae.setInput(
                            gti.pixels,
                            px - scae.getInputWidth()/2,
                            py - scae.getInputHeight()/2
                    );
                    
                    scae.forward();
                    
                    float[] features = scae.getFeatureVector();
                    int label = gti.classNum[px][py];
              //      System.out.println(n);
                    String featuresAndLable = "";
                    for (int indexFeature=0; indexFeature < features.length; indexFeature++){
                    	featuresAndLable += Float.toString(features[indexFeature]);
                    	featuresAndLable += ",";
                    }
                    featuresAndLable += Integer.toString(label);
                    bw.write(featuresAndLable);
            		bw.newLine();
                }
            }  
        }
        bw.close();
    }
    
    public void generateArff (String txtFile, String arffFile) throws IOException{
    	
    	File fout = new File(arffFile);
    	FileOutputStream fos = new FileOutputStream(fout);    
    	BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
    	String arffName = "pz_train";
    	arffName = "@RELATION " + arffName;
    	bw.write(arffName);
    	bw.newLine();
    	bw.newLine();
    	
    	for (int i = 1; i <= scae.getFeatureLength(); i++){
    		bw.write("@ATTRIBUTE feature" + i + " NUMERIC");
    		bw.newLine();
    	}
    	bw.write("@ATTRIBUTE class {0,1,2,3,4}");
    	bw.newLine();
    	bw.newLine();
    	bw.write("@DATA");
    	bw.newLine();
    	
    	BufferedReader br = new BufferedReader(new FileReader(txtFile));
    	try {
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
    
    public void featureSelection (String arffFile) throws Exception {
    	wp_NB_Disc_SFS sfs = new wp_NB_Disc_SFS();
    	selectedFeatures = sfs.run(arffFile, "null", "null", "null");
    }
    
    public void deleteFeatures (String saveScae) throws IOException{
    	int nbSelectedFeatures = selectedFeatures.length - 1;
    	int[] deletedFeatures = new int[scae.getFeatureLength()-nbSelectedFeatures];
    	int indexDeletedFeature = 0;
    	boolean selected = false;
    	for (int i = 0; i < scae.getFeatureLength(); i++){
    		selected = false;
    		for (int j = 0; j < nbSelectedFeatures; j ++){
    			if (i == selectedFeatures[j]){
    				selected = true;
    				break;
    			}
    		}
    		if (selected == false){
				deletedFeatures[indexDeletedFeature] = i;
				indexDeletedFeature++;
			}
    	}
    	scae.deleteFeatures(deletedFeatures);
    	scae.save(saveScae);
    }
    
    public double crossValidation(String arffFile) throws Exception {
    	DataSource source = new DataSource(arffFile);
		Instances trainingData = source.getDataSet();
		if (trainingData.classIndex() == -1)
			trainingData.setClassIndex(trainingData.numAttributes() - 1);
		NaiveBayes nb = new NaiveBayes();
		nb.setUseSupervisedDiscretization(true);
		Evaluation evaluation = new Evaluation(trainingData);
		evaluation.crossValidateModel(nb, trainingData, 10, new Random(1));
		System.out.println(evaluation.toSummaryString());
		return evaluation.errorRate();
	}
    

    /**
     * @param args the command line arguments
     * @throws Exception 
     */
    
    public static void main(String[] args) throws Exception {
        LayoutAnalysis la = new LayoutAnalysis();
        la.setup("scae.dat");
        la.getFeaturesAndLables("weka\\pz_train.txt");
        la.generateArff("weka\\pz_train.txt", "weka\\pz_train.arff");
        la.featureSelection("E:\\HisDoc project\\SCAE\\SCAE_v2\\gae\\weka\\pz_train.arff");
        la.deleteFeatures("scae_reduced.dat");
        //================
        la.setup("scae_reduced.dat");
        la.getFeaturesAndLables("weka\\pz_train_reduced.txt");
        la.generateArff("weka\\pz_train_reduced.txt", "weka\\pz_train_reduced.arff");
        la.crossValidation("weka\\pz_train_reduced.arff");
    }
    
}
