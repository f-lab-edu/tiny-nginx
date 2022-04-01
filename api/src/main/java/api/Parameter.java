package api;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;

@Table(name = "parameter")
@Entity
@Getter
public class Parameter {
	@Id
	@Column
	private String id;

	@Column
	private String data;

	private Parameter(ParameterBuilder builder) {
		this.id = builder.id;
		this.data = builder.data;
	}

	public Parameter() {
	}

	public static class ParameterBuilder {
		private final String id;
		private final String data;

		public ParameterBuilder(ParameterDto parameterDto) {
			this.id = parameterDto.getId();
			this.data = parameterDto.getData();
		}

		public Parameter build() {
			return new Parameter(this);
		}
	}
}
