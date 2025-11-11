/**
 * Pagination Component
 * 
 * Best Practice: Reusable pagination with keyboard navigation and accessibility
 */

interface PaginationProps {
  currentPage: number;
  totalPages: number;
  onPageChange: (page: number) => void;
  maxVisible?: number;
  showFirstLast?: boolean;
  className?: string;
}

export function Pagination({
  currentPage,
  totalPages,
  onPageChange,
  maxVisible = 5,
  showFirstLast = true,
  className = '',
}: PaginationProps) {
  // Don't render if there's only one page
  if (totalPages <= 1) return null;

  const canGoPrev = currentPage > 1;
  const canGoNext = currentPage < totalPages - 1;

  // Calculate visible page numbers
  const getVisiblePages = (): number[] => {
    const pages: number[] = [];
    let start = Math.max(1, currentPage - Math.floor(maxVisible / 2));
    const end = Math.min(totalPages - 1, start + maxVisible - 1);

    // Adjust start if we're near the end
    if (end - start < maxVisible - 1) {
      start = Math.max(1, end - maxVisible + 1);
    }

    for (let i = start; i <= end; i++) {
      pages.push(i);
    }

    return pages;
  };

  const visiblePages = getVisiblePages();

  return (
    <nav className={`pagination ${className}`} aria-label="Pagination">
      <ul className="pagination-list">
        {/* First Page */}
        {showFirstLast && currentPage > 1 && (
          <li>
            <button
              onClick={() => onPageChange(1)}
              className="pagination-btn pagination-first"
              aria-label="Go to first page"
              type="button"
            >
              ««
            </button>
          </li>
        )}

        {/* Previous Page */}
        <li>
          <button
            onClick={() => onPageChange(currentPage - 1)}
            className="pagination-btn pagination-prev"
            disabled={!canGoPrev}
            aria-label="Go to previous page"
            type="button"
          >
            ‹
          </button>
        </li>

        {/* Page Numbers */}
        {visiblePages.map((page) => (
          <li key={page}>
            <button
              onClick={() => onPageChange(page)}
              className={`pagination-btn ${page === currentPage ? 'active' : ''}`}
              aria-label={`Go to page ${page + 1}`}
              aria-current={page === currentPage ? 'page' : undefined}
              type="button"
            >
              {page + 1}
            </button>
          </li>
        ))}

        {/* Next Page */}
        <li>
          <button
            onClick={() => onPageChange(currentPage + 1)}
            className="pagination-btn pagination-next"
            disabled={!canGoNext}
            aria-label="Go to next page"
            type="button"
          >
            ›
          </button>
        </li>

        {/* Last Page */}
        {showFirstLast && currentPage < totalPages - 1 && (
          <li>
            <button
              onClick={() => onPageChange(totalPages - 1)}
              className="pagination-btn pagination-last"
              aria-label="Go to last page"
              type="button"
            >
              »»
            </button>
          </li>
        )}
      </ul>

      {/* Page Info */}
      <div className="pagination-info">
        Page {currentPage + 1} of {totalPages}
      </div>
    </nav>
  );
}

