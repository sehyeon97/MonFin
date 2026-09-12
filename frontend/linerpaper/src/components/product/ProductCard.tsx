type ProductCardProps = {
    businessName: string;
    brand: string;
    price: number;
    desc: string;
    count: number;
}

export function ProductCard({businessName, brand, price, desc, count}: ProductCardProps) {
    return (
        <div className="rounded-lg border p-4 shadow-sm">
            <h2 className="text-lg font-semibold">{businessName}</h2>

            <p className="text-sm text-gray-500">{brand}</p>

            <p className="mt-2 text-xl font-bold">
                ${price.toFixed(2)}
            </p>

            <p className="mt-2 text-sm">{desc}</p>

            <p className="mt-2 text-sm">
                Quantity: {count}
            </p>
        </div>
    );
}