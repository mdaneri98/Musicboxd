import { useEffect, useState } from 'react';
import { useRouter } from 'next/router';
import Link from 'next/link';
import { Layout } from '@/components/layout';
import { ReviewForm } from '@/components/forms';
import { ConfirmationModal } from '@/components/ui';
import { useAppSelector, useAppDispatch } from '@/store/hooks';
import { 
  selectIsAuthenticated, 
  selectCurrentUser, 
  fetchSongByIdAsync, 
  fetchAlbumByIdAsync, 
  fetchSongReviewsAsync, 
  updateReviewAsync, 
  createSongReviewAsync, 
  deleteReviewAsync,
  selectCurrentSong,
  selectLoadingSong,
  clearCurrentSong
} from '@/store/slices';
import { imageRepository } from '@/repositories';
import type { Album, ReviewFormData, HALResource, Review } from '@/types';

const SongReviewPage = () => {
  const router = useRouter();
  const { songId, edit } = router.query;
  const dispatch = useAppDispatch();
  const isAuthenticated = useAppSelector(selectIsAuthenticated);
  const currentUser = useAppSelector(selectCurrentUser);

  const song = useAppSelector(selectCurrentSong);
  const loadingSong = useAppSelector(selectLoadingSong);
  const [album, setAlbum] = useState<Album | null>(null);
  const [existingReview, setExistingReview] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [submitLoading, setSubmitLoading] = useState(false);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const isEditMode = edit === 'true';

  useEffect(() => {
    return () => {
      dispatch(clearCurrentSong());
    };
  }, [dispatch]);

  const formatDuration = (seconds: number) => {
    const minutes = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${minutes}:${secs.toString().padStart(2, '0')}`;
  };

  useEffect(() => {
    if (!isAuthenticated) {
      router.push('/login');
      return;
    }

    const fetchData = async () => {
      if (!songId) return;

      try {
        setLoading(true);
        const songIdNum = parseInt(songId as string);
        const fetchedSong = await dispatch(fetchSongByIdAsync(songIdNum)).unwrap();

        const albumData = await dispatch(fetchAlbumByIdAsync(fetchedSong.data.album_id)).unwrap();
        setAlbum(albumData.data);

        if (currentUser) {
          const reviews = await dispatch(fetchSongReviewsAsync({ songId: songIdNum, page: 1, size: 100 })).unwrap();
          const userReview = reviews.items.find((r: HALResource<Review>) => r.data.user_id === currentUser.id);
          
          if (userReview && !isEditMode) {
            router.push(`/songs/${songId}/reviews?edit=true`);
            return;
          }
          
          if (userReview) {
            setExistingReview(userReview.data);
          } else if (isEditMode) {
            router.push(`/songs/${songId}/reviews`);
            return;
          }
        }
      } catch (error) {
        console.error('Failed to fetch song:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [songId, isAuthenticated, currentUser, isEditMode, router, dispatch]);

  const handleSubmit = async (data: ReviewFormData) => {
    if (!song) return;

    try {
      setSubmitLoading(true);

      if (isEditMode && existingReview) {
        await dispatch(updateReviewAsync({ id: existingReview.id, reviewData: data })).unwrap();
      } else {
        await dispatch(createSongReviewAsync({ 
          songId: song.id, 
          reviewData: {
            title: data.title,
            description: data.description,
            rating: data.rating
          }
        })).unwrap();
      }

      router.push(`/songs/${song.id}`);
    } catch (error) {
      console.error('Failed to submit review:', error);
    } finally {
      setSubmitLoading(false);
    }
  };

  const handleDelete = async () => {
    if (!existingReview) return;

    try {
      await dispatch(deleteReviewAsync(existingReview.id)).unwrap();
      router.push(`/songs/${songId}`);
    } catch (error) {
      console.error('Failed to delete review:', error);
    }
  };

  if (!isAuthenticated) {
    return null;
  }

  if (loading || loadingSong || !song) {
    return (
      <Layout title="Loading...">
        <div className="content-wrapper">
          <div className="loading">Loading...</div>
        </div>
      </Layout>
    );
  }

  const albumImgUrl = album?.image_id ? imageRepository.getImageUrl(album.image_id) : '/assets/default-album.png';

  return (
    <Layout title={`Musicboxd - Review ${song.title}`}>
      <div className="content-wrapper">
        <h1 className="page-title">
          {isEditMode ? 'Edit Your Review' : 'Make a Review'}
        </h1>

        {/* Song Preview */}
        <div className="review-preview">
          <Link href={`/songs/${song.id}`} className="review-preview-link">
            <img src={albumImgUrl} alt={song.title} className="review-preview-image" />
            <div className="review-preview-info">
              <h2 className="review-preview-title">{song.title}</h2>
              <p className="review-preview-subtitle">{formatDuration(parseInt(song.duration))}</p>
            </div>
          </Link>
        </div>

        {/* Review Form */}
        <div className="review-section">
          <ReviewForm
            onSubmit={handleSubmit}
            onCancel={() => router.push(`/songs/${song.id}`)}
            isLoading={submitLoading}
            defaultValues={existingReview ? {
              title: existingReview.title,
              description: existingReview.description,
              rating: existingReview.rating,
            } : undefined}
          />

          {isEditMode && existingReview && (
            <div className="delete-review-container">
              <button
                type="button"
                onClick={() => setShowDeleteModal(true)}
                className="btn btn-danger"
              >
                Delete Review
              </button>
            </div>
          )}
        </div>
      </div>

      {/* Confirmation Modal */}
      <ConfirmationModal
        isOpen={showDeleteModal}
        message="Are you sure you want to delete this review?"
        onConfirm={handleDelete}
        onCancel={() => setShowDeleteModal(false)}
        confirmText="Yes"
        cancelText="No"
      />
    </Layout>
  );
};

export default SongReviewPage;

