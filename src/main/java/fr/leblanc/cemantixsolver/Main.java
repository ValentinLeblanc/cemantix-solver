package fr.leblanc.cemantixsolver;

public class Main {

	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		CemantixSolver cemantixSolver = new CemantixSolver();
		cemantixSolver.solve();
		System.out.println("cemantixSolver total time = " + (double) (System.currentTimeMillis() - start) / 1000 + " s");
	}

}
