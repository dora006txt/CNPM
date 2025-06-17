import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    react(),
    tailwindcss(),
    // require('@tailwindcss/line-clamp'),
  ],
  define: {
    global: {}, // 🛠️ Fix lỗi 'global is not defined' khi dùng stompjs
  },
  server: {
    proxy: {
      "/api": {
        target: "http://localhost:8080",
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, ''), // Loại bỏ '/api' khỏi đường dẫn khi gửi đến backend
      },
    },
  },
})
