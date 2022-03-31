package api;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RestApiService {

	@Autowired
	ParameterRepository parameterRepository;

	public List<ParameterDto> getAllList() {
		List<ParameterDto> list = parameterRepository.findAll().stream().map(e -> {
			ParameterDto dto = new ParameterDto();
			dto.setId(e.getId());
			dto.setData(e.getData());
			return dto;
		}).collect(Collectors.toList());
		return list;
	}

	public ParameterDto getListById(String id) {
		ParameterDto dto = new ParameterDto();

		if (parameterRepository.existsById(id)) {
			Parameter entity = parameterRepository.getListById(id);
			dto.setId(entity.getId());
			dto.setData(entity.getData());
		}
		return dto;
	}

	@Transactional
	public ParameterDto save(ParameterDto parameterDto) {
		Parameter parameter = new Parameter.ParameterBuilder(parameterDto).build();
		Parameter list = parameterRepository.save(parameter);

		ParameterDto dto = new ParameterDto(list.getId(), list.getData());
		return dto;
	}

	@Transactional
	public ParameterDto update(String id, ParameterDto parameterDto) {
		ParameterDto list = new ParameterDto();
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
