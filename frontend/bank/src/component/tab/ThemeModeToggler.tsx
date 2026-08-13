type ThemeModeTogglerProps = {
    onToggle: () => void;
}

export function ThemeModeToggler({ onToggle }: ThemeModeTogglerProps) {
    return (
        <button onClick={onToggle}>Toggle</button>
    );
}