const LocDonHang = ({ filterSoBan, setFilterSoBan, filterTrangThai, setFilterTrangThai, filterTenMon, setFilterTenMon }) => (
  <div className="grid grid-cols-2 md:grid-cols-5 gap-4 mb-6">
    <input
      type="text"
      placeholder="Lọc theo số bàn"
      value={filterSoBan}
      onChange={(e) => setFilterSoBan(e.target.value)}
      className="border px-3 py-2 rounded"
    />
    <select
      value={filterTrangThai}
      onChange={(e) => setFilterTrangThai(e.target.value)}
      className="border px-3 py-2 rounded text-gray-400"
    >
      <option value="">Tất cả trạng thái</option>
      <option value="Đang chuẩn bị">Đang chuẩn bị</option>
      <option value="Hoàn thành">Hoàn thành</option>
      <option value="Đã thanh toán">Đã thanh toán</option>
      <option value="Hủy">Hủy</option>
    </select>
    <input
      type="text"
      placeholder="Tìm theo tên món"
      value={filterTenMon}
      onChange={(e) => setFilterTenMon(e.target.value)}
      className="border px-3 py-2 rounded col-span-2"
    />
  </div>
);

export default LocDonHang;
