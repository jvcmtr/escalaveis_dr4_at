## INSTITUTO INFNET 
#### ESCOLA SUPERIOR DE TECNOLOGIA
#### GRADUAÇÃO EM ENGENHARIA DE SOFTWARE    
## Assessment AT da disciplina Domain-Driven Design (DDD) e Arquitetura de Softwares Escaláveis com Java [25E4_4]
#### João Victor Cícero de M. T. Ramos
#### Dezembro de 2025

---
# Repositório
> O repositorio referente a este TP pode ser  
> encontrado atravéz do seguinte link do **Github** :
> https://github.com/jvcmtr/escalaveis_dr4_at

> Considera-se `src\main\java\edu\infnet\joao_ramos_dr4_at\` como a raiz de todos os caminhos referentes a arquivos de codigo.

---

# 1. Transformar monólitos em microsserviços eficazes, aplicando princípios de DDD e técnicas de decomposição:

#### a. Implemente a classe entity e seu respectivo repository que represente o agregado mais representativo do microsserviço PetFriends_Almoxarifado.

``` java
// almoxarifado\domain\ItemEstoque.java
@Data
@Entity
public class ItemEstoque {
    @Id private String pedidoId;
    @Column(unique = true) String codigoBarras;
    @Nullable private LocalDate validade; 
    private LocalDate recebidoEm;
    private Boolean emEstoque;
    @Embedded private TipoItem tipo;
}
```

``` java
// almoxarifado\domain\ItemEstoqueRepository.java
@Repository
public interface ItemEstoqueRepository extends JpaRepository<ItemEstoque, String> {}
```
#### b. Implemente um exemplo de Value Object a ser usado na classe entity da questão anterior.
``` java
// almoxarifado\domain\TipoItem.java
@Data
@AllArgsConstructor
@Embeddable
public class TipoItem{
    private String nomeItem;
    private int volumes;
    private String fornescidoPor;
}
```
#### c. Implemente a classe entity e seu respectivo repository que represente o agregado mais representativo do microsserviço PetFriends_Transporte.
``` java
// transporte\domain\Entrega.java
@Data
@Entity
public class Entrega {

    @Id private String pedidoId;
    @Embedded private Endereco endereco;
    private String status;
}
```

``` java
// transporte\domain\EntregaRepository.java
@Repository
public interface EntregaRepository extends JpaRepository<Entrega, String> {}
```
#### d. Implemente um exemplo de Value Object a ser usado na classe entity da questão anterior.
``` java
// transporte\domain\Endereco.java
@Data
@AllArgsConstructor
@Embeddable
public class Endereco {
    private String rua;
    private String numero;
    private String complemento;
    private String cidade;
    private String estado;
    private String cep;
}
```
# 2. Projetar softwares usando "domain events":

#### a. O módulo PetFriends_Web foi desenvolvido em ReactJS, acessando os microsserviços de Clientes, Produtos e Pedidos de forma síncrona, via REST API. Que funcionalidade síncrona executada pelo cliente é diretamente afetada pelos eventos de domínio?
> Segundo os diagramas fornescidos, a principal funcionalidade afetada pelos eventos de domínio é o acompanhamento de pedido. Já que a arquitetura orientada a eventos possui consistência eventual. 


#### b. Explique de forma sucinta qual é a diferença de enviar eventos somente com o ID do agregado e enviar um payload completo?
> Enviar somente o ID do pedido requer que o serviço saiba da implementação da persistência dos dados, já que terá que acessar-lo para resgatar as informações com base no ID recebido, o que aumenta o acoplamento. 
> 
> Por outro lado, receber os dados atravéz de um payload reduz o acoplamento e permite a introdução de ACLs. Mas ao mesmo tempo pode aumenta o volume de dados transitados, o que pode levar a eventuais aumentos de custo ou problemas com timeout dependendo do volumetria.

#### c. A partir da resposta dada na questão anterior, como você projetaria o evento a ser enviado pelo PetFriends_Pedido para o PetFriends_Almoxarifado?

> Ambos os eventos `PagamentoConfirmadoEvent` e `PedidoCanceladoEvent` possuem todas as informações nescessárias para a operação do serviço de almoxarifado. No caso do evento de cancelamento, a unica informação nescessária é o IDdo pedido.

``` java
// almoxarifado\domain\events\PagamentoConfirmadoEvent.java
@Data
@AllArgsConstructor
public class PagamentoConfirmadoEvent {
    private String pedidoId;
    private String nomeItem;
    private int quantidade;
}
```

``` java
// almoxarifado\domain\events\PedidoCanceladoEvent.java
@Data
@AllArgsConstructor
public class PedidoCanceladoEvent {
    private String pedidoId;
}
```

#### d. A partir da resposta dada na questão anterior, como você projetaria o evento a ser enviado pelo PetFriends_Pedido para o PetFriends_Transporte?

> O evento `PedidoDespachadoEvent` possuiria todas as informações nescessárias para a operação do serviço de transporte para evitar o acoplamento.

``` java
// transporte\domain\events\PedidoDespachadoEvent.java
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
```

# 3. Desenvolver microsserviços event-driven e com outros padrões de comunicação assíncrona:

#### a. No microsserviço PetFriends_Almoxarifado, implemente a classe de configuração para o tratamento de mensagens para receber os eventos do PetFriends_Pedidos.
``` java
// almoxarifado\config\AlmoxarifadoMessageConfig.java
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
```

#### b. No microsserviço PetFriends_Almoxarifado, implemente o serviço que receberá os eventos do PetFriends_Pedidos.
``` java
// almoxarifado\domain\AlmoxarifadoEventHandler.java
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
```
#### c. No microsserviço PetFriends_Transporte, implemente a classe de configuração para o tratamento de mensagens para receber os eventos do PetFriends_Pedidos.
``` java
// transporte\config\TransporteMessageConfig.java
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
    public Queue filaDespachado() { return new Queue(Q_DESPACHADO, false); }

    @Bean
    public Binding bindingDespachado(Queue fila, TopicExchange exchange) {
        return BindingBuilder
            .bind(filaDespachado)
            .to(exchange)
            .with(RK_DESPACHADO);
    }
}
```

#### d. No microsserviço PetFriends_Transporte, implemente o serviço que receberá os eventos do PetFriends_Pedidos.
``` java
// transporte\services\TransporteEventHandler.java
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
```

#  4. Implementar testes e observabilidade em microsserviços com Zipkin, Spring Cloud Sleuth e ELK Stack:

#### a. Explique de forma sucinta o que é um Gateway de Serviço, suas vantagens e desvantagens.
> O gateway de serviço possui a vantagem de centralizar o gerenciamento de acesso, roteamento e permissionamento para os demais microserviços, contudo, ao mesmo tempo que essa centralização é benéfica para a administração do sistema, ela gera um ponto unico de falha, onde nenhum dos serviços pode ser acessado caso ocorra algum problema com o gateway.

#### b. O que é ID de Correlação e quais são os seus pré-requisitos?
> Um id de correlação é um identificador que permite associar diferentes eventos ou objetos que perpassam multiplos serviços a fim de manter a rastreabilidade de uma entidade ou agregado. Pre-requisitos para um ID de Correlação são a uniquicidade do valor e compartilhamento deste ID entre serviços. 

#### c. Qual é a função do Spring Cloud Sleuth e sua relação com o serviço Zipkin?
> Enquanto o SpringClous Sleuth é o responsavel por gerar IDs de correlação, o zipkin é o responsavel por coletar e armazenar estes dados para fins de auditori#### a. 

#### d. Explique de forma sucinta o que é Agregador de Logs, suas vantagens e desvantagens.
> um agregador de logs, como o nome sugere, é um serviço ou ferramenta responsavel por agregar logs de fontes diferentes em uma lugar centralizado. Uma vantagem deste seviço é a facilidade que promove para o debug e rastreio (que são conhecidamente mais complexas em uma arquitetura de microsserviços). Uma desvantagem da utilização deste tipo de serviço é a crescente complexidade do sistema e sua dependencia da infraestrutura.
