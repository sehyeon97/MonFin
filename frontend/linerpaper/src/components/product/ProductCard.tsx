import '../../stylesheets/popups/ViewProductPopup.css';

type ProductCardProps = {
    index: number;
    businessName: string;
    brand: string;
    price: number;
    desc: string;
    count: number;
    showSettingsIcon: boolean;
    onClickSettings?: (index: number) => void;
}

export function ProductCard({ index, businessName, brand, price, desc, count, showSettingsIcon, onClickSettings }: ProductCardProps) {
    return (
        <div className="product-card">
            <div className='product-card-header'>
                <h2 className="product-card-title">{businessName}</h2>

                {
                    showSettingsIcon &&
                    <button className="product-settings-button" onClick={() => onClickSettings!(index)}>
                        ⚙
                    </button>
                }
            </div>

            <div className='product-card-content'>
                <div className="product-image"> {/* Image goes here */} </div>

                <div className='product-info'>
                    <div className='product-info-row'>
                        <p className="product-subtitle">{brand}</p>
                        <p className="product-price"> ${(price / 100).toFixed(2)}</p>
                        <p className="product-quantity"> Quantity: {count}</p>
                    </div>
                    <p className="product-description">{desc}</p>
                </div>
            </div>
        </div>
    );
}