import { useState } from "react";
import { useTranslation, initReactI18next } from "react-i18next";
import i18n from "i18next";

const resources = {
  vi: {
    translation: {
      "Menu Management": "Quản lý thực đơn",
      "Add": "Thêm",
      "Update": "Sửa",
      "Cancel": "Xóa",
      "Name": "Tên món",
      "Price": "Giá",
      "Category": "Loại",
      "Note": "Ghi chú",
      "Ingredients": "Nguyên liệu",
      "Image": "Ảnh",
      "Revenue": "Doanh thu",
      "Action": "Hành động",
      "Main Dish": "Món chính",
      "Starter": "Khai vị",
      "Drink": "Đồ uống",
      "Dessert": "Tráng miệng",
      "All": "Tất cả",
      "Delete Confirmation": "Bạn có chắc chắn muốn xoá món này không?",
      "Total Revenue": "Tổng doanh thu từ món ăn",
      "Statistics by Category": "Thống kê số lượng món theo loại"
    }
  },
  en: {
    translation: {
      "Menu Management": "Menu Management",
      "Add": "Add",
      "Update": "Update",
      "Cancel": "Cancel",
      "Name": "Name",
      "Price": "Price",
      "Category": "Category",
      "Note": "Note",
      "Ingredients": "Ingredients",
      "Image": "Image",
      "Revenue": "Revenue",
      "Action": "Action",
      "Main Dish": "Main Dish",
      "Starter": "Starter",
      "Drink": "Drink",
      "Dessert": "Dessert",
      "All": "All",
      "Delete Confirmation": "Are you sure you want to delete this dish?",
      "Total Revenue": "Total revenue from dishes",
      "Statistics by Category": "Dish count by category"
    }
  }
};

i18n.use(initReactI18next).init({
  resources,
  lng: "vi",
  fallbackLng: "vi",
  interpolation: { escapeValue: false }
});

const categoryMap = {
  main: "Main Dish",
  starter: "Starter",
  drink: "Drink",
  dessert: "Dessert"
};

const MenuPage = () => {
  const { t, i18n } = useTranslation();

  const [userRole, setUserRole] = useState("admin");
  const [menus, setMenus] = useState([
    {
      id: 1,
      name: "Phở bò",
      price: 45000,
      category: "main",
      note: "Nổi bật",
      image: "/public/image/pho-bo.jpg",
      ingredients: "Bò, bánh phở",
      revenue: 120000
    },
    {
      id: 2,
      name: "pizza",
      price: 34000,
      category: "main",
      note: "Nổi bật",
      image: "",
      ingredients: "Bò, bánh phở",
      revenue: 120000
    }
  ]);

  const [form, setForm] = useState({ name: "", price: "", category: "main", note: "", image: "", ingredients: "" });
  const [isEditing, setIsEditing] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [searchTerm, setSearchTerm] = useState("");
  const [filterCategory, setFilterCategory] = useState("all");

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const resetForm = () => {
    setForm({ name: "", price: "", category: "main", note: "", image: "", ingredients: "" });
    setIsEditing(false);
    setEditingId(null);
  };

  const handleAdd = () => {
    if (!form.name || !form.price) return;
    const newItem = { ...form, id: Date.now(), price: parseFloat(form.price), revenue: 0 };
    setMenus([...menus, newItem]);
    resetForm();
  };

  const handleEdit = (item) => {
    setForm({ ...item });
    setIsEditing(true);
    setEditingId(item.id);
  };

  const handleUpdate = () => {
    setMenus(menus.map((m) => (m.id === editingId ? { ...form, id: editingId, price: parseFloat(form.price) } : m)));
    resetForm();
  };

  const handleDelete = (id) => {
    if (window.confirm(t("Delete Confirmation"))) {
      setMenus(menus.filter((m) => m.id !== id));
    }
  };

  const filteredMenus = menus.filter((m) => {
    return (
      m.name.toLowerCase().includes(searchTerm.toLowerCase()) &&
      (filterCategory === "all" || m.category === filterCategory)
    );
  });

  const totalRevenue = menus.reduce((sum, item) => sum + item.revenue, 0);
  const countByCategory = menus.reduce((acc, item) => {
    acc[item.category] = (acc[item.category] || 0) + 1;
    return acc;
  }, {});

  return (
    <div className="p-4 space-y-4 flex-1 relative z-10 overflow-auto md:p-6">
      <div className="flex justify-between items-center">
        <h1 className="text-2xl font-bold">{t("Menu Management")}</h1>
        <select value={i18n.language} onChange={(e) => i18n.changeLanguage(e.target.value)} className="border p-1 rounded text-gray-400">
          <option value="vi">🇻🇳 Tiếng Việt</option>
          <option value="en">🇺🇸 English</option>
        </select>
      </div>

      <div>
        <label>🔐 Role: </label>
        <select value={userRole} onChange={(e) => setUserRole(e.target.value)} className="border p-1 rounded text-gray-400">
          <option value="admin">Admin</option>
          <option value="chef">Chef</option>
          <option value="staff">Staff</option>
        </select>
      </div>

      {userRole === "admin" && (
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <input name="name" placeholder={t("Name")} value={form.name} onChange={handleChange} className="border p-2 rounded" />
          <input name="price" type="number" placeholder={t("Price")} value={form.price} onChange={handleChange} className="border p-2 rounded" />
          <select name="category" value={form.category} onChange={handleChange} className="border p-2 rounded text-gray-400">
            <option value="main">{t("Main Dish")}</option>
            <option value="starter">{t("Starter")}</option>
            <option value="drink">{t("Drink")}</option>
            <option value="dessert">{t("Dessert")}</option>
          </select>
          <input name="note" placeholder={t("Note")} value={form.note} onChange={handleChange} className="border p-2 rounded col-span-2" />
          <input name="image" placeholder={t("Image")} value={form.image} onChange={handleChange} className="border p-2 rounded" />
          <input name="ingredients" placeholder={t("Ingredients")} value={form.ingredients} onChange={handleChange} className="border p-2 rounded col-span-3" />

          <div className="flex gap-2">
            {isEditing ? (
              <>
                <button onClick={handleUpdate} className="bg-blue-500 text-white px-4 py-2 rounded">{t("Update")}</button>
                <button onClick={resetForm} className="bg-gray-300 px-4 py-2 rounded">{t("Cancel")}</button>
              </>
            ) : (
              <button onClick={handleAdd} className="bg-green-500 text-white px-4 py-2 rounded">{t("Add")}</button>
            )}
          </div>
        </div>
      )}

      <div className="flex items-center gap-4">
        <input
          type="text"
          placeholder="Tìm kiếm món..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="border p-2 rounded"
        />
        <select value={filterCategory} onChange={(e) => setFilterCategory(e.target.value)} className="border p-2 rounded text-gray-400">
          <option value="all">{t("All")}</option>
          <option value="main">{t("Main Dish")}</option>
          <option value="starter">{t("Starter")}</option>
          <option value="drink">{t("Drink")}</option>
          <option value="dessert">{t("Dessert")}</option>
        </select>
      </div>

      <div className="font-semibold">📊 {t("Total Revenue")}: {totalRevenue.toLocaleString()} đ</div>
      <div>
        <h2 className="font-semibold">📦 {t("Statistics by Category")}:</h2>
        <ul className="list-disc list-inside">
          {Object.entries(countByCategory).map(([cat, count]) => (
            <li key={cat}>{t(categoryMap[cat])}: {count}</li>
          ))}
        </ul>
      </div>

      <table className="w-full border">
        <thead className="bg-gray-400">
          <tr>
            <th className="p-2 border">#</th>
            <th className="p-2 border">{t("Image")}</th>
            <th className="p-2 border">{t("Name")}</th>
            <th className="p-2 border">{t("Price")}</th>
            <th className="p-2 border">{t("Category")}</th>
            <th className="p-2 border">{t("Ingredients")}</th>
            <th className="p-2 border">{t("Note")}</th>
            <th className="p-2 border">{t("Revenue")}</th>
            {userRole === "admin" && <th className="p-2 border">{t("Action")}</th>}
          </tr>
        </thead>
        <tbody>
          {filteredMenus.map((item, index) => (
            <tr key={item.id} className="hover:bg-gray-600">
              <td className="p-2 border">{index + 1}</td>
              <td className="p-2 border">{item.image ? <img src={item.image} className="w-16 h-16 object-cover" /> : <em>❌</em>}</td>
              <td className="p-2 border">{item.name}</td>
              <td className="p-2 border">{item.price.toLocaleString()} đ</td>
              <td className="p-2 border">{t(categoryMap[item.category])}</td>
              <td className="p-2 border">{item.ingredients}</td>
              <td className="p-2 border">{item.note}</td>
              <td className="p-2 border">{item.revenue.toLocaleString()} đ</td>
              {userRole === "admin" && (
                <td className="p-2 border">
                  <button onClick={() => handleEdit(item)} className="bg-yellow-500 hover:bg-yellow-600 text-white px-3 py-1 rounded shadow">
                    ✏️ {t("Update")}
                  </button>
                  <button onClick={() => handleDelete(item.id)} className="bg-red-500 hover:bg-red-600 text-white px-3 py-1 rounded shadow">
                    🗑 {t("Cancel")}
                  </button>
                </td>
              )}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default MenuPage;
