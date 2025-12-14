/**
 * Validation Schemas
 * Yup validation schemas for all forms in the application
 * 
 * IMPORTANT: All validation messages use i18n keys.
 * The keys follow the pattern: validation.<fieldType>.<validationType>
 * These keys must be present in all locale JSON files.
 */

import * as Yup from 'yup';
import { t } from 'i18next';

// ============================================================================
// Auth Forms
// ============================================================================

export const loginSchema = Yup.object().shape({
  username: Yup.string()
    .required(t('validation.username.required'))
    .min(3, t('validation.username.minLength'))
    .max(50, t('validation.username.maxLength')),
  password: Yup.string()
    .required(t('validation.password.required'))
    .min(6, t('validation.password.minLength')),
  rememberMe: Yup.boolean(),
});

export const registerSchema = Yup.object().shape({
  username: Yup.string()
    .required(t('validation.username.required'))
    .min(4, t('validation.username.minLengthRegister'))
    .max(50, t('validation.username.maxLength'))
    .matches(/^[a-zA-Z][a-zA-Z0-9]*$/, t('validation.username.format')),
  email: Yup.string()
    .required(t('validation.email.required'))
    .email(t('validation.email.invalid'))
    .max(100, t('validation.email.maxLength')),
  password: Yup.string()
    .required(t('validation.password.required'))
    .min(8, t('validation.password.minLengthRegister'))
    .max(100, t('validation.password.maxLength')),
  repeatPassword: Yup.string()
    .required(t('validation.password.confirmRequired'))
    .min(8, t('validation.password.minLengthRegister'))
    .oneOf([Yup.ref('password')], t('validation.password.mismatch')),
});

export const forgotPasswordSchema = Yup.object().shape({
  email: Yup.string()
    .required(t('validation.email.required'))
    .email(t('validation.email.invalid')),
});

export const resetPasswordSchema = Yup.object().shape({
  password: Yup.string()
    .required(t('validation.password.required'))
    .min(6, t('validation.password.minLength'))
    .max(100, t('validation.password.maxLength')),
  repeatPassword: Yup.string()
    .required(t('validation.password.confirmRequired'))
    .oneOf([Yup.ref('password')], t('validation.password.mismatch')),
});

// ============================================================================
// User Forms
// ============================================================================

export const editProfileSchema = Yup.object().shape({
  username: Yup.string()
    .optional()
    .min(3, t('validation.username.minLength'))
    .max(50, t('validation.username.maxLength'))
    .matches(/^[a-zA-Z0-9_-]+$/, t('validation.username.formatProfile')),
  name: Yup.string()
    .optional()
    .max(100, t('validation.name.maxLength')),
  bio: Yup.string()
    .optional()
    .max(500, t('validation.bio.maxLength')),
  profilePicture: Yup.mixed().optional().nullable(),
});

// ============================================================================
// Review Forms
// ============================================================================

export const reviewSchema = Yup.object().shape({
  title: Yup.string()
    .required(t('validation.title.required'))
    .min(3, t('validation.title.minLength'))
    .max(100, t('validation.title.maxLength')),
  description: Yup.string()
    .required(t('validation.description.required'))
    .min(10, t('validation.description.minLength'))
    .max(2000, t('validation.description.maxLength')),
  rating: Yup.number()
    .required(t('validation.rating.required'))
    .min(1, t('validation.rating.min'))
    .max(5, t('validation.rating.max'))
});

// ============================================================================
// Comment Forms
// ============================================================================

export const commentSchema = Yup.object().shape({
  content: Yup.string()
    .required(t('validation.comment.required'))
    .min(2, t('validation.comment.minLength'))
    .max(500, t('validation.comment.maxLength')),
  review_id: Yup.number().required(),
});

// ============================================================================
// Music Forms (Moderator)
// ============================================================================

export const artistSchema = Yup.object().shape({
  name: Yup.string()
    .required(t('validation.artistName.required'))
    .min(1, t('validation.artistName.minLength'))
    .max(100, t('validation.artistName.maxLength')),
  bio: Yup.string()
    .optional()
    .max(2000, t('validation.bio.maxLengthLong')),
  artist_img_id: Yup.number().optional().nullable(),
});

export const albumSchema = Yup.object().shape({
  title: Yup.string()
    .required(t('validation.albumTitle.required'))
    .min(1, t('validation.albumTitle.minLength'))
    .max(100, t('validation.albumTitle.maxLength')),
  artist_id: Yup.number()
    .required(t('validation.artist.required'))
    .positive(t('validation.artist.select')),
  release_date: Yup.date()
    .optional()
    .nullable()
    .max(new Date(), t('validation.releaseDate.future')),
  genre: Yup.string()
    .optional()
    .max(50, t('validation.genre.maxLength')),
  album_image_id: Yup.number().optional().nullable(),
});

export const songSchema = Yup.object().shape({
  title: Yup.string()
    .required(t('validation.songTitle.required'))
    .min(1, t('validation.songTitle.minLength'))
    .max(100, t('validation.songTitle.maxLength')),
  album_id: Yup.number()
    .required(t('validation.album.required'))
    .positive(t('validation.album.select')),
  duration: Yup.string()
    .required(t('validation.duration.required'))
    .matches(/^\d+:\d{2}$/, t('validation.duration.format')),
  trackNumber: Yup.number()
    .nullable()
    .positive(t('validation.trackNumber.positive'))
    .max(999, t('validation.trackNumber.max')),
  song_image_id: Yup.number().optional().nullable(),
});

// ============================================================================
// Music Editor Forms (Integrated Moderator Form)
// ============================================================================

// Song schema for nested validation in music editor
export const modSongSchema = Yup.object().shape({
  id: Yup.number().optional().nullable(),
  title: Yup.string()
    .required(t('validation.songTitleRequired'))
    .min(1, t('validation.songTitleLength'))
    .max(100, t('validation.songTitleLength')),
  duration: Yup.string()
    .required(t('validation.songDurationRequired'))
    .matches(/^(?:(?:([0-9]{1,2}):)?([0-5]?[0-9]):)?([0-5][0-9])$/, t('validation.songDurationFormat')),
  trackNumber: Yup.number()
    .optional()
    .nullable()
    .positive(t('validation.trackNumberPositive'))
    .max(500, t('validation.trackNumberMax')),
  deleted: Yup.boolean().optional(),
});

// Album schema for nested validation in music editor
export const modAlbumSchema = Yup.object().shape({
  id: Yup.number().optional().nullable(),
  title: Yup.string()
    .required(t('validation.albumTitleRequired'))
    .min(1, t('validation.albumTitleLength'))
    .max(100, t('validation.albumTitleLength')),
  genre: Yup.string()
    .required(t('validation.genre.required'))
    .max(50, t('validation.genreLength')),
  releaseDate: Yup.string()
    .required(t('validation.releaseDate.required'))
    .matches(/^\d{4}-\d{2}-\d{2}$/, t('validation.releaseDate.required')),
  deleted: Yup.boolean().optional(),
  songs: Yup.array().of(modSongSchema).optional(),
});

// Artist schema for music editor with nested albums and songs
export const modArtistSchema = Yup.object().shape({
  id: Yup.number().optional().nullable(),
  name: Yup.string()
    .required(t('validation.nameRequired'))
    .min(2, t('validation.nameLength'))
    .max(50, t('validation.nameLength')),
  bio: Yup.string()
    .optional()
    .nullable()
    .max(2048, t('validation.bioLength')),
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
    .required(t('validation.search.required'))
    .min(1, t('validation.search.minLength'))
    .max(100, t('validation.search.maxLength')),
  type: Yup.string()
    .oneOf(['all', 'users', 'artists', 'albums', 'songs', 'reviews'], t('validation.search.invalidType')),
});

// ============================================================================
// Settings Forms
// ============================================================================

export const changePasswordSchema = Yup.object().shape({
  currentPassword: Yup.string()
    .required(t('validation.password.currentRequired')),
  newPassword: Yup.string()
    .required(t('validation.password.newRequired'))
    .min(6, t('validation.password.minLength'))
    .max(100, t('validation.password.maxLength')),
  confirmPassword: Yup.string()
    .required(t('validation.password.confirmNewRequired'))
    .oneOf([Yup.ref('newPassword')], t('validation.password.mismatch')),
});

export const deleteAccountSchema = Yup.object().shape({
  password: Yup.string()
    .required(t('validation.password.requiredForDelete')),
  confirmation: Yup.string()
    .required(t('validation.deleteConfirmation.required'))
    .oneOf(['DELETE'], t('validation.deleteConfirmation.invalid')),
});
