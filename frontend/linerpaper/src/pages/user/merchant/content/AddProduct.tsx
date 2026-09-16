import { useState } from "react";

import "../../../../stylesheets/popups/AddProductPopup.css";

import type { ProductRequest } from "../../../../dto/merchant/ProductRequest";
import { AddProductForMerchant } from "../../../../api/merchant/AddMerchantProduct";
import type { ProductResponse } from "../../../../dto/merchant/ProductResponse";

type AddProductProps = {
    setShowPopup: (showPopup: boolean) => void;
}

export function AddProduct({ setShowPopup }: AddProductProps) {
    const [businessName, setBusinessName] = useState("");
    const [brand, setBrand] = useState("");
    const [price, setPrice] = useState("9.99");
    const [desc, setDesc] = useState("Max 250 characters");
    const [count, setCount] = useState(1);

    const [error, setError] = useState("");

    async function handleFormSubmit(event: React.SubmitEvent) {
        event.preventDefault();
        setError("");

        const productRequest: ProductRequest = {
            businessName: businessName,
            brand: brand,
            price: Math.round(Number(price) * 100),
            desc: desc,
            count: count,
        };

        const addedProduct: ProductResponse | null = await AddProductForMerchant(productRequest);
        if (!addedProduct) {
            setError("Could not add this product");
        }
        console.log(addedProduct);
        setShowPopup(false);
    }

    return (
        <div className="add-product-modal-backdrop" onClick={() => setShowPopup(false)}>
            <div
                className="add-product-modal"
                onClick={(event) => event.stopPropagation()}
            >
                <h2>Add Product</h2>
                <form className="form-row" onSubmit={handleFormSubmit}>
                    <label htmlFor="businessName">Business Name</label>
                    <input
                        id="businessName"
                        type="text"
                        value={businessName}
                        onChange={(event) => setBusinessName(event.target.value)}
                    />

                    <label htmlFor="brand">Brand</label>
                    <input
                        id="brand"
                        type="text"
                        value={brand}
                        onChange={(event) => setBrand(event.target.value)}
                    />

                    <label htmlFor="productPrice" >Price</label>
                    <input
                        id="productPrice"
                        type="text"
                        inputMode="decimal" 
                        onChange={(event) => {
                            const value: string = event.target.value;
                            if (/^\d*\.?\d{0,2}$/.test(value)) {
                                setPrice(value);
                            }
                        }}
                        value={price}
                    />

                    <label htmlFor="description" >Description</label>
                    <textarea
                        id="description"
                        maxLength={250}
                        value={desc}
                        onChange={(event) => setDesc(event.target.value)}
                    />

                    <label htmlFor="count" >Count</label>
                    <input
                        id="count"
                        type="number"
                        value={count}
                        min={1}
                        onChange={(event) => setCount(event.target.valueAsNumber)}
                    />

                    {error && <p>{error}</p>}
                    <button type="submit" >Add Product</button>
                </form>
            </div>
        </div>
    );
}