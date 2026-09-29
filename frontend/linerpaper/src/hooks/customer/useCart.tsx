import { useEffect, useState } from "react";
import type { CartItem } from "./CartItem";

// checks previous cart status from localStorage
function getPrevCart(): CartItem[] {
    const storedCart = localStorage.getItem("cart");

    return storedCart ? JSON.parse(storedCart) : [];
}

// frontend owns cart until customer is ready to checkout
export function useCart() {
    const [cartItems, setCartItems] = useState<CartItem[]>(() => getPrevCart());
    const [cartSubtotal, setCartSubtotal] = useState(0);

    const addCartItem = (cartItem: CartItem) =>
        setCartItems(prevCartItems => [...prevCartItems, cartItem]);

    const removeCartItem = (cartItem: CartItem) =>
        setCartItems(prevCartItems => prevCartItems.filter(currCartItem => 
            currCartItem.businessName !== cartItem.businessName ||
            currCartItem.brand !== cartItem.brand ||
            currCartItem.price !== cartItem.price ||
            currCartItem.desc !== cartItem.desc ||
            currCartItem.count !== cartItem.count
        ));

    const updateCartItemQuantity = (cartItem: CartItem, count: number) =>
        setCartItems(prevCartItems =>
            prevCartItems.map(currCartItem =>
                currCartItem.businessName === cartItem.businessName &&
                currCartItem.brand === cartItem.brand &&
                currCartItem.price === cartItem.price &&
                currCartItem.desc === cartItem.desc
                    ? { ...currCartItem, count: count }
                    : currCartItem
            )
        );

    // any time cart gets modified, update the cart subtotal
    useEffect(() => {
        const calculateSubTotal = () => {
            const subtotal = cartItems.reduce(
                // total: Value to get the sum of
                // cartItem: the current item to process
                // right of the arrow: how to add to total
                (total, cartItem) => total + cartItem.price * cartItem.count,
                0 // starting value
            );
            setCartSubtotal(subtotal);
        }

        const saveToLocalStorage = () =>
            localStorage.setItem("cart", JSON.stringify(cartItems));

        calculateSubTotal();
        saveToLocalStorage();
    }, [cartItems]);

    return { cartItems, cartSubtotal, addCartItem, removeCartItem, updateCartItemQuantity };
}