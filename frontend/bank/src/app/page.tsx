'use client'

import "./globals.css";

import { useEffect, useState } from "react";

import { useNavigation } from "@/hook/navigation/useNavigation";
import { validateReturningUser } from "@/service/api/authService";
import { HomeTabBar } from "@/component/tab/HomeTabBar";
import { HomeTab } from "@/component/tab/type/HomeTab";
import { DashboardContent } from "@/component/content/DashboardContent";
import { MAIN_CLASS_NAME } from "@/style/classnames";
import { AppTheme } from "@/theme/types/AppTheme";
import { THEME_CONTEXT } from "@/theme/ThemeContext";
import { CardManagementContent } from "@/component/content/card-management/CardContent";

export default function Home() {
  const [theme, setTheme] = useState(AppTheme.LAVENDER);
  const [activeTab, setActiveTab] = useState(HomeTab.DASHBOARD)
  const navigator = useNavigation();

  // useEffect can be used to make sure our client-side pages can avoid being an async function
  // runs once
  useEffect(() => {
    // isAuthenticated becomes true when jwt access token is set and still valid
    async function checkAuthentication() {
        const response: Response = await validateReturningUser();
        if (!response.ok) {
          navigator.replaceWithAuthPage();
        }
    }

    checkAuthentication();
  }, [navigator])

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
    <main className={`${THEME_CONTEXT[theme]} ${MAIN_CLASS_NAME}`}>
        <HomeTabBar
          activeTab={activeTab}
          onTabChange={setActiveTab}
          onToggle={changeTheme}
        />

        {activeTab === HomeTab.DASHBOARD && <DashboardContent />}
        {activeTab === HomeTab.CARDS && <CardManagementContent />}
    </main>
  );
}
