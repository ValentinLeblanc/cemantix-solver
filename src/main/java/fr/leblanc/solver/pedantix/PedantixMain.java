package fr.leblanc.solver.pedantix;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

public class PedantixMain {

	public static void main(String[] args) {
		LocalDateTime currentDate = LocalDateTime.now(ZoneId.of("Europe/Paris")).minusHours(12);
        String date = currentDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        PedantixSolver pedantixSolver = new PedantixSolver(date);
        ConcurrentHashMap<Integer, String> results = new ConcurrentHashMap<>();

        IntStream.range(0, 100)
                 .parallel()
                 .forEach(i -> {
                     String result = pedantixSolver.solve(Integer.toString(i));
                     results.put(i, result);
                     printResults(results);
                 });
    }

    private static void printResults(ConcurrentHashMap<Integer, String> results) {
        StringBuilder stringBuilder = new StringBuilder();
        results.entrySet().stream()
               .sorted(java.util.Map.Entry.comparingByKey())
               .forEach(entry -> stringBuilder
                                            .append(entry.getValue())
                                            .append(" "));
        System.out.println(stringBuilder.toString());
    }

}
