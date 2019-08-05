package com.awaymeet.dl4j.test;
import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.WorkspaceMode;
import org.deeplearning4j.nn.conf.layers.LSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.stats.StatsListener;
import org.deeplearning4j.ui.storage.FileStatsStorage;
import org.deeplearning4j.util.ModelSerializer;
import org.junit.Test;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.RmsProp;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.io.File;
import java.io.IOException;

public class ShuangSeQiuTrainTest {

	private static int batchSize = 64;
    private static long seed = 123;
    private static int numEpochs = 40000;
    private static boolean modelType = true;
    private static int numResult = 49;
    
    @Test
    public void testTrain() throws IOException{
    	int ori[][]={{2,4,14,16,20,22,11},{13,14,15,21,23,29,13},{4,5,7,9,21,30,4},{16,22,24,26,28,31,6},{1,4,14,18,24,29,4},{4,8,14,18,20,27,3},{6,15,17,26,28,31,3},{7,16,19,22,24,28,2},{5,24,27,29,31,32,10},{3,6,8,20,24,32,7},{1,3,6,9,19,31,16},{1,17,27,29,31,33,12},{9,11,13,18,21,22,15},{12,15,19,20,29,32,14},{5,8,20,22,31,33,3},{12,21,27,29,31,33,4},{1,2,6,12,16,18,8},{2,9,13,15,22,30,15},{1,8,19,24,29,30,4},{6,15,18,19,24,32,9},{6,11,16,19,21,25,1},{3,14,20,24,26,33,10},{4,6,8,11,30,33,11},{1,14,17,20,22,32,4},{6,9,11,15,20,26,10},{12,20,24,25,30,33,12},{1,14,19,22,29,31,16},{7,13,16,23,26,30,1},{3,17,19,24,27,31,12},{3,4,14,20,23,27,1},{4,5,7,9,16,18,6},{7,8,12,21,23,27,12},{4,5,6,8,13,18,16},{13,14,17,19,21,29,1},{1,6,11,15,19,31,10},{7,10,11,15,24,26,11},{4,16,22,25,29,31,8},{3,6,9,13,16,19,16},{8,9,10,13,15,28,9},{4,6,10,11,21,23,2},{3,10,13,22,23,28,15},{3,7,10,12,18,29,10},{3,11,18,25,30,33,14},{2,12,16,22,25,32,6},{1,6,17,19,27,31,14},{6,14,16,17,23,29,7},{1,6,12,13,24,32,13},{15,17,19,22,25,26,4},{2,9,13,23,24,26,16},{5,6,9,18,23,31,11},{6,7,11,14,27,32,8},{9,12,21,27,29,30,5},{1,7,12,14,18,25,10},{2,10,13,16,23,32,8},{1,5,7,9,10,20,16},{9,11,15,22,24,26,3},{9,15,19,21,23,29,15},{4,8,9,13,28,33,4},{3,13,15,18,21,33,16},{4,5,7,10,12,22,16},{8,11,17,23,32,33,10},{4,19,22,26,29,30,11},{2,6,8,10,11,17,13},{3,13,15,19,20,27,14},{15,16,21,27,30,33,4},{1,8,23,25,28,29,10},{1,10,14,15,18,31,13},{3,7,11,21,30,33,7},{2,5,7,8,20,27,4},{2,12,13,23,27,28,12},{3,11,17,18,24,25,6},{4,11,18,19,26,32,4},{4,5,24,28,30,33,9},{5,7,9,11,19,25,5},{11,15,16,20,24,31,4},{1,2,3,14,19,33,3},{5,7,14,16,18,21,1},{7,10,21,23,31,33,14},{10,13,19,21,24,30,7},{2,4,5,8,11,30,2},{1,7,10,22,31,32,15},{2,6,9,13,28,32,12},{6,10,14,15,19,23,15},{1,5,10,19,26,28,12},{21,22,26,28,31,32,7},{8,12,16,19,26,32,3},{13,17,20,21,22,27,1},{4,5,6,8,9,18,11},{6,10,13,15,32,33,15},{1,7,17,23,25,31,11},{4,14,16,23,28,29,3},{5,15,19,25,26,29,15},{6,8,15,19,20,31,5},{1,7,8,10,12,24,1},{3,6,18,19,21,31,1},{3,15,17,23,27,30,11},{2,10,11,17,18,29,16},{3,9,13,22,23,25,6},{8,13,17,18,20,27,13},{4,6,15,28,32,33,14}};
		int ssq[][]=new int[ori.length][7];
		for(int i=ori.length-1;i>=0;i--){
			ssq[(ori.length-1)-i]=ori[i];
		}
		INDArray input = Nd4j.zeros(1, numResult,ssq.length);
		INDArray labels = Nd4j.zeros(1, numResult,ssq.length);
		for(int i=1;i<ssq.length;i++){
			//System.out.println(ssq[i-1][0]+"————"+ssq[i][0]+"————"+input.shape()[0]+"————"+input.shape()[1]+"————"+input.shape()[2]);
			input = setV(ssq[i-1], input, i-1);
			labels = setV(ssq[i], labels, i);
		}
		/*if(true)return;*/
		DataSet trainingData = new DataSet(input, labels);
		
		
		MultiLayerNetwork model = getNetModel(numResult,numResult);
        UIServer uiServer = UIServer.getInstance();
        StatsStorage statsStorage = new FileStatsStorage(new File(System.getProperty("java.io.tmpdir"), "ui-stats.dl4j"));
        uiServer.attach(statsStorage);
        System.out.println(model.summary());
        model.setListeners(new StatsListener(statsStorage), new ScoreIterationListener(10));
        long startTime = System.currentTimeMillis();
        for (int epoch = 0; epoch <= numEpochs; epoch++) {
            model.fit(trainingData.get(0));
            model.rnnClearPreviousState();
            if(epoch%500==0){
            	try {
        			model.save(new File("ssq"+epoch+"via.net"), true);
        		} catch (IOException e) {
        			e.printStackTrace();
        		}
            }
        }
        long endTime = System.currentTimeMillis();
        System.out.println("=============run time=====================" + (endTime - startTime));
        INDArray indArray = model.rnnTimeStep(labels);
		shelp2(indArray, 100);
    }
    
    @Test
    public void testPredict() throws IOException{
    	File f=new File("ssq4000via.net");
    	MultiLayerNetwork model =ModelSerializer.restoreMultiLayerNetwork(f);
    	
    	int ori[][]={{2,4,14,16,20,22,11},{13,14,15,21,23,29,13},{4,5,7,9,21,30,4},{16,22,24,26,28,31,6},{1,4,14,18,24,29,4},{4,8,14,18,20,27,3},{6,15,17,26,28,31,3},{7,16,19,22,24,28,2},{5,24,27,29,31,32,10},{3,6,8,20,24,32,7},{1,3,6,9,19,31,16},{1,17,27,29,31,33,12},{9,11,13,18,21,22,15},{12,15,19,20,29,32,14},{5,8,20,22,31,33,3},{12,21,27,29,31,33,4},{1,2,6,12,16,18,8},{2,9,13,15,22,30,15},{1,8,19,24,29,30,4},{6,15,18,19,24,32,9},{6,11,16,19,21,25,1},{3,14,20,24,26,33,10},{4,6,8,11,30,33,11},{1,14,17,20,22,32,4},{6,9,11,15,20,26,10},{12,20,24,25,30,33,12},{1,14,19,22,29,31,16},{7,13,16,23,26,30,1},{3,17,19,24,27,31,12},{3,4,14,20,23,27,1},{4,5,7,9,16,18,6},{7,8,12,21,23,27,12},{4,5,6,8,13,18,16},{13,14,17,19,21,29,1},{1,6,11,15,19,31,10},{7,10,11,15,24,26,11},{4,16,22,25,29,31,8},{3,6,9,13,16,19,16},{8,9,10,13,15,28,9},{4,6,10,11,21,23,2},{3,10,13,22,23,28,15},{3,7,10,12,18,29,10},{3,11,18,25,30,33,14},{2,12,16,22,25,32,6},{1,6,17,19,27,31,14},{6,14,16,17,23,29,7},{1,6,12,13,24,32,13},{15,17,19,22,25,26,4},{2,9,13,23,24,26,16},{5,6,9,18,23,31,11},{6,7,11,14,27,32,8},{9,12,21,27,29,30,5},{1,7,12,14,18,25,10},{2,10,13,16,23,32,8},{1,5,7,9,10,20,16},{9,11,15,22,24,26,3},{9,15,19,21,23,29,15},{4,8,9,13,28,33,4},{3,13,15,18,21,33,16},{4,5,7,10,12,22,16},{8,11,17,23,32,33,10},{4,19,22,26,29,30,11},{2,6,8,10,11,17,13},{3,13,15,19,20,27,14},{15,16,21,27,30,33,4},{1,8,23,25,28,29,10},{1,10,14,15,18,31,13},{3,7,11,21,30,33,7},{2,5,7,8,20,27,4},{2,12,13,23,27,28,12},{3,11,17,18,24,25,6},{4,11,18,19,26,32,4},{4,5,24,28,30,33,9},{5,7,9,11,19,25,5},{11,15,16,20,24,31,4},{1,2,3,14,19,33,3},{5,7,14,16,18,21,1},{7,10,21,23,31,33,14},{10,13,19,21,24,30,7},{2,4,5,8,11,30,2},{1,7,10,22,31,32,15},{2,6,9,13,28,32,12},{6,10,14,15,19,23,15},{1,5,10,19,26,28,12},{21,22,26,28,31,32,7},{8,12,16,19,26,32,3},{13,17,20,21,22,27,1},{4,5,6,8,9,18,11},{6,10,13,15,32,33,15},{1,7,17,23,25,31,11},{4,14,16,23,28,29,3},{5,15,19,25,26,29,15},{6,8,15,19,20,31,5},{1,7,8,10,12,24,1},{3,6,18,19,21,31,1},{3,15,17,23,27,30,11},{2,10,11,17,18,29,16},{3,9,13,22,23,25,6},{8,13,17,18,20,27,13},{4,6,15,28,32,33,14}};
		int ssq[][]=new int[ori.length][7];
		for(int i=ori.length-1;i>=0;i--){
			ssq[(ori.length-1)-i]=ori[i];
		}
		INDArray input = Nd4j.zeros(1, numResult,ssq.length);
		INDArray labels = Nd4j.zeros(1, numResult,ssq.length);
		for(int i=1;i<ssq.length;i++){
			//System.out.println(ssq[i-1][0]+"————"+ssq[i][0]+"————"+input.shape()[0]+"————"+input.shape()[1]+"————"+input.shape()[2]);
			input = setV(ssq[i-1], input, i-1);
			labels = setV(ssq[i], labels, i);
		}
		
		INDArray indArray = model.rnnTimeStep(labels);
		shelp2(indArray, 100);
    }
    private static INDArray setV(int item[], INDArray indArray, int currentRow) {
    	for(int i=0;i<item.length;i++){
    		int temp;
    		if(i==item.length-1){
    			temp=33+item[i];
    		}else{
    			temp=item[i];
    		}
    		temp=temp-1;
    		System.out.println(0+"——————"+currentRow+"——————"+temp);
    		indArray.putScalar(new int[] { 0, temp,currentRow}, 1.0);
		}
		return indArray;
	}
    
	private static void shelp(INDArray labels,int colsNumber){
		long all=1;
		for(long a:labels.shape()){
			System.out.println(a);
			all*=a;
		}
		
		String row="";
		for(long i=0;i<all;i++){
			row+=labels.getDouble(i)+"	";
			if((i+1)%(colsNumber)==0){
				System.out.println(row);
				row="";
			}
		}
	}
	private static void shelp2(INDArray indArray,int colsNumber){
		long all=1;
		for(long a:indArray.shape()){
			System.out.println(a);
			all*=a;
		}
		String row="";
		for(long i=0;i<all;i++){
			double b = indArray.getDouble(i);
			if(b<0.001){
				b=0;
			}
			String c=b+"00000";
			c=c.substring(0,c.indexOf(".")+3);
			row+=c+"	";
			if((i+1)%(colsNumber)==0){
				System.out.println(row);
				row="";
			}
		}
	}
	
	private static MultiLayerNetwork getNetModel(int inputNum, int outputNum) {
        	MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                    .trainingWorkspaceMode(WorkspaceMode.ENABLED).inferenceWorkspaceMode(WorkspaceMode.ENABLED)
                    .seed(seed)
                    .optimizationAlgo( OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                    .weightInit(WeightInit.XAVIER)
                    .updater(new RmsProp.Builder().rmsDecay(0.95).learningRate(0.005).build())
                    .list()
                    .layer(new LSTM.Builder().name("lstm1")
                            .activation(Activation.TANH).nIn(inputNum).nOut(256).build())
                    .layer(new LSTM.Builder().name("lstm2")
                            .activation(Activation.TANH).nOut(512).build())
                    .layer(new LSTM.Builder().name("lstm3")
                            .activation(Activation.TANH).nOut(256).build())
                    .layer(new LSTM.Builder().name("lstm4")
                            .activation(Activation.TANH).nOut(128).build())
                    .layer(new LSTM.Builder().name("lstm5")
                            .activation(Activation.TANH).nOut(128).build())
                    .layer(new RnnOutputLayer.Builder().name("output")
                            .activation(Activation.SOFTMAX).nOut(outputNum).lossFunction(LossFunctions.LossFunction.MSE)
                            .build())
                    .build();

            MultiLayerNetwork net = new MultiLayerNetwork(conf);
            net.init();
            return net;
        }
}
