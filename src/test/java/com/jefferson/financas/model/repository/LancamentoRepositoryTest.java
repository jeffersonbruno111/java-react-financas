package com.jefferson.financas.model.repository;

import com.jefferson.financas.model.entity.Lancamento;
import com.jefferson.financas.model.entity.StatusLancamento;
import com.jefferson.financas.model.entity.TipoLancamento;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class LancamentoRepositoryTest {

    @Autowired
    LancamentoRepository repository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    public void  deveSalvarUmLancamento(){
        Lancamento lancamento = criarLancamento();

        lancamento = repository.save(lancamento);

        Assertions.assertThat(lancamento.getId()).isNotNull();
    }

    @Test
    public void deveDeletarUmLancamento(){
        Lancamento lancamento = criarEPersistirUmLancamento();

        lancamento = entityManager.find(Lancamento.class, lancamento.getId());

        repository.delete(lancamento);

        Lancamento lancamentoInexistente = entityManager.find(Lancamento.class, lancamento.getId());
        Assertions.assertThat(lancamentoInexistente).isNull();
    }

    @Test
    public void deveAtualizarUmLancamento(){
        Lancamento lancamento = criarEPersistirUmLancamento();

        lancamento.setAno(2011);
        lancamento.setDescricao("Teste Atualizar");
        lancamento.setStatus(StatusLancamento.CANCELADO);

        repository.save(lancamento);

        Lancamento lancamentoTest = entityManager.find(Lancamento.class, lancamento.getId());

        Assertions.assertThat(lancamento).isEqualTo(lancamentoTest);
    }

    @Test
    public void deveBuscarUmLancamentoPorId(){
        Lancamento lancamento = criarEPersistirUmLancamento();

        Optional<Lancamento> lancamentoEncontrado = repository.findById(lancamento.getId());

        Assertions.assertThat(lancamentoEncontrado.isPresent()).isTrue();
    }

    private Lancamento criarEPersistirUmLancamento() {
        Lancamento lancamento = criarLancamento();
        entityManager.persist(lancamento);
        return lancamento;
    }

    public static Lancamento criarLancamento(){
        return Lancamento.builder()
                .ano(2022)
                .mes(1)
                .descricao("Cartão")
                .valor(BigDecimal.valueOf(300))
                .tipo(TipoLancamento.RECEITA)
                .status(StatusLancamento.PENDENTE)
                .dataCadastro(LocalDate.now())
                .build();
    }
}
