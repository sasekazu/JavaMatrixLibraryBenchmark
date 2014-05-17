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
import org.la4j.matrix.Matrix;
import org.la4j.vector.Vector;

public class JavaMatrixLibraryBenchmark extends JFrame {
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		JavaMatrixLibraryBenchmark frame = new JavaMatrixLibraryBenchmark();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(10, 10, 500, 500);
		frame.setTitle("Benchmark of Linear Solver");
		frame.setVisible(true);
	}

	JavaMatrixLibraryBenchmark() {

		final int n = 1000;
		long seed = System.currentTimeMillis();
		Random rand = new Random(seed);
		
		// jblas
		XYSeries dataJblas = new XYSeries("jblas");
		System.out.println("Matrix Solver Benchmark. Solve for X in AX = B");
		for (int i = 2; i < n; i+=10) {
			System.out.println("jblas i="+i);
			org.jblas.DoubleMatrix A = org.jblas.DoubleMatrix.rand(i, i);
			org.jblas.DoubleMatrix b = org.jblas.DoubleMatrix.rand(i);
			long s = System.nanoTime();
			org.jblas.Solve.solve(A, b);
			long e = System.nanoTime();
			dataJblas.add(i, (e - s)*0.000001);
		}
		
		// EJML
		// float型マトリクスがないみたい
		// ベクトル型はないみたい
		// LinearSolverは細かくいろいろ設定できるみたい
		// 行列演算の関数multなどが、出力を引数に与える形式で好みでない
		XYSeries dataEJML = new XYSeries("ejml");
		for(int i=2; i<n; i+=10){
			System.out.println("ejml i="+i);
			org.ejml.data.DenseMatrix64F A = RandomMatrices.createRandom(i, i,rand);;
			org.ejml.data.DenseMatrix64F b = RandomMatrices.createRandom(i, 1, rand);
			org.ejml.data.DenseMatrix64F x = new org.ejml.data.DenseMatrix64F(i, 1);
			long s = System.nanoTime();
			LinearSolver<DenseMatrix64F> solver = LinearSolverFactory.linear(i);
			if( !solver.setA(A) ) {
				System.out.println("Singular matrix");
				System.exit(0);
			}
			if( solver.quality() <= 1e-8 ){
				System.out.println("Nearly Singular matrix");
//				System.exit(0);
			}
			solver.solve(b,x);			
			long e = System.nanoTime();
			dataEJML.add(i, (e - s)*0.000001);
		}
		
		// la4j
		// float非対応
		// Sparse対応
		// なぜかGAUSSIANが一番早い
		XYSeries dataLA4J = new XYSeries("la4j");
		for(int i=2; i<n; i+=10){
			System.out.println("la4j i="+i);
			Matrix a = new Basic1DFactory().createRandomMatrix(i,i);
			Vector b = new Basic1DFactory().createRandomVector(i);
			long s = System.nanoTime();
			LinearSystemSolver solver = a.withSolver(LinearAlgebra.GAUSSIAN);
			solver.solve(b, LinearAlgebra.DENSE_FACTORY);
			long e = System.nanoTime();
			dataLA4J.add(i, (e - s)*0.000001);
		}
		
		// jama
		// float非対応
		XYSeries dataJAMA = new XYSeries("JAMA");
		for(int i=2; i<n; i+=10){
			System.out.println("jama i="+i);
			Jama.Matrix A = Jama.Matrix.random(i, i);
			Jama.Matrix b = Jama.Matrix.random(i,1);
			long s = System.nanoTime();
			A.solve(b);
			long e = System.nanoTime();
			dataJAMA.add(i, (e - s)*0.000001);
		}


		
		// JFreeChartによるグラフの作成
		XYSeriesCollection data = new XYSeriesCollection();
		data.addSeries(dataJblas);
		data.addSeries(dataEJML);
		data.addSeries(dataLA4J);
		data.addSeries(dataJAMA);
		JFreeChart chart = ChartFactory.createScatterPlot(
				"Benchmark of linear solver", "Matrix size", "Time [ms]", data,
				PlotOrientation.VERTICAL, true, false, false);

		ChartPanel cpanel = new ChartPanel(chart);
		getContentPane().add(cpanel, BorderLayout.CENTER);
	}
}