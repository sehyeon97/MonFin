import type { CustomerMerchantProductsResponse } from "./MerchantProducts";

export interface MerchantAndProducts {
  businessName: string;
  products: CustomerMerchantProductsResponse[];
}
