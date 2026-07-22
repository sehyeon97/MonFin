import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module';
import { ValidationPipe } from '@nestjs/common';

async function bootstrap() {
    const app = await NestFactory.create(AppModule);

    app.enableCors({
        // allows payment frontend to access backend api
        origin: 'http://localhost:5173',
    });

    app.useGlobalPipes(
        new ValidationPipe({
            // removes fields not defined by DTO
            whitelist: true,
            // transforms incoming json requests as specified DTOs
            transform: true,
        }),
    );

    await app.listen(process.env.PORT ?? 3000);
}
bootstrap();
