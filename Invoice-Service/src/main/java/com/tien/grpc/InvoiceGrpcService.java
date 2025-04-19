package com.tien.grpc;

import com.tien.repository.InvoiceRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceGrpcService extends InvoiceServiceGrpc.InvoiceServiceImplBase {

    private final InvoiceRepository invoiceRepository;

//    @PostConstruct
//    public void init() {
//        System.out.println("✅ InvoiceGrpcService has been initialized!");
//    }

    @Override
    public void getMonthlyRevenue(MonthlyRevenueRequest request, StreamObserver<MonthlyRevenueResponse> responseObserver) {
        List<Object[]> results = invoiceRepository.getMonthlyRevenue(request.getYear());
        MonthlyRevenueResponse.Builder response = MonthlyRevenueResponse.newBuilder();

        for (Object[] result : results) {
            int month = (int) result[0];
            double totalAmount = (double) result[1];

            MonthlyRevenue revenueData = MonthlyRevenue.newBuilder()
                    .setMonth(month)
                    .setTotalAmount(totalAmount)
                    .build();

            response.addRevenues(revenueData);
        }

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }
}