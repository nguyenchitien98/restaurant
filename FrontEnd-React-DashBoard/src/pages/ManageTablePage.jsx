import { useState,useEffect, useRef } from "react";

const ManageTablesPage = () => {
  const [tables, setTables] = useState([
    { id: 1, name: "Bàn 1", capacity: 4, status: "Trống", note: "", reservedAt: "" },
    { id: 2, name: "Bàn 2", capacity: 6, status: "Đang sử dụng", note: "Khách VIP", reservedAt: "" },
  ]);

  const [form, setForm] = useState({
    name: "",
    capacity: "",
    status: "Trống",
    note: "",
    reservedDate: "",
    reservedTime: ""
  });

  const [filterReservedDate, setFilterReservedDate] = useState("");
  const [isEditing, setIsEditing] = useState(false);
  const [editingId, setEditingId] = useState(null);

  const [filterStatus, setFilterStatus] = useState("Tất cả");
  const [searchTerm, setSearchTerm] = useState("");
  const [reminders, setReminders] = useState([]);
  const [showReminder, setShowReminder] = useState(true);

  const audioRef = useRef(null);

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
    setForm({ name: "", capacity: "", status: "Trống", note: "", reservedDate: "", reservedTime: "" });
    setIsEditing(false);
    setEditingId(null);
  };

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleAdd = () => {
    if (!form.name || !form.capacity) return;
    const reservedAt = form.status === "Đã đặt trước" && form.reservedDate && form.reservedTime
      ? `${form.reservedDate} ${form.reservedTime}`
      : "";
    const newTable = {
      id: Date.now(),
      name: form.name,
      capacity: parseInt(form.capacity),
      status: form.status,
      note: form.note,
      reservedAt
    };
    setTables([...tables, newTable]);
    resetForm();
  };

  const handleEdit = (table) => {
    const [reservedDate = "", reservedTime = ""] = table.reservedAt?.split(" ") || [];
    setForm({
      name: table.name,
      capacity: table.capacity,
      status: table.status,
      note: table.note,
      reservedDate,
      reservedTime
    });
    setIsEditing(true);
    setEditingId(table.id);
  };

  const handleUpdate = () => {
    const reservedAt = form.status === "Đã đặt trước" && form.reservedDate && form.reservedTime
      ? `${form.reservedDate} ${form.reservedTime}`
      : "";
    setTables(
      tables.map((t) =>
        t.id === editingId ? {
          ...t,
          ...form,
          capacity: parseInt(form.capacity),
          reservedAt
        } : t
      )
    );
    resetForm();
  };

  const handleDelete = (id) => {
    const confirmDelete = window.confirm("Bạn có chắc chắn muốn xoá bàn này không?");
    if (confirmDelete) {
      setTables(tables.filter((t) => t.id !== id));
    }
  };

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
    const matchSearch = t.name.toLowerCase().includes(searchTerm.toLowerCase());
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
              <p>🪑 {t.name} - ⏰ {t.reservedAt}</p>
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
          type="text"
          name="name"
          value={form.name}
          onChange={handleChange}
          placeholder="Tên bàn"
          className="border p-2 rounded"
        />

        <input
          type="number"
          name="capacity"
          value={form.capacity}
          onChange={handleChange}
          placeholder="Sức chứa"
          className="border p-2 rounded"
        />

        <select
          name="status"
          value={form.status}
          onChange={handleChange}
          className="border p-2 rounded text-gray-400"
        >
          <option value="Trống">Trống</option>
          <option value="Đang sử dụng">Đang sử dụng</option>
          <option value="Đã đặt trước">Đã đặt trước</option>
        </select>

        <input
          type="text"
          name="note"
          value={form.note}
          onChange={handleChange}
          placeholder="Ghi chú"
          className="border p-2 rounded"
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
          className="border p-2 rounded w-full md:w-auto text-gray-400"
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
          className="border p-2 rounded w-full md:w-64"
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
                  {table.name}
                </td>
                <td className="border px-2 py-2 text-center">{table.capacity}</td>
                <td className={`border px-2 py-2 text-center ${getStatusColor(table.status)}`}>
                  {table.status}
                </td>
                <td className="border px-2 py-2">{table.note}</td>
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
              <div key={t.id} className="border p-4 rounded bg-yellow-50">
                <p className="font-semibold">🪑 {t.name} - Sức chứa: {t.capacity}</p>
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