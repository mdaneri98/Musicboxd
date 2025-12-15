interface RatingDisplayProps {
  rating: number; // 1-5
  maxRating?: number;
  showNumber?: boolean;
  size?: 'small' | 'medium' | 'large';
}

const RatingDisplay = ({
  rating,
  maxRating = 5,
  showNumber = false,
  size = 'medium',
}: RatingDisplayProps) => {
  return (
    <div className={`rating-display rating-display-${size}`}>
      <div className="star-rating">
        {Array.from({ length: maxRating }, (_, i) => (
          <span
            key={i}
            className={`star ${i < Math.floor(rating) ? 'filled' : ''}`}
          >
            &#9733;
          </span>
        ))}
      </div>
      {showNumber && (
        <span className="rating-value">
          {rating.toFixed(1)} / {maxRating}
        </span>
      )}
    </div>
  );
};

export default RatingDisplay;

