interface CvvInputProps {
    value: string;
    onChange: (event: string) => void;
}

export function CvvInput ({ value, onChange }: CvvInputProps) {
    return (
        <input
            id="cvv"
            type="number"
            placeholder="3 or 4 digit security code"
            minLength={3}
            maxLength={4}
            value={value}
            onChange={(event) => {onChange(event.target.value)}}
        />
    );
}