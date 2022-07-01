package mvc;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ParameterDto {
	private String id;
	private String data;

	public ParameterDto(String id, String data) {
		this.id = id;
		this.data = data;
	}
}
