package com.lab.jpa.gestaoufc.service;

import com.lab.jpa.gestaoufc.domain.model.Evento;
import com.lab.jpa.gestaoufc.dto.EventoRequestDTO;
import com.lab.jpa.gestaoufc.dto.EventoResponseDTO;
import com.lab.jpa.gestaoufc.exception.ResourceNotFoundException;
import com.lab.jpa.gestaoufc.mapper.EventoMapper;
import com.lab.jpa.gestaoufc.repository.EventoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventoServiceTest {

    @Mock private EventoRepository eventoRepository;
    @Mock private EventoMapper eventoMapper;

    @InjectMocks
    private EventoService eventoService;

    private UUID eventoId;
    private Evento evento;
    private EventoRequestDTO requestDTO;
    private EventoResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        eventoId = UUID.randomUUID();

        evento = new Evento();
        evento.setId(eventoId);

        requestDTO = new EventoRequestDTO("UFC 310", LocalDate.of(2026, 12, 6), "Las Vegas, Nevada");
        responseDTO = new EventoResponseDTO(eventoId, "UFC 310", LocalDate.of(2026, 12, 6), "Las Vegas, Nevada");
    }

    @Test
    void salvar_deveSalvarComSucesso() {
        when(eventoMapper.toEntity(requestDTO)).thenReturn(evento);
        when(eventoRepository.save(evento)).thenReturn(evento);
        when(eventoMapper.toDTO(evento)).thenReturn(responseDTO);

        EventoResponseDTO resultado = eventoService.salvar(requestDTO);

        assertThat(resultado).isEqualTo(responseDTO);
        verify(eventoRepository).save(evento);
    }

    @Test
    void buscarPorID_deveRetornar_quandoEncontrado() {
        when(eventoRepository.findById(eventoId)).thenReturn(Optional.of(evento));
        when(eventoMapper.toDTO(evento)).thenReturn(responseDTO);

        EventoResponseDTO resultado = eventoService.buscarPorID(eventoId);

        assertThat(resultado).isEqualTo(responseDTO);
    }

    @Test
    void buscarPorID_deveLancarExcecao_quandoNaoEncontrado() {
        UUID idInexistente = UUID.randomUUID();
        when(eventoRepository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventoService.buscarPorID(idInexistente))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void remover_deveRemover_quandoEncontrado() {
        when(eventoRepository.existsById(eventoId)).thenReturn(true);

        eventoService.remover(eventoId);

        verify(eventoRepository).deleteById(eventoId);
    }

    @Test
    void remover_deveLancarExcecao_quandoNaoEncontrado() {
        UUID idInexistente = UUID.randomUUID();
        when(eventoRepository.existsById(idInexistente)).thenReturn(false);

        assertThatThrownBy(() -> eventoService.remover(idInexistente))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(eventoRepository, never()).deleteById(any());
    }
}