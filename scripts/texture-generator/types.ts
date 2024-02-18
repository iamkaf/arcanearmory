export interface MaterialDatabase {
  materials: Material[];
}

export interface Material {
  name: string;
  type: string;
  hue_shift: HueShift[];
  overrides?: MaterialOverrides[];
}

export interface HueShift {
  item_type: ItemType | 'all';
  amount: number;
  shift_base: boolean;
  shift_overlay: boolean;
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
  | 'layer_1'
  | 'layer_2'
  | 'ingot'
  | 'nugget'
  | 'raw'
  | 'block'
  | 'raw_block'
  | 'ore';
