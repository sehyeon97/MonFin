import { useState } from "react";

import { AddProduct } from "./content/AddProduct";
import { ViewProducts } from "./content/ViewProducts";
import { UserTypes } from "../../../types/UserType";

export function MerchantHomePage() {
    const [showAddProductPopup, setShowAddProductPopup] = useState(false);
    const [showPreviewProductsPopup, setShowPreviewProductsPopup] = useState(false);

    return (
        <div>
            <h1>WELCOME MERCHANT</h1>

            <button onClick={() => setShowAddProductPopup(true)}>
                Add Product
            </button>
            {showAddProductPopup && <AddProduct setShowPopup={setShowAddProductPopup} />}

            <button onClick={() => setShowPreviewProductsPopup(true)}>
                Preview Products
            </button>
            {showPreviewProductsPopup && <ViewProducts role={UserTypes.Merchant} setShowPopup={setShowPreviewProductsPopup} />}
        </div>
    );
}