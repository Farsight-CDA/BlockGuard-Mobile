import { CapacitorConfig } from '@capacitor/cli';
import { join } from "path";

const config: CapacitorConfig = {
  appId: 'com.farsight.cda.blockguard',
  appName: 'BlockGuard',
  webDir: join("frontend", "build"),
  bundledWebRuntime: false
};

export default config;