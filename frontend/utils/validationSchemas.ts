/**
 * Validation Schemas
 * Yup validation schemas for all forms in the application
 * 
 * IMPORTANT: All validation messages use i18n keys.
 * The keys follow the pattern: validation.<fieldType>.<validationType>
 * These keys must be present in all locale JSON files.
 */

import * as Yup from 'yup';

// ============================================================================
// Auth Forms
// ============================================================================

export const loginSchema = Yup.object().shape({
  username: Yup.string()
    .required('validation.username.required')
    .min(3, 'validation.username.minLength')
    .max(50, 'validation.username.maxLength'),
  password: Yup.string()
    .required('validation.password.required')
    .min(6, 'validation.password.minLength'),
  rememberMe: Yup.boolean(),
});

export const registerSchema = Yup.object().shape({
  username: Yup.string()
    .required('validation.username.required')
    .min(4, 'validation.username.minLengthRegister')
    .max(50, 'validation.username.maxLength')
    .matches(/^[a-zA-Z][a-zA-Z0-9]*$/, 'validation.username.format'),
  email: Yup.string()
    .required('validation.email.required')
    .email('validation.email.invalid')
    .max(100, 'validation.email.maxLength'),
  password: Yup.string()
    .required('validation.password.required')
    .min(8, 'validation.password.minLengthRegister')
    .max(100, 'validation.password.maxLength'),
  repeatPassword: Yup.string()
    .required('validation.password.confirmRequired')
    .min(8, 'validation.password.minLengthRegister')
    .oneOf([Yup.ref('password')], 'validation.password.mismatch'),
});

export const forgotPasswordSchema = Yup.object().shape({
  email: Yup.string()
    .required('validation.email.required')
    .email('validation.email.invalid'),
});

export const resetPasswordSchema = Yup.object().shape({
  password: Yup.string()
    .required('validation.password.required')
    .min(6, 'validation.password.minLength')
    .max(100, 'validation.password.maxLength'),
  repeatPassword: Yup.string()
    .required('validation.password.confirmRequired')
    .oneOf([Yup.ref('password')], 'validation.password.mismatch'),
});

// ============================================================================
// User Forms
// ============================================================================

export const editProfileSchema = Yup.object().shape({
  username: Yup.string()
    .optional()
    .min(3, 'validation.username.minLength')
    .max(50, 'validation.username.maxLength')
    .matches(/^[a-zA-Z0-9_-]+$/, 'validation.username.formatProfile'),
  name: Yup.string()
    .optional()
    .max(100, 'validation.name.maxLength'),
  bio: Yup.string()
    .optional()
    .max(500, 'validation.bio.maxLength'),
  profilePicture: Yup.mixed().optional().nullable(),
});

// ============================================================================
// Review Forms
// ============================================================================

export const reviewSchema = Yup.object().shape({
  title: Yup.string()
    .required('validation.title.required')
    .min(3, 'validation.title.minLength')
    .max(100, 'validation.title.maxLength'),
  description: Yup.string()
    .required('validation.description.required')
    .min(10, 'validation.description.minLength')
    .max(2000, 'validation.description.maxLength'),
  rating: Yup.number()
    .required('validation.rating.required')
    .min(1, 'validation.rating.min')
    .max(5, 'validation.rating.max')
});

// ============================================================================
// Comment Forms
// ============================================================================

export const commentSchema = Yup.object().shape({
  content: Yup.string()
    .required('validation.comment.required')
    .min(2, 'validation.comment.minLength')
    .max(500, 'validation.comment.maxLength'),
  review_id: Yup.number().required(),
});

// ============================================================================
// Music Forms (Moderator)
// ============================================================================

export const artistSchema = Yup.object().shape({
  name: Yup.string()
    .required('validation.artistName.required')
    .min(1, 'validation.artistName.minLength')
    .max(100, 'validation.artistName.maxLength'),
  bio: Yup.string()
    .optional()
    .max(2000, 'validation.bio.maxLengthLong'),
  artist_img_id: Yup.number().optional().nullable(),
});

export const albumSchema = Yup.object().shape({
  title: Yup.string()
    .required('validation.albumTitle.required')
    .min(1, 'validation.albumTitle.minLength')
    .max(100, 'validation.albumTitle.maxLength'),
  artist_id: Yup.number()
    .required('validation.artist.required')
    .positive('validation.artist.select'),
  release_date: Yup.date()
    .optional()
    .nullable()
    .max(new Date(), 'validation.releaseDate.future'),
  genre: Yup.string()
    .optional()
    .max(50, 'validation.genre.maxLength'),
  album_image_id: Yup.number().optional().nullable(),
});

export const songSchema = Yup.object().shape({
  title: Yup.string()
    .required('validation.songTitle.required')
    .min(1, 'validation.songTitle.minLength')
    .max(100, 'validation.songTitle.maxLength'),
  album_id: Yup.number()
    .required('validation.album.required')
    .positive('validation.album.select'),
  duration: Yup.string()
    .required('validation.duration.required')
    .matches(/^\d+:\d{2}$/, 'validation.duration.format'),
  trackNumber: Yup.number()
    .nullable()
    .positive('validation.trackNumber.positive')
    .max(999, 'validation.trackNumber.max'),
  song_image_id: Yup.number().optional().nullable(),
});

// ============================================================================
// Music Editor Forms (Integrated Moderator Form)
// ============================================================================

// Song schema for nested validation in music editor
export const modSongSchema = Yup.object().shape({
  id: Yup.number().optional().nullable(),
  title: Yup.string()
    .required('validation.songTitleRequired')
    .min(1, 'validation.songTitleLength')
    .max(100, 'validation.songTitleLength'),
  duration: Yup.string()
    .required('validation.songDurationRequired')
    .matches(/^(?:(?:([0-9]{1,2}):)?([0-5]?[0-9]):)?([0-5][0-9])$/, 'validation.songDurationFormat'),
  trackNumber: Yup.number()
    .optional()
    .nullable()
    .positive('validation.trackNumberPositive')
    .max(500, 'validation.trackNumberMax'),
  deleted: Yup.boolean().optional(),
});

// Album schema for nested validation in music editor
export const modAlbumSchema = Yup.object().shape({
  id: Yup.number().optional().nullable(),
  title: Yup.string()
    .required('validation.albumTitleRequired')
    .min(1, 'validation.albumTitleLength')
    .max(100, 'validation.albumTitleLength'),
  genre: Yup.string()
    .optional()
    .nullable()
    .max(50, 'validation.genreLength'),
  releaseDate: Yup.string().optional().nullable(),
  deleted: Yup.boolean().optional(),
  songs: Yup.array().of(modSongSchema).optional(),
});

// Artist schema for music editor with nested albums and songs
export const modArtistSchema = Yup.object().shape({
  id: Yup.number().optional().nullable(),
  name: Yup.string()
    .required('validation.nameRequired')
    .min(2, 'validation.nameLength')
    .max(50, 'validation.nameLength'),
  bio: Yup.string()
    .optional()
    .nullable()
    .max(2048, 'validation.bioLength'),
  albums: Yup.array().of(modAlbumSchema).optional(),
});

// Helper function to validate the music editor form and return formatted errors
export const validateMusicEditorForm = async (
  formData: any,
  t: (key: string) => string
): Promise<Record<string, string>> => {
  const errors: Record<string, string> = {};

  try {
    // Validate artist fields
    await modArtistSchema.validate(
      { 
        id: formData.id, 
        name: formData.name, 
        bio: formData.bio 
      },
      { abortEarly: false }
    );
  } catch (err) {
    if (err instanceof Yup.ValidationError) {
      err.inner.forEach((error) => {
        if (error.path) {
          const key = error.message.startsWith('validation.') 
            ? `moderator.${error.message}` 
            : error.message;
          errors[error.path] = t(key);
        }
      });
    }
  }

  // Validate each album and its songs
  if (formData.albums && Array.isArray(formData.albums)) {
    for (let albumIndex = 0; albumIndex < formData.albums.length; albumIndex++) {
      const album = formData.albums[albumIndex];
      if (album.deleted) continue;

      try {
        await modAlbumSchema.validate(
          { 
            id: album.id, 
            title: album.title, 
            genre: album.genre,
            releaseDate: album.releaseDate,
          },
          { abortEarly: false }
        );
      } catch (err) {
        if (err instanceof Yup.ValidationError) {
          err.inner.forEach((error) => {
            if (error.path) {
              const fieldName = error.path === 'title' ? `album_${albumIndex}_title` : `album_${albumIndex}_${error.path}`;
              const key = error.message.startsWith('validation.') 
                ? `moderator.${error.message}` 
                : error.message;
              errors[fieldName] = t(key);
            }
          });
        }
      }

      // Validate songs
      if (album.songs && Array.isArray(album.songs)) {
        for (let songIndex = 0; songIndex < album.songs.length; songIndex++) {
          const song = album.songs[songIndex];
          if (song.deleted) continue;

          try {
            await modSongSchema.validate(
              { 
                id: song.id, 
                title: song.title, 
                duration: song.duration,
                trackNumber: song.trackNumber,
              },
              { abortEarly: false }
            );
          } catch (err) {
            if (err instanceof Yup.ValidationError) {
              err.inner.forEach((error) => {
                if (error.path) {
                  const fieldName = `album_${albumIndex}_song_${songIndex}_${error.path}`;
                  const key = error.message.startsWith('validation.') 
                    ? `moderator.${error.message}` 
                    : error.message;
                  errors[fieldName] = t(key);
                }
              });
            }
          }
        }
      }
    }
  }

  return errors;
};

// ============================================================================
// Search Form
// ============================================================================

export const searchSchema = Yup.object().shape({
  query: Yup.string()
    .required('validation.search.required')
    .min(1, 'validation.search.minLength')
    .max(100, 'validation.search.maxLength'),
  type: Yup.string()
    .oneOf(['all', 'users', 'artists', 'albums', 'songs', 'reviews'], 'validation.search.invalidType'),
});

// ============================================================================
// Settings Forms
// ============================================================================

export const changePasswordSchema = Yup.object().shape({
  currentPassword: Yup.string()
    .required('validation.password.currentRequired'),
  newPassword: Yup.string()
    .required('validation.password.newRequired')
    .min(6, 'validation.password.minLength')
    .max(100, 'validation.password.maxLength'),
  confirmPassword: Yup.string()
    .required('validation.password.confirmNewRequired')
    .oneOf([Yup.ref('newPassword')], 'validation.password.mismatch'),
});

export const deleteAccountSchema = Yup.object().shape({
  password: Yup.string()
    .required('validation.password.requiredForDelete'),
  confirmation: Yup.string()
    .required('validation.deleteConfirmation.required')
    .oneOf(['DELETE'], 'validation.deleteConfirmation.invalid'),
});
