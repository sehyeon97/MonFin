interface ExpiryYearInputProps {
    value: string;
    onChange: (event: string) => void;
};

export function ExpiryYearInput({ value, onChange }: ExpiryYearInputProps) {
    return (
        <input
            id="expyear"
            type="number"
            placeholder="2030"
            value={value}
            minLength={4}
            maxLength={4}
            onChange={(event) => {onChange(event.target.value)}}
        />
    );
}