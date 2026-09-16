import { useEffect, useState } from "react";
import type { ProductResponse } from "../../../dto/merchant/ProductResponse";
import { ProductCard } from "../../../components/product/ProductCard";
import { GetMerchantProducts } from "../../../api/merchant/GetMerchantProducts";
import { UserTypes } from "../../../types/UserType";
import type { AllProductsResponse } from "../../../dto/merchant/AllProductsResponse";

export function CustomerHomePage() {
    const [products, setProducts] = useState<ProductResponse[]>([]);

    useEffect(() => {
        const getProducts = async () => {
            const data: AllProductsResponse = await GetMerchantProducts(UserTypes.Customer, {businessName: "Biz"});
            setProducts(data.products);
        }

        getProducts();
    }, []);

    return (
        <div>
            <h1>WELCOME CUSTOMER</h1>
            {products.map((product, index) => (
                <ProductCard
                    key={index}
                    businessName={product.businessName}
                    brand={product.brand}
                    price={product.price}
                    desc={product.desc}
                    count={product.count}
                    showSettingsIcon={false}
                />
            ))}
        </div>
    );
}