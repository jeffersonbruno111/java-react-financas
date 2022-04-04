package com.jefferson.financas.service.impl;

import com.jefferson.financas.exception.ErroAutenticacao;
import com.jefferson.financas.exception.RegraNegocioException;
import com.jefferson.financas.model.entity.Usuario;
import com.jefferson.financas.model.repository.UsuarioRepository;
import com.jefferson.financas.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {


    private UsuarioRepository repository;

    @Autowired
    public UsuarioServiceImpl(UsuarioRepository repository) {
        super();
        this.repository = repository;
    }

    @Override
    public Usuario autenticar(String email, String senha) {
        Optional<Usuario> userOptional = repository.findByEmail(email);

        if(!userOptional.isPresent()){
            throw new ErroAutenticacao("Usuário não encontrado para o email informado.");
        }

        if (!userOptional.get().getSenha().equals(senha)){
            throw new ErroAutenticacao("Senha inválida.");
        }
        return userOptional.get();
    }

    @Override
    @Transactional
    public Usuario salvarUsuario(Usuario usuario) {
        validarEmail(usuario.getEmail());
        return repository.save(usuario);
    }

    @Override
    public void validarEmail(String email) {

        boolean existe = repository.existsByEmail(email);
        if(existe){
            throw new RegraNegocioException("Já existe um usuário cadastrado com este email.");
        }
    }

    @Override
    public Optional<Usuario> obterPorId(Long id) {
        return repository.findById(id);
    }
}
