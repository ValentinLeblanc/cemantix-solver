import static org.junit.Assert.assertNotNull;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import fr.leblanc.solver.ResourceHelper;
import fr.leblanc.solver.cemantix.CemantixScoreService;
import fr.leblanc.solver.cemantix.CemantixSolver;

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
