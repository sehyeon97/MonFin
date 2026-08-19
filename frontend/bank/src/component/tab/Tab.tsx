import { TAB_CLASS_NAME, TAB_SELECT_CLASS_NAME } from "@/style/classnames";
import { Button } from "../Button";

type TabProps = {
    tabLabel: string;
    isSelected: boolean;
    onClick: () => void;
}

export function Tab({ tabLabel, isSelected = false, onClick }: TabProps) {
    return (
        // loading spinner not used because tab bar stays consistent on page
        <Button 
            className={`${TAB_CLASS_NAME} ${isSelected ? `${TAB_SELECT_CLASS_NAME}` : ""}`}
            text={tabLabel} role="tab" onClick={onClick}
        ></Button>
    );
}