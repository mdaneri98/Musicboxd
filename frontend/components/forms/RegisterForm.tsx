import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import { registerSchema } from '@/utils/validationSchemas';
import { RegisterFormData } from '@/types';
import { useTranslation } from 'react-i18next';

interface RegisterFormProps {
  onSubmit: (data: RegisterFormData) => void;
  error?: string;
  isLoading?: boolean;
}

const RegisterForm = ({ onSubmit, error, isLoading }: RegisterFormProps) => {
  const { t } = useTranslation();
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
        <label htmlFor="register_username" className="form-label">{t('auth.register.username')}</label>
        <input
          type="text"
          id="register_username"
          {...register('username')}
          className="form-control"
        />
        {errors.username && (
          <p className="form-error">{errors.username.message}</p>
        )}
      </div>

      <div className="form-group">
        <label htmlFor="register_email" className="form-label">{t('auth.register.email')}</label>
        <input
          type="text"
          id="register_email"
          {...register('email')}
          className="form-control"
        />
        {errors.email && <p className="form-error">{errors.email.message}</p>}
      </div>

      <div className="form-group">
        <label htmlFor="register_password" className="form-label">{t('auth.register.password')}</label>
        <input
          type="password"
          id="register_password"
          {...register('password')}
          className="form-control"
        />
        {errors.password && (
          <p className="form-error">{errors.password.message}</p>
        )}
      </div>

      <div className="form-group">
        <label htmlFor="register_repeatPassword" className="form-label">{t('auth.register.repeatPassword')}</label>
        <input
          type="password"
          id="register_repeatPassword"
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
        {isLoading ? t('auth.register.registering') : t('auth.register.submit')}
      </button>
    </form>
  );
};

export default RegisterForm;

