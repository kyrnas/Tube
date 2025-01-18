import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      "/api": {
        target: "http://kyrstreams.com/",
        changeOrigin: true,
      },
      "/api/progress": {
        target: "ws://kyrstreams.com/",
        ws: true,
      },
    },
  },
});
