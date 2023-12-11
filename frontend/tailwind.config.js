/** @type {import('tailwindcss').Config} */
const defaultTheme = require('tailwindcss/defaultTheme')

module.exports = {
  content: ['./index.html', './src/**/*.{vue, js, ts}'],
  theme: {
    extend: {
      fontFamily: {
        sans: ['Open Sans', ...defaultTheme.fontFamily.sans]
      },
      colors: {
        'krygo-burgundy': '#770865',
        'krygo-scarlet': '#8E001C',
        'krygo-hre-gold': '#FACF00',
        'krygo-n-red': '#DE0000'
      },
      boxShadow: {
        'mcl-orange': '0 0 3px 3px #ff6d01',
        'mcl-pale-orange': '0 0 3px 3px #ff984a'
      }
    }
  },
  plugins: []
}
