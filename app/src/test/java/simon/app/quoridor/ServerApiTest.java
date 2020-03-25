package simon.app.quoridor;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

public class ServerApiTest {
	@Test
	public void beginGameRequest() {

		assertNotNull(ServerAPI.beginGame(ServerAPI.apiBaseUrl + "débuter/", "simar86"));

	}

	@Test
	public void makeMoveRequest() {
		assertNotNull(ServerAPI.makeMove(ServerAPI.apiBaseUrl + "jouer/",
				"4cd7a0e3-256a-4245-b88e-62e250e11754", "MH", "(4, 4)"));
	}
}
