package com.tien.config;

import com.tien.grpc.InvoiceGrpcService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.IOException;

// Cấu hình thủ công cho Grpc, để có thể chạy song song 2 port của spring web và grpc (port 8085 và 9090)
@Configuration
public class GrpcServerConfig {

    @Autowired
    private InvoiceGrpcService invoiceGrpcService;

    @PostConstruct
    public void startGrpcServer() throws IOException, InterruptedException {
        Server server = ServerBuilder
                .forPort(9090)
                .addService(invoiceGrpcService)
                .build()
                .start();

        System.out.println("✅ gRPC server started on port 9090");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("🛑 Shutting down gRPC server...");
            server.shutdown();
        }));
    }
}
