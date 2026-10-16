package com.lab.jpa.gestaoufc.service;

import com.lab.jpa.gestaoufc.domain.model.Evento;
import com.lab.jpa.gestaoufc.dto.EventoRequestDTO;
import com.lab.jpa.gestaoufc.dto.EventoResponseDTO;
import com.lab.jpa.gestaoufc.exception.ResourceNotFoundException;
import com.lab.jpa.gestaoufc.mapper.EventoMapper;
import com.lab.jpa.gestaoufc.repository.EventoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventoService {

    private final EventoRepository eventoRepository;
    private final EventoMapper eventoMapper;

    @Transactional(readOnly = true)
    public Page<EventoResponseDTO> listarTodos(Pageable pageable){
        return eventoRepository.findAll(pageable).map(eventoMapper::toDTO);
    }

    @Transactional
    public EventoResponseDTO salvar(EventoRequestDTO dto){
        Evento evento = eventoMapper.toEntity(dto);
        Evento eventoSalvo = eventoRepository.save(evento);
        return eventoMapper.toDTO(eventoSalvo);
    }

    @Transactional(readOnly = true)
    public EventoResponseDTO buscarPorID(UUID id){
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado pelo ID "+id));
        return eventoMapper.toDTO(evento);
    }

    @Transactional
    public EventoResponseDTO atualizarEvento(UUID id, EventoRequestDTO dto){
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado pelo ID "+id));
        eventoMapper.updateEntityFromDTO(dto, evento);
        Evento eventoNovo = eventoRepository.save(evento);
        return eventoMapper.toDTO(eventoNovo);
    }

    @Transactional
    public void remover(UUID id){
        if (!eventoRepository.existsById(id)){
            throw new ResourceNotFoundException("Evento não encontrado pelo ID "+id);
        }
        eventoRepository.deleteById(id);
    }
}
