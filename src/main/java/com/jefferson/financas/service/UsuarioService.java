package com.jefferson.financas.service;

import com.jefferson.financas.model.entity.Usuario;

import java.util.Optional;

public interface UsuarioService {
    Usuario autenticar(String email, String senha);

    Usuario salvarUsuario(Usuario usuario);

    void validarEmail(String email);

    Optional<Usuario> obterPorId(Long id);
}
