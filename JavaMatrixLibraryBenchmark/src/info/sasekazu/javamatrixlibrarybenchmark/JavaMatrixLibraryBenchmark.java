package info.sasekazu.javamatrixlibrarybenchmark;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import org.jblas.FloatMatrix;
import org.jblas.Solve;
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

		XYSeries series = new XYSeries("solve");

		System.out.println("Matrix Solver Benchmark. Solve for X in AX = B");
		for (int i = 2; i < 10; i++) {
			System.out.println("i="+i);
			FloatMatrix A = FloatMatrix.rand(i, i);
			FloatMatrix B = FloatMatrix.rand(i);
			long s = System.currentTimeMillis();
			FloatMatrix X = Solve.solve(A, B);
			long e = System.currentTimeMillis();
			series.add(i, e - s);
		}

		XYSeriesCollection data = new XYSeriesCollection();
		data.addSeries(series);
		JFreeChart chart = ChartFactory.createScatterPlot(
				"Benchmark of linear solver", "Matrix size", "Time [ms]", data,
				PlotOrientation.VERTICAL, true, false, false);

		ChartPanel cpanel = new ChartPanel(chart);
		getContentPane().add(cpanel, BorderLayout.CENTER);
	}
}