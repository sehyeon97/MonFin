import { INPUT_CLASS_NAME } from "@/style/classnames";

/**
 * 
 */
type InputProps = {
    type: string;
}

export function Input({ type }: InputProps) {
    return (
        <input className={INPUT_CLASS_NAME} type={type}></input>
    );
}