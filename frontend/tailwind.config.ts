import daisyui from 'daisyui';

/** @type {import('tailwindcss').Config} */
module.exports = {
    content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
    theme: {
        extend: {
            colors: {
                primary: '#3B3B3B',
                primaryHover: '#484848',
                primaryHoverFocus: '#767676',
                secondary: '#A259FF',
                secondaryHoverFocus: '#7b14ff',
                gray: '#858584',
                bg: '#2B2B2B',
            },
            lineHeight: {
                xs: '0.1rem',
            },
            fontFamily: {
                spaceMono: ['Space Mono', 'monospace'],
            },
            minHeight: {
                xl: '140px',
            },
        },
    },
    daisyui: {
        themes: [
            {
                dark: {
                    // ...import('daisyui/src/colors/themes.js')[
                    //     '[data-theme=dark]'
                    // ],
                    info: '#A259FF',
                    primary: '#A259FF',
                    'primary-focus': '#7b14ff',
                    'base-100': '#2B2B2B',
                    'base-200': '#262626',
                    'base-300': '#1F1F1F',
                },
            },
        ],
    },
    plugins: [daisyui],
};
