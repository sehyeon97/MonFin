import { Spinner } from "@/loading/Spinner";
import { BUTTON_CLASS_NAME } from "@/style/classnames";
import { ButtonHTMLAttributes, ReactNode } from "react";

// wraps around the <button> element
type ButtonProps = ButtonHTMLAttributes<HTMLButtonElement> & {
  text: string;
  loadingIcon?: ReactNode;
  className?: string;
};

/**
 * Do not use for buttons in Container
 * Loading icon used when signing up, logging in, or changing UI within Content
 */
export function Button({ text, onClick, disabled: isLoading, loadingIcon, className }: ButtonProps) {
    return (
        <button className={`${BUTTON_CLASS_NAME} ${className}`} onClick={onClick} disabled={isLoading}>
            {isLoading ? <Spinner icon={loadingIcon} /> : text}
        </button>
    );
}