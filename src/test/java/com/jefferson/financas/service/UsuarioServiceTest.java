package com.jefferson.financas.service;

import com.jefferson.financas.exception.ErroAutenticacao;
import com.jefferson.financas.exception.RegraNegocioException;
import com.jefferson.financas.model.entity.Usuario;
import com.jefferson.financas.model.repository.UsuarioRepository;
import com.jefferson.financas.service.impl.UsuarioServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {

    @SpyBean
    UsuarioServiceImpl service;

    @MockBean
    UsuarioRepository repository;

    /*@BeforeEach
    public void setUp() {
        service = Mockito.spy(UsuarioServiceImpl.class);
    }*/

    @Test
    public void deveSalvarUmUsuario(){
        //cenario
        Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
        Usuario usuario = Usuario.builder().id(1l).nome("nome").email("email@email.com").senha("senha").build();
        Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);

        //acao
        Usuario usuarioSalvo = service.salvarUsuario((new Usuario()));

        //verificacao
        Assertions.assertThat(usuarioSalvo).isNotNull();
        Assertions.assertThat(usuarioSalvo.getId()).isEqualTo(1l);
        Assertions.assertThat(usuarioSalvo.getNome()).isEqualTo("nome");
        Assertions.assertThat(usuarioSalvo.getEmail()).isEqualTo("email@email.com");
        Assertions.assertThat(usuarioSalvo.getSenha()).isEqualTo("senha");
    }

    @Test
    public void naoDeveSalvarUmUsuarioComEmailJaCadastrado(){
        //cenario
        String email = "email@email.com";
        Usuario usuario = Usuario.builder().email(email).build();
        Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail(email);

        //acao
        org.junit.jupiter.api.Assertions.assertThrows(RegraNegocioException.class, () -> service.salvarUsuario(usuario));

        //verificacao
        Mockito.verify(repository, Mockito.never()).save(usuario);
        //Assertions.assertThat(exception).isInstanceOf(RegraNegocioException.class).hasMessage("Usuário não encontrado para o email informado.");
      /*  assertThrows(
                RegraNegocioException.class,
                () -> Mockito.verify(repository, Mockito.never()).save(usuario));*/
    }

    @Test
    public void deveAutenticarUmUsuarioComSucesso(){
        //cenario
        String email = "email@email.com";
        String senha = "senha";

        Usuario usuario = Usuario.builder().email(email).senha(senha).id(1l).build();
        Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));

        //acao
        Usuario result = assertDoesNotThrow(() -> service.autenticar(email, senha));

        //verificacao
        Assertions.assertThat(result).isNotNull();
    }

    @Test
    public void deveLancarErroQuandoNaoEncontrarUsuarioCadastradoComEmailInformado(){
        //cenario
        Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());

        //acao
        /*ErroAutenticacao result = assertThrows(
                ErroAutenticacao.class,
                () -> service.autenticar("email@email.com", "senha"));*/
        Throwable exception = Assertions.catchThrowable(() -> service.autenticar("email@email.com", "123"));
        //verificacao
        Assertions.assertThat(exception).isInstanceOf(ErroAutenticacao.class).hasMessage("Usuário não encontrado para o email informado.");
    }

    @Test
    public void deveLancarErroQuandoSenhaNaoBater(){
        //cenario
        String senha = "senha";
        Usuario usuario = Usuario.builder().email("email@email.com").senha(senha).build();
        Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));

        //acao
        /*ErroAutenticacao result = assertThrows(
                ErroAutenticacao.class,
                () -> service.autenticar("email@email.com", "123"));*/
        Throwable exception = Assertions.catchThrowable(() ->service.autenticar("email@email.com", "123"));
        Assertions.assertThat(exception).isInstanceOf(ErroAutenticacao.class).hasMessage("Senha inválida.");
    }

    @Test
    public void deveValidarEmail(){
        //cenario
        //repository.deleteAll();
        Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);

        //acao
        assertDoesNotThrow(() -> service.validarEmail("email@email.com"));
    }

    @Test
    public void deveLancarErroAoValidarEmailQuandoExistirEmailCadastrado(){
        //cenario
        //repository.deleteAll();
        /*Usuario usuario = Usuario.builder().nome("usuario").email("usuario@email.com").build();
        repository.save(usuario);*/
        Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);

        //acao
        assertThrows(
                RegraNegocioException.class,
                () -> service.validarEmail("usuario@email.com"));
    }
}
