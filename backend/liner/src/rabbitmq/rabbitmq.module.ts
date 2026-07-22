import { Module } from '@nestjs/common';
import { ClientsModule, Transport } from '@nestjs/microservices';

@Module({
    imports: [
        // Create RabbitMQ Client
        ClientsModule.register([
            {
                name: 'PAYMENT_EVENTS',
                transport: Transport.RMQ,
                options: {
                    urls: ['amqp://admin:admin@localhost:5672'],
                    queue: 'payment_queue',
                    queueOptions: {
                        durable: true,
                    },
                },
            },
        ]),
    ],
    exports: [ClientsModule],
})
export class RabbitMQModule {}
