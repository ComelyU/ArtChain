import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000,
    host: "0.0.0.0",
    // hmr: {
    //   // HMR 연결을 위한 설정
    //   protocol: "wss", // 보안 연결을 위해 'wss' 사용
    //   host: "j10a708.p.ssafy.io", // 도메인 이름
    //   port: 443, // 서버가 SSL을 사용하여 HTTPS를 통해 접근할 때 사용하는 포트
    //   // public: 'j10a708.p.ssafy.io:443' // 공개적으로 접근 가능한 URL (필요한 경우)
    // },
  },
});
