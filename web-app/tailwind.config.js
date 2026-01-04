/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        primary: {
          DEFAULT: '#5D4037', // brown 700
          light: '#8D6E63',   // brown 300
          dark: '#3E2723',    // brown 900
        },
        secondary: {
          DEFAULT: '#8D6E63', // brown 300
          light: '#A1887F',   // brown 200
          dark: '#5D4037',    // brown 700
        },
        accent: {
          DEFAULT: '#A1887F', // brown 200
          light: '#BCAAA4',   // brown 100
          dark: '#8D6E63',    // brown 300
        },
        background: {
          DEFAULT: '#EFEBE9', // brown 50
          light: '#FFFFFF',   // white
          dark: '#D7CCC8',    // brown 100
        },
        text: {
          DEFAULT: '#3E2723', // brown 900
          light: '#5D4037',   // brown 700
          muted: '#8D6E63',   // brown 300
        },
      },
    },
  },
  plugins: [],
}
