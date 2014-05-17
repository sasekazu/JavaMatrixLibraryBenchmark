package info.sasekazu.javamatrixlibrarybenchmark;

import java.awt.BorderLayout;
import java.util.Random;

import javax.swing.JFrame;

import org.ejml.data.DenseMatrix64F;
import org.ejml.factory.LinearSolver;
import org.ejml.factory.LinearSolverFactory;
import org.ejml.ops.RandomMatrices;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.la4j.LinearAlgebra;
import org.la4j.factory.Basic1DFactory;
import org.la4j.linear.LinearSystemSolver;

public class JavaMatrixLibraryBenchmark extends JFrame {
	private static final long serialVersionUID = 1L;
	final int max = 1000;
	final int min = 2;
	final int bin = 20;

	public static void main(String[] args) {
		JavaMatrixLibraryBenchmark frame = new JavaMatrixLibraryBenchmark();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(10, 10, 500, 500);
		frame.setTitle("Benchmark of Linear Solver");
		frame.setVisible(true);
	}

	JavaMatrixLibraryBenchmark() {
		
		// ベンチマーク実行
		XYSeries dataJblasDouble = runJblasDouble();
		XYSeries dataJblasFloat = runJblasFloat();
		XYSeries dataEjml = runEjml();
		XYSeries dataLa4j = runLa4j();
		XYSeries dataJama = runJama();
		XYSeries dataColt = runColt();
		XYSeries dataCommonsMath = runCommonsMath();

		// JFreeChartによるグラフの作成
		XYSeriesCollection data = new XYSeriesCollection();
		data.addSeries(dataJblasDouble);
		data.addSeries(dataJblasFloat);
		data.addSeries(dataEjml);
		data.addSeries(dataLa4j);
		data.addSeries(dataJama);
		data.addSeries(dataColt);
		data.addSeries(dataCommonsMath);
		JFreeChart chart = ChartFactory.createScatterPlot(
				"Benchmark of linear solver", "Matrix size", "Time [ms]", data,
				PlotOrientation.VERTICAL, true, false, false);
		ChartPanel cpanel = new ChartPanel(chart);
		getContentPane().add(cpanel, BorderLayout.CENTER);
	}
	
	
	XYSeries runJblasDouble(){
		XYSeries data = new XYSeries("jblas(double)");
		for (int i = min; i < max; i += bin) {
			System.out.println("jblas(double) i=" + i);
			org.jblas.DoubleMatrix A = org.jblas.DoubleMatrix.rand(i, i);
			org.jblas.DoubleMatrix b = org.jblas.DoubleMatrix.rand(i);
			long s = System.nanoTime();
			org.jblas.Solve.solve(A, b);
			long e = System.nanoTime();
			data.add(i, (e - s) * 0.000001);
		}
		return data;
	}
	
	XYSeries runJblasFloat(){
		XYSeries data = new XYSeries("jblas(float)");
		for (int i = min; i < max; i += bin) {
			System.out.println("jblas(float) i=" + i);
			org.jblas.FloatMatrix A = org.jblas.FloatMatrix.rand(i, i);
			org.jblas.FloatMatrix b = org.jblas.FloatMatrix.rand(i);
			long s = System.nanoTime();
			org.jblas.Solve.solve(A, b);
			long e = System.nanoTime();
			data.add(i, (e - s) * 0.000001);
		}
		return data;
	}
	
	XYSeries runEjml(){
		// LinearSolverは細かくいろいろ設定できるみたい
		// 行列演算の関数multなどが、出力を引数に与える形式で好みでない
		XYSeries data = new XYSeries("ejml");
		long seed = System.currentTimeMillis();
		Random rand = new Random(seed);
		for (int i = min; i < max; i += bin) {
			System.out.println("ejml i=" + i);
			org.ejml.data.DenseMatrix64F A = RandomMatrices.createRandom(i, i,rand);
			org.ejml.data.DenseMatrix64F b = RandomMatrices.createRandom(i, 1,rand);
			org.ejml.data.DenseMatrix64F x = new org.ejml.data.DenseMatrix64F(i, 1);
			long s = System.nanoTime();
			LinearSolver<DenseMatrix64F> solver = LinearSolverFactory.linear(i);
			if (!solver.setA(A)) {
				System.out.println("Singular matrix");
				System.exit(0);
			}
			if (solver.quality() <= 1e-8) {
				System.out.println("Nearly Singular matrix");
				// System.exit(0);
			}
			solver.solve(b, x);
			long e = System.nanoTime();
			data.add(i, (e - s) * 0.000001);
		}
		return data;
	}
	
	XYSeries runLa4j(){
		// シンプル
		// Sparse対応
		// なぜかGAUSSIANが一番早い
		XYSeries data = new XYSeries("la4j");
		for (int i = min; i < max; i += bin) {
			System.out.println("la4j i=" + i);
			org.la4j.matrix.Matrix a = new Basic1DFactory().createRandomMatrix(i, i);
			org.la4j.vector.Vector b = new Basic1DFactory().createRandomVector(i);
			long s = System.nanoTime();
			LinearSystemSolver solver = a.withSolver(LinearAlgebra.GAUSSIAN);
			solver.solve(b, LinearAlgebra.DENSE_FACTORY);
			long e = System.nanoTime();
			data.add(i, (e - s) * 0.000001);
		}
		return data;
	}
	
	XYSeries runJama(){
		// シンプル
		XYSeries data = new XYSeries("JAMA");
		for (int i = min; i < max; i += bin) {
			System.out.println("jama i=" + i);
			Jama.Matrix A = Jama.Matrix.random(i, i);
			Jama.Matrix b = Jama.Matrix.random(i, 1);
			long s = System.nanoTime();
			A.solve(b);
			long e = System.nanoTime();
			data.add(i, (e - s) * 0.000001);
		}
		return data;
	}
	
	XYSeries runColt(){
		XYSeries data = new XYSeries("colt");
		cern.colt.matrix.DoubleFactory2D F = cern.colt.matrix.DoubleFactory2D.dense;
		for (int i = min; i < max; i += bin) {
			System.out.println("colt i=" + i);
			cern.colt.matrix.DoubleMatrix2D A = F.random(i, i);
			cern.colt.matrix.DoubleMatrix2D B = F.random(i, 1);
			long s = System.nanoTime();
			cern.colt.matrix.linalg.Algebra alg = new cern.colt.matrix.linalg.Algebra();
			alg.solve(A, B);
			long e = System.nanoTime();
			data.add(i, (e - s) * 0.000001);
		}
		return data;
	}
	
	XYSeries runCommonsMath(){
		XYSeries data = new XYSeries("Commons Math");
		Random rand = new Random();
		for (int i = min; i < max; i += bin) {
			System.out.println("Commons Math i=" + i);
			double [][] Araw = new double[i][i];
			for(int r=0; r<i; r++){
				for(int c=0; c<i; c++){
					Araw[r][c] = rand.nextDouble();
				}
			}
			org.apache.commons.math3.linear.RealMatrix A = new org.apache.commons.math3.linear.Array2DRowRealMatrix(Araw, false);
			double [] braw = new double[i];
			for(int n=0; n<i; n++){
				braw[n]=rand.nextDouble();
			}
			org.apache.commons.math3.linear.RealVector b = new org.apache.commons.math3.linear.ArrayRealVector(braw, false);
			long s = System.nanoTime();
			org.apache.commons.math3.linear.DecompositionSolver solver =
					new org.apache.commons.math3.linear.LUDecomposition(A).getSolver();
			solver.solve(b);
			long e = System.nanoTime();
			data.add(i, (e - s) * 0.000001);
		}
		return data;
	}
	
	void testCommonsMath(){
		// sparse対応
		// 共役勾配法などもある
		// 機械学習など多彩な機能が充実
		// User Manualが詳しいが、基本的な行列演算の説明は皆無
		Random rand = new Random();
		int i = 3;
		double [][] Araw = new double[i][i];
		for(int r=0; r<i; r++){
			for(int c=0; c<i; c++){
				Araw[r][c] = rand.nextDouble();
			}
		}
		org.apache.commons.math3.linear.RealMatrix A = new org.apache.commons.math3.linear.Array2DRowRealMatrix(Araw, false);
		double [] braw = new double[i];
		for(int n=0; n<i; n++){
			braw[n]=rand.nextDouble();
		}
		org.apache.commons.math3.linear.RealVector b = new org.apache.commons.math3.linear.ArrayRealVector(braw, false);
		org.apache.commons.math3.linear.DecompositionSolver solver =
				new org.apache.commons.math3.linear.LUDecomposition(A).getSolver();
		org.apache.commons.math3.linear.RealVector x = solver.solve(b);
		org.apache.commons.math3.linear.RealVector Ax = A.operate(x);
		System.out.println("Ax\t" + Ax);
		System.out.println("b\t" + b);
	}
}