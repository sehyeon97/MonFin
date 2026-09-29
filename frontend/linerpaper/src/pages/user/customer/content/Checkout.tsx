import { useEffect, useState } from "react";
import { CartUI } from "../../../../components/cart/CartUI";
import { useCart } from "../../../../hooks/customer/useCart";

import '../../../../stylesheets/customer/Checkout.css';
import type { CustomerSavedPaymentMethodResponse } from "../../../../dto/processor/CustomerSavedPaymentMethodResponse";
import { ViewCustomerCards } from "../../../../api/payment/ViewCustomerCards";

export function Checkout() {
    const cart = useCart();
    const taxRate = 0.0875;
    const shippingFee = 500; // may change in the future to an actual equation

    const [paymentMethods, setPaymentMethods] = useState<CustomerSavedPaymentMethodResponse[]>([]);

    useEffect(() => {
        async function fetchCustomerSavedPaymentMethods(): Promise<void> {
            const data: CustomerSavedPaymentMethodResponse[] =  await ViewCustomerCards();
            setPaymentMethods(data);
        }

        fetchCustomerSavedPaymentMethods();
    }, []);

    return (
        <div className="checkout-page">
            <h1>Checkout</h1>

            <div className="checkout-layout">

                {/* LEFT - Cart */}
                <section className="cart-section">
                    <h2>My Cart</h2>
                    <CartUI items={cart.cartItems} />
                </section>


                {/* RIGHT - Checkout */}
                <section className="checkout-section">

                    {/* Delivery Address */}
                    <div className="checkout-card">
                        <h2>Delivery Address</h2>

                        <select></select>
                        <p>This feature is coming soon! By end of 2027</p>
                    </div>


                    {/* Payment Method */}
                    <div className="checkout-card">
                        <h2>Payment Method</h2>

                        <select>
                            {
                                paymentMethods.map((pm) => (
                                    <option key={pm.id}>
                                        ... {pm.lastFour}
                                    </option>
                                ))
                            }
                        </select>
                    </div>


                    {/* Summary */}
                    <div className="checkout-card">
                        <h2>Order Summary</h2>

                        <div className="summary-row">
                            <span>Subtotal</span>
                            <span>${(cart.cartSubtotal / 100).toFixed(2)}</span>
                        </div>

                        <div className="summary-row">
                            <span>Shipping</span>
                            <span>${(shippingFee / 100).toFixed(2)}</span>
                        </div>

                        <div className="summary-row">
                            <span>Tax</span>
                            <span>${(Math.round(cart.cartSubtotal * taxRate) / 100).toFixed(2)}</span>
                        </div>

                        <div className="summary-row total">
                            <span>Total</span>
                            <span>${((cart.cartSubtotal + shippingFee + Math.round(cart.cartSubtotal * taxRate)) / 100).toFixed(2)}</span>
                        </div>

                        <button className="checkout-button">
                            Checkout
                        </button>
                    </div>

                </section>

            </div>
        </div>
    );
}