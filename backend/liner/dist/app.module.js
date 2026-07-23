"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.AppModule = void 0;
const payment_processor_module_1 = require("./payment/module/payment-processor.module");
const merchant_module_1 = require("./merchant/module/merchant.module");
const customer_module_1 = require("./customer/module/customer.module");
const common_1 = require("@nestjs/common");
const typeorm_1 = require("@nestjs/typeorm");
const config_1 = require("@nestjs/config");
const merchant_sales_module_1 = require("./merchant/module/merchant-sales.module");
const customer_order_history_module_1 = require("./customer/module/customer.order-history.module");
const jwt_module_1 = require("./auth/jwt.module");
let AppModule = class AppModule {
};
exports.AppModule = AppModule;
exports.AppModule = AppModule = __decorate([
    (0, common_1.Module)({
        imports: [
            jwt_module_1.JWTModule,
            payment_processor_module_1.PaymentProcessorModule,
            merchant_module_1.MerchantModule,
            merchant_sales_module_1.MerchantSalesModule,
            customer_module_1.CustomerModule,
            customer_order_history_module_1.CustomerOrderHistoryModule,
            config_1.ConfigModule.forRoot({
                isGlobal: true,
            }),
            typeorm_1.TypeOrmModule.forRootAsync({
                inject: [config_1.ConfigService],
                useFactory: (configService) => ({
                    type: 'postgres',
                    host: configService.get('DB_HOST'),
                    port: Number(configService.get('DB_PORT')),
                    username: configService.get('DB_USERNAME'),
                    password: configService.get('DB_PASSWORD'),
                    database: configService.get('DB_DATABASE_NAME'),
                    autoLoadEntities: true,
                    synchronize: true,
                }),
            }),
        ],
    })
], AppModule);
//# sourceMappingURL=app.module.js.map