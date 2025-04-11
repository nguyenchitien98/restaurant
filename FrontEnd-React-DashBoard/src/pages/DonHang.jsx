import { useState, useEffect } from 'react';
import axios from 'axios';

const getBadgeColor = (status) => {
  switch (status) {
    case 'Đang chuẩn bị': return 'bg-yellow-100 text-yellow-800';
    case 'Hoàn thành': return 'bg-green-100 text-green-800';
    case 'Đã thanh toán': return 'bg-blue-100 text-blue-800';
    case 'Hủy': return 'bg-red-100 text-red-800';
    default: return 'bg-gray-100 text-gray-800';
  }
};

const convertStatus = (status) => {
  switch (status) {
    case 'PENDING': return 'Đang chuẩn bị';
    case 'COMPLETED': return 'Hoàn thành';
    case 'PAID': return 'Đã thanh toán';
    case 'CANCELLED': return 'Hủy';
    default: return 'Không rõ';
  }
};

const DonHang = () => {
  const [orders, setOrders] = useState([]);
  const [monAnDaChon, setMonAnDaChon] = useState([]);
  const [soBan, setSoBan] = useState('');
  const [monAn, setMonAn] = useState('');
  const [soLuong, setSoLuong] = useState(1);
  const [ghiChu, setGhiChu] = useState('');
  const [trangThai, setTrangThai] = useState('Đang chuẩn bị');
  const [editIndex, setEditIndex] = useState(null);
  const [filterSoBan, setFilterSoBan] = useState('');
  const [filterTrangThai, setFilterTrangThai] = useState('');
  const [filterTenMon, setFilterTenMon] = useState('');
  const [modalOrder, setModalOrder] = useState(null);
  const [danhSachMonAn, setDanhSachMonAn] = useState([]);
  const [danhSachBan, setDanhSachBan] = useState([]);

  useEffect(() => {
    axios.get("http://localhost:8080/api/menus")
        .then(res => {
          const convert = res.data.map(item => ({
            tenMon: item.name,
            donGia: item.price
          }));
          setDanhSachMonAn(convert);
        })
        .catch(err => console.error("Lỗi lấy danh sách món ăn:", err));

    axios.get("http://localhost:8080/api/tables")
        .then(res => {
          const convert = res.data.map(ban => ({ id: ban.table_number }));
          setDanhSachBan(convert);
        })
        .catch(err => console.error("Lỗi lấy danh sách bàn:", err));

    axios.get("http://localhost:8080/api/orders")
        .then(res => {
          const convert = res.data.map(order => ({
            soBan: order.tableNumber || '—',
            monAn: order.items.map(item => ({
              tenMon: item.menuName,
              soLuong: item.quantity,
              donGia: item.price,
              trangThai: convertStatus(order.status) // chuyển "PENDING" -> "Đang chuẩn bị"
            })),
            ghiChu: order.note,
            trangThai: convertStatus(order.status),
            thoiGian: order.createdAt
                ? new Date(order.createdAt).toLocaleString()
                : new Date().toLocaleString()
          }));
          setOrders(convert);
        })
        .catch(err => console.error("Lỗi lấy danh sách đơn hàng:", err));
  }, []);

  const tinhTongTien = (monAnList) => {
    return monAnList.reduce((tong, mon) => {
      const donGia = mon.donGia ?? 0;
      return tong + mon.soLuong * donGia;
    }, 0);
  };

  const handleChonBan = (banId) => {
    setSoBan(banId);
  };

  const handleThemMon = () => {
    if (!soBan || monAnDaChon.length === 0) {
      return alert('Vui lòng chọn bàn và ít nhất một món ăn');
    }

    const donMoi = monAnDaChon.map(mon => ({
      tenMon: mon.tenMon,
      soLuong: parseInt(mon.soLuong),
      donGia: mon.donGia || 0,
      trangThai: 'Đang chuẩn bị'
    }));

    if (editIndex !== null) {
      const updated = [...orders];
      updated[editIndex] = {
        ...updated[editIndex],
        monAn: donMoi,
        ghiChu,
        trangThai
      };
      setOrders(updated);
      setEditIndex(null);
    } else {
      const indexBan = orders.findIndex(o => o.soBan === soBan && o.trangThai !== 'Đã thanh toán');

      if (indexBan !== -1) {
        const updated = [...orders];
        donMoi.forEach(mon => {
          const monDaCo = updated[indexBan].monAn.find(m => m.tenMon === mon.tenMon);
          if (monDaCo) {
            monDaCo.soLuong += mon.soLuong;
          } else {
            updated[indexBan].monAn.push(mon);
          }
        });

        updated[indexBan].ghiChu += ghiChu ? ` | ${ghiChu}` : '';
        setOrders(updated);
      } else {
        const newOrder = {
          soBan,
          monAn: donMoi,
          ghiChu,
          trangThai,
          thoiGian: new Date().toLocaleString()
        };
        setOrders([...orders, newOrder]);

        const orderBody = {
          tableId: soBan,
          userId: 1,
          orderDetails: donMoi.map(m => ({
            menuName: m.tenMon,
            quantity: m.soLuong,
            price: m.donGia
          })),
          note: ghiChu
        };

        // ✅ Gửi đơn hàng
        axios.post("http://localhost:8080/api/orders", orderBody)
            .then(res => {
              console.log("Đã gửi đơn hàng:", res.data);
            })
            .catch(err => console.error("Lỗi tạo đơn hàng:", err));

        // ✅ Gọi PUT để cập nhật trạng thái bàn sang OCCUPIED
        axios.put(`http://localhost:8080/api/tables/${soBan}/status`, {
          status: 'OCCUPIED'
        }).then(() => {
          console.log(`Đã cập nhật bàn ${soBan} thành OCCUPIED`);
        }).catch(err => {
          console.error("Lỗi cập nhật trạng thái bàn:", err);
        });
      }
    }

    // Reset form
    setSoBan('');
    setMonAn('');
    setSoLuong(1);
    setGhiChu('');
    setTrangThai('Đang chuẩn bị');
    setMonAnDaChon([]);
  };

  const handleXoa = (index) => {
    if (window.confirm('Xóa đơn hàng này?')) {
      setOrders(orders.filter((_, i) => i !== index));
    }
  };

  const handleSua = (index) => {
    const order = orders[index];
    setSoBan(order.soBan);
    setMonAn(order.monAn[0].tenMon);
    setSoLuong(order.monAn[0].soLuong);
    setGhiChu(order.ghiChu);
    setTrangThai(order.trangThai);
    setEditIndex(index);
  };

  const handleChangeMonStatus = (orderIndex, monIndex, newStatus) => {
    const updated = [...orders];
    updated[orderIndex].monAn[monIndex].trangThai = newStatus;
    setOrders(updated);
  };


  const handleInHoaDon = (order) => {
    const ngayIn = new Date().toLocaleString('vi-VN');

    const hoaDonHTML = `
    <html>
      <head>
        <title>Hóa Đơn Bàn ${order.soBan}</title>
        <style>
          body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            padding: 30px;
            color: #333;
          }
          h2 {
            text-align: center;
            margin-bottom: 20px;
          }
          .info {
            margin-bottom: 15px;
          }
          table {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 15px;
          }
          th, td {
            border: 1px solid #ccc;
            padding: 8px 12px;
            text-align: left;
          }
          th {
            background-color: #f4f4f4;
          }
          .total {
            text-align: right;
            font-size: 18px;
            font-weight: bold;
            margin-top: 10px;
          }
          .footer {
            text-align: center;
            margin-top: 20px;
            font-style: italic;
            font-size: 13px;
            color: #777;
          }
        </style>
      </head>
      <body>
        <h2>🍜 NHÀ HÀNG HƯƠNG VIỆT</h2>

        <div class="info">
          <p><strong>Bàn:</strong> ${order.soBan}</p>
          <p><strong>Thời gian gọi món:</strong> ${order.thoiGian}</p>
          <p><strong>Thời gian in hóa đơn:</strong> ${ngayIn}</p>
          <p><strong>Trạng thái:</strong> Đã thanh toán</p>
          ${order.ghiChu ? `<p><strong>Ghi chú:</strong> ${order.ghiChu}</p>` : ''}
        </div>

        <table>
          <thead>
            <tr>
              <th>Món ăn</th>
              <th>Số lượng</th>
              <th>Đơn giá (₫)</th>
              <th>Thành tiền (₫)</th>
            </tr>
          </thead>
          <tbody>
            ${order.monAn.map(m => {
      const gia = m.donGia ?? 0;
      return `
                <tr>
                  <td>${m.tenMon}</td>
                  <td>${m.soLuong}</td>
                  <td>${gia.toLocaleString('vi-VN')}</td>
                  <td>${(gia * m.soLuong).toLocaleString('vi-VN')}</td>
                </tr>`;
    }).join('')}
          </tbody>
        </table>

        <div class="total">
          Tổng cộng: ${tinhTongTien(order.monAn).toLocaleString('vi-VN')}₫
        </div>

        <div class="footer">
          Cảm ơn quý khách và hẹn gặp lại! 💖
        </div>
      </body>
    </html>
  `;

    const printWindow = window.open('', '_blank');
    printWindow.document.write(hoaDonHTML);
    printWindow.document.close();

    printWindow.focus();
    printWindow.print();

    // ✅ Cập nhật trạng thái "Đã thanh toán" sau khi in
    const updatedOrders = orders.map((o) =>
        o === order ? { ...o, trangThai: 'Đã thanh toán' } : o
    );
    setOrders(updatedOrders);
  };

  const handleChangeStatus = (index, newStatus) => {
    const updated = [...orders];
    updated[index].trangThai = newStatus;
    setOrders(updated);
  };

  const filteredOrders = orders.filter(order => {
    const matchTenMon = filterTenMon === '' || order.monAn.some(m => m.tenMon.toLowerCase().includes(filterTenMon.toLowerCase()));
    return (
        (filterSoBan === '' || String(order.soBan) === String(filterSoBan)) &&
        (filterTrangThai === '' || order.trangThai === filterTrangThai) &&
        matchTenMon
    );
  });

  const tongSoMon = orders.reduce((acc, curr) => {
    return acc + curr.monAn.reduce((sum, item) => sum + item.soLuong, 0);
  }, 0);

  {danhSachMonAn.map(mon => {
    const daChon = monAnDaChon.find(m => m.tenMon === mon.tenMon);
    return (
        <div key={mon.tenMon} className="flex items-center gap-2 mb-2">
          <input
              type="checkbox"
              checked={!!daChon}
              onChange={() => {
                if (daChon) {
                  setMonAnDaChon(monAnDaChon.filter(m => m.tenMon !== mon.tenMon));
                } else {
                  setMonAnDaChon([...monAnDaChon, { ...mon, soLuong: 1 }]);
                }
              }}
          />
          <span>{mon.tenMon}</span>
          {daChon && (
              <input
                  type="number"
                  value={daChon.soLuong}
                  min={1}
                  className="w-12 ml-2"
                  onChange={(e) => {
                    const soLuong = parseInt(e.target.value);
                    setMonAnDaChon(monAnDaChon.map(m =>
                        m.tenMon === mon.tenMon ? { ...m, soLuong } : m
                    ));
                  }}
              />
          )}
        </div>
    );
  })}

  return (

      <div className="flex-1 relative z-1 overflow-auto p-4">
        <h2 className="text-xl font-semibold mb-4">🧾 Quản Lý Đơn Hàng</h2>

        <div className="flex flex-col md:flex-row gap-4 mb-6">
          {/* Giao diện chọn bàn */}
          <div className="border rounded px-3 py-2 w-1/2 mb-6">
            <h4 className="font-semibold mb-2">📍 Chọn bàn bằng sơ đồ</h4>
            <div className="grid grid-cols-3 sm:grid-cols-4 md:grid-cols-5 gap-3 max-w-2xl">
              {danhSachBan.map((ban) => (
                  <button
                      key={ban.id}
                      onClick={() => handleChonBan(ban.id)}
                      className={`px-4 py-3 rounded-lg border shadow-sm hover:bg-gray-600 transition 
                ${soBan === ban.id ? 'bg-green-500 text-white font-semibold' : 'bg-green-700'}`}
                  >
                    {ban.id}
                  </button>
              ))}
            </div>
          </div>

          {/* Giao diện chọn nhiều món ăn */}
          <div className="mb-6 border rounded px-3 py-2 w-1/2">
            <h4 className="font-semibold mb-2">🍽️ Chọn món ăn</h4>
            <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-3">
              {danhSachMonAn.map(mon => {
                const daChon = monAnDaChon.find(m => m.tenMon === mon.tenMon);
                return (
                    <div key={mon.tenMon} className="flex items-center gap-2 border rounded px-2 py-1 shadow">
                      <input
                          type="checkbox"
                          checked={!!daChon}
                          onChange={() => {
                            if (daChon) {
                              setMonAnDaChon(monAnDaChon.filter(m => m.tenMon !== mon.tenMon));
                            } else {
                              setMonAnDaChon([...monAnDaChon, { ...mon, soLuong: 1 }]);
                            }
                          }}
                      />
                      <span className="text-sm">{mon.tenMon}</span>
                      {daChon && (
                          <input
                              type="number"
                              min={1}
                              value={daChon.soLuong}
                              className="w-12 ml-auto border rounded text-sm px-1 text-gray-500"
                              onChange={(e) => {
                                const value = parseInt(e.target.value);
                                setMonAnDaChon(monAnDaChon.map(m =>
                                    m.tenMon === mon.tenMon ? { ...m, soLuong: value } : m
                                ));
                              }}
                          />
                      )}
                    </div>
                );
              })}
            </div>
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-5 gap-4 mb-4">
        </div>

        <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-4">
          <input type="text" value={soBan} readOnly placeholder="Bàn" className="border rounded px-3 py-2 bg-gray-100 text-gray-600" />

          <select
              value={trangThai}
              onChange={(e) => setTrangThai(e.target.value)}
              className="border rounded px-3 py-2 text-gray-400"
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
              className="border rounded px-3 py-2 text-gray-600">
        </textarea>
          <div className="flex flex-col sm:flex-row gap-2 w-full">
            <button onClick={handleThemMon} className="bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700">
              {editIndex !== null
                  ? 'Lưu'
                  : orders.some(order => order.soBan === soBan)
                      ? 'Thêm Món'
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
                    }}
                    className="bg-gray-400 text-white px-4 py-2 rounded hover:bg-gray-500"
                >
                  Hủy
                </button>
            )}
          </div>
        </div>



        <div className="grid grid-cols-2 md:grid-cols-5 gap-4 mb-6">
          <input type="text" placeholder="Lọc theo số bàn" value={filterSoBan} onChange={(e) => setFilterSoBan(e.target.value)} className="border px-3 py-2 rounded" />
          <select value={filterTrangThai} onChange={(e) => setFilterTrangThai(e.target.value)} className="border px-3 py-2 rounded text-gray-400">
            <option value="">Tất cả trạng thái</option>
            <option value="Đang chuẩn bị">Đang chuẩn bị</option>
            <option value="Hoàn thành">Hoàn thành</option>
            <option value="Đã thanh toán">Đã thanh toán</option>
            <option value="Hủy">Hủy</option>
          </select>
          <input type="text" placeholder="Tìm theo tên món" value={filterTenMon} onChange={(e) => setFilterTenMon(e.target.value)} className="border px-3 py-2 rounded col-span-2" />
        </div>

        <div className="mb-4 text-right text-sm text-gray-500">
          Tổng số món đã gọi: <strong>{tongSoMon}</strong>
        </div>

        <div className="overflow-x-auto">
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
                  <td className="py-2 px-4 ">
                    <select
                        value={order.trangThai}
                        onChange={(e) => handleChangeStatus(idx, e.target.value)}
                        className={`px-2 py-1  text-sm rounded  ${getBadgeColor(order.trangThai)}`}
                    >
                      <option value="Đang chuẩn bị">Đang chuẩn bị</option>
                      <option value="Hoàn thành">Hoàn thành</option>
                      <option value="Đã thanh toán">Đã thanh toán</option>
                      <option value="Hủy">Hủy</option>
                    </select>
                  </td>
                  <td className="py-2 px-4 text-gray-400">{order.thoiGian}</td>
                  <td className="py-2 px-4 space-x-2">
                    <button onClick={() => handleSua(idx)} className="bg-yellow-500 text-white px-3 py-1 rounded hover:bg-yellow-600">Sửa</button>
                    <button onClick={() => handleXoa(idx)} className="bg-red-600 text-white px-3 py-1 rounded hover:bg-red-700">Xóa</button>
                    <button onClick={() => handleInHoaDon(order)} className="bg-blue-600 text-white px-3 py-1 rounded hover:bg-blue-700">In</button>
                  </td>
                </tr>
            ))}
            {filteredOrders.length === 0 && (
                <tr>
                  <td colSpan="5" className="text-center py-4 text-gray-500">Không có đơn hàng phù hợp</td>
                </tr>
            )}
            </tbody>
          </table>
        </div>

        {/* Modal hiển thị chi tiết đơn hàng */}
        {modalOrder && (
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
        )}
      </div>
  );
};
export default DonHang;

