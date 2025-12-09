package edu.infnet.joao_ramos_dr4_at.almoxarifado.domain;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Embeddable
public class TipoItem{
    private String nomeItem;
    private int volumes;
    private String fornescidoPor;
}
