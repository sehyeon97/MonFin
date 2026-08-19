import { CONTENT_ROW_CLASS_NAME } from "@/style/classnames";

type RowAlignment =
    | "left"
    | "center"
    | "right"
    | "between"
    | "around"
    | "evenly";

const ROW_ALIGNMENT: Record<RowAlignment, string> = {
    left: "justify-start",
    center: "justify-center",
    right: "justify-end",
    between: "justify-between",
    around: "justify-around",
    evenly: "justify-evenly",
};

type RowProps = React.ComponentProps<"div"> & {
    children: React.ReactNode;
    alignment?: RowAlignment;
};

export function Row({ children, alignment = "left", ...props }: RowProps) {
    return (
        <div className={`${CONTENT_ROW_CLASS_NAME} ${ROW_ALIGNMENT[alignment]}`} {...props}>
            {children}
        </div>
    );
}