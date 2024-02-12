import static org.junit.Assert.assertNotNull;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.jupiter.api.Test;

import fr.leblanc.cemantixsolver.CemantixSolver;

public class CemantixSolverTest {

	@Test
	void cemantixSolverTest() {
		
		CemantixSolver solver = new CemantixSolver();
		solver.setTest(true);
		
		JSONObject scores = parseScores();
		
		for (String date : scores.keySet()) {
			solver.setDate(date);
			String result = solver.solve();
			assertNotNull(result);
			System.out.println(date + " => " + result);
		}
		
	}
	
	private JSONObject parseScores() {
		JSONObject json = new JSONObject();
		try (InputStream inputStream = new FileInputStream("scores.json")) {
			json = new JSONObject(new JSONTokener(inputStream));
		} catch (IOException ignored) {
			// ignore
		}
		return json;
	}
	
}
