package com.awaymeet.dl4j.test;

import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.LSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.ui.stats.StatsListener;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.RmsProp;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.io.File;
import java.sql.*;

/*
 * 训练快三
 * training
 * */
public class KuaiSanTrainTest {
	private static int HIDDEN_LAYER_WIDTH = 18;
	private static final int HIDDEN_LAYER_CONT = 3;
	
	private static INDArray setV(String r1s, String r2s, String r3s, INDArray indArray, int currentRow) {
		int r1 = Integer.parseInt(r1s), r2 = Integer.parseInt(r2s), r3 = Integer.parseInt(r3s);
		int c1 = r1 - 1;
		int c2 = 6 + r2 - 1;
		int c3 = 6 + 6 + r3 - 1;
		indArray.putScalar(new int[] { 0, c1, currentRow}, 1.0);
		indArray.putScalar(new int[] { 0, c2, currentRow}, 1.0);
		indArray.putScalar(new int[] { 0, c3, currentRow}, 1.0);
		System.out.println(indArray);
		return indArray;
	}
	public static void main(String[] args) throws Exception {
		String kj[][] = { { "2", "1", "6" }, { "4", "2", "4" }, { "3", "5", "1" }, { "2", "2", "2" }, { "1", "1", "1" }};
		//String kj[][] = Read.getData();
		INDArray input = Nd4j.zeros(1, 18, kj.length-1);
		INDArray labels = Nd4j.zeros(1, 18, kj.length-1);
		System.out.println("kj.length"+kj.length);
		for (int kjNumber = 0; kjNumber < kj.length; kjNumber++) {
			if (kjNumber + 1 < kj.length) {
				System.out.println(kj[kjNumber][0]+"——————————————————————"+kj[kjNumber][1]+"——————————————————————"+kj[kjNumber][2]);
				input = setV(kj[kjNumber][0], kj[kjNumber][1], kj[kjNumber][2], input, kjNumber);
				labels = setV(kj[kjNumber + 1][0], kj[kjNumber + 1][1], kj[kjNumber + 1][2], labels, kjNumber);
			}
		}
		System.out.println(input);
		System.out.println("——————————————————————");
		System.out.println(labels);
		System.out.println("——————————————————————");
		DataSet trainingData = new DataSet(input, labels);
		MultiLayerNetwork net = getNet();
		for (int epoch = 0; epoch < 300; epoch++) {
            System.out.println("Epoch是" + epoch);
            net.fit(trainingData.get(0));
            net.rnnClearPreviousState();
        }
		net.save(new File("lottery.net3"));
		System.out.println("2——————————————————————————————————————————————————————————————————————————————————————————————————————————————2");
		
		/*INDArray last = Nd4j.zeros(1, 18,3);
		last.putScalar(new int[] { 0, 1, 0}, 1.0);
		last.putScalar(new int[] { 0, 6, 0}, 1.0);
		last.putScalar(new int[] { 0, 17, 0}, 1.0);
		System.out.println(last);
		INDArray indArray = net.rnnTimeStep(last);
		System.out.println(indArray);*/
	}

	private static MultiLayerNetwork getNet() {
		NeuralNetConfiguration.Builder builder = new NeuralNetConfiguration.Builder();
		builder.seed(123);
		builder.biasInit(0);
		builder.miniBatch(false);
		builder.updater(new RmsProp(0.001));
		builder.weightInit(WeightInit.XAVIER);
		NeuralNetConfiguration.ListBuilder listBuilder = builder.list();
		for (int i = 0; i < HIDDEN_LAYER_CONT; i++) {
			LSTM.Builder hiddenLayerBuilder = new LSTM.Builder();
			hiddenLayerBuilder.nIn(HIDDEN_LAYER_WIDTH);
			hiddenLayerBuilder.nOut(HIDDEN_LAYER_WIDTH);
			hiddenLayerBuilder.activation(Activation.TANH);
			listBuilder.layer(i, hiddenLayerBuilder.build());
		}
		RnnOutputLayer.Builder outputLayerBuilder = new RnnOutputLayer.Builder(LossFunctions.LossFunction.MCXENT);
		outputLayerBuilder.activation(Activation.SOFTMAX);
		outputLayerBuilder.nIn(HIDDEN_LAYER_WIDTH);
		outputLayerBuilder.nOut(HIDDEN_LAYER_WIDTH);
		listBuilder.layer(HIDDEN_LAYER_CONT, outputLayerBuilder.build());
		MultiLayerConfiguration conf = listBuilder.build();
		MultiLayerNetwork net = new MultiLayerNetwork(conf);
		net.init();
		StatsStorage statsStorage = new InMemoryStatsStorage();
		StatsListener statsListener = new StatsListener(statsStorage);
		org.deeplearning4j.ui.api.UIServer server=org.deeplearning4j.ui.api.UIServer.getInstance();
		server.attach(statsStorage);
		net.setListeners(statsListener,new ScoreIterationListener(1));
		return net;
	}
}

class Read {
	static Connection con;     //声明Connection对象
	static Statement sql;      //声明Statement对象
	static ResultSet res;      //声明ResultSet对象
	public Connection getConnection(){    //建立返回值为Connection的方法
		try {                             //加载数据库驱动
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("数据库驱动加载成功");
		}catch(ClassNotFoundException e){
			e.printStackTrace();
		}
		try {                 //通过访问数据库的URL获取数据库连接对象
			con=DriverManager.getConnection("jdbc:mysql://localhost:3306/dianfu","root","ailinn");
			System.out.println("数据库连接成功");
			System.out.print('\n');
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return con;           //按方法要求放回一个Connection对象
	}
	public static String[][] getData() {
		Read c = new Read();       //创建本类对象
		con =c.getConnection();              //与数据库建立连接
		try {
			int jrows=0;
			int serows=1024;
			sql = con.createStatement();
			ResultSet jres = sql.executeQuery("select count(*) jrows from ks");
			while(jres.next()) {
				jrows=jres.getInt("jrows");
			}
			System.out.println(jrows);
			String jsql="select * from ks order by term asc limit "+(jrows-serows)+","+jrows;
			System.out.println(jsql);
			res = sql.executeQuery(jsql);
			String kj[][]=new String[serows][3];
			int i=0;
			while(res.next()) {        //如果当前语句不是最后一条，则进入循环
				String r1 = res.getString("r1");
				String r2 = res.getString("r2");
				String r3 = res.getString("r3");
				System.out.println(r1+"——————————————————————"+r2+"——————————————————————"+r3);
				kj[i][0]=r1;
				kj[i][1]=r2;
				kj[i][2]=r3;
				i++;
			}
			return kj;
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
