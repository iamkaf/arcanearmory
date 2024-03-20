export interface MaterialDatabase {
  materials: Material[];
}

export interface Material {
  name: string;
  type: string;
  composite?: Composite[];
  tint?: Tint[];
  modulate?: Modulate[];
  overrides?: MaterialOverrides[];
}

export interface Composite {
  item_type: ItemType | 'all';
  base_texture?: CompositeTexture[];
  overlay_texture?: CompositeTexture[];
}

export interface CompositeTexture {
  texture: string;
  tint?: Omit<Tint, 'item_type' | 'shift_base' | 'shift_overlay'>;
  modulate?: Modulate['options'];
}

export interface Tint {
  item_type: ItemType | 'all';
  r: number;
  g: number;
  b: number;
  shift_base: boolean;
  shift_overlay: boolean;
}

export interface Modulate {
  item_type: ItemType | 'all';
  options: {
    brightness: number;
    hue: number;
    saturation: number;
    lightness: number;
  };
  modulate_base: boolean;
  modulate_overlay: boolean;
}

export interface MaterialOverrides {
  item_type: ItemType;
  variant_texture: string;
}

export type ItemType =
  | 'helmet'
  | 'chestplate'
  | 'leggings'
  | 'boots'
  | 'sword'
  | 'pickaxe'
  | 'axe'
  | 'shovel'
  | 'hoe'
  | 'hammer'
  | 'layer_1'
  | 'layer_2'
  | 'ingot'
  | 'nugget'
  | 'raw'
  | 'block'
  | 'raw_block'
  | 'ore'
  | 'deepslate_ore';
