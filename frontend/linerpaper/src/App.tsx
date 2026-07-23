import { BrowserRouter, Route, Routes } from 'react-router-dom'
import './App.css'
import { LoginPortal } from './pages/auth/LoginPortal'
import { CustomerHomePage } from './pages/user/customer/CustomerHomePage'
import { MerchantHomePage } from './pages/user/merchant/MerchantHomePage'
import { SaveCardPage } from './pages/user/SaveCardPage'
import { ViewSavedCardsPage } from './pages/user/ViewSavedCardsPage'
import { HomeLayout } from './layouts/HomeLayout'

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<LoginPortal />}/>
        <Route element={<HomeLayout />}>
          <Route path="/shopping" element={<CustomerHomePage />}/>
          <Route path="/my-business" element={<MerchantHomePage />}/>
          <Route path="/save-card-information" element={<SaveCardPage />}/>
          <Route path="/view-saved-cards" element={<ViewSavedCardsPage />}/>
        </Route>
      </Routes>
    </BrowserRouter>
  )
}

export default App
