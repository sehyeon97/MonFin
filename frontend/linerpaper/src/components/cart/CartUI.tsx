import type { CartItem } from '../../hooks/customer/CartItem';
import '../../stylesheets/customer/CartItemUI.css';

import '../../stylesheets/customer/CartItemUI.css';

type CartUIProps = {
    items: CartItem[];
}

export function CartUI({ items }: CartUIProps) {
    return items.map((cartItem, index) => (
        <div className="checkout-cart-item" key={index}>
            <div>
                <h3>{cartItem.businessName}</h3>
                <p>{cartItem.brand}</p>
            </div>

            <div>
                <span>
                    ${(cartItem.price * cartItem.count / 100).toFixed(2)}
                </span>
                <br />
                <span>Qty: {cartItem.count}</span>
            </div>
        </div>
    ))
}