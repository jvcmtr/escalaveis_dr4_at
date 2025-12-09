package edu.infnet.joao_ramos_dr4_at.transporte.domain.events;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class PedidoDespachadoEvent {
    private String pedidoId;
    private String rua;
    private String numero;
    private String complemento;
    private String cidade;
    private String estado;
    private String cep;
    private LocalDateTime despachadoEm;
}