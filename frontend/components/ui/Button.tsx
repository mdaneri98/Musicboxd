import { ButtonHTMLAttributes, forwardRef } from 'react';

/**
 * Base Button Component with variants
 * 
 * Best Practice: Provides a consistent button component with variants,
 * following the Single Responsibility Principle and composition
 */

interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'primary' | 'secondary' | 'danger' | 'ghost';
  size?: 'small' | 'medium' | 'large';
  fullWidth?: boolean;
  loading?: boolean;
}

export const Button = forwardRef<HTMLButtonElement, ButtonProps>(
  (
    {
      children,
      variant = 'primary',
      size = 'medium',
      fullWidth = false,
      loading = false,
      disabled,
      className = '',
      ...props
    },
    ref
  ) => {
    // Build class names based on props
    const variantClasses = {
      primary: 'btn-primary',
      secondary: 'btn-secondary',
      danger: 'btn-danger',
      ghost: 'btn-ghost',
    };

    const sizeClasses = {
      small: 'btn-small',
      medium: '',
      large: 'btn-large',
    };

    const classes = [
      'btn',
      variantClasses[variant],
      sizeClasses[size],
      fullWidth ? 'btn-full-width' : '',
      loading ? 'btn-loading' : '',
      className,
    ]
      .filter(Boolean)
      .join(' ');

    return (
      <button
        ref={ref}
        className={classes}
        disabled={disabled || loading}
        {...props}
      >
        {loading ? (
          <>
            <span className="btn-spinner"></span>
            {children}
          </>
        ) : (
          children
        )}
      </button>
    );
  }
);

Button.displayName = 'Button';

