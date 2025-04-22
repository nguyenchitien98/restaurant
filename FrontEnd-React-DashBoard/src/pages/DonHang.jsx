import { useState, useEffect } from 'react';
import axios from 'axios';
import BanSoDo from '../components/donhang/BanSoDo';
import DanhSachMonAn from '../components/donhang/DanhSachMonAn';
import FormDonHang from '../components/donhang/FormDonHang';
import LocDonHang from '../components/donhang/LocDonHang';
import BangDanhSachDon from '../components/donhang/BangDanhSachDon';
import ModalChiTietDon from '../components/donhang/ModalChiTietDon';
import { getBadgeColor } from "../utils/badgeColors";

const DonHang = () => {
  const [orders, setOrders] = useState([]);
  const [monAnDaChon, setMonAnDaChon] = useState([]);
  const [soBan, setSoBan] = useState('');
  const [selectedTableId, setSelectedTableId] = useState('');
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
  const [trangThaiDisabled, setTrangThaiDisabled] = useState(false);

  useEffect(() => {
    axios.get("http://localhost:8080/api/menus")
      .then(res => {
        const convert = res.data.map(item => ({
          menuId: item.menuId,
          tenMon: item.name,
          donGia: item.price
        }));
        setDanhSachMonAn(convert);
      });

    axios.get("http://localhost:8080/api/tables")
      .then(res => {
        const convert = res.data.map(ban => ({ 
          id: ban.table_number,
          status: ban.status,
          table_id: ban.table_id }));
        setDanhSachBan(convert);
      });

    axios.get("http://localhost:8080/api/orders")
      .then(res => {
        const convert = res.data.map(order => ({
          orderId: order.orderId,
          tableId: order.tableId, 
          soBan: order.tableNumber || '—',
          monAn: order.items.map(item => ({
            tenMon: item.menuName,
            soLuong: item.quantity,
            donGia: item.price,
            trangThai: convertStatus(order.status)
          })),
          ghiChu: order.note,
          trangThai: convertStatus(order.status),
          thoiGian: order.createdAt ? new Date(order.createdAt).toLocaleString() : new Date().toLocaleString()
        }));
        setOrders(convert);
      });
  }, []);

  const convertStatus = (status) => {
    switch (status) {
      case 'PENDING': return 'Đang chuẩn bị';
      case 'COMPLETED': return 'Hoàn thành';
      case 'PAID': return 'Đã thanh toán';
      case 'CANCELLED': return 'Hủy';
      default: return 'Không rõ';
    }
  };

  const tinhTongTien = (monAnList) => monAnList.reduce((tong, mon) => tong + mon.soLuong * (mon.donGia ?? 0), 0);
  const handleChonBan = (banSo) => {
  if (editIndex !== null) return;
  setSoBan(banSo); // giữ lại để hiển thị
  const table = danhSachBan.find(b => b.id === banSo);
  if (table) {
    setSelectedTableId(table.table_id); // <-- đây mới là ID thật
    console.log(table.table_id)
  }
};

 const handleThemMon = () => {
  if (!soBan || monAnDaChon.length === 0) return alert('Vui lòng chọn bàn và ít nhất một món ăn');

  const donMoi = monAnDaChon.map(mon => ({
    tenMon: mon.tenMon,
    menuId: mon.menuId, 
    soLuong: parseInt(mon.soLuong),
    donGia: mon.donGia || 0,
    trangThai: 'Đang chuẩn bị'
  }));

  if (editIndex !== null) {
            const orderToEdit = orders[editIndex];
            const updated = [...orders];

            const donHangUpdate = {
              tableId: orderToEdit.tableId,
              userId: 1,
              note: ghiChu,
              orderDetails: donMoi.map(m => ({
                menuId: m.menuId,
                quantity: m.soLuong,
                price: m.donGia
              }))
            };

            axios.put("http://localhost:8080/api/orders/update", donHangUpdate)
              .then(res => {
                const updatedData = res.data;
                updated[editIndex] = {
                  ...updated[editIndex],
                  orderId: updatedData.orderId,
                  tableId: updatedData.table?.table_id || orderToEdit.tableId,
                  monAn: updatedData.orderDetails.map(d => ({
                    tenMon: danhSachMonAn.find(m => m.menuId === d.menuId)?.tenMon || '—',
                    soLuong: d.quantity,
                    donGia: d.price,
                    trangThai: convertStatus(updatedData.status)
                  })),
                  ghiChu: updatedData.note,
                  trangThai: convertStatus(updatedData.status),
                  thoiGian: new Date().toLocaleString()
                };
                setTrangThaiDisabled(false);
                setOrders(updated);
                setEditIndex(null);
                alert("✅ Cập nhật đơn hàng thành công!");
                console.log("📦 Res từ backend:", res.data);
              })
              .catch(err => {
                console.error("❌ Lỗi cập nhật đơn hàng:", err);
                alert("Lỗi khi cập nhật đơn hàng, vui lòng kiểm tra.");
              });

            return;
  } else {
    const indexBan = orders.findIndex(o => o.soBan === soBan && o.trangThai !== 'Đã thanh toán');

    if (indexBan !== -1) {
      const updated = [...orders];
      donMoi.forEach(mon => {
        const monDaCo = updated[indexBan].monAn.find(m => m.tenMon === mon.tenMon);
        if (monDaCo) monDaCo.soLuong += mon.soLuong;
        else updated[indexBan].monAn.push(mon);
      });
      updated[indexBan].ghiChu += ghiChu ? ` | ${ghiChu}` : '';
      setOrders(updated);
    } else {
      axios.post(`http://localhost:8080/api/orders/${selectedTableId}/create`, {
        userId: 1,
        note: ghiChu,
        orderDetails: donMoi.map(m => ({
          menuId: m.menuId,
          quantity: m.soLuong,
          price: m.donGia
        }))
      }).then(res => {
        console.log("🚀 API trả về:", res.data);
        const newOrder = {
          orderId: res.data.orderId, // Đảm bảo rằng orderId được trả về từ API
          tableId: res.data.table?.table_id,    // Đảm bảo rằng tableId đúng
          soBan,
          monAn: donMoi,
          ghiChu,
          trangThai,
          thoiGian: new Date().toLocaleString()
        };
        setOrders(prevOrders => [...prevOrders, newOrder]); // Cập nhật ngay orders với đơn hàng mới
        console.log("✅ Đã tạo đơn hàng:", res.data);
      }).catch(err => {
        console.error("❌ Lỗi tạo đơn hàng:", err);
      });

      axios.put(`http://localhost:8080/api/tables/${selectedTableId}/status`, { status: 'OCCUPIED' }).then(() => {
        // Cập nhật lại danh sách bàn
        axios.get("http://localhost:8080/api/tables")
          .then(res => {
            const convert = res.data.map(ban => ({
              id: ban.table_number,
              status: ban.status,
              table_id: ban.table_id   // giữ lại `table_id` khi mapping
            }));
            setDanhSachBan(convert);
          });
      });
    }
  }

  setSoBan('');
  setMonAn('');
  setSoLuong(1);
  setGhiChu('');
  setTrangThai('Đang chuẩn bị');
  setMonAnDaChon([]);
};

  const handleXoa = (index) => {
     const orderToDelete = orders[index];

  if (!orderToDelete.orderId || !orderToDelete.tableId) {
    alert("Không thể xóa: thiếu orderId hoặc tableId.");
    return;
  }

  if (window.confirm('Bạn có chắc chắn muốn xóa đơn hàng này?')) {
    axios.delete(`http://localhost:8080/api/orders/${orderToDelete.orderId}?tableId=${orderToDelete.tableId}`)
      .then(() => {
            axios.get("http://localhost:8080/api/tables")
          .then(res => {
            const convert = res.data.map(ban => ({
              id: ban.table_number,
              status: ban.status,
              table_id: ban.table_id
            }));
            setDanhSachBan(convert);
          });
        setOrders(orders.filter((_, i) => i !== index));
        console.log("✅ Đã xóa đơn hàng khỏi hệ thống");
      })
      .catch(err => {
        console.error("❌ Lỗi khi xóa đơn hàng:", err);
        alert("Lỗi khi xóa đơn hàng, vui lòng kiểm tra lại.");
      });
  }
  };

  const handleSua = (index) => {
    const order = orders[index];
    setSoBan(order.soBan);
    // setMonAn(order.monAn[0].tenMon);
    // setSoLuong(order.monAn[0].soLuong);
    if (order.monAn && order.monAn.length > 0) {
    setMonAn(order.monAn[0].tenMon);
    setSoLuong(order.monAn[0].soLuong);
  } else {
    setMonAn('');
    setSoLuong(1);
  }
    setGhiChu(order.ghiChu);
    setTrangThai(order.trangThai);
    setEditIndex(index);
    setTrangThaiDisabled(true);
  };

  const handleInHoaDon = (order) => {
    const hoaDonHTML = `...`; // Có thể dùng từ template gốc để render hóa đơn
    const printWindow = window.open('', '_blank');
    printWindow.document.write(hoaDonHTML);
    printWindow.document.close();
    printWindow.focus();
    printWindow.print();
    setOrders(orders.map((o) => o === order ? { ...o, trangThai: 'Đã thanh toán' } : o));
  };

  const handleChangeMonStatus = (orderIndex, monIndex, newStatus) => {
    const updated = [...orders];
    updated[orderIndex].monAn[monIndex].trangThai = newStatus;
    setOrders(updated);
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

  const tongSoMon = orders.reduce((acc, curr) => acc + curr.monAn.reduce((sum, item) => sum + item.soLuong, 0), 0);

  return (
    <div className="flex-1 relative z-1 overflow-auto p-4">
      <h2 className="text-xl font-semibold mb-4">🧾 Quản Lý Đơn Hàng</h2>

      <div className="flex flex-col md:flex-row gap-4 mb-6">
        <BanSoDo
          danhSachBan={danhSachBan}
          soBan={soBan}
          editIndex={editIndex}     
          handleChonBan={handleChonBan}
        />
        <DanhSachMonAn danhSachMonAn={danhSachMonAn} monAnDaChon={monAnDaChon} setMonAnDaChon={setMonAnDaChon} />
      </div>

      <FormDonHang
        soBan={soBan}
        trangThai={trangThai}
        setTrangThai={setTrangThai}
        ghiChu={ghiChu}
        setGhiChu={setGhiChu}
        handleThemMon={handleThemMon}
        editIndex={editIndex}
        setEditIndex={setEditIndex}
        setSoBan={setSoBan}
        setMonAn={setMonAn}
        setSoLuong={setSoLuong}
        setMonAnDaChon={setMonAnDaChon}
        orders={orders}
        trangThaiDisabled={trangThaiDisabled}
        setTrangThaiDisabled={setTrangThaiDisabled}
      />

      <LocDonHang
        filterSoBan={filterSoBan}
        setFilterSoBan={setFilterSoBan}
        filterTrangThai={filterTrangThai}
        setFilterTrangThai={setFilterTrangThai}
        filterTenMon={filterTenMon}
        setFilterTenMon={setFilterTenMon}
      />

      <div className="mb-4 text-right text-sm text-gray-500">
        Tổng số món đã gọi: <strong>{tongSoMon}</strong>
      </div>

      <BangDanhSachDon
        filteredOrders={filteredOrders}
        handleSua={handleSua}
        handleXoa={handleXoa}
        handleInHoaDon={handleInHoaDon}
        handleChangeStatus={handleChangeStatus}
        setModalOrder={setModalOrder}
      />

      {modalOrder && (
        <ModalChiTietDon
          modalOrder={modalOrder}
          setModalOrder={setModalOrder}
          orders={orders}
          handleChangeMonStatus={handleChangeMonStatus}
          tinhTongTien={tinhTongTien}
        />
      )}
    </div>
  );
};

export default DonHang;