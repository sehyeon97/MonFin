interface FullNameInputProps {
    value: string;
    onChange: (event: string) => void;
};

export function FullNameInput({ value, onChange }: FullNameInputProps) {
    return (
        <input
            id="fullname"
            type="text"
            placeholder="Full name on card"
            value={value}
            onChange={(event) => {onChange(event.target.value)}}
        />
    );
}