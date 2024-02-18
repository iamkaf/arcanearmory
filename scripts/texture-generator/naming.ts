
export function makeFileName(material, itemType, isOverlay) {
  const ITEMS_THAT_SKIP_OVERLAY = ['ingot', 'gem', 'nugget', 'block', 'raw_block', 'ore'];
  const ITEMS_WHOSE_NAME_STANDS_SLONE = ['gem'];

  const overlaySuffix = isOverlay ? '_overlay' : '';

  if (isOverlay && ITEMS_THAT_SKIP_OVERLAY.includes(itemType)) {
    return null;
  }

  let adjustedItemTypeDisplay = itemType;

  if (material.type === 'gem' && itemType === 'ingot') {
    adjustedItemTypeDisplay = 'gem';
  }

  if (ITEMS_WHOSE_NAME_STANDS_SLONE.includes(adjustedItemTypeDisplay) && itemType === 'ingot') {
    return `${material.name}${overlaySuffix}.png`;
  }

  if (itemType === 'raw') {
    return `raw_${material.name}${overlaySuffix}.png`;
  }

  if (itemType === 'raw_block') {
    return `raw_${material.name}_block.png`;
  }

  return `${material.name}_${adjustedItemTypeDisplay}${overlaySuffix}.png`;
}
