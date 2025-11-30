/**
 * CommentForm Component
 * Comment creation/edit form
 */

import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import { commentSchema } from '@/utils/validationSchemas';
import { CommentFormData } from '@/types';

interface CommentFormProps {
  onSubmit: (data: CommentFormData) => void;
  onCancel?: () => void;
  defaultValues?: Partial<CommentFormData>;
  isLoading?: boolean;
  placeholder?: string;
  reviewId: number;
}

const CommentForm = ({
  onSubmit,
  onCancel,
  defaultValues,
  isLoading,
  placeholder = 'Write a comment...',
  reviewId,
}: CommentFormProps) => {
  const {
    register,
    handleSubmit,
    formState: { errors },
    reset,
  } = useForm<CommentFormData>({
    resolver: yupResolver(commentSchema) as any,
    defaultValues,
  });

  const handleFormSubmit = (data: CommentFormData) => {
    console.log('Form submitted:', data);
    onSubmit(data);
    reset();
  };

  return (
    <form onSubmit={handleSubmit(handleFormSubmit)} className="comment-form">
      <div className="form-group">
        <textarea
          {...register('content')}
          className="form-control"
          rows={3}
          placeholder={placeholder}
        />
        {errors.content && (
          <p className="form-error">{errors.content.message}</p>
        )}
      </div>
      <input type="hidden" value={reviewId} {...register("review_id")} />

      <div className="form-actions">
        {onCancel && (
          <button
            type="button"
            onClick={onCancel}
            className="btn btn-secondary"
          >
            Cancel
          </button>
        )}
        <button
          type="submit"
          className="btn btn-primary"
          disabled={isLoading}
        >
          {isLoading ? 'Posting...' : 'Post Comment'}
        </button>
      </div>
    </form>
  );
};

export default CommentForm;

