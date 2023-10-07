import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;


public class Consumer {
    public static void main(String[] args) throws Exception {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");

        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();

        String QUEUE_NAME = "TESTE";
        String SECONDARY_QUEUE_NAME = "TESTE_TESTE";
        boolean durable = false;

        channel.queueDeclare(QUEUE_NAME, durable, false, false, null);
        channel.queueDeclare(SECONDARY_QUEUE_NAME, durable, false, false, null);

        DeliverCallback callback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody());

            Long receivedTimestamp = System.currentTimeMillis();
            int id = (int) Long.parseLong(message.split("-")[0]);
            Long sentTimestamp = Long.parseLong(message.split("-")[1]);

            if (id == 1 || id == 1000000) {
                channel.basicPublish("", SECONDARY_QUEUE_NAME, null, message.getBytes());
                System.out.println("Mensagem enviada para a fila secundaria: " + message);
            } else {
                System.out.println("Took " + (receivedTimestamp - sentTimestamp) + "ms to receive message: " + message);

            }

            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        };

        boolean autoAck = false;
        channel.basicConsume(QUEUE_NAME, autoAck, callback, consumerTag -> {
            System.out.println("Cancelaram a fila: " + QUEUE_NAME);
        });
    }
}


