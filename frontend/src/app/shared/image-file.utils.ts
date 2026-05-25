const ALLOWED_IMAGE_TYPES = new Set(['image/jpeg', 'image/png', 'image/webp']);
const MAX_IMAGE_BYTES = 5 * 1024 * 1024;

export function validateImageFile(file: File): string | null {
  if (!ALLOWED_IMAGE_TYPES.has(file.type)) {
    return 'Formato permitido: jpg, jpeg, png o webp';
  }

  if (file.size > MAX_IMAGE_BYTES) {
    return 'La imagen no puede superar 5MB';
  }

  return null;
}

export function revokeObjectUrl(url?: string): void {
  if (url?.startsWith('blob:')) {
    URL.revokeObjectURL(url);
  }
}
