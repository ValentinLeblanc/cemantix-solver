package fr.leblanc.cemantixsolver;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Main {

	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		
		LocalDate currentDate = LocalDate.now(ZoneId.of("Europe/Paris"));
        // Format the current date
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String date = currentDate.format(formatter);
		CemantixSolver cemantixSolver = new CemantixSolver();
		cemantixSolver.setDate(date);
		cemantixSolver.solve();
		System.out.println("cemantixSolver total time = " + (double) (System.currentTimeMillis() - start) / 1000 + " s");
	}

}
