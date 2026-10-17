package com.lab.jpa.gestaoufc.controller;

import com.lab.jpa.gestaoufc.dto.EventoRequestDTO;
import com.lab.jpa.gestaoufc.dto.EventoResponseDTO;
import com.lab.jpa.gestaoufc.dto.LutaRequestDTO;
import com.lab.jpa.gestaoufc.dto.LutaResponseDTO;
import com.lab.jpa.gestaoufc.service.EventoService;
import com.lab.jpa.gestaoufc.service.LutaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/lutas")
@RequiredArgsConstructor
public class LutaController {

    private final LutaService lutaService;

    @GetMapping
    public ResponseEntity<Page<LutaResponseDTO>> listar(@PageableDefault(
            page = 0, size = 10, sort = "evento.data", direction = Sort.Direction.ASC) Pageable pageable){
        Page<LutaResponseDTO> lutas = lutaService.listarTodos(pageable);
        return ResponseEntity.ok(lutas);
    }

    @PostMapping
    public ResponseEntity<LutaResponseDTO> criar(@Valid @RequestBody LutaRequestDTO dto){
        LutaResponseDTO lutaSalva = lutaService.salvarLuta(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(lutaSalva);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LutaResponseDTO> obter(@PathVariable UUID id){
        LutaResponseDTO luta = lutaService.buscarLutaPorID(id);
        return ResponseEntity.ok(luta);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LutaResponseDTO> atualizar(@PathVariable UUID id, @Valid @RequestBody LutaRequestDTO dto){
        LutaResponseDTO lutaAtualizada = lutaService.atualizarLuta(id, dto);
        return ResponseEntity.ok(lutaAtualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id){
        lutaService.removerLuta(id);
        return ResponseEntity.noContent().build();
    }
}