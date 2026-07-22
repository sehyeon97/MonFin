import type { ProductResponse } from "./ProductResponse";

export interface ProductsResponse {
  merchantID: string;
  products: ProductResponse[];
}
