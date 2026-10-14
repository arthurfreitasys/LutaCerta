package com.lab.jpa.gestaoufc.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "evento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Evento {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String nomeEvento; //Exemplo: UFC 331

    @Column(nullable = false)
    private LocalDate data;

    @Column(nullable = false)
    private String localEvento; //Exemplo: Arena Madison Square Garden

    @OneToMany(mappedBy = "evento")
    private List<Luta> lutas;
}
