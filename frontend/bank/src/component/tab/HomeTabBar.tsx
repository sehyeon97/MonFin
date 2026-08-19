import { CONTAINER_CLASS_NAME, TAB_BAR_CLASS_NAME } from "@/style/classnames";
import { Tab } from "./Tab";
import { HomeTab } from "./type/HomeTab";
import { TabDivider } from "./TabDivider";
import { ThemeModeToggler } from "./ThemeModeToggler";
import { Button } from "../Button";

type TabBarProps = {
    activeTab: HomeTab;
    onTabChange: (tab: HomeTab) => void;
    onToggle: () => void;
    onLogout: () => void;
}

export function HomeTabBar({ activeTab, onTabChange, onToggle, onLogout }: TabBarProps) {
    return (
        <div role="tablist" className={`${CONTAINER_CLASS_NAME} ${TAB_BAR_CLASS_NAME}`}>
            {/* LEFT GROUP */}
            <div className="flex items-center">
                {/* default opened tab. displays informational charts/graphs */}
                <Tab
                    tabLabel="Dashboard"
                    isSelected={activeTab === HomeTab.DASHBOARD}
                    onClick={() => onTabChange(HomeTab.DASHBOARD)}
                />
                <TabDivider />

                {/* Shows all monthly expenses */}
                <Tab
                    tabLabel="Payments"
                    isSelected={activeTab === HomeTab.PAYMENTS}
                    onClick={() => onTabChange(HomeTab.PAYMENTS)}
                />
                <TabDivider />

                {/* Shows all weekly and monthly income  */}
                <Tab
                    tabLabel="Income"
                    isSelected={activeTab === HomeTab.INCOME}
                    onClick={() => onTabChange(HomeTab.INCOME)}
                />
                <TabDivider />

                {/* Transaction history */}
                <Tab
                    tabLabel="Transactions"
                    isSelected={activeTab === HomeTab.TRANSACTIONS}
                    onClick={() => onTabChange(HomeTab.TRANSACTIONS)}
                />
                <TabDivider />

                {/* See active or frozen cards. Add or remove cards for account. View details on cards */}
                <Tab
                    tabLabel="Cards"
                    isSelected={activeTab === HomeTab.CARDS}
                    onClick={() => onTabChange(HomeTab.CARDS)}
                />
                <TabDivider />

                {/* View inbox for messages and notifications */}
                <Tab
                    tabLabel="Inbox"
                    isSelected={activeTab === HomeTab.INBOX}
                    onClick={() => onTabChange(HomeTab.INBOX)}
                />
                <TabDivider />

                {/* Change full name, view inbox for messages */}
                <Tab
                    tabLabel="Profile"
                    isSelected={activeTab === HomeTab.PROFILE}
                    onClick={() => onTabChange(HomeTab.PROFILE)}
                />
                <TabDivider />

                {/* Change to dark mode, pink mode (in the future, 
                    add or hide components belonging to selected tab?) */}
                <Tab
                    tabLabel="Settings"
                    isSelected={activeTab === HomeTab.SETTINGS}
                    onClick={() => onTabChange(HomeTab.SETTINGS)}
                />
                <TabDivider />

                {/* Chat bot for help later? FAQ? */}
                <Tab
                    tabLabel="Help"
                    isSelected={activeTab === HomeTab.HELP}
                    onClick={() => onTabChange(HomeTab.HELP)}
                />
                <TabDivider />

                {/* Log out */}
                <Button
                    text="Log out"
                    onClick={onLogout}
                />
            </div>
            {/* LEFT GROUP */}

            {/* RIGHT GROUP */}
            {/* ml-auto puts as much empty space as possible on the left before this element */}
            <div className="ml-auto pr-5">
                <ThemeModeToggler onToggle={onToggle} />
            </div>
            {/* RIGHT GROUP */}
        </div>
    );
}