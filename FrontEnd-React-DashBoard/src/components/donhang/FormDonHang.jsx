const FormDonHang = ({
  soBan,
  trangThai,
  setTrangThai,
  ghiChu,
  setGhiChu,
  handleThemMon,
  editIndex,
  setEditIndex,
  setSoBan,
  setMonAn,
  setSoLuong,
  setMonAnDaChon,
  trangThaiDisabled,
  setTrangThaiDisabled,
  orders
}) => (
  <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-4">
    <input type="text" value={soBan} readOnly placeholder="Bàn" className="border rounded px-3 py-2 bg-gray-100 text-gray-600" />

    <select
      value={trangThai}
      onChange={(e) => setTrangThai(e.target.value)}
      disabled={trangThaiDisabled}
      className={`border rounded px-3 py-2 ${trangThaiDisabled ? 'bg-gray-100 text-gray-400 cursor-not-allowed' : 'text-gray-700'}`}
    >
      <option value="Đang chuẩn bị">Đang chuẩn bị</option>
      {editIndex !== null && (
        <>
          <option value="Hoàn thành">Hoàn thành</option>
          <option value="Đã thanh toán">Đã thanh toán</option>
          <option value="Hủy">Hủy</option>
        </>
      )}
    </select>

    <textarea
      placeholder="Ghi chú"
      value={ghiChu}
      onChange={(e) => setGhiChu(e.target.value)}
      className="border rounded px-3 py-2 text-gray-600"
    />

    <div className="flex flex-col sm:flex-row gap-2 w-full">
      <button onClick={handleThemMon} className="bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700">
        {editIndex !== null
          ? 'Lưu'
            : 'Thêm Đơn'}
      </button>
      {editIndex !== null && (
        <button
          onClick={() => {
            setEditIndex(null);
            setSoBan('');
            setMonAn('');
            setSoLuong(1);
            setGhiChu('');
            setTrangThai('Đang chuẩn bị');
            setMonAnDaChon([]);
            setTrangThaiDisabled(false);
          }}
          className="bg-gray-400 text-white px-4 py-2 rounded hover:bg-gray-500" 
        >
          Hủy
        </button>
      )}
    </div>
  </div>
);

export default FormDonHang;