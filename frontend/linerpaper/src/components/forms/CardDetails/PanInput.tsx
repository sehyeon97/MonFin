interface PanInputProps {
    value: string;
    onChange: (value: string) => void;
}

export function PanInput({ value, onChange }: PanInputProps) {
    return (
        <input
            id="pan"
            type="number"
            placeholder="16 digit card number"
            value={value}
            maxLength={16}
            onChange={(event) => {onChange(event.target.value)}}
        />
    );
}