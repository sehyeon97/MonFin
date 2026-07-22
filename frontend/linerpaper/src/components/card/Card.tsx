interface CardProps {
  network: string;
  lastFour: string;
  expMonth: number;
  expYear: number;
}

export function Card({ network, lastFour, expMonth, expYear }: CardProps) {
    return (
        <div className="card">
            <h1>{network}</h1>
            <h3>{lastFour}</h3>
            <p>{expMonth + "/" + expYear}</p>
        </div>
    );
}