package edu.infnet.joao_ramos_dr4_at.almoxarifado.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AlmoxarifadoMessageConfig {

    public static final String EX_PEDIDO = "pedido.events";
    public static final String Q_PAGAMENTO = "almoxarifado.pedido.pagamentosucesso.queue";
    public static final String Q_CANCELAMENTO = "almoxarifado.pedido.cancelamento.queue";
    public static final String RK_PAGAMENTO_CONFIRMADO = "pedido.enviar_pedido";
    public static final String RK_CANCELAR = "pedido.cancelar";

    @Bean
    public TopicExchange pedidoEventsExchange() {
        return new TopicExchange(EX_PEDIDO);
    }

    @Bean
    public Queue filaPagamentoConfirmado() {
        return new Queue(Q_PAGAMENTO, false);
    }

    @Bean
    public Queue filaCancelamento() {
        return new Queue(Q_CANCELAMENTO, false);
    }

    @Bean
    public Binding bindingPagamentoConfirmado(Queue fila, TopicExchange exchange) {
        return BindingBuilder
            .bind(fila)
            .to(exchange)
            .with(RK_PAGAMENTO_CONFIRMADO);
    }

    @Bean
    public Binding bindingCancelamento(Queue fila, TopicExchange exchange) {
        return BindingBuilder
            .bind(fila)
            .to(exchange)
            .with(RK_CANCELAR);
    }
}
