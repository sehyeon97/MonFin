import { CONTENT_CLASS_NAME } from "@/style/classnames";
import { HTMLAttributes } from "react";

// General UI used by all Content components
type ContentProps = HTMLAttributes<HTMLDivElement> & {
  children: React.ReactNode;
};

export function Content({ children, className = "", ...props }: ContentProps) {
    return (
        <div className={`${CONTENT_CLASS_NAME} ${className}`} {...props}>
            {children}
        </div>
    );
}