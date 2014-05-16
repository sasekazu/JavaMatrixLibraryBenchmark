package info.sasekazu.javamatrixlibrarybenchmark;

public class JavaMatrixLibraryBenchmark {

	public JavaMatrixLibraryBenchmark() {
		System.out.println("test");
        long start = System.currentTimeMillis();
        for(int i=0; i<10000; i++){
        	System.out.println(""+i);
        }
        long end = System.currentTimeMillis();
        System.out.println((end - start)  + "ms");
	}

	public static void main(String[] args) {
		new JavaMatrixLibraryBenchmark();
	}

}
