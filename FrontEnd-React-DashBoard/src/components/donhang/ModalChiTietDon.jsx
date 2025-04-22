import { getBadgeColor } from "../../utils/badgeColors";

const ModalChiTietDon = ({ modalOrder, setModalOrder, orders, handleChangeMonStatus, tinhTongTien }) => (
  <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
    <div className="bg-white p-6 rounded-lg shadow-lg w-full max-w-md relative">
      <button
        className="absolute top-2 right-3 text-gray-600 hover:text-black"
        onClick={() => setModalOrder(null)}
      >✖</button>
      <h3 className="text-lg font-semibold mb-2">Chi tiết đơn hàng</h3>
      <p><strong>Bàn:</strong> {modalOrder.soBan}</p>
      <p><strong>Thời gian:</strong> {modalOrder.thoiGian}</p>
      <p><strong>Ghi chú:</strong> {modalOrder.ghiChu}</p>
      <p><strong>Trạng thái đơn:</strong> {modalOrder.trangThai}</p>
      <ul className="mt-2 space-y-2">
        {modalOrder.monAn.map((m, i) => (
          <li key={i} className="border p-2 rounded flex items-center justify-between">
            <div>
              {m.tenMon} x{m.soLuong} – {m.donGia.toLocaleString('vi-VN')}₫
            </div>
            <select
              className={`text-sm rounded px-2 py-1 ${getBadgeColor(m.trangThai)}`}
              value={m.trangThai}
              onChange={(e) => {
                const orderIndex = orders.findIndex(o => o.soBan === modalOrder.soBan);
                handleChangeMonStatus(orderIndex, i, e.target.value);
              }}
            >
              <option>Đang chuẩn bị</option>
              <option>Hoàn thành</option>
              <option>Đã thanh toán</option>
              <option>Hủy</option>
            </select>
          </li>
        ))}
      </ul>
      <p className="mt-4 font-semibold text-right">
        Tổng tiền: {tinhTongTien(modalOrder.monAn).toLocaleString('vi-VN')}₫
      </p>
    </div>
  </div>
);

export default ModalChiTietDon;