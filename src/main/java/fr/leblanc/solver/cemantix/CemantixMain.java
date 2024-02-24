package fr.leblanc.solver.cemantix;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class CemantixMain {

	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		LocalDate currentDate = LocalDate.now(ZoneId.of("Europe/Paris"));
        String date = currentDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
		CemantixSolver cemantixSolver = new CemantixSolver(date);
		System.out.println("result = " + cemantixSolver.solve());
		System.out.println("total time = " + (double) (System.currentTimeMillis() - start) / 1000 + " s");
	}

}
