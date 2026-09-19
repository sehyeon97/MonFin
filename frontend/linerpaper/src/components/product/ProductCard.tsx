import '../../stylesheets/popups/ViewProductPopup.css';

type ProductCardProps = {
    businessName: string;
    brand: string;
    price: number;
    desc: string;
    count: number;
    showSettingsIcon: boolean;
    onClickSettings?: (index: number) => void;
    showPurchaseButton: boolean;
    onProductClick?: () => void;
}

export function ProductCard({ 
    businessName, brand, price, desc, count,
    showSettingsIcon, onClickSettings,
    showPurchaseButton, onProductClick,
 }: ProductCardProps) {
    return (
        <div className="product-card">
            <div className='product-card-header'>
                <h2 className="product-card-title">{businessName}</h2>

                {
                    showSettingsIcon &&
                    <button className="product-settings-button" onClick={() => onClickSettings}>
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
                    {showPurchaseButton && <button onClick={() => onProductClick!()}>Purchase</button>}
                </div>
            </div>
        </div>
    );
}