package com.lab.jpa.gestaoufc.domain.model;

import com.lab.jpa.gestaoufc.domain.enums.CategoriaPeso;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "lutador")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lutador {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = true, length = 150)
    private String apelido;

    @Enumerated(EnumType.STRING)
    @Column(name = "categoria", nullable = false, length = 20)
    private CategoriaPeso categoriaPeso;

    @Column(nullable = false)
    private Integer vitorias;

    @Column(nullable = false)
    private Integer derrotas;

    @Column(nullable = false)
    private Integer empates;

    @Column(nullable = false, length = 250)
    private String paisOrigem;


}
