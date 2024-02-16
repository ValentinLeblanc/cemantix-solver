import static org.junit.Assert.assertNotNull;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import fr.leblanc.cemantixsolver.CemantixScoreService;
import fr.leblanc.cemantixsolver.CemantixSolver;
import fr.leblanc.cemantixsolver.ResourceHelper;

class CemantixSolverTest {

	@Test
	void cemantixSolverTest() {
		
		JSONObject scores = ResourceHelper.parseJSON(CemantixScoreService.SCORES_JSON);
		
		for (String date : scores.keySet()) {
			CemantixSolver solver = new CemantixSolver(date);
			String result = solver.solve();
			assertNotNull(result);
			System.out.println(date + " => " + result);
		}
		
	}
	
}
