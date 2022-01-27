import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class BootstrapTest {
	@Test
	void create() {
		Bootstrap bootstrap = new Bootstrap();
		BootstrapConfiguration configuration = bootstrap.configuration();

		Assertions.assertThat(configuration)
			.hasFieldOrProperty("workerCount");
	}
}
