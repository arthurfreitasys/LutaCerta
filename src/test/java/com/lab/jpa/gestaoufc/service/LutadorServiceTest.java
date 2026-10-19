package com.lab.jpa.gestaoufc.service;

import com.lab.jpa.gestaoufc.domain.enums.CategoriaPeso;
import com.lab.jpa.gestaoufc.domain.model.Lutador;
import com.lab.jpa.gestaoufc.dto.LutadorRequestDTO;
import com.lab.jpa.gestaoufc.dto.LutadorResponseDTO;
import com.lab.jpa.gestaoufc.exception.RegraDeNegocioException;
import com.lab.jpa.gestaoufc.exception.ResourceNotFoundException;
import com.lab.jpa.gestaoufc.mapper.LutadorMapper;
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

@ExtendWith(MockitoExtension.class)
class LutadorServiceTest {

    @Mock private LutadorRepository repository;
    @Mock private LutadorMapper mapper;
    @Mock private LutaRepository lutaRepository;

    @InjectMocks
    private LutadorService lutadorService;

    private UUID lutadorId;
    private Lutador lutador;
    private LutadorRequestDTO requestDTO;
    private LutadorResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        lutadorId = UUID.randomUUID();

        lutador = new Lutador();
        lutador.setId(lutadorId);

        requestDTO = new LutadorRequestDTO(
                "Jon Jones", "Bones", CategoriaPeso.PESO_PESADO, 28, 0, 1, "Estados Unidos"
        );

        responseDTO = new LutadorResponseDTO(
                lutadorId, "Jon Jones", "Bones", CategoriaPeso.PESO_PESADO, 28, 1, 0, "Estados Unidos"
        );
    }

    @Test
    void salvar_deveSalvarComSucesso() {
        when(mapper.toEntity(requestDTO)).thenReturn(lutador);
        when(repository.save(lutador)).thenReturn(lutador);
        when(mapper.toDto(lutador)).thenReturn(responseDTO);

        LutadorResponseDTO resultado = lutadorService.salvar(requestDTO);

        assertThat(resultado).isEqualTo(responseDTO);
        verify(repository).save(lutador);
    }

    @Test
    void buscarPorID_deveRetornar_quandoEncontrado() {
        when(repository.findById(lutadorId)).thenReturn(Optional.of(lutador));
        when(mapper.toDto(lutador)).thenReturn(responseDTO);

        LutadorResponseDTO resultado = lutadorService.buscarPorID(lutadorId);

        assertThat(resultado).isEqualTo(responseDTO);
    }

    @Test
    void buscarPorID_deveLancarExcecao_quandoNaoEncontrado() {
        UUID idInexistente = UUID.randomUUID();
        when(repository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> lutadorService.buscarPorID(idInexistente))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void remover_deveRemover_quandoNaoTemHistorico() {
        when(repository.existsById(lutadorId)).thenReturn(true);
        when(lutaRepository.existsByLutador1IdOrLutador2Id(lutadorId, lutadorId)).thenReturn(false);

        lutadorService.remover(lutadorId);

        verify(repository).deleteById(lutadorId);
    }

    @Test
    void remover_deveLancarExcecao_quandoNaoEncontrado() {
        UUID idInexistente = UUID.randomUUID();
        when(repository.existsById(idInexistente)).thenReturn(false);

        assertThatThrownBy(() -> lutadorService.remover(idInexistente))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(repository, never()).deleteById(any());
    }

    @Test
    void remover_deveLancarExcecao_quandoTemHistoricoDeLutas() {
        when(repository.existsById(lutadorId)).thenReturn(true);
        when(lutaRepository.existsByLutador1IdOrLutador2Id(lutadorId, lutadorId)).thenReturn(true);

        assertThatThrownBy(() -> lutadorService.remover(lutadorId))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("já participou de lutas");

        verify(repository, never()).deleteById(any());
    }
}