import { useState, useEffect } from "react";
import axios from "axios";

const MenuPage = () => {
  const [userRole, setUserRole] = useState("admin");
  const [menus, setMenus] = useState([]);
  const [form, setForm] = useState({
    name: "",
    price: "",
    category: "",
    description: "",
    imageUrl: "",
    ingredient: "",
  });
  const [isEditing, setIsEditing] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [searchTerm, setSearchTerm] = useState("");
  const [filterCategory, setFilterCategory] = useState("Tất cả");
  const [selectedFile, setSelectedFile] = useState(null);
  const [previewUrl, setPreviewUrl] = useState("");
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 8;

  useEffect(() => {
    axios
      .get("http://localhost:8080/api/menus")
      .then((res) => setMenus(res.data))
      .catch((err) => console.error("Lỗi khi lấy dữ liệu:", err));
  }, []);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleFileChange = (e) => {
    const file = e.target.files[0];
    if (!file) return;
    setSelectedFile(file);
    setPreviewUrl(URL.createObjectURL(file));
  };

  const resetForm = () => {
    setForm({
      name: "",
      price: "",
      category: "",
      description: "",
      imageUrl: "",
      ingredient: "",
    });
    setIsEditing(false);
    setEditingId(null);
    setSelectedFile(null);
    setPreviewUrl("");
  };

  const handleSave = async () => {
    let imageUrl = form.imageUrl;
    if (selectedFile) {
      const formData = new FormData();
      formData.append("image", selectedFile);
      try {
        const res = await axios.post(
          "http://localhost:8080/api/menus/upload",
          formData,
          { headers: { "Content-Type": "multipart/form-data" } }
        );
        imageUrl = res.data.imageUrl;
      } catch (err) {
        alert("Lỗi khi upload ảnh");
        return;
      }
    }
    const fullData = { ...form, imageUrl };
    try {
      if (isEditing) {
        await axios.put(`http://localhost:8080/api/menus/${editingId}`, fullData);
      } else {
        await axios.post("http://localhost:8080/api/menus", fullData);
      }
      const updatedMenus = await axios.get("http://localhost:8080/api/menus");
      setMenus(updatedMenus.data);
      resetForm();
    } catch (err) {
      alert("Lỗi khi lưu món ăn");
    }
  };

  const handleEdit = (item) => {
    setForm({ ...item });
    setIsEditing(true);
    setEditingId(item.menuId);
    setPreviewUrl(item.imageUrl);
    setSelectedFile(null);
  };

  const handleDelete = async (id) => {
    if (window.confirm("Bạn có chắc chắn muốn xoá món này không?")) {
      try {
        await axios.delete(`http://localhost:8080/api/menus/${id}`);
        setMenus(menus.filter((m) => m.menuId !== id));
      } catch (err) {
        alert("Lỗi khi xoá món");
      }
    }
  };

  const filteredMenus = menus.filter(
    (m) =>
      m.name.toLowerCase().includes(searchTerm.toLowerCase()) &&
      (filterCategory === "Tất cả" || m.category === filterCategory)
  );

  const countByCategory = menus.reduce((acc, item) => {
    acc[item.category] = (acc[item.category] || 0) + 1;
    return acc;
  }, {});

  const totalPages = Math.ceil(filteredMenus.length / itemsPerPage);
  const paginatedMenus = filteredMenus.slice(
    (currentPage - 1) * itemsPerPage,
    currentPage * itemsPerPage
  );

  useEffect(() => {
    setCurrentPage(1);
  }, [searchTerm, filterCategory]);

  return (
    <div className="p-4 space-y-4 flex-1 relative z-10 overflow-auto md:p-6 text-gray-800">
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-2">
        <h1 className="text-2xl font-bold text-white">Quản lý thực đơn</h1>
        <div className="flex items-center gap-2">
          <label className="text-sm font-medium text-white">🔐 Quyền:</label>
          <select
            value={userRole}
            onChange={(e) => setUserRole(e.target.value)}
            className="border p-1 rounded text-sm"
          >
            <option value="admin">Admin</option>
            <option value="chef">Đầu bếp</option>
            <option value="staff">Nhân viên</option>
          </select>
        </div>
      </div>

      {userRole === "admin" && (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
          <input name="name" placeholder="Tên món" value={form.name} onChange={handleChange} className="border p-2 rounded" />
          <input name="price" type="number" placeholder="Giá" value={form.price} onChange={handleChange} className="border p-2 rounded" />
          <select name="category" value={form.category} onChange={handleChange} className="border p-2 rounded">
            <option value="Món chính">Món chính</option>
            <option value="Khai vị">Khai vị</option>
            <option value="Đồ uống">Đồ uống</option>
            <option value="Tráng miệng">Tráng miệng</option>
          </select>
          <input name="description" placeholder="Ghi chú" value={form.description} onChange={handleChange} className="border p-2 rounded sm:col-span-2" />
          <div className="flex items-center gap-2">
            <input type="file" accept="image/*" onChange={handleFileChange} className="border p-2 rounded" />
            {previewUrl && <img src={previewUrl} alt="preview" className="w-12 h-12 object-cover rounded" />}
          </div>
          <input name="ingredient" placeholder="Nguyên liệu" value={form.ingredient} onChange={handleChange} className="border p-2 rounded sm:col-span-2 lg:col-span-3" />
          <div className="flex gap-2">
            <button onClick={handleSave} className="bg-green-600 text-white px-4 py-2 rounded">
              {isEditing ? "Sửa" : "Thêm"}
            </button>
            <button onClick={resetForm} className="bg-gray-400 text-white px-4 py-2 rounded">Huỷ</button>
          </div>
        </div>
      )}

      <div className="flex flex-col sm:flex-row items-start sm:items-center gap-2">
        <input
          type="text"
          placeholder="Tìm kiếm món..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="border p-2 rounded w-full sm:w-auto"
        />
        <select
          value={filterCategory}
          onChange={(e) => setFilterCategory(e.target.value)}
          className="border p-2 rounded w-full sm:w-auto"
        >
          <option value="Tất cả">Tất cả</option>
          <option value="Món chính">Món chính</option>
          <option value="Khai vị">Khai vị</option>
          <option value="Đồ uống">Đồ uống</option>
          <option value="Tráng miệng">Tráng miệng</option>
        </select>
        <p className="text-sm text-gray-500 italic">Đang hiển thị {filteredMenus.length} món ăn phù hợp</p>
      </div>

      <div>
        <h2 className="font-semibold mb-2">📦 Thống kê số lượng món theo loại</h2>
        <div className="flex flex-wrap gap-2">
          {Object.entries(countByCategory).map(([cat, count]) => (
            <div key={cat} className="border rounded px-4 py-2 bg-gray-100 text-gray-800 shadow-sm">
              {cat}: <strong>{count}</strong>
            </div>
          ))}
        </div>
      </div>

            <div className="overflow-x-auto">
          {/* Bảng trên màn hình lớn */}
          <table className="min-w-[800px] w-full border text-sm hidden md:table">
            <thead className="bg-gray-800 text-white">
              <tr>
                <th className="p-2 border">#</th>
                <th className="p-2 border">Ảnh</th>
                <th className="p-2 border">Tên món</th>
                <th className="p-2 border">Giá</th>
                <th className="p-2 border">Loại</th>
                <th className="p-2 border">Nguyên liệu</th>
                <th className="p-2 border">Ghi chú</th>
                {userRole === "admin" && <th className="p-2 border">Hành động</th>}
              </tr>
            </thead>
            <tbody>
              {paginatedMenus.map((item, index) => (
                <tr key={item.menuId} className="hover:bg-gray-600">
                  <td className="p-2 border text-white">{(currentPage - 1) * itemsPerPage + index + 1}</td>
                  <td className="p-2 border">
                    {item.imageUrl ? (
                      <img src={`http://localhost:8080${item.imageUrl}`} className="w-16 h-16 object-cover" />
                    ) : (
                      <em>❌</em>
                    )}
                  </td>
                  <td className="p-2 border text-white">{item.name}</td>
                  <td className="p-2 border text-white">{parseInt(item.price).toLocaleString()} đ</td>
                  <td className="p-2 border text-white">{item.category}</td>
                  <td className="p-2 border text-white">{item.ingredient}</td>
                  <td className="p-2 border text-white whitespace-pre-wrap break-words max-w-xs">{item.description}</td>
                  {userRole === "admin" && (
                    <td className="p-2 border space-y-1 md:space-x-2">
                      <button
                        onClick={() => handleEdit(item)}
                        className="bg-yellow-500 hover:bg-yellow-600 text-white px-3 py-1 rounded shadow block md:inline"
                      >
                        ✏️ Sửa
                      </button>
                      <button
                        onClick={() => handleDelete(item.menuId)}
                        className="bg-red-500 hover:bg-red-600 text-white px-3 py-1 rounded shadow block md:inline"
                      >
                        🗑 Xoá
                      </button>
                    </td>
                  )}
                </tr>
              ))}
            </tbody>
          </table>

          {/* Card View cho màn hình nhỏ */}
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6 md:hidden">
            {paginatedMenus.map((item, index) => (
              <div key={item.menuId} className="bg-white p-4 rounded-lg shadow-md hover:shadow-lg transition duration-300">
                {/* Hiển thị ảnh món */}
                <div className="w-full h-40 mb-4 flex justify-center items-center">
                  {item.imageUrl ? (
                    <img src={`http://localhost:8080${item.imageUrl}`} className="w-32 h-32 object-cover rounded-full" />
                  ) : (
                    <em>❌</em>
                  )}
                </div>
                {/* Thông tin món ăn */}
                <h3 className="text-lg font-semibold text-gray-800">{item.name}</h3>
                <p className="text-sm text-gray-600">{item.category}</p>
                <p className="text-sm text-gray-600">{item.ingredient}</p>
                <p className="text-sm text-gray-600">{parseInt(item.price).toLocaleString()} đ</p>
                <p className="text-sm text-gray-600 mt-2">{item.description}</p>

                {/* Hành động cho admin */}
                {userRole === "admin" && (
                  <div className="mt-4 flex space-x-2">
                    <button
                      onClick={() => handleEdit(item)}
                      className="bg-yellow-500 hover:bg-yellow-600 text-white px-4 py-2 rounded shadow"
                    >
                      ✏️ Sửa
                    </button>
                    <button
                      onClick={() => handleDelete(item.menuId)}
                      className="bg-red-500 hover:bg-red-600 text-white px-4 py-2 rounded shadow"
                    >
                      🗑 Xoá
                    </button>
                  </div>
                )}
              </div>
            ))}
          </div>
        </div>

      <div className="flex flex-wrap justify-center mt-4 gap-2">
        <button
          disabled={currentPage === 1}
          onClick={() => setCurrentPage(currentPage - 1)}
          className="px-3 py-1 bg-green-900 text-white rounded hover:bg-gray-300 disabled:opacity-50"
        >
          ⏮ Trước
        </button>
        {[...Array(totalPages)].map((_, idx) => (
          <button
            key={idx}
            onClick={() => setCurrentPage(idx + 1)}
            className={`px-3 py-1 rounded ${currentPage === idx + 1 ? "bg-blue-600 text-white" : "bg-gray-100 hover:bg-gray-200"}`}
          >
            {idx + 1}
          </button>
        ))}
        <button
          disabled={currentPage === totalPages}
          onClick={() => setCurrentPage(currentPage + 1)}
          className="px-3 py-1 bg-green-900 text-white rounded hover:bg-gray-300 disabled:opacity-50"
        >
          Tiếp ⏭
        </button>
      </div>
    </div>
  );
};

export default MenuPage;