package edu.infnet.joao_ramos_dr4_at.transporte.services;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.infnet.joao_ramos_dr4_at.transporte.domain.Endereco;
import edu.infnet.joao_ramos_dr4_at.transporte.domain.Entrega;
import edu.infnet.joao_ramos_dr4_at.transporte.domain.EntregaRepository;
import edu.infnet.joao_ramos_dr4_at.transporte.domain.events.PedidoDespachadoEvent;

@Service
public class TransporteEventHandler {

    @Autowired private EntregaRepository repository;

    @RabbitListener(queues = "transporte.pedido.despachado.queue")
    public void onPedidoDespachado(PedidoDespachadoEvent event) {
        
        Entrega entrega = new Entrega(
            event.getPedidoId(), 
            new Endereco(event.getRua(), event.getNumero(), event.getComplemento(), event.getCidade(), event.getEstado(), event.getCep()),
            "emTransito"
        );
        repository.save(entrega);
    }
}
