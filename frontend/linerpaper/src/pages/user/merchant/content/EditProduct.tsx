import { useState } from 'react';
import type { ProductResponse } from '../../../../dto/merchant/ProductResponse';

import '../../../../stylesheets/popups/EditProductPopup.css';

type EditProductProps = {
    product: ProductResponse;
    onSave: (price: number, desc: string, count: number) => void;
    setShowPopup: (showPopup: boolean) => void;
};

export function EditProduct({ product, onSave, setShowPopup }: EditProductProps) {
    const [price, setPrice] = useState((product.price / 100).toFixed(2).toString());
    const [desc, setDesc] = useState(product.desc);
    const [count, setCount] = useState(product.count);

    return (
        <div className="settings-backdrop" onClick={() => setShowPopup(false)}>
            <div className="settings-modal" onClick={(event) => event.stopPropagation()}>
                <h2>Edit Product</h2>

                <div className="settings-row">
                    <label>Price:</label>
                    <input
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
                </div>

                <div className="settings-row">
                    <label>Quantity:</label>
                    <input
                        type="number"
                        value={count}
                        min={1}
                        readOnly={true}
                    />

                    <div className="quantity-buttons">
                        <button onClick={() => setCount(count + 1)}>▲</button>
                        <button onClick={() => {
                            if (count - 1 > 0) setCount(count - 1)
                        }}>▼</button>
                    </div>
                </div>

                <div className="settings-description">
                    <label>Description:</label>
                    <textarea
                        maxLength={250}
                        value={desc}
                        onChange={(event) => setDesc(event.target.value)}
                    />
                </div>

                <div className="settings-actions">
                    <button className="settings-save-button" onClick={() => onSave(Number(price), desc, count)}>
                        Save
                    </button>
                    <button className="settings-cancel-button" onClick={() => setShowPopup(false)}>
                        Cancel
                    </button>
                </div>
            </div>
        </div>
    );
}