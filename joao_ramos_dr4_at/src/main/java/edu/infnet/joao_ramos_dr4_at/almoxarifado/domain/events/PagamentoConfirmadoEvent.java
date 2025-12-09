package edu.infnet.joao_ramos_dr4_at.almoxarifado.domain.events;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PagamentoConfirmadoEvent {
    private String pedidoId;
    private String nomeItem;
    private int quantidade;
}