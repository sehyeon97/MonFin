import { useEffect, useState } from "react";
import type { ProductResponse } from "../../../dto/merchant/ProductResponse";
import { ProductCard } from "../../../components/product/ProductCard";
import { GetMerchantProducts } from "../../../api/payment/GetMerchantProducts";

// testing for one merchant for now
const merchantID: string = "84069a79-bc5c-49c2-bb36-b7299f0b9375";

export function CustomerHomePage() {
    const [products, setProducts] = useState<ProductResponse[]>([]);

    useEffect(() => {
        const getProducts = async () => {
            const data: ProductResponse[] = await GetMerchantProducts(merchantID);
            setProducts(data);
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
                />
            ))}
        </div>
    );
}