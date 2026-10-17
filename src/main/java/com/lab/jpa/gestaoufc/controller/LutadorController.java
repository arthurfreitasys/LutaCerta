package com.lab.jpa.gestaoufc.controller;

import com.lab.jpa.gestaoufc.dto.LutadorRequestDTO;
import com.lab.jpa.gestaoufc.dto.LutadorResponseDTO;
import com.lab.jpa.gestaoufc.service.LutadorService;
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
@RequestMapping("/api/lutadores")
@RequiredArgsConstructor
public class LutadorController {

    private final LutadorService lutadorService;

    @GetMapping
    public ResponseEntity<Page<LutadorResponseDTO>> listar(@PageableDefault(
            page = 0, size = 10, sort = "nome", direction = Sort.Direction.ASC)Pageable pageable){
        Page<LutadorResponseDTO> lutadores = lutadorService.listarTodos(pageable);
        return ResponseEntity.ok(lutadores);
    }

    @PostMapping
    public ResponseEntity<LutadorResponseDTO> criar(@Valid @RequestBody LutadorRequestDTO dto){
        LutadorResponseDTO lutadorSalvo = lutadorService.salvar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(lutadorSalvo);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LutadorResponseDTO> obter(@PathVariable UUID id){
        LutadorResponseDTO lutador = lutadorService.buscarPorID(id);
        return ResponseEntity.ok(lutador);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LutadorResponseDTO> atualizar(@PathVariable UUID id, @Valid @RequestBody LutadorRequestDTO dto ){
        LutadorResponseDTO lutadorAtualizado = lutadorService.atualizar(id, dto);
        return ResponseEntity.ok(lutadorAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id){
        lutadorService.remover(id);
        return ResponseEntity.noContent().build();
    }

}
