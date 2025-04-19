package com.tien.grpc;

import com.tien.dto.response.MonthlyRevenueResponseDTO;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportServiceGrpcClient {

    private final ManagedChannel channel;
    private final InvoiceServiceGrpc.InvoiceServiceBlockingStub invoiceServiceBlockingStub;

    public ReportServiceGrpcClient() {
        // Tạo kênh kết nối gRPC
        channel = ManagedChannelBuilder.forAddress("localhost", 9090)
                .usePlaintext()  // Không sử dụng mã hóa TLS trong môi trường phát triển
                .build();

        // Tạo BlockingStub để gọi các phương thức của InvoiceService
        invoiceServiceBlockingStub = InvoiceServiceGrpc.newBlockingStub(channel);
    }

    @PreDestroy
    public void destroy() {
        // Đảm bảo đóng channel khi không sử dụng nữa để tránh rò rỉ tài nguyên
        if (channel != null && !channel.isShutdown()) {
            channel.shutdown();
        }
    }

    // Phương thức gọi gRPC để lấy doanh thu hàng tháng
    public List<MonthlyRevenueResponseDTO> getMonthlyRevenue(int year) {
        MonthlyRevenueRequest request = MonthlyRevenueRequest.newBuilder()
                .setYear(year)
                .build();

        MonthlyRevenueResponse response = invoiceServiceBlockingStub.getMonthlyRevenue(request);

//        // Trả về danh sách MonthlyRevenue trực tiếp từ gRPC response ( trả kiểu này sẽ gây lỗi vì Jackson không biết serialize com.google.protobuf.*, đặc biệt là UnknownFieldSet. )
//        return response.getRevenuesList();

        // Chuyển từ protobuf → DTO
        return response.getRevenuesList().stream()
                .map(proto -> new MonthlyRevenueResponseDTO(proto.getMonth(), proto.getTotalAmount()))
                .collect(Collectors.toList());
    }
}