import { useEffect, useState } from "react";
import axios from "axios";
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";

const KitchenPage = () => {
  const [statusTab, setStatusTab] = useState("PENDING");
  const [orders, setOrders] = useState([]);
  const [isUpdating, setIsUpdating] = useState(false);
  const [updatingId, setUpdatingId] = useState(null);
  const [newStatus, setNewStatus] = useState("PENDING");
  const [stompClient, setStompClient] = useState(null);

  const fetchOrdersByStatus = async (status) => {
    try {
      const res = await axios.get(`http://localhost:8080/api/order-details/today?status=${status}`);
      setOrders(res.data);
    } catch (err) {
      console.error("Lỗi khi lấy dữ liệu order detail:", err);
    }
  };

  useEffect(() => {
    fetchOrdersByStatus(statusTab);
  }, [statusTab]);

  useEffect(() => {
    const socket = new SockJS("http://localhost:8080/ws"); // endpoint WebSocket của bạn
    const client = new Client({
      webSocketFactory: () => socket,
      debug: (str) => console.log(str),
      reconnectDelay: 5000,
      onConnect: () => {
        client.subscribe("/topic/kitchen", (message) => {
          const data = JSON.parse(message.body);
          if (data.status === statusTab) {
            setOrders((prevOrders) => {
              // tránh trùng lặp
              const exists = prevOrders.some((item) => item.orderDetailId === data.orderDetailId);
              return exists ? prevOrders : [...prevOrders, data];
            });
          }
        });
      },
    });

    client.activate();
    setStompClient(client);

    return () => {
      if (client.connected) {
        client.deactivate();
      }
    };
  }, [statusTab]);

  const handleUpdateClick = (orderDetailId, currentStatus) => {
    setIsUpdating(true);
    setUpdatingId(orderDetailId);
    setNewStatus(currentStatus);
  };

  const handleUpdateSave = async () => {
    try {
      await axios.put(`http://localhost:8080/api/order-details/${updatingId}/status`, {
        status: newStatus,
      });
      fetchOrdersByStatus(statusTab);
      setIsUpdating(false);
      setUpdatingId(null);
    } catch (err) {
      alert("Lỗi khi cập nhật trạng thái");
    }
  };

  const statusOptions = ["PENDING", "COOKING", "COMPLETED", "CANCELLED"];

  return (
    <div className="p-4 space-y-4 flex-1 relative z-10 overflow-auto md:p-6">
      <h1 className="text-2xl font-bold">👨‍🍳 Quản lý bếp</h1>

      {/* Tabs */}
      <div className="flex gap-4">
        {statusOptions.map((status) => (
          <button
            key={status}
            onClick={() => setStatusTab(status)}
            className={`px-4 py-2 rounded ${statusTab === status ? "bg-blue-600 text-white" : "bg-gray-600"}`}
          >
            {status}
          </button>
        ))}
      </div>

      {/* Table */}
      <table className="w-full border">
        <thead className="bg-gray-700">
          <tr>
            <th className="p-2 border">#</th>
            <th className="p-2 border">Tên món</th>
            <th className="p-2 border">Số lượng</th>
            <th className="p-2 border">Bàn</th>
            <th className="p-2 border">Thời gian</th>
            <th className="p-2 border">Trạng thái</th>
            <th className="p-2 border">Hành động</th>
          </tr>
        </thead>
        <tbody>
          {orders.map((item, index) => (
            <tr key={item.orderDetailId} className="hover:bg-gray-500">
              <td className="p-2 border">{index + 1}</td>
              <td className="p-2 border">{item.menuName}</td>
              <td className="p-2 border">{item.quantity}</td>
              <td className="p-2 border">{item.tableNumber}</td>
              <td className="p-2 border"> {item.createdAt ? new Date(item.createdAt).toLocaleTimeString() : "N/A"}</td>
              <td className="p-2 border">{item.status}</td>
              <td className="p-2 border">
                {isUpdating && updatingId === item.orderDetailId ? (
                  <div className="flex gap-2">
                    <select
                      value={newStatus}
                      onChange={(e) => setNewStatus(e.target.value)}
                      className="border p-1 rounded"
                    >
                      {statusOptions.map((s) => (
                        <option key={s} value={s}>
                          {s}
                        </option>
                      ))}
                    </select>
                    <button
                      onClick={handleUpdateSave}
                      className="bg-green-600 text-white px-2 py-1 rounded"
                    >
                      💾 Lưu
                    </button>
                  </div>
                ) : (
                  <button
                    onClick={() => handleUpdateClick(item.orderDetailId, item.status)}
                    className="bg-yellow-500 text-white px-2 py-1 rounded"
                  >
                    ✏️ Sửa
                  </button>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default KitchenPage;