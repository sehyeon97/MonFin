import { useEffect, useState } from "react";
import { UserTypes, type UserType } from "../../../../types/UserType";
import type { ProductResponse } from "../../../../dto/merchant/ProductResponse";
import type { AllProductsResponse } from "../../../../dto/merchant/AllProductsResponse";
import { GetMerchantProducts } from "../../../../api/merchant/GetMerchantProducts";
import { ProductCard } from "../../../../components/product/ProductCard";

import '../../../../stylesheets/popups/ViewProductPopup.css';
import { EditProduct } from "./EditProduct";
import { UpdateProductForMerchant } from "../../../../api/merchant/UpdateMerchantProduct";
import type { UpdateProductRequest } from "../../../../dto/merchant/UpdateProductRequest";

type ViewProductsProps = {
    role: UserType;
    businessName?: string;
    setShowPopup: (showPopup: boolean) => void;
};

// merchant uses access token cookie
// customer uses businessName
export function ViewProducts({ role, businessName, setShowPopup }: ViewProductsProps) {
    const [products, setProducts] = useState<ProductResponse[]>([]);

    const [showSettings, setShowSettings] = useState(false);
    const [selectedIndex, setSelectedIndex] = useState(0);

    useEffect(() => {
        async function getProductsForMerchant() {
            const data: AllProductsResponse = await GetMerchantProducts(UserTypes.Merchant, {});
            setProducts(data.products);
        }

        async function getProductsForCustomer() {
            const data: AllProductsResponse = await GetMerchantProducts(UserTypes.Customer, {businessName: businessName!});
            setProducts(data.products);
        }

        if (role === UserTypes.Merchant) {
            getProductsForMerchant();
        } else {
            getProductsForCustomer();
        }
    }, [role, businessName]);

    function onClickSettings(index: number) {
        setShowSettings(true);
        setSelectedIndex(index);
    }

    // only change if any new value is different from old value
    async function onSaveEditProduct(price: number, desc: string, count: number) {
        const product: ProductResponse = products[selectedIndex];
        console.log(`old price: ${product.price}`);
        console.log(`new price: ${price}`);

        if (product.price !== price || product.desc !== desc || product.count !== count) {
            const updateRequest: UpdateProductRequest = {
                businessName: product.businessName,
                brand: product.brand,
                price: product.price,
                desc: product.desc,
                count: product.count,
                newPrice: Math.round(price * 100),
                newDesc: desc,
                newCount: count,
            };
            // not awaiting, just have it resolve on its own
            // should be updated in database next time we pull all products
            await UpdateProductForMerchant(updateRequest);

            setProducts((currentProducts) =>
                currentProducts.map((product, index) =>
                    index === selectedIndex
                        ? {
                            ...product,
                            price: Math.round(price * 100),
                            desc: desc,
                            count: count
                        }
                        : product
                )
            );
        }

        setShowSettings(false);
    }

    return(
        <>
            <div className="modal-backdrop" onClick={() => setShowPopup(false)}>
                <div className="modal" onClick={(event) => event.stopPropagation()}>
                    <div className="product-grid">
                        {products.map((product, index) => (
                            <ProductCard
                                key={index}
                                businessName={product.businessName}
                                brand={product.brand}
                                price={product.price}
                                desc={product.desc}
                                count={product.count}

                                // Merchant Exclusive
                                showSettingsIcon={true}
                                onClickSettings={() => onClickSettings(index)}

                                // Not Customer
                                showPurchaseButton={false}
                            />
                        ))}
                    </div>
                    {products.length < 1 && <p>Add a product first!</p>}
                </div>
            </div>

            {showSettings && 
                <EditProduct product={products[selectedIndex]} onSave={onSaveEditProduct} setShowPopup={setShowSettings} />
            }
        </>
    );
}