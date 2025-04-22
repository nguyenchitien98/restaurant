import { getBadgeColor } from "../../utils/badgeColors";

const BangDanhSachDon = ({ filteredOrders, handleSua, handleXoa, handleInHoaDon, handleChangeStatus, setModalOrder }) => (
  <div className="w-full">
    {/* BẢNG TRUYỀN THỐNG - Chỉ hiển thị trên màn hình md trở lên */}
    <div className="overflow-x-auto hidden md:block">
      <table className="min-w-full bg-white rounded shadow">
        <thead>
          <tr className="bg-gray-200 text-gray-700 text-sm uppercase">
            <th className="py-2 px-4 text-left">Bàn</th>
            <th className="py-2 px-4 text-left">Món Ăn</th>
            <th className="py-2 px-4 text-left">Ghi chú</th>
            <th className="py-2 px-4 text-left">Trạng Thái</th>
            <th className="py-2 px-4 text-left">Thời Gian</th>
            <th className="py-2 px-4 text-left">Hành Động</th>
          </tr>
        </thead>
        <tbody>
          {filteredOrders.map((order, idx) => (
            <tr key={idx} className="border-b hover:bg-gray-50">
              <td className="py-2 px-4 cursor-pointer text-gray-400" onClick={() => setModalOrder(order)}>{order.soBan}</td>
              <td className="py-2 px-4 text-gray-400">
                {order.monAn.map((m, i) => (
                  <div key={i}>{m.tenMon} x{m.soLuong}</div>
                ))}
              </td>
              <td className="py-2 px-4 text-gray-500 text-sm italic">
                {order.ghiChu || '—'}
              </td>
              <td className="py-2 px-4">
                <select
                  value={order.trangThai}
                  onChange={(e) => handleChangeStatus(idx, e.target.value)}
                  className={`px-2 py-1 text-sm rounded ${getBadgeColor(order.trangThai)}`}
                >
                  <option value="Đang chuẩn bị">Đang chuẩn bị</option>
                  <option value="Hoàn thành">Hoàn thành</option>
                  <option value="Đã thanh toán">Đã thanh toán</option>
                  <option value="Hủy">Hủy</option>
                </select>
              </td>
              <td className="py-2 px-4 text-gray-400">{order.thoiGian}</td>
              <td className="py-2 px-4 space-x-2">
                <button onClick={() => handleSua(idx)} className="bg-yellow-500 text-white px-3 py-1 rounded hover:bg-yellow-600">Thêm Món</button>
                <button onClick={() => handleXoa(idx)} className="bg-red-600 text-white px-3 py-1 rounded hover:bg-red-700">Xóa</button>
                <button onClick={() => handleInHoaDon(order)} className="bg-blue-600 text-white px-3 py-1 rounded hover:bg-blue-700">In</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>

    {/* THẺ RESPONSIVE - Hiển thị trên màn hình nhỏ */}
    <div className="block md:hidden space-y-4">
      {filteredOrders.length === 0 ? (
        <div className="text-center py-4 text-gray-500">Không có đơn hàng phù hợp</div>
      ) : (
        filteredOrders.map((order, idx) => (
          <div key={idx} className="bg-white rounded shadow p-4 space-y-2">
            <div className="text-sm text-gray-600"><strong>Bàn:</strong> {order.soBan}</div>
            <div className="text-sm text-gray-600">
              <strong>Món Ăn:</strong>
              <ul className="list-disc list-inside">
                {order.monAn.map((m, i) => (
                  <li key={i}>{m.tenMon} x{m.soLuong}</li>
                ))}
              </ul>
            </div>
            <div className="text-sm text-gray-500 italic"><strong>Ghi chú:</strong> {order.ghiChu || '—'}</div>
            <div>
              <select
                value={order.trangThai}
                onChange={(e) => handleChangeStatus(idx, e.target.value)}
                className={`w-full px-2 py-1 text-sm rounded ${getBadgeColor(order.trangThai)}`}
              >
                <option value="Đang chuẩn bị">Đang chuẩn bị</option>
                <option value="Hoàn thành">Hoàn thành</option>
                <option value="Đã thanh toán">Đã thanh toán</option>
                <option value="Hủy">Hủy</option>
              </select>
            </div>
            <div className="text-xs text-gray-400"><strong>Thời gian:</strong> {order.thoiGian}</div>
            <div className="flex flex-wrap gap-2 mt-2">
              <button onClick={() => handleSua(idx)} className="flex-1 bg-yellow-500 text-white px-3 py-1 rounded hover:bg-yellow-600">Thêm Món</button>
              <button onClick={() => handleXoa(idx)} className="flex-1 bg-red-600 text-white px-3 py-1 rounded hover:bg-red-700">Xóa</button>
              <button onClick={() => handleInHoaDon(order)} className="flex-1 bg-blue-600 text-white px-3 py-1 rounded hover:bg-blue-700">In</button>
            </div>
          </div>
        ))
      )}
    </div>
  </div>
);

export default BangDanhSachDon;