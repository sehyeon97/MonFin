import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  // to mitigate CORs because nestjs and spring boot runs on different port
  server: {
    proxy: {
      // spring boot (bank backend)
      "/api": {
        target: "http://localhost:8080",
        changeOrigin: true,
      },
      // nestjs (payment processor backend)
      "/payment-api": {
        target: "http://localhost:3000",
        changeOrigin: true,
      },
    },
  },
});
