package com.lab.jpa.gestaoufc.controller;

import com.lab.jpa.gestaoufc.dto.EventoRequestDTO;
import com.lab.jpa.gestaoufc.dto.EventoResponseDTO;
import com.lab.jpa.gestaoufc.service.EventoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/eventos")
@RequiredArgsConstructor
public class EventoController {

    private final EventoService eventoService;

    @GetMapping
    public ResponseEntity<Page<EventoResponseDTO>> listar(@PageableDefault(
            page = 0, size = 10, sort = "data", direction = Sort.Direction.ASC)Pageable pageable){
        Page<EventoResponseDTO> eventos = eventoService.listarTodos(pageable);
        return ResponseEntity.ok(eventos);
    }

    @PostMapping
    public ResponseEntity<EventoResponseDTO> criar (@Valid @RequestBody EventoRequestDTO dto){
    EventoResponseDTO eventoSalvo = eventoService.salvar(dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(eventoSalvo);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventoResponseDTO> obter(@PathVariable UUID id){
        EventoResponseDTO evento = eventoService.buscarPorID(id);
        return ResponseEntity.ok(evento);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventoResponseDTO> atualizar(@PathVariable UUID id, @Valid @RequestBody EventoRequestDTO dto){
        EventoResponseDTO eventoAtualizado = eventoService.atualizarEvento(id, dto);
        return ResponseEntity.ok(eventoAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id){
        eventoService.remover(id);
        return ResponseEntity.noContent().build();
    }
}