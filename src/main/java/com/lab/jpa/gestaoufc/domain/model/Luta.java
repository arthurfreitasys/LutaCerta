package com.lab.jpa.gestaoufc.domain.model;
import com.lab.jpa.gestaoufc.domain.enums.CategoriaPeso;
import com.lab.jpa.gestaoufc.domain.enums.ResultadoLuta;
import jakarta.persistence.*;

import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "lutas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Luta {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lutador1_id", nullable = false)
    private Lutador lutador1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lutador2_id", nullable = false)
    private Lutador lutador2;

    @Enumerated(EnumType.STRING)
    @Column(name = "resultadoLuta")
    private ResultadoLuta resultadoLuta;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "categoria")
    private CategoriaPeso categoriaPeso;

}
