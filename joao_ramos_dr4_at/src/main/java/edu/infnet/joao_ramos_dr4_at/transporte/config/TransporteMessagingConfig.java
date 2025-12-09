package edu.infnet.joao_ramos_dr4_at.transporte.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TransporteMessagingConfig {

    public static final String EX_PEDIDO = "pedido.events";
    public static final String Q_DESPACHADO = "transporte.pedido.despachado.queue";
    public static final String RK_DESPACHADO = "pedido.despachado";

    @Bean
    public TopicExchange pedidoEventsExchange() {
        return new TopicExchange(EX_PEDIDO);
    }

    @Bean
    public Queue filaDespachado() {
        return new Queue(Q_DESPACHADO, false);
    }

    @Bean
    public Binding bindingDespachado(Queue fila, TopicExchange exchange) {
        return BindingBuilder
            .bind(fila)
            .to(exchange)
            .with(RK_DESPACHADO);
    }
}
