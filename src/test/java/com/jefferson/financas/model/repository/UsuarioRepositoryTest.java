package com.jefferson.financas.model.repository;

import com.jefferson.financas.model.entity.Usuario;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

//@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UsuarioRepositoryTest {

    @Autowired
    UsuarioRepository repository;
    @Autowired
    TestEntityManager entityManager;

    //Teste de integração
    @Test
    public void deveVerificarAExistenciaDeUmEmail(){
        //cenário
        Usuario user = Usuario.builder().nome("usuario").email("usuario@email.com").build();
        //repository.save(user);
        entityManager.persist(user);

        //Ação/ execução
        boolean result = repository.existsByEmail("usuario@email.com");

        //Verificação
        Assertions.assertThat(result).isTrue();
    }

    @Test
    public void deveRetornarFalsoQuandoNaoHouberUsuarioCadastradoComOEmail(){
        //Cenario
        //repository.deleteAll();

        //Acao
        boolean result = repository.existsByEmail("usuario@email.com");

        //verificacao
        Assertions.assertThat(result).isFalse();
    }

    @Test
    public void devePersistirUmUsuarioNaBaseDeDados(){
        //cenario
        Usuario  usuario = criarusuario();

        //acao
        Usuario usuarioSalvo = repository.save(usuario);

        //verificacao
        Assertions.assertThat(usuarioSalvo.getId()).isNotNull();
    }

    @Test
    public void deveBuscarOUsuarioPorEmail(){
        //cenario
        Usuario  usuario = criarusuario();
        entityManager.persist(usuario);

        //verificacao
        Optional<Usuario> usuarioOptional = repository.findByEmail("email@email.com");

        Assertions.assertThat(usuarioOptional.isPresent()).isTrue();

    }

    @Test
    public void deveRetornarVazioAoBuscarUsuarioPorEmailQuandoNaoExistenaBase(){
        //cenario

        //verificacao
        Optional<Usuario> usuarioOptional = repository.findByEmail("email@email.com");

        Assertions.assertThat(usuarioOptional.isPresent()).isFalse();
    }

    public static Usuario criarusuario(){
        return Usuario
                .builder()
                .nome("usuario")
                .email("email@email.com")
                .senha("senha")
                .build();
    }
}
