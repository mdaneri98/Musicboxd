# Route Guards Implementation Guide

## Overview
This guide explains how to use the route guards and HOCs (Higher-Order Components) to protect pages in the Musicboxd application.

## Available Guards

### 1. `withAuth` HOC
Protects pages that require authentication. Redirects to login if not authenticated.

```typescript
import { withAuth } from '@/utils';

function ProfilePage() {
  // Your component code
}

export default withAuth(ProfilePage);
```

### 2. `withGuest` HOC
Protects pages that should only be accessible to guests (non-authenticated users). Redirects to home if authenticated.

```typescript
import { withGuest } from '@/utils';

function LoginPage() {
  // Your component code
}

export default withGuest(LoginPage);
```

### 3. `withModerator` HOC
Protects pages that require moderator privileges. Redirects to 403 if not a moderator.

```typescript
import { withModerator } from '@/utils';

function ModeratorDashboard() {
  // Your component code
}

export default withModerator(ModeratorDashboard);
```

### 4. `useOwnershipCheck` Hook
Custom hook for checking resource ownership. Useful for edit pages.

```typescript
import { useOwnershipCheck } from '@/hooks';

function EditProfilePage() {
  const { hasAccess, isChecking } = useOwnershipCheck(userId, {
    redirectTo: '/403',
    allowModerators: true, // Allow moderators to edit
  });

  if (isChecking) return <div>Loading...</div>;
  if (!hasAccess) return null;

  // Your component code
}
```

## Route Guard Utilities

### Functions
- `requireAuth(user)`: Check if user is authenticated
- `requireGuest(user)`: Check if user is a guest
- `requireModerator(user)`: Check if user is a moderator
- `requireOwner(user, resourceOwnerId)`: Check if user owns a resource
- `requireOwnerOrModerator(user, resourceOwnerId)`: Check if user owns a resource OR is a moderator

### Helpers
- `redirectWithReturnUrl(router, path, currentPath)`: Redirect with return URL in query params
- `getRedirectUrl(router, fallback)`: Get redirect URL from query params

## Page Protection Matrix

| Page/Route | Protection | HOC/Hook |
|------------|------------|----------|
| `/` (Home - Authenticated) | Requires Auth | `withAuth` |
| `/landing` (Home - Anonymous) | Guest Only | `withGuest` |
| `/login` | Guest Only | `withGuest` |
| `/register` | Guest Only | `withGuest` |
| `/profile` | Requires Auth | `withAuth` |
| `/profile/edit` | Requires Auth + Ownership | `withAuth` + `useOwnershipCheck` |
| `/notifications` | Requires Auth | `withAuth` |
| `/settings` | Requires Auth | `withAuth` |
| `/search` | Requires Auth | `withAuth` |
| `/moderator/*` | Requires Auth + Moderator | `withModerator` |
| `/artists/[id]` | Public | None |
| `/albums/[id]` | Public | None |
| `/songs/[id]` | Public | None |
| `/reviews/[id]` | Public | None |
| `/users/[id]` | Public | None |

## Best Practices

### 1. Use HOCs for Page-Level Protection
```typescript
// ✅ Good: Wrap the entire page component
export default withAuth(ProfilePage);

// ❌ Bad: Conditional rendering inside component
function ProfilePage() {
  if (!isAuthenticated) return <Redirect to="/login" />;
  // ...
}
```

### 2. Combine Multiple Protections
```typescript
// For pages that need both auth and ownership check
function EditProfilePage() {
  const { hasAccess, isChecking, currentUser } = useOwnershipCheck(userId);
  
  if (isChecking) return <LoadingSpinner />;
  if (!hasAccess) return null;
  
  return <EditForm user={currentUser} />;
}

export default withAuth(EditProfilePage);
```

### 3. Handle Loading States
```typescript
function ProtectedPage() {
  const isAuthenticated = useAppSelector(selectIsAuthenticated);
  const authLoading = useAppSelector(selectAuthLoading);

  // ✅ Good: Show loading state
  if (authLoading) {
    return <LoadingSpinner />;
  }

  // ❌ Bad: Flash of unauthorized content
  if (!isAuthenticated) {
    return null;
  }
  
  return <PageContent />;
}
```

### 4. Preserve Return URL
```typescript
// HOCs automatically add redirect query param:
// /login?redirect=/profile/edit

// After login, redirect back:
const redirect = getRedirectUrl(router, '/');
router.push(redirect);
```

### 5. Moderator vs Owner Checks
```typescript
// Use allowModerators option to determine access model
const { hasAccess } = useOwnershipCheck(userId, {
  allowModerators: true, // Moderators can edit any user's profile
});

const { hasAccess } = useOwnershipCheck(reviewId, {
  allowModerators: false, // Only review owner can edit
});
```

## Examples

### Protected User Profile Edit
```typescript
import { withAuth } from '@/utils';
import { useOwnershipCheck } from '@/hooks';

function EditProfilePage() {
  const router = useRouter();
  const { id } = router.query;
  const userId = parseInt(id as string);
  
  const { hasAccess, isChecking, currentUser } = useOwnershipCheck(userId, {
    allowModerators: true,
  });

  if (isChecking) return <LoadingSpinner />;
  if (!hasAccess) return null;

  return (
    <Layout title="Edit Profile">
      <EditProfileForm user={currentUser} />
    </Layout>
  );
}

export default withAuth(EditProfilePage);
```

### Moderator Dashboard
```typescript
import { withModerator } from '@/utils';

function ModeratorDashboard() {
  return (
    <Layout title="Moderator Dashboard">
      <h1>Moderator Tools</h1>
      <ModeratorActions />
    </Layout>
  );
}

export default withModerator(ModeratorDashboard);
```

### Guest-Only Login Page
```typescript
import { withGuest } from '@/utils';

function LoginPage() {
  return (
    <Layout title="Login">
      <LoginForm />
    </Layout>
  );
}

export default withGuest(LoginPage);
```

## Testing Guards

When writing tests, mock the Redux selectors:

```typescript
import { renderWithProviders } from '@/test-utils';
import { withAuth } from '@/utils';

describe('withAuth HOC', () => {
  it('redirects to login when not authenticated', () => {
    const mockStore = {
      auth: {
        isAuthenticated: false,
        loading: false,
      },
    };
    
    const Component = withAuth(() => <div>Protected</div>);
    const { container } = renderWithProviders(<Component />, { 
      preloadedState: mockStore 
    });
    
    expect(container).toBeEmptyDOMElement();
  });
});
```

## Migration Checklist

- [x] Create HOCs (`withAuth`, `withGuest`, `withModerator`)
- [x] Create `useOwnershipCheck` hook
- [x] Create route guard utilities
- [ ] Apply `withGuest` to `/login` and `/register` pages
- [ ] Apply `withAuth` to protected pages
- [ ] Apply `withModerator` to moderator pages
- [ ] Add ownership checks to edit pages
- [ ] Test all protection scenarios
- [ ] Update page components to remove inline auth checks

