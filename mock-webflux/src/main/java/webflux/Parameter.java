package webflux;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table("parameter")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Parameter {
	@Id
	@Column("id")
	private Long id;
	@Column("data")
	private String data;

	public Parameter(String data) {
		this.data = data;
	}
}

