# Musicboxd Frontend

Modern Single Page Application (SPA) for Musicboxd using Next.js, React, TypeScript, and Redux.

## Tech Stack

- **Framework:** Next.js 16 (Pages Router)
- **UI Library:** React 19
- **Language:** TypeScript
- **State Management:** Redux Toolkit
- **Forms:** React Hook Form + Yup
- **HTTP Client:** Axios
- **Testing:** Jest + React Testing Library
- **Linting:** ESLint
- **Formatting:** Prettier

## Prerequisites

- Node.js 18+ or 20+
- npm 10+

## Setup

1. Install dependencies:
```bash
npm install
```

2. Create environment file:
```bash
cp .env.local.example .env.local
```

3. Update environment variables in `.env.local` as needed.

## Development

Run the development server:

```bash
npm run dev
```

Open [http://localhost:3000](http://localhost:3000) with your browser.

## Building

Build for production:

```bash
npm run build
```

This generates a static export in the `out/` directory.

Preview production build:

```bash
npm run start
```

## Testing

Run tests:

```bash
npm test
```

Run tests in watch mode:

```bash
npm run test:watch
```

Generate coverage report:

```bash
npm run test:coverage
```

## Linting & Formatting

Run ESLint:

```bash
npm run lint
```

Format code with Prettier:

```bash
npm run format
```

Check formatting:

```bash
npm run format:check
```

## Project Structure

```
frontend/
├── components/          # React components
│   ├── layout/         # Layout components (Sidebar, Footer, etc.)
│   ├── cards/          # Card components (UserCard, ArtistCard, etc.)
│   ├── forms/          # Form components
│   └── ui/             # UI utility components
├── pages/              # Next.js pages
├── store/              # Redux store and slices
│   ├── slices/         # Redux slices
│   ├── index.ts        # Store configuration
│   └── hooks.ts        # Typed Redux hooks
├── repositories/       # API repositories (HATEOAS-aware)
├── types/              # TypeScript type definitions
├── utils/              # Utility functions
├── lib/                # Core libraries (ApiClient, etc.)
├── hooks/              # Custom React hooks
├── styles/             # CSS files
└── public/             # Static assets
```

## Environment Variables

- `NEXT_PUBLIC_API_BASE_URL`: Backend API base URL (default: `http://localhost:8080/api`)

## Integration with Maven

This frontend is built and packaged with the backend using `frontend-maven-plugin`.

The Maven build process:
1. Installs Node.js and npm
2. Runs `npm install`
3. Runs `npm run build`
4. Copies the `out/` directory to Spring's static resources

## Contributing

1. Follow the existing code style
2. Run tests before committing
3. Format code with Prettier
4. Ensure ESLint passes

## License

Private project - ITBA PAW 2024B
