import react from '@vitejs/plugin-react';
import path, { resolve } from 'path';
import { defineConfig } from 'vite';
import tsconfigPaths from 'vite-tsconfig-paths';

export default defineConfig(({ mode }) => {
    if (mode === 'lib') {
        return {
            base: './',
            build: {
                lib: {
                    entry: path.resolve(__dirname, 'src/lib/index.tsx'),
                    name: 'nftMarketPlace',
                    formats: ['es', 'iife', 'umd'],
                },
                rollupOptions: {
                    external: ['react', 'react-dom'],
                    output: {
                        assetFileNames: `nftMarketPlace.[ext]`,
                        globals: {
                            react: 'React',
                            'react-dom': 'ReactDOM',
                        },
                        exports: 'named',
                    },
                },
                emptyOutDir: true,
                assetsDir: 'assets',
            },
            plugins: [react({})],
            resolve: {
                alias: {
                    '@/*': path.resolve(__dirname, 'src'),
                },
            },
        };
    }

    return {
        plugins: [react(), tsconfigPaths()],
        resolve: {
            alias: {
                '@/*': resolve(__dirname, 'src'),
            },
        },
    };
});
