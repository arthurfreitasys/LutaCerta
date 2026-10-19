package com.lab.jpa.gestaoufc.service;

import com.lab.jpa.gestaoufc.domain.enums.CategoriaPeso;
import com.lab.jpa.gestaoufc.domain.model.Evento;
import com.lab.jpa.gestaoufc.domain.model.Luta;
import com.lab.jpa.gestaoufc.domain.model.Lutador;
import com.lab.jpa.gestaoufc.dto.LutaRequestDTO;
import com.lab.jpa.gestaoufc.dto.LutaResponseDTO;
import com.lab.jpa.gestaoufc.exception.RegraDeNegocioException;
import com.lab.jpa.gestaoufc.exception.ResourceNotFoundException;
import com.lab.jpa.gestaoufc.mapper.LutaMapper;
import com.lab.jpa.gestaoufc.repository.EventoRepository;
import com.lab.jpa.gestaoufc.repository.LutaRepository;
import com.lab.jpa.gestaoufc.repository.LutadorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) //ativa o mockito
class LutaServiceTest {

    @Mock private LutaRepository lutaRepository;
    @Mock private LutaMapper lutaMapper;
    @Mock private EventoRepository eventoRepository;
    @Mock private LutadorRepository lutadorRepository;

    @InjectMocks
    private LutaService lutaService;

    private UUID eventoId;
    private UUID lutador1Id;
    private UUID lutador2Id;
    private Evento evento;
    private Lutador lutador1;
    private Lutador lutador2;

    @BeforeEach //feito pra rodar antes de qualquer teste pra criar objetos de exemplo
    void setUp() {
        eventoId = UUID.randomUUID();
        lutador1Id = UUID.randomUUID();
        lutador2Id = UUID.randomUUID();

        evento = new Evento();
        evento.setId(eventoId);

        lutador1 = new Lutador();
        lutador1.setId(lutador1Id);

        lutador2 = new Lutador();
        lutador2.setId(lutador2Id);
    }

    @Test
    void salvarLuta_deveSalvarComSucesso_quandoDadosValidos() {
        LutaRequestDTO dto = new LutaRequestDTO(eventoId, lutador1Id, lutador2Id, CategoriaPeso.PESO_PESADO, null);
        LutaResponseDTO responseEsperado = new LutaResponseDTO(
                UUID.randomUUID(), "UFC 310", "Lutador 1", "Lutador 2", CategoriaPeso.PESO_PESADO, null
        ); //monta o cenario do teste

        when(eventoRepository.findById(eventoId)).thenReturn(Optional.of(evento));  //"quando alguém perguntar ao eventoRepository por esse ID, responda que achou"
        when(lutadorRepository.findById(lutador1Id)).thenReturn(Optional.of(lutador1));
        when(lutadorRepository.findById(lutador2Id)).thenReturn(Optional.of(lutador2));
        when(lutaRepository.save(any(Luta.class))).thenAnswer(inv -> inv.getArgument(0));
        when(lutaMapper.toDTO(any(Luta.class))).thenReturn(responseEsperado);

        //executa o que está sendo testado
        LutaResponseDTO resultado = lutaService.salvarLuta(dto);

        assertThat(resultado).isEqualTo(responseEsperado); //verifica se teve a resposta esperada
        verify(lutaRepository).save(any(Luta.class)); // confirma se realmente foi feito o teste
    }

    @Test
    void salvarLuta_deveLancarExcecao_quandoLutadoresIguais() {
        LutaRequestDTO dto = new LutaRequestDTO(eventoId, lutador1Id, lutador1Id, CategoriaPeso.PESO_PESADO, null);

        when(eventoRepository.findById(eventoId)).thenReturn(Optional.of(evento));
        when(lutadorRepository.findById(lutador1Id)).thenReturn(Optional.of(lutador1));

        assertThatThrownBy(() -> lutaService.salvarLuta(dto))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("Lutadores não podem ser iguais");

        verify(lutaRepository, never()).save(any());
    }

    @Test
    void salvarLuta_deveLancarExcecao_quandoEventoNaoEncontrado() {
        LutaRequestDTO dto = new LutaRequestDTO(eventoId, lutador1Id, lutador2Id, CategoriaPeso.PESO_PESADO, null);

        when(eventoRepository.findById(eventoId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> lutaService.salvarLuta(dto))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(lutaRepository, never()).save(any());
    }

    @Test
    void buscarLutaPorID_deveLancarExcecao_quandoNaoEncontrado() {
        UUID idInexistente = UUID.randomUUID();
        when(lutaRepository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> lutaService.buscarLutaPorID(idInexistente))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void removerLuta_deveRemover_quandoEncontrada() {
        UUID id = UUID.randomUUID();
        when(lutaRepository.existsById(id)).thenReturn(true);

        lutaService.removerLuta(id);

        verify(lutaRepository).deleteById(id);
    }

    @Test
    void removerLuta_deveLancarExcecao_quandoNaoEncontrada() {
        UUID id = UUID.randomUUID();
        when(lutaRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> lutaService.removerLuta(id))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(lutaRepository, never()).deleteById(any());
    }
}