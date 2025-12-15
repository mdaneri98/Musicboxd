const getBasePath = (): string => {
  const isProd = process.env.NODE_ENV === 'production';
  return isProd ? '/paw-2024b-02' : '';
};


export const getAssetUrl = (assetPath: string): string => {
  const basePath = getBasePath();
  return `${basePath}${assetPath}`;
};

export const ASSETS = {
  DEFAULT_AVATAR: getAssetUrl('/assets/default-avatar.png'),
  IMAGE_PLACEHOLDER: getAssetUrl('/assets/image-placeholder.png'),
  LOGO: getAssetUrl('/logo.png'),
  REVIEW_ICON: getAssetUrl('/assets/reviewIcon.png'),
  COMMUNITY_ICON: getAssetUrl('/assets/communityIcon.png'),
  DISCOVER_ICON: getAssetUrl('/assets/discoverIcon.png'),
} as const;
