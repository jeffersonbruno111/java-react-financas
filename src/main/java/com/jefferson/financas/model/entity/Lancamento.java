package com.jefferson.financas.model.entity;

import lombok.*;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "tb_lancamento")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Lancamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer mes;
    private Integer ano;
    private  String descricao;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario idUsuario;

    private BigDecimal valor;
    @Convert(converter = Jsr310JpaConverters.LocalDateConverter.class)
    private LocalDate dataCadastro;

    @Enumerated(value = EnumType.STRING)
    private TipoLancamento tipo;

    @Enumerated(value = EnumType.STRING)
    private StatusLancamento status;
}
