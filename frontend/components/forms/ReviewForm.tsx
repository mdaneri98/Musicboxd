import { Controller, useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import { reviewSchema } from '@/utils/validationSchemas';
import { ReviewFormData } from '@/types';

interface ReviewFormProps {
  onSubmit: (data: ReviewFormData) => void;
  onCancel: () => void;
  defaultValues?: Partial<ReviewFormData>;
  isLoading?: boolean;
}

const ReviewForm = ({
  onSubmit,
  onCancel,
  defaultValues,
  isLoading,
}: ReviewFormProps) => {
  const {
    register,
    handleSubmit,
    formState: { errors },
    control,
    watch,
  } = useForm<ReviewFormData>({
    resolver: yupResolver(reviewSchema) as any,
    defaultValues,
  });

  const currentRating = watch('rating');

  return (
    <div className="review-form-container">
      <form onSubmit={handleSubmit(onSubmit)} className="review-form">
        <div className="form-group">
          <label htmlFor="title" className="form-label">
            Title
          </label>
          <input
            type="text"
            id="title"
            {...register('title')}
            className="form-control"
          />
          {errors.title && (
            <p className="form-error">{errors.title.message}</p>
          )}
        </div>

        <div className="form-group">
          <label htmlFor="description" className="form-label">
            Description
          </label>
          <textarea
            id="description"
            rows={4}
            {...register('description')}
            className="form-control"
          />
          {errors.description && (
            <p className="form-error">{errors.description.message}</p>
          )}
        </div>

        <div className="form-group">
          <label className="form-label">Rating</label>
          <div className="rating-input">
            <Controller
              name="rating"
              control={control}
              render={({ field }) => (
                <div className="star-rating-input">
                  {[5, 4, 3, 2, 1].map((star) => (
                    <div key={star}>
                      <input
                        type="radio"
                        id={`star${star}`}
                        name={field.name}
                        value={star}
                        className="star-radio"
                        checked={Number(field.value ?? currentRating ?? 0) === star}
                        onChange={() => field.onChange(star)}
                      />
                      <label htmlFor={`star${star}`} className="star-label">
                        &#9733;
                      </label>
                    </div>
                  ))}
                </div>
              )}
            />
            {errors.rating && (
              <p className="form-error">{errors.rating.message}</p>
            )}
          </div>
        </div>

        <div className="form-actions">
          <button
            type="button"
            onClick={onCancel}
            className="btn btn-secondary"
          >
            Cancel
          </button>
          <button
            type="submit"
            className="btn btn-primary"
            disabled={isLoading}
          >
            {isLoading ? 'Submitting...' : 'Submit Review'}
          </button>
        </div>
      </form>
    </div>
  );
};

export default ReviewForm;

