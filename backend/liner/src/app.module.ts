import { PaymentProcessorModule } from './payment/module/payment-processor.module';
import { MerchantModule } from './merchant/module/merchant.module';
import { CustomerModule } from './customer/module/customer.module';
import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { ConfigModule, ConfigService } from '@nestjs/config';
import { MerchantSalesModule } from './merchant/module/merchant-sales.module';
import { CustomerOrderHistoryModule } from './customer/module/customer.order-history.module';

@Module({
    imports: [
        PaymentProcessorModule,
        MerchantModule,
        MerchantSalesModule,
        CustomerModule,
        CustomerOrderHistoryModule,
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
    ],
})
export class AppModule {}
