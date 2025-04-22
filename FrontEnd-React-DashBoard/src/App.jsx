import { Route, Routes } from "react-router-dom";

import Sidebar from "./components/common/Sidebar";

import OverviewPage from "./pages/OverviewPage";
// import ProductsPage from "./pages/ProductsPage";
// import UsersPage from "./pages/UsersPage";
// import SalesPage from "./pages/SalesPage";
// import OrdersPage from "./pages/OrdersPage";
// import AnalyticsPage from "./pages/AnalyticsPage";
import SettingsPage from "./pages/SettingsPage";
import DonHang from "./pages/DonHang";
import ManageTablePage from "./pages/ManageTablePage";
import MenuPage from "./pages/MenuPage";
import Report from "./pages/Report";
import KitchenPage from "./pages/KitchenPage";

function App() {

	return (
		
		<div className='flex h-screen bg-gray-900 text-gray-100 overflow-hidden'>
			
			{/* BG */}
			<div className='fixed inset-0 z-0'>
				<div className='absolute inset-0 bg-gradient-to-br from-gray-900 via-gray-800 to-gray-900 opacity-80' />
				<div className='absolute inset-0 backdrop-blur-sm' />
			</div>

			<Sidebar />
			<Routes>
				<Route path='/' element={<OverviewPage />} />
				{/* <Route path='/products' element={<ProductsPage />} />
				<Route path='/users' element={<UsersPage />} />
				<Route path='/sales' element={<SalesPage />} />
				<Route path='/orders' element={<OrdersPage />} />
				<Route path='/analytics' element={<AnalyticsPage />} /> */}
				<Route path='/menu' element={<MenuPage />} />
				<Route path='/table' element={<ManageTablePage />} />
				<Route path='/don-hang' element={<DonHang />} />
				<Route path='/kitchen' element={<KitchenPage />} />
				<Route path='/report' element={<Report />} />
				<Route path='/settings' element={<SettingsPage />} />
			</Routes>
			</div>
	);
}

export default App;
