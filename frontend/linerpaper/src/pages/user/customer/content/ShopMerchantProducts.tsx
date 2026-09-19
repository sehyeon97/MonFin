import { useEffect, useState } from "react";
import type { AllProductsResponse } from "../../../../dto/merchant/AllProductsResponse";
import { GetMerchantProducts } from "../../../../api/merchant/GetMerchantProducts";
import { UserTypes } from "../../../../types/UserType";
import type { ProductResponse } from "../../../../dto/merchant/ProductResponse";
import { ProductCard } from "../../../../components/product/ProductCard";

import '../../../../stylesheets/customer/Shopping.css';
import '../../../../stylesheets/popups/CartOrCheckoutPopup.css';

export function ShopMerchantProducts() {
    // product changes on merchant selection
    const [products, setProducts] = useState<ProductResponse[]>([]);

    // for now, select merchant/business from a list of buttons
    // in the future, implement search algorithm
    const [businessName, setBusinessName] = useState("");

    // shows popup to add product to cart or to purchase
    const [showCartOrCheckoutPopup, setShowCartOrCheckoutPopup] = useState(false);
    // clicking checkout shows different popup content (payment method selection)
    const [showPaymentMethod, setShowPaymentMethod] = useState(false);

    // for search bar

    // on selecting business name, fetch all of their products
    useEffect(() => {
        const getBusinessProducts = async () => {
            const data: AllProductsResponse = await GetMerchantProducts(UserTypes.Customer, {businessName});
            if (data) {
                setProducts(data.products);
            }
        }
        
        if (businessName.length > 0) {
            getBusinessProducts();
        }
    }, [businessName]);

    // show default payment card and a dropdown selection to choose a card from their saved cards
    function onClickPurchaseItem() {
        setShowCartOrCheckoutPopup(true);
    }

    return (
        <div>
            {/* name changer button is for test purposes */}
            {products.length < 1 && <button onClick={() => setBusinessName("Name Changer")}>Name Changer</button>}
            {products.length > 0 && <button onClick={() => {
                setProducts([]);
                setBusinessName("");
            }}>Back</button>}
            {products.length > 0 && products.map((product) => (
                <ProductCard
                    key={product.desc.length * product.price / product.count}
                    businessName={businessName}
                    brand={product.brand}
                    price={product.price}
                    desc={product.desc}
                    count={product.count}
                    showSettingsIcon={false}
                    showPurchaseButton={true}
                    onProductClick={() => onClickPurchaseItem()}
                />
            ))}

            {showCartOrCheckoutPopup && 
            <div className="popup-backdrop" onClick={() => {
                setShowCartOrCheckoutPopup(false);
                setShowPaymentMethod(false);
            }}>
                <div className="popup-modal" onClick={(event) => event.stopPropagation()}>
                    {showPaymentMethod && <h2>Select Payment Method</h2>}

                    {!showPaymentMethod && <div className="popup-row">
                        <button>Add to Cart</button>
                        <button onClick={() => setShowPaymentMethod(true)}>Buy Now</button>
                    </div>}

                    {showPaymentMethod && <div>
                        <button>Use default payment method</button>
                    </div>}
                </div>
            </div>
            }
        </div>
    );
}