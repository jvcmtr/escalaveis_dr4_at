package edu.infnet.joao_ramos_dr4_at.almoxarifado.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.infnet.joao_ramos_dr4_at.almoxarifado.domain.ItemEstoque;
import edu.infnet.joao_ramos_dr4_at.almoxarifado.domain.ItemEstoqueRepository;
import edu.infnet.joao_ramos_dr4_at.almoxarifado.domain.events.PagamentoConfirmadoEvent;
import edu.infnet.joao_ramos_dr4_at.almoxarifado.domain.events.PedidoCanceladoEvent;

@Service
public class AlmoxarifadoEventHandler {

    @Autowired private ItemEstoqueRepository repository;

    @RabbitListener(queues = "almoxarifado.pedido.pagamentosucesso.queue")
    public void onPagamentoConfirmado(PagamentoConfirmadoEvent event) {

        LocalDate LimiteDataValidade = LocalDate.now().plusDays(30);
        
        // Encontra itens que se encaixam com o pedido e estão dentro das normas
        List<ItemEstoque> items = repository.findAll().stream()
                .filter(i -> i.getPedidoId() == null || i.getPedidoId().isEmpty() )
                .filter(i -> i.getTipo().getNomeItem().equalsIgnoreCase(event.getNomeItem()))
                .filter(i -> i.getEmEstoque() ) 
                .filter(i -> i.getValidade() == null || !i.getValidade().isBefore(LimiteDataValidade)) 
                .limit(event.getQuantidade())
                .toList();

        for (ItemEstoque item : items) {
            item.setPedidoId(event.getPedidoId());
        }

        repository.saveAll(items);
    }

    @RabbitListener(queues = "almoxarifado.pedido.cancelamento.queue")
    public void onPedidoCancelado(PedidoCanceladoEvent event) {

        List<ItemEstoque> items = repository.findAll().stream()
                .filter(i -> i.getPedidoId().equals(event.getPedidoId()))
                .toList();

        for (ItemEstoque item : items){
            item.setPedidoId(null);
        }

        repository.saveAll(items);
    }
}
