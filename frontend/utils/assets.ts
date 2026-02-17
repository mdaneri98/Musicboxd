const getBasePath = (): string => {
  const isProd = process.env.NODE_ENV === 'production';
  return isProd ? '/paw-2024b-02' : '';
};

import defaultAvatar from '../assets/default-avatar.png';
import imagePlaceholder from '../assets/image-placeholder.png';
import logo from '../assets/logo.png';
import reviewIcon from '../assets/reviewIcon.png';
import communityIcon from '../assets/communityIcon.png';
import discoverIcon from '../assets/discoverIcon.png';

export const getAssetUrl = (assetPath: string): string => {
  const basePath = getBasePath();
  return `${basePath}${assetPath}`;
};

export const ASSETS = {
  DEFAULT_AVATAR: defaultAvatar.src,
  IMAGE_PLACEHOLDER: imagePlaceholder.src,
  LOGO: logo.src,
  REVIEW_ICON: reviewIcon.src,
  COMMUNITY_ICON: communityIcon.src,
  DISCOVER_ICON: discoverIcon.src,
} as const;
