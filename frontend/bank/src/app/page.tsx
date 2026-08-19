'use client'

import "./globals.css";

import { useState } from "react";

import { HomeTabBar } from "@/component/tab/HomeTabBar";
import { HomeTab } from "@/component/tab/type/HomeTab";
import { DashboardContent } from "@/component/content/DashboardContent";
import { MAIN_CLASS_NAME } from "@/style/classnames";
import { AppTheme } from "@/theme/types/AppTheme";
import { THEME_CONTEXT } from "@/theme/ThemeContext";
import { CardManagementContent } from "@/component/content/card-management/CardContent";
import { useSession } from "@/hook/auth/useSession";
import { RefreshSessionPopup } from "@/component/popup/RefreshSessionPopup";
import { AuthenticateReturningUser } from "@/component/returning/AuthenticateReturningUser";

export default function Home() {
  const [theme, setTheme] = useState(AppTheme.LAVENDER);
  const [activeTab, setActiveTab] = useState(HomeTab.DASHBOARD)
  const { showPopup, stayLoggedIn, logout } = useSession();

  function changeTheme() {
    if (theme === AppTheme.LAVENDER) {
      window.localStorage.setItem("theme", "dark");
      setTheme(AppTheme.DARK);
    } else if (theme === AppTheme.DARK) {
      window.localStorage.setItem("theme", "pink");
      setTheme(AppTheme.PINK);
    } else {
      window.localStorage.setItem("theme", "lavender");
      setTheme(AppTheme.LAVENDER);
    }
  }

  // Show home page for authenticated user
  return (
    <AuthenticateReturningUser>
      <main className={`${THEME_CONTEXT[theme]} ${MAIN_CLASS_NAME}`}>
        {showPopup && <RefreshSessionPopup onClick={stayLoggedIn} />}
        <HomeTabBar
          activeTab={activeTab}
          onTabChange={setActiveTab}
          onToggle={changeTheme}
          onLogout={logout}
        />

        {activeTab === HomeTab.DASHBOARD && <DashboardContent />}
        {activeTab === HomeTab.CARDS && <CardManagementContent />}
      </main>
    </AuthenticateReturningUser>
  );
}
