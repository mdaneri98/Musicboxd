/**
 * RegisterForm Component
 * User registration form with validation
 * Migrated from: users/register.jsp
 */

import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import { registerSchema } from '@/utils/validationSchemas';
import { RegisterFormData } from '@/types';

interface RegisterFormProps {
  onSubmit: (data: RegisterFormData) => void;
  error?: string;
  isLoading?: boolean;
}

const RegisterForm = ({ onSubmit, error, isLoading }: RegisterFormProps) => {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<RegisterFormData>({
    resolver: yupResolver(registerSchema) as any,
  });

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="auth-form">
      <div className="form-group">
        <label className="form-label">Username</label>
        <input
          type="text"
          {...register('username')}
          className="form-control"
        />
        {errors.username && (
          <p className="form-error">{errors.username.message}</p>
        )}
      </div>

      <div className="form-group">
        <label className="form-label">Email</label>
        <input type="text" {...register('email')} className="form-control" />
        {errors.email && <p className="form-error">{errors.email.message}</p>}
      </div>

      <div className="form-group">
        <label className="form-label">Password</label>
        <input
          type="password"
          {...register('password')}
          className="form-control"
        />
        {errors.password && (
          <p className="form-error">{errors.password.message}</p>
        )}
      </div>

      <div className="form-group">
        <label className="form-label">Repeat Password</label>
        <input
          type="password"
          {...register('repeatPassword')}
          className="form-control"
        />
        {errors.repeatPassword && (
          <p className="form-error">{errors.repeatPassword.message}</p>
        )}
      </div>

      {error && <p className="form-error">{error}</p>}

      <button
        type="submit"
        className="btn btn-primary btn-block"
        disabled={isLoading}
      >
        {isLoading ? 'Registering...' : 'Register'}
      </button>
    </form>
  );
};

export default RegisterForm;

