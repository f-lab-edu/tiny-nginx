package mvc;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class ParameterRepositoryTest {

	@Autowired
	private ParameterRepository parameterRepository;

	private static final String PARAMETER_ID = "id01";
	private static final String PARAMETER_DATA = "data01";

	@BeforeEach
	void setUp() {
		ParameterDto dto = new ParameterDto(PARAMETER_ID, PARAMETER_DATA);
		Parameter parameter = new Parameter.ParameterBuilder(dto).build();
		Parameter saveParameter = parameterRepository.save(parameter);

		assertEquals(parameter.getId(), saveParameter.getId(), PARAMETER_ID);
		assertEquals(parameter.getData(), saveParameter.getData(), PARAMETER_DATA);
	}

	@AfterEach
	void TearDown() {
		parameterRepository.deleteAll();
		Assertions.assertEquals(parameterRepository.findAll(), new ArrayList<>());
	}

}