import { useState,useEffect, useRef } from "react";
import axios from "axios";

const statusMap = {
  EMPTY: "Trống",
  OCCUPIED: "Đang sử dụng",
  RESERVED: "Đã đặt trước"
};

const statusReverseMap = {
  "Trống": "EMPTY",
  "Đang sử dụng": "OCCUPIED",
  "Đã đặt trước": "RESERVED"
};

const formatReservedAt = (datetimeString) => {
  if (!datetimeString) return "";
  const date = new Date(datetimeString);
  const datePart = date.toLocaleDateString("vi-VN");
  const timePart = date.toLocaleTimeString("vi-VN", {
    hour: "2-digit",
    minute: "2-digit",
  });
  return `${datePart} ${timePart}`;
};

const ManageTablesPage = () => {
  const [tables, setTables] = useState([]);
  const [loading, setLoading] = useState(true);
  const audioRef = useRef(null);

  const [form, setForm] = useState({
    table_number: "",
    capacity: "",
    status: "Trống",
    note: "",
    reservedDate: "",
    reservedTime: ""
  });

  const [filterStatus, setFilterStatus] = useState("Tất cả");
  const [filterReservedDate, setFilterReservedDate] = useState("");
  const [searchTerm, setSearchTerm] = useState("");
  const [reminders, setReminders] = useState([]);
  const [showReminder, setShowReminder] = useState(true);

  const [isEditing, setIsEditing] = useState(false);
  const [editingId, setEditingId] = useState(null);

  const handleChangeStatus = async (tableId, newStatusLabel) => {
    const confirm = window.confirm("Bạn có chắc muốn cập nhật trạng thái?");
    if (!confirm) return;

    try {
      const backendStatus = statusReverseMap[newStatusLabel];

      await axios.put("http://localhost:8080/api/tables/status", {
        tableId,
        status: backendStatus
      });

      // Cập nhật lại state frontend
      setTables(prev =>
          prev.map(t => t.id === tableId ? { ...t, status: newStatusLabel } : t)
      );

      alert("Cập nhật trạng thái thành công!");
    } catch (error) {
      console.error("Lỗi khi cập nhật trạng thái:", error);
      alert("Đã xảy ra lỗi khi cập nhật trạng thái.");
    }
  };

  // Gọi API lấy danh sách bàn
  const fetchTables = async () => {
    try {
      const res = await axios.get("http://localhost:8080/api/tables");
      const formatted = res.data.map((t) => ({
        id: t.table_id,
        table_number: `Bàn ${t.table_number}`,
        capacity: t.capacity,
        status: statusMap[t.status] || "Không xác định",
        note: t.note,
        // reservedAt: t.reserved_at ? t.reserved_at : "",
        reservedAt: t.reserved_at ? formatReservedAt(t.reserved_at) : "",
        orders: t.orders || [],
      }));
      setTables(formatted);
    } catch (err) {
      console.error("Lỗi khi load danh sách bàn:", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchTables();
  }, []);



  // Check reminders
  useEffect(() => {
    const checkReminders = () => {
      const now = new Date();
      const soonTables = tables.filter((t) => {
        if (t.status !== "Đã đặt trước" || !t.reservedAt) return false;
        const reservedDate = new Date(t.reservedAt);
        const diff = (reservedDate - now) / (1000 * 60); // phút
        return diff > 0 && diff <= 15;
      });

      if (soonTables.length > 0) {
        setReminders(soonTables);
        setShowReminder(true);
        if (audioRef.current) {
          audioRef.current.play();
        }
        setTimeout(() => setShowReminder(false), 30000); // Tự tắt sau 30s
      }
    };

    checkReminders();
    const interval = setInterval(checkReminders, 60000);
    return () => clearInterval(interval);
  }, [tables]);

  const resetForm = () => {
    setForm({
      table_number: "",
      capacity: "",
      status: "Trống",
      note: "",
      reservedDate: "",
      reservedTime: ""
    });
    setIsEditing(false);
    setEditingId(null);
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm(prevForm => ({
      ...prevForm,
      [name]: value
    }));
  };

  const handleAdd = async () => {
    if (!form.table_number || !form.capacity) return;
    try {
      const backendStatus = statusReverseMap[form.status];
      await axios.post("http://localhost:8080/api/tables", {
        table_number: parseInt(form.table_number),
        capacity: parseInt(form.capacity),
        status: backendStatus,
        note: form.note
      });
      fetchTables();
      resetForm();
      alert("Thêm bàn thành công!");
    } catch (error) {
      console.error("Lỗi khi thêm bàn:", error);
      alert("Thêm bàn thất bại.");
    }
  };

  const handleUpdate = async () => {
    const reservedAt = form.status === "Đã đặt trước" && form.reservedDate && form.reservedTime
        ? `${form.reservedDate}T${form.reservedTime}`
        : null;
    try {
      const backendStatus = statusReverseMap[form.status];
      await axios.put(`http://localhost:8080/api/tables/${editingId}`, {
        table_number: parseInt(form.table_number),
        capacity: parseInt(form.capacity),
        status: backendStatus,
        note: form.note,
        reserved_at: reservedAt
      },{
        headers: {
          "Content-Type": "application/json"}
      });
      fetchTables();
      resetForm();
      alert("Cập nhật bàn thành công!");
    } catch (error) {
      console.error("Lỗi khi cập nhật bàn:", error);
      alert("Cập nhật bàn thất bại.");
    }
  };

  const handleDelete = async (id) => {
    const confirmDelete = window.confirm("Bạn có chắc chắn muốn xoá bàn này không?");
    if (!confirmDelete) return;
    try {
      await axios.delete(`http://localhost:8080/api/tables/${id}`);
      fetchTables();
      alert("Xoá bàn thành công!");
    } catch (error) {
      console.error("Lỗi khi xoá bàn:", error);
      alert("Xoá bàn thất bại.");
    }
  };

  const handleEdit = (table) => {
    console.log('Editing table:', table);
    const [reservedDate = "", reservedTime = ""] = table.reservedAt?.split("T") || [];

    // Loại bỏ tiền tố "Bàn " từ table_number
    const tableNumber = table.table_number.replace('Bàn ', '');

    setForm({
      table_number: tableNumber || "",  // Đảm bảo không có null
      capacity: table.capacity ? table.capacity.toString() : "",  // Đảm bảo không có null
      status: table.status || "",
      note: table.note || "",
      reservedDate: reservedDate || "",  // Đảm bảo không có null
      reservedTime: reservedTime || "",  // Đảm bảo không có null
    });
    setIsEditing(true);
    setEditingId(table.id);
  };


  if (loading) {
    return <div className="p-4">Đang tải dữ liệu bàn ăn...</div>;
  }

  const getStatusColor = (status) => {
    switch (status) {
      case "Trống":
        return "bg-green-100 text-green-700";
      case "Đang sử dụng":
        return "bg-red-100 text-red-700";
      case "Đã đặt trước":
        return "bg-yellow-100 text-yellow-700";
      default:
        return "";
    }
  };

  const filteredTables = tables.filter((t) => {
    const matchStatus = filterStatus === "Tất cả" || t.status === filterStatus;
    const matchSearch = typeof t.table_number === "string" && t.table_number.toLowerCase().includes(searchTerm.toLowerCase());
    return matchStatus && matchSearch;
  });

  const countByStatus = (status) => tables.filter((t) => t.status === status).length;

  return (
      <div className="flex-1 relative z-1 overflow-auto p-4 md:p-6 ">
        <h1 className="text-2xl font-bold mb-4">Quản lý bàn ăn</h1>

        <audio ref={audioRef} src="/public/sounds/mixkit-classic-alarm-995.wav" preload="auto" />

        {reminders.length > 0 && showReminder && (
            <div className="bg-orange-100 text-orange-800 p-4 rounded mb-4">
              <div className="flex justify-between items-center">
                <h3 className="font-semibold">🔔 Bàn sắp đến giờ đặt:</h3>
                <button onClick={() => setShowReminder(false)} className="text-sm text-orange-600 hover:underline">Tắt thông báo</button>
              </div>
              {reminders.map((t) => (
                  <div key={t.id}>
                    <p>🪑 {t.table_number} - ⏰ {t.reservedAt}</p>
                  </div>
              ))}
            </div>
        )}

        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-4 gap-4 mb-6">
          <div className="p-4 rounded bg-green-100">
            <p className="text-green-700 font-bold">Trống: {countByStatus("Trống")}</p>
          </div>
          <div className="p-4 rounded bg-red-100">
            <p className="text-red-700 font-bold">Đang sử dụng: {countByStatus("Đang sử dụng")}</p>
          </div>
          <div className="p-4 rounded bg-yellow-100">
            <p className="text-yellow-700 font-bold">Đã đặt trước: {countByStatus("Đã đặt trước")}</p>
          </div>
          <div className="p-4 rounded bg-gray-100">
            <p className="text-gray-700 font-bold">Tổng số bàn: {tables.length}</p>
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-6 gap-4 mb-4">
          <input
              type="number"
              name="table_number"
              value={form.table_number}
              onChange={handleChange}
              placeholder="Số bàn"
              className="border p-2 rounded text-gray-700"
          />

          <input
              type="number"
              name="capacity"
              value={form.capacity}
              onChange={handleChange}
              placeholder="Sức chứa"
              className="border p-2 rounded  text-gray-700"
          />

          <select
              name="status"
              value={form.status}
              onChange={handleChange}
              className="border p-2 rounded text-gray-700"
          >
            <option value="Trống">Trống</option>
            {isEditing && (
                <>
                  <option value="Đang sử dụng">Đang sử dụng</option>
                  <option value="Đã đặt trước">Đã đặt trước</option>
                </>
            )}
          </select>

          <input
              type="text"
              name="note"
              value={form.note}
              onChange={handleChange}
              placeholder="Ghi chú"
              className="border p-2 rounded  text-gray-700"
          />

          {form.status === "Đã đặt trước" && (
              <>
                <input
                    type="date"
                    name="reservedDate"
                    value={form.reservedDate}
                    onChange={handleChange}
                    className="border p-2 rounded"
                />
                <input
                    type="time"
                    name="reservedTime"
                    value={form.reservedTime}
                    onChange={handleChange}
                    className="border p-2 rounded"
                />
              </>
          )}

          <div className="flex space-x-2">
            {isEditing ? (
                <>
                  <button
                      onClick={handleUpdate}
                      className="bg-yellow-500 text-white px-4 py-2 rounded"
                  >
                    Cập nhật
                  </button>
                  <button
                      onClick={resetForm}
                      className="bg-gray-400 text-white px-4 py-2 rounded"
                  >
                    Huỷ
                  </button>
                </>
            ) : (
                <button
                    onClick={handleAdd}
                    className="bg-blue-500 text-white px-4 py-2 rounded"
                >
                  Thêm mới
                </button>
            )}
          </div>
        </div>

        <div className="mb-4 flex flex-col md:flex-row gap-4">
          <select
              value={filterStatus}
              onChange={(e) => setFilterStatus(e.target.value)}
              className="border p-2 rounded w-full md:w-auto  text-gray-700"
          >
            <option value="Tất cả">Tất cả trạng thái</option>
            <option value="Trống">Trống</option>
            <option value="Đang sử dụng">Đang sử dụng</option>
            <option value="Đã đặt trước">Đã đặt trước</option>
          </select>

          <input
              type="text"
              placeholder="Tìm kiếm bàn..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="border p-2 rounded w-full md:w-64  text-gray-700"
          />
        </div>

        <div className="overflow-x-auto">
          <table className="w-full table-auto border text-sm md:text-base">
            <thead className="bg-gray-400">
            <tr>
              <th className="border px-2 py-2">STT</th>
              <th className="border px-2 py-2">Tên bàn</th>
              <th className="border px-2 py-2">Sức chứa</th>
              <th className="border px-2 py-2">Trạng thái</th>
              <th className="border px-2 py-2">Ghi chú</th>
              <th className="border px-2 py-2">Thời gian đặt</th>
              <th className="border px-2 py-2">Hành động</th>
            </tr>
            </thead>
            <tbody>
            {filteredTables.map((table, index) => (
                <tr key={table.id} className="hover:bg-gray-600">
                  <td
                      className="border px-2 py-2 text-center cursor-pointer text-white"
                      onClick={() => handleEdit(table)}
                  >
                    {index + 1}
                  </td>
                  <td
                      className="border px-2 py-2 cursor-pointer text-white"
                      onClick={() => handleEdit(table)}
                  >
                    {table.table_number}
                  </td>
                  <td className="border px-2 py-2 text-center align-top">{table.capacity}</td>
                  {/* <td className={`border px-2 py-2 text-center ${getStatusColor(table.status)}`}>
                  {table.status}
                </td> */}
                  <td className="border px-2 py-2 text-center">
                    <select
                        value={table.status}
                        onChange={(e) => handleChangeStatus(table.id, e.target.value)}
                        className={`border px-2 py-2 text-center ${getStatusColor(table.status)}`}
                    >
                      <option value="Trống">Trống</option>
                      <option value="Đang sử dụng">Đang sử dụng</option>
                      <option value="Đã đặt trước">Đã đặt trước</option>
                    </select>
                  </td>
                  <td className="border px-2 py-2 align-top whitespace-pre-wrap break-words max-w-xs">{table.note || "-"}</td>
                  <td className="border px-2 py-2 text-center">{table.reservedAt || "-"}</td>
                  <td className="border px-2 py-2 text-center space-x-2">
                    <button
                        onClick={() => handleEdit(table)}
                        className="bg-yellow-400 text-white px-2 py-1 rounded"
                    >
                      Sửa
                    </button>
                    <button
                        onClick={() => handleDelete(table.id)}
                        className="bg-red-500 text-white px-2 py-1 rounded"
                    >
                      Xoá
                    </button>
                  </td>
                </tr>
            ))}
            </tbody>
          </table>
        </div>
        <div className="mt-10">
          <h2 className="text-xl font-bold mb-2">📅 Lịch bàn đã đặt</h2>

          <div className="mb-4 text-gray-400">
            <label className="mr-2">Chọn ngày:</label>
            <input
                type="date"
                value={filterReservedDate}
                onChange={(e) => setFilterReservedDate(e.target.value)}
                className="border p-2 rounded"
            />
          </div>

          <div className="space-y-2">
            {tables.filter((t) => {
              if (t.status !== "Đã đặt trước") return false;
              if (!filterReservedDate) return true;
              return t.reservedAt.startsWith(filterReservedDate);
            }).length === 0 ? (
                <p className="text-gray-500 italic">Không có bàn nào được đặt trong ngày này.</p>
            ) : (
                tables.filter((t) => {
                  if (t.status !== "Đã đặt trước") return false;
                  if (!filterReservedDate) return true;
                  return t.reservedAt.startsWith(filterReservedDate);
                }).map((t) => (
                    <div key={t.id} className="border p-4 rounded bg-yellow-50 text-gray-700">
                      <p className="font-semibold">🪑 {t.table_number} - Sức chứa: {t.capacity}</p>
                      <p>⏰ Thời gian: {t.reservedAt}</p>
                      {t.note && <p>📝 Ghi chú: {t.note}</p>}
                    </div>
                ))
            )}
          </div>
        </div>
      </div>
  );
};

export default ManageTablesPage;