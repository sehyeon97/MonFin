import { CONTENT_COLUMN_CLASS_NAME } from "@/style/classnames";

type ColumnAlignment =
    | "top"
    | "center"
    | "bottom"
    | "between"
    | "around"
    | "evenly";

const COLUMN_ALIGNMENT: Record<ColumnAlignment, string> = {
    top: "justify-start",
    center: "justify-center",
    bottom: "justify-end",
    between: "justify-between",
    around: "justify-around",
    evenly: "justify-evenly",
};

type ColumnProps = React.ComponentProps<"div"> & {
    children: React.ReactNode;
    alignment?: ColumnAlignment;
}

export function Column({ children, alignment = "top", ...props }: ColumnProps) {
    return (
        <div className={`${CONTENT_COLUMN_CLASS_NAME} ${COLUMN_ALIGNMENT[alignment]}`} {...props}>
            {children}
        </div>
    );
}