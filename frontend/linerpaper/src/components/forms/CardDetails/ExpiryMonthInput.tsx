interface ExpiryMonthInputProps {
    value: string;
    onChange: (event: string) => void;
};

export function ExpiryMonthInput({ value, onChange }: ExpiryMonthInputProps) {
    return (
        <input
            id="expmonth"
            type="number"
            placeholder="12"
            value={value}
            minLength={1}
            maxLength={2}
            onChange={(event) => {onChange(event.target.value)}} // event.target.value is always a string
        />
    );
}