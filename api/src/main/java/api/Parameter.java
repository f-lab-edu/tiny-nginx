package api;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table("parameter")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Parameter {
	@Id
	@Column("id")
	private Long id;
	@Column("data")
	private String data;
}

