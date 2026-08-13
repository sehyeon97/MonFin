import { CONTAINER_CHILD_CLASS_NAME, CONTAINER_CLASS_NAME, CONTAINER_TITLE_CLASS_NAME } from "@/style/classnames";

/**
 * A Container UI that has a title, and a
 * react children (has to be named children not child) (meaning any component or tags, including img)
 * It also accepts classname, which is used to customize the Container further
 */
type ContainerProps = {
    title: string;
    children: React.ReactNode;
    className?: string;
}

export function Container({title, children, className = ""}: ContainerProps) {
    return (
        <section className={`${CONTAINER_CLASS_NAME} ${className}`}>
            <header>
                <h1 className={CONTAINER_TITLE_CLASS_NAME}>
                    {title}
                </h1>
            </header>
            <div className={CONTAINER_CHILD_CLASS_NAME}>
                {children}
            </div>
        </section>
    );
}