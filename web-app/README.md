# Calm Burst Web App

This is the web application for Calm Burst, built with React, TypeScript, and Vite. This app is designed to be Capacitor-ready for deployment to mobile platforms.

## Tech Stack

- **React 18.x** - UI library
- **TypeScript 5.x** - Type-safe JavaScript
- **Vite 5.x** - Fast build tool and dev server
- **Tailwind CSS 3.x** - Utility-first CSS framework
- **React Router DOM 6.x** - Client-side routing

## Color Palette (Earthy Theme)

- **Primary**: #5D4037 (brown 700)
- **Secondary**: #8D6E63 (brown 300)
- **Accent**: #A1887F (brown 200)
- **Background**: #EFEBE9 (brown 50)
- **Text**: #3E2723 (brown 900)

## Getting Started

### Install Dependencies

```bash
npm install
```

### Development Server

```bash
npm run dev
```

The app will be available at `http://localhost:3000`

### Build for Production

```bash
npm run build
```

### Preview Production Build

```bash
npm run preview
```

## Code Quality

### Linting

```bash
npm run lint
npm run lint:fix
```

### Formatting

```bash
npm run format
npm run format:check
```

## Project Structure

```
web-app/
├── src/
│   ├── components/     # Reusable UI components
│   ├── hooks/          # Custom React hooks
│   ├── services/       # Business logic and API calls
│   ├── types/          # TypeScript type definitions
│   ├── assets/         # Static assets (images, fonts, etc.)
│   ├── App.tsx         # Main application component
│   ├── main.tsx        # Application entry point
│   └── index.css       # Global styles and Tailwind directives
├── index.html          # HTML template
├── vite.config.ts      # Vite configuration
├── tsconfig.json       # TypeScript configuration
├── tailwind.config.js  # Tailwind CSS configuration
└── package.json        # Project dependencies
```

## Path Aliases

The project is configured to use `@/` as an alias for the `src/` directory:

```typescript
// Instead of:
import Component from '../../components/Component'

// You can use:
import Component from '@/components/Component'
```

## Next Steps

- Add Capacitor for mobile deployment
- Implement breathing exercise logic
- Add state management (Context API or Zustand)
- Create reusable components
- Add animations and transitions
- Implement local storage for settings
