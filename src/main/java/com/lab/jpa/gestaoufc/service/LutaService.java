package com.lab.jpa.gestaoufc.service;

import com.lab.jpa.gestaoufc.domain.model.Evento;
import com.lab.jpa.gestaoufc.domain.model.Luta;
import com.lab.jpa.gestaoufc.domain.model.Lutador;
import com.lab.jpa.gestaoufc.dto.LutaRequestDTO;
import com.lab.jpa.gestaoufc.dto.LutaResponseDTO;
import com.lab.jpa.gestaoufc.exception.ResourceNotFoundException;
import com.lab.jpa.gestaoufc.mapper.LutaMapper;
import com.lab.jpa.gestaoufc.repository.EventoRepository;
import com.lab.jpa.gestaoufc.repository.LutaRepository;
import com.lab.jpa.gestaoufc.repository.LutadorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LutaService {

    private final LutaRepository lutaRepository;
    private final LutaMapper lutaMapper;
    private final EventoRepository eventoRepository;
    private final LutadorRepository lutadorRepository;

    private Evento buscarEvento(UUID id){
        return eventoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Não foi encontrado o evento"));
    }

    private Lutador buscarLutador(UUID id){
        return lutadorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Não foi encontrado o lutador"));
    }

    @Transactional(readOnly = true)
    public Page<LutaResponseDTO> listarTodos(Pageable pageable){
        return lutaRepository.findAll(pageable).map(lutaMapper::toDTO);
    }

    @Transactional
    public LutaResponseDTO salvarLuta(LutaRequestDTO dto){
        Evento evento = buscarEvento(dto.eventoID());
        Lutador lutador1 = buscarLutador(dto.lutador1ID());
        Lutador lutador2 = buscarLutador(dto.lutador2ID());

        Luta luta = new Luta();
        luta.setEvento(evento);
        luta.setLutador1(lutador1);
        luta.setLutador2(lutador2);
        luta.setCategoriaPeso(dto.categoriaPeso());
        luta.setResultadoLuta(dto.resultadoLuta());
        Luta lutaSalva = lutaRepository.save(luta);
        return lutaMapper.toDTO(lutaSalva);
    }

    @Transactional(readOnly = true)
    public LutaResponseDTO buscarLutaPorID(UUID id){
        Luta luta = lutaRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Luta não encontrada"));
                return lutaMapper.toDTO(luta);
    }

    @Transactional
    public LutaResponseDTO atualizarLuta(UUID id, LutaRequestDTO dto){
        Luta luta = lutaRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Luta não encontrada"));
        Evento evento = buscarEvento(dto.eventoID());
        Lutador lutador1 = buscarLutador(dto.lutador1ID());
        Lutador lutador2 = buscarLutador(dto.lutador2ID());

        luta.setEvento(evento);
        luta.setLutador1(lutador1);
        luta.setLutador2(lutador2);
        luta.setCategoriaPeso(dto.categoriaPeso());
        luta.setResultadoLuta(dto.resultadoLuta());
        Luta lutaNova = lutaRepository.save(luta);
        return lutaMapper.toDTO(lutaNova);
    }

    @Transactional
    public void removerLuta(UUID id){
        if (!lutaRepository.existsById(id)){
            throw new ResourceNotFoundException(
                    "Luta não encontrada pelo ID: "+id);
        }
        lutaRepository.deleteById(id);
    }
}
