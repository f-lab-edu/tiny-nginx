package mvc;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RestApiService {

	@Autowired
	ParameterRepository parameterRepository;

	@Transactional(readOnly = true)
	public List<ParameterDto> getAllList() {
		return parameterRepository.findAll().stream().map(e -> {
			ParameterDto dto = new ParameterDto();
			dto.setId(e.getId());
			dto.setData(e.getData());
			return dto;
		}).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public ParameterDto getListById(String id) {
		ParameterDto dto = new ParameterDto();

		if (parameterRepository.existsById(id)) {
			Parameter entity = parameterRepository.getById(id);
			dto.setId(entity.getId());
			dto.setData(entity.getData());
		}
		return dto;
	}

	@Transactional
	public ParameterDto save(ParameterDto parameterDto) {
		Parameter parameter = new Parameter.ParameterBuilder(parameterDto).build();
		Parameter list = parameterRepository.save(parameter);

		return new ParameterDto(list.getId(), list.getData());
	}

	@Transactional
	public ParameterDto update(String id, ParameterDto parameterDto) {
		ParameterDto list;
		ParameterDto updateResult = new ParameterDto();

		if (parameterRepository.existsById(id)) {
			list = getListById(id);
			list.setData(parameterDto.getData());
			updateResult = save(list);
		}

		return updateResult;
	}

	@Transactional
	public boolean deleteById(String id) {
		System.out.println(parameterRepository.existsById(id));
		if (parameterRepository.existsById(id)) {
			parameterRepository.deleteById(id);
			return true;
		}
		return false;
	}
}
