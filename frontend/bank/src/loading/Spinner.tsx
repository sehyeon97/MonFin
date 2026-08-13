import { SPINNER_CLASS_NAME } from "@/style/classnames";
import { ReactNode } from "react";

type SpinnerProps = {
    icon?: ReactNode;
}

export function Spinner({ icon }: SpinnerProps) {
    return(
        <span className={SPINNER_CLASS_NAME}>{icon}</span>
    );
}