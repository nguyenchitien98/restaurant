const BanSoDo = ({ danhSachBan, soBan, handleChonBan, editIndex }) => (
  <div className="border rounded px-3 py-2 w-full md:w-1/2 mb-6">
    <h4 className="font-semibold mb-2">📍 Chọn bàn bằng sơ đồ</h4>
    <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-3">
          {[...danhSachBan]
            .sort((a, b) => a.id - b.id) // Sắp xếp tăng dần theo số bàn
            .map((ban) => {
              return (
                <button
                  key={ban.id}
                  disabled={ban.status !== 'EMPTY' && ban.id !== soBan}
                  className={`p-2 rounded-lg font-semibold transition
                    ${ban.id === soBan && editIndex !== null
                      ? 'bg-yellow-400 text-white cursor-not-allowed'
                      : ban.status === 'EMPTY'
                        ? 'bg-green-100 text-green-900 hover:bg-green-200'
                        : 'bg-gray-300 text-gray-500 cursor-not-allowed'}
                  `}
                  onClick={() => handleChonBan(ban.id)}
                >
                  Bàn {ban.id}
                </button>
              );
          })}
    </div>
  </div>
);
export default BanSoDo;