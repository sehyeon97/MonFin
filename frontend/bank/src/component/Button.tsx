import { Spinner } from "@/loading/Spinner";
import { BUTTON_CLASS_NAME, BUTTON_NOT_SELECTED_CLASS_NAME, BUTTON_SELECTED_CLASS_NAME } from "@/style/classnames";
import { ButtonHTMLAttributes, ReactNode } from "react";

// wraps around the <button> element
type ButtonProps = ButtonHTMLAttributes<HTMLButtonElement> & {
  text: string;
  loadingIcon?: ReactNode;
  className?: string;
  isSelected?: boolean;
};

/**
 * Loading icon used when signing up, logging in, or changing UI within Content
 */
export function Button({ text, onClick, disabled: isLoading, loadingIcon, className, isSelected }: ButtonProps) {
    return (
        <button 
            className={
                `${BUTTON_CLASS_NAME} ${className} 
                ${isSelected ? BUTTON_SELECTED_CLASS_NAME : BUTTON_NOT_SELECTED_CLASS_NAME}
            `} onClick={onClick} disabled={isLoading}>
            {isLoading ? <Spinner icon={loadingIcon} /> : text}
        </button>
    );
}