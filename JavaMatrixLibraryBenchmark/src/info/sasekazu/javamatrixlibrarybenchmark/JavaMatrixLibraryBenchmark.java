package info.sasekazu.javamatrixlibrarybenchmark;

import java.awt.BorderLayout;
import java.util.Random;

import javax.swing.JFrame;

import org.ejml.ops.RandomMatrices;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class JavaMatrixLibraryBenchmark extends JFrame {
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		JavaMatrixLibraryBenchmark frame = new JavaMatrixLibraryBenchmark();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(10, 10, 500, 500);
		frame.setTitle("Benchmark");
		frame.setVisible(true);
	}

	JavaMatrixLibraryBenchmark() {

		final int n = 500;

		// jblas
		XYSeries dataJblas = new XYSeries("jblas");
		System.out.println("Matrix Solver Benchmark. Solve for X in AX = B");
		for (int i = 2; i < n; i++) {
			System.out.println("jblas i="+i);
			org.jblas.FloatMatrix A = org.jblas.FloatMatrix.rand(i, i);
			org.jblas.FloatMatrix b = org.jblas.FloatMatrix.rand(i);
			long s = System.nanoTime();
			org.jblas.Solve.solve(A, b);
			long e = System.nanoTime();
			dataJblas.add(i, (e - s)*0.000001);
		}
		
		// EJML
		// float型マトリクスがないみたい
		// ベクトル型はないみたい
		// LinearSolverは細かくいろいろ設定できるみたい
		// 演算の記述がわかりづらい
		XYSeries dataEJML = new XYSeries("ejml");
		for(int i=2; i<n; i++){
			System.out.println("ejml i="+i);
			Random rand = new Random();
			org.ejml.data.DenseMatrix64F A = RandomMatrices.createRandom(i, i,rand);;
			org.ejml.data.DenseMatrix64F b = RandomMatrices.createRandom(i, 1, rand);
			org.ejml.data.DenseMatrix64F x = new org.ejml.data.DenseMatrix64F(i, 1);
			long s = System.nanoTime();
			if (!org.ejml.ops.CommonOps.solve(A, b, x)) {
				System.out.println("EJML ERROR");
				System.exit(0);
			}
			long e = System.nanoTime();
			dataEJML.add(i, (e - s)*0.000001);
		}

		XYSeriesCollection data = new XYSeriesCollection();
		data.addSeries(dataJblas);
		data.addSeries(dataEJML);
		JFreeChart chart = ChartFactory.createScatterPlot(
				"Benchmark of linear solver", "Matrix size", "Time [ms]", data,
				PlotOrientation.VERTICAL, true, false, false);

		ChartPanel cpanel = new ChartPanel(chart);
		getContentPane().add(cpanel, BorderLayout.CENTER);
	}
}