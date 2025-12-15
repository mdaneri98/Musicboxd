import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import { loginSchema } from '@/utils/validationSchemas';
import { LoginFormData } from '@/types';
import { useTranslation } from 'react-i18next';

interface LoginFormProps {
  onSubmit: (data: LoginFormData) => void;
  error?: string;
  isLoading?: boolean;
}

const LoginForm = ({ onSubmit, error, isLoading }: LoginFormProps) => {
  const { t } = useTranslation();
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginFormData>({
    resolver: yupResolver(loginSchema) as any,
  });

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="auth-form">
      <div className="form-group">
        <label htmlFor="username" className="form-label">
          {t('auth.login.username')}
        </label>
        <input
          type="text"
          id="username"
          {...register('username')}
          className="form-control"
        />
        {errors.username && (
          <p className="form-error">{errors.username.message}</p>
        )}
      </div>

      <div className="form-group">
        <label htmlFor="password" className="form-label">
          {t('auth.login.password')}
        </label>
        <input
          type="password"
          id="password"
          {...register('password')}
          className="form-control"
        />
        {errors.password && (
          <p className="form-error">{errors.password.message}</p>
        )}
      </div>

      {error && (
        <div className="form-group">
          <p className="form-error">{error}</p>
        </div>
      )}

      <div className="form-group">
        <label className="checkbox-label">
          <input type="checkbox" {...register('rememberMe')} />
          <span className="checkbox-text">{t('auth.login.rememberMe')}</span>
        </label>
      </div>

      <button
        type="submit"
        className="btn btn-primary btn-block"
        disabled={isLoading}
      >
        {isLoading ? t('auth.login.loggingIn') : t('auth.login.submit')}
      </button>
    </form>
  );
};

export default LoginForm;

