package edu.infnet.joao_ramos_dr4_at.transporte.domain;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Entity
public class Entrega {

    @Id private String pedidoId;
    @Embedded private Endereco endereco;
    private String status;
}
