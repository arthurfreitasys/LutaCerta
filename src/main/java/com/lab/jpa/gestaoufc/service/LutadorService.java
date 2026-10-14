package com.lab.jpa.gestaoufc.service;

import com.lab.jpa.gestaoufc.domain.model.Lutador;
import com.lab.jpa.gestaoufc.dto.LutadorRequestDTO;
import com.lab.jpa.gestaoufc.dto.LutadorResponseDTO;
import com.lab.jpa.gestaoufc.exception.ResourceNotFoundException;
import com.lab.jpa.gestaoufc.mapper.LutadorMapper;
import com.lab.jpa.gestaoufc.repository.LutadorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor //construtor com os dois parametros final
public class LutadorService {

    private final LutadorRepository repository; //usado pra conversar com o banco
    private final LutadorMapper mapper;
    //final garante que depois de criado o server

    @Transactional(readOnly = true)
    public Page<LutadorResponseDTO> listarTodos(Pageable pageable){
        return repository.findAll(pageable).map(mapper::toDto);
    } // Ativa paginação na busca geral

    @Transactional
    public LutadorResponseDTO salvar(LutadorRequestDTO dto){
        Lutador lutador = mapper.toEntity(dto);
        Lutador salvo = repository.save(lutador);
        return mapper.toDto(salvo);
    }

    @Transactional(readOnly = true)
    public LutadorResponseDTO buscarPorID(UUID id){
        Lutador lutador = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException( "Lutador não encontrado pelo ID: "+id));
        return mapper.toDto(lutador);
    }

    @Transactional
    public LutadorResponseDTO atualizar(UUID id, LutadorRequestDTO dto){
        Lutador lutador = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Lutador não encontrado pelo ID: "+id));
        mapper.updateEntityFromDTO(dto, lutador);
        Lutador novo = repository.save(lutador);
        return mapper.toDto(novo);
    }

    @Transactional
    public void remover(UUID id){
        if (!repository.existsById(id)){
            throw new ResourceNotFoundException(
                    "Lutador não encontrado pelo ID: "+id);
        }
        repository.deleteById(id);
    }
}
