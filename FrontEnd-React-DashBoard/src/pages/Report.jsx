import React, { useEffect, useState } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import axios from 'axios';

const Report = () => {
    const [orderId, setOrderId] = useState('');
    const [amount, setAmount] = useState('');
    const [revenue, setRevenue] = useState(0);

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

    const submitInvoice = async () => {
        await axios.post('http://localhost:8080/api/invoice', {
            orderId,
            totalAmount: parseFloat(amount),
        });
        setOrderId('');
        setAmount('');
    };

    return (
        <div className="p-4 space-y-4 flex-1 relative z-10 overflow-auto md:p-6">
            <h1 className="text-2xl font-bold mb-4">💸 Realtime Revenue Report</h1>

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

            <div className="text-xl font-semibold">
                🔁 Current Revenue: <span className="text-blue-600">${revenue.toFixed(2)}</span>
            </div>
        </div>
    );
};

export default Report;
