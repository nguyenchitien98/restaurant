const DanhSachMonAn = ({ danhSachMonAn, monAnDaChon, setMonAnDaChon }) => (
  <div className="border rounded px-3 py-2 w-full md:w-1/2 mb-6">
    <h4 className="font-semibold mb-2">🍽️ Chọn món ăn</h4>
    <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-3">
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
);

export default DanhSachMonAn;