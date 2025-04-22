export const getBadgeColor = (status) => {
  switch (status) {
    case 'Đang chuẩn bị':
      return 'bg-yellow-100 text-yellow-800';
    case 'Hoàn thành':
      return 'bg-green-100 text-green-800';
    case 'Đã thanh toán':
      return 'bg-blue-100 text-blue-800';
    case 'Hủy':
      return 'bg-red-100 text-red-800';
    default:
      return 'bg-gray-100 text-gray-800';
  }
};
