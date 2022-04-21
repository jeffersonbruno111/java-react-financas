package com.jefferson.financas.api.resource;

import com.jefferson.financas.api.dto.AtualizaStatusDTO;
import com.jefferson.financas.api.dto.LancamentoDTO;
import com.jefferson.financas.exception.RegraNegocioException;
import com.jefferson.financas.model.entity.Lancamento;
import com.jefferson.financas.model.entity.StatusLancamento;
import com.jefferson.financas.model.entity.TipoLancamento;
import com.jefferson.financas.model.entity.Usuario;
import com.jefferson.financas.service.LancamentoService;
import com.jefferson.financas.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/lancamentos")
@RequiredArgsConstructor
public class LancamentoResource {

    private final LancamentoService service;
    private final UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity salvar(@RequestBody LancamentoDTO dto){

        try {
            Lancamento entidade = converter(dto);
            entidade = service.salvar(entidade);
            return new ResponseEntity(entidade, HttpStatus.CREATED);
        }catch (RegraNegocioException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @GetMapping
    public ResponseEntity buscar(
            //@RequestParam java.util.Map<String, String> params
            @RequestParam(value = "descricao", required = false) String descricao,
            @RequestParam(value = "mes", required = false) Integer mes,
            @RequestParam(value = "ano", required = false) Integer ano,
            @RequestParam("usuario") Long idUsuario
    ){
        Lancamento lancamentoFiltro = new Lancamento();
        lancamentoFiltro.setDescricao(descricao);
        lancamentoFiltro.setMes(mes);
        lancamentoFiltro.setAno(ano);

        Optional<Usuario> usuario = usuarioService.obterPorId(idUsuario);
        if(!usuario.isPresent()){
            return ResponseEntity.badRequest().body("Não foi possível realizar a consulta. Usuáro não encontrado para o Id informado.");
        }else{
            lancamentoFiltro.setIdUsuario(usuario.get());
        }

        List<Lancamento> lancamentos = service.buscar(lancamentoFiltro);
        return ResponseEntity.ok(lancamentos);
    }

    @GetMapping("{id}")
    public ResponseEntity<LancamentoDTO> buscarPorId(@PathVariable Long id){
        return service.obterPorId(id)
                .map( lancamento -> new ResponseEntity(converterParaDTO(lancamento), HttpStatus.OK))
                .orElseGet( () -> new ResponseEntity(HttpStatus.NOT_FOUND));
    }

    @PutMapping("{id}")
    public ResponseEntity atualizar(@PathVariable Long id, @RequestBody LancamentoDTO dto){
        return service.obterPorId(id).map( entity -> {
            try {
                Lancamento lancamento = converter(dto);
                lancamento.setId(entity.getId());
                service.atualizar(lancamento);
                return ResponseEntity.ok(lancamento);
            }catch (RegraNegocioException e){
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }).orElseGet( () ->
                new ResponseEntity("Lançamento não encontrado na base de dados.", HttpStatus.BAD_REQUEST));
    }

    @PutMapping("{id}/atualiza-status")
    public ResponseEntity atualizarStatus(@PathVariable Long id, @RequestBody AtualizaStatusDTO dto){
        return service.obterPorId(id).map( entity -> {
            StatusLancamento statusSelecionado = StatusLancamento.valueOf(dto.getStatus());

            if(statusSelecionado == null){
                return ResponseEntity.badRequest().body("Não foi possível atualizar o status do lançamento, envie um status válido.");
            }
            try {
                entity.setStatus(statusSelecionado);
                service.atualizar(entity);
                return ResponseEntity.ok(entity);
            }catch (RegraNegocioException e){
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }).orElseGet(() ->
                new ResponseEntity("Lançamento não encontrado na base de dados.", HttpStatus.BAD_REQUEST));
    }

    @DeleteMapping("{id}")
    public ResponseEntity deletar( @PathVariable("id") Long id){
        return service.obterPorId(id).map( entidade -> {
            service.deletar(entidade);
            return new ResponseEntity( HttpStatus.NO_CONTENT);
        }).orElseGet(() ->
                new ResponseEntity("Lançamento não encontrado na base de dados.", HttpStatus.BAD_REQUEST));
    }

    private LancamentoDTO converterParaDTO(Lancamento lancamento){
        return LancamentoDTO.builder()
                .id(lancamento.getId())
                .descricao(lancamento.getDescricao())
                .valor(lancamento.getValor())
                .mes(lancamento.getMes())
                .ano(lancamento.getAno())
                .status(lancamento.getStatus().name())
                .tipo(lancamento.getTipo().name())
                .idUsuario(lancamento.getIdUsuario().getId())
                .build();
    }

    private Lancamento converter(LancamentoDTO dto){
        Lancamento lancamento = new Lancamento();
        lancamento.setId(dto.getId());
        lancamento.setDescricao(dto.getDescricao());
        lancamento.setAno(dto.getAno());
        lancamento.setMes(dto.getMes());
        lancamento.setValor(dto.getValor());

        Usuario usuario = usuarioService
                .obterPorId(dto.getIdUsuario())
                        .orElseThrow( () -> new RegraNegocioException("Usuário não encontrado para o Id informado."));

        lancamento.setIdUsuario(usuario);

        if(dto.getTipo() != null) {
            lancamento.setTipo(TipoLancamento.valueOf(dto.getTipo()));
        }

        if(dto.getStatus() != null){
            lancamento.setStatus(StatusLancamento.valueOf(dto.getStatus()));
        }
        return lancamento;
    }
}
