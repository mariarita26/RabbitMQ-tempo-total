import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.util.ArrayList;


public class SecondaryConsumer {
    public static void main(String[] args) throws Exception {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");

        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();

        String QUEUE_NAME = "TESTE_TESTE";
        boolean durable = false;

        channel.queueDeclare(QUEUE_NAME, durable, false, false, null);

        ArrayList<Long> timestamps = new ArrayList<>();

        DeliverCallback callback = (consumerTag, delivery) -> {
            Long receivedTimestamp = System.currentTimeMillis();
            timestamps.add(receivedTimestamp);

            if (timestamps.size() == 2) {
                System.out.println("Message 1: " + timestamps.get(0));
                System.out.println("Message 2: " + timestamps.get(1));
                System.out.println("Took " + (timestamps.get(1) - timestamps.get(0)) + "ms processing messages");
                System.out.println("Or " + (timestamps.get(1) - timestamps.get(0)) / 1000 + "seconds");
                timestamps.clear();
            }

            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        };

        boolean autoAck = false; // Nomeando a variavel pra facilitar leitura
        channel.basicConsume(QUEUE_NAME, autoAck, callback, consumerTag -> {
            System.out.println("Cancelaram a fila: " + QUEUE_NAME);
        });
    }
}


