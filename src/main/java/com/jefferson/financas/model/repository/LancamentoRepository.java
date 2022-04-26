package com.jefferson.financas.model.repository;

import com.jefferson.financas.model.entity.Lancamento;
import com.jefferson.financas.model.entity.StatusLancamento;
import com.jefferson.financas.model.entity.TipoLancamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

    @Query( value =
            " select sum(l.valor) from Lancamento l join l.idUsuario u "
          + " where u.id = :idUsuario and l.tipo = :tipo and l.status = :status group by u ")
    BigDecimal obterSaldoPorTipoLancamentoEUsuarioEStatus(@Param("idUsuario") Long idUsuario, @Param("tipo") TipoLancamento tipo, @Param("status") StatusLancamento status);
}
