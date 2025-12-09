package edu.infnet.joao_ramos_dr4_at.almoxarifado.domain;

import java.time.LocalDate;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data; 

@Data
@Entity
public class ItemEstoque {
    @Id String codigoBarras;
    private String pedidoId;
    @Nullable private LocalDate validade; 
    private LocalDate recebidoEm;
    private Boolean emEstoque;
    @Embedded private TipoItem tipo;
}
