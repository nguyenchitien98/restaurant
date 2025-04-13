import React, { useEffect, useState } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import axios from 'axios';
import { Bar } from 'react-chartjs-2';
import {
    Chart as ChartJS,
    CategoryScale,
    LinearScale,
    BarElement,
    Title,
    Tooltip,
    Legend
} from 'chart.js';

ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend);

const Report = () => {
    const [orderId, setOrderId] = useState('');
    const [amount, setAmount] = useState('');
    const [revenue, setRevenue] = useState(0);
    const [chartData, setChartData] = useState(null);
    const [weekOffset, setWeekOffset] = useState(0);

    // Lấy doanh thu tuần hiện tại khi khởi tạo
    useEffect(() => {
        axios.get('http://localhost:8080/api/invoice/weekly-total')
            .then(res => {
                setRevenue(parseFloat(res.data));
            })
            .catch(err => {
                console.error('Lỗi khi lấy doanh thu ban đầu:', err);
            });
    }, []);

    // Realtime revenue via WebSocket
    useEffect(() => {
        const socket = new SockJS('http://localhost:8080/ws-report');
        const client = new Client({
            webSocketFactory: () => socket,
            onConnect: () => {
                client.subscribe('/topic/revenue', (message) => {
                    console.log("Received revenue update:", message.body);
                    setRevenue(parseFloat(message.body));
                });
            },
        });
        client.activate();
    }, []);

    const fetchChartData = async () => {
        try {
            const response = await axios.get('http://localhost:8080/api/invoice/weekly-revenue');
            const data = response.data;
            data.sort((a, b) => a.dayOfWeek - b.dayOfWeek);
            const labels = data.map(item => item.weekday);
            const revenues = data.map(item => item.totalRevenue);

            setChartData({
                labels,
                datasets: [
                    {
                        label: 'Doanh thu (VND)',
                        data: revenues,
                        backgroundColor: 'rgba(54, 162, 235, 0.6)',
                        borderColor: 'rgba(54, 162, 235, 1)',
                        borderWidth: 1,
                    },
                ],
            });
        } catch (error) {
            console.error('Lỗi khi load dữ liệu biểu đồ:', error);
        }
    };

    useEffect(() => {
        fetchChartData();
    }, []);

    // Gửi invoice
    const submitInvoice = async () => {
        await axios.post('http://localhost:8080/api/invoice', {
            orderId,
            totalAmount: parseFloat(amount),
        });
        setOrderId('');
        setAmount('');
        fetchChartData(); // Cập nhật biểu đồ sau khi gửi
    };

    // Lấy dữ liệu biểu đồ doanh thu theo tuần
    useEffect(() => {
        axios.get('http://localhost:8080/api/invoice/weekly-revenue')
            .then(response => {
                const data = response.data;
                data.sort((a, b) => a.dayOfWeek - b.dayOfWeek);
                const labels = data.map(item => item.weekday);
                const revenues = data.map(item => item.totalRevenue);
                setChartData({
                    labels,
                    datasets: [
                        {
                            label: 'Doanh thu (VND)',
                            data: revenues,
                            backgroundColor: 'rgba(54, 162, 235, 0.6)',
                            borderColor: 'rgba(54, 162, 235, 1)',
                            borderWidth: 1,
                        },
                    ],
                });
            })
            .catch(error => {
                console.error('Lỗi khi load dữ liệu biểu đồ:', error);
            });
    }, []);

    return (
        <div className="p-4 space-y-4 flex-1 relative z-10 overflow-auto md:p-6">
            <h1 className="text-2xl font-bold mb-4">💸 Realtime Revenue Report</h1>

            {/* Form gửi invoice */}
            <div className="mb-4">
                <input
                    value={orderId}
                    onChange={(e) => setOrderId(e.target.value)}
                    className="border p-2 rounded mr-2"
                    placeholder="OrderId"
                />
                <input
                    type="number"
                    value={amount}
                    onChange={(e) => setAmount(e.target.value)}
                    className="border p-2 rounded mr-2"
                    placeholder="Amount"
                />
                <button
                    onClick={submitInvoice}
                    className="bg-green-500 text-white px-4 py-2 rounded"
                >
                    Submit Invoice
                </button>
            </div>

            {/* Tổng doanh thu realtime */}
            <div className="text-xl font-semibold">
                🔁 Doanh Thu Hiện Tại Trong Tuần: <span className="text-blue-600">{revenue.toFixed(2)} VNĐ</span>
            </div>

            {/* Biểu đồ doanh thu tuần hiện tại */}
            <div className="mt-8">
                <h2 className="text-lg font-semibold mb-2">📊 Biểu đồ doanh thu theo ngày trong tuần</h2>
                {chartData ? (
                    <Bar
                        data={chartData}
                        options={{
                            responsive: true,
                            plugins: {
                                legend: { position: 'top' },
                                title: { display: false }
                            },
                            scales: {
                                y: {
                                    beginAtZero: true,
                                    ticks: {
                                        callback: (value) => value.toLocaleString('vi-VN') + '₫',
                                    }
                                }
                            }
                        }}
                    />
                ) : (
                    <p>Đang tải biểu đồ...</p>
                )}
            </div>
        </div>
    );
};

export default Report;