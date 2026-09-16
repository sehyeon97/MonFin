import { BrowserRouter, Route, Routes } from 'react-router-dom'
import { LoginPortal } from './pages/auth/LoginPortal'
import { CustomerHomePage } from './pages/user/customer/CustomerHomePage'
import { MerchantHomePage } from './pages/user/merchant/MerchantHomePage'
import { SaveCardPage } from './pages/user/SaveCardPage'
import { ViewSavedCardsPage } from './pages/user/ViewSavedCardsPage'
import { HomeLayout } from './layouts/HomeLayout'
import { useState } from 'react'
import { type UserType, UserTypes } from './types/UserType'

function App() {
  const [userType, setUserType] = useState<UserType>(UserTypes.Customer);

  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<LoginPortal userType={userType} setUserType={setUserType} />}/>
        <Route element={<HomeLayout userType={userType} />}>
          {userType === UserTypes.Customer && <Route path="/shopping" element={<CustomerHomePage />}/>}
          {userType === UserTypes.Merchant && <Route path="/my-business" element={<MerchantHomePage />}/>}
          <Route path="/save-card-information" element={<SaveCardPage />}/>
          <Route path="/view-saved-cards" element={<ViewSavedCardsPage />}/>
        </Route>
      </Routes>
    </BrowserRouter>
  )
}

export default App
