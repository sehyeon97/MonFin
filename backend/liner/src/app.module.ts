import { OrderHistoryService } from './customer/services/order-history.service';
import { PaymentProcessorModule } from './payment/module/payment-processor.module';
import { MerchantModule } from './merchant/module/merchant.module';
import { CustomerAccountService } from './customer/services/customer-account.service';
import { CustomerModule } from './customer/module/customer.module';
import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { ConfigModule, ConfigService } from '@nestjs/config';
import { AppController } from './app.controller';
import { AppService } from './app.service';
import { ClientsModule, Transport } from '@nestjs/microservices';

@Module({
    imports: [
        PaymentProcessorModule,
        MerchantModule,
        CustomerModule,
        ConfigModule.forRoot({
            isGlobal: true,
        }),

        TypeOrmModule.forRootAsync({
            inject: [ConfigService],
            useFactory: (configService: ConfigService) => ({
                type: 'postgres',
                host: configService.get<string>('DB_HOST'),
                port: Number(configService.get<string>('DB_PORT')),
                username: configService.get<string>('DB_USERNAME'),
                password: configService.get<string>('DB_PASSWORD'),
                database: configService.get<string>('DB_DATABASE_NAME'),

                autoLoadEntities: true,
                synchronize: true, // in production, change to false and add migrationsRun: true
            }),
        }),

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
    controllers: [AppController],
    providers: [OrderHistoryService, CustomerAccountService, AppService],
})
export class AppModule {}
