#!/usr/bin/env python3
"""
生成 24 Points 应用的 mipmap 图标
设计：2和4左右排列，部分重叠，4压着2，透明底色+蓝色字体
覆盖率约 77% x 54%
"""

from PIL import Image, ImageDraw, ImageFont
import os

BLUE = (33, 150, 243)       # #2196F3
TRANSPARENT = (0, 0, 0, 0)

MIPMAP_SIZES = {
    'mdpi': 48, 'hdpi': 72, 'xhdpi': 96,
    'xxhdpi': 144, 'xxxhdpi': 192,
}

def create_font(size):
    font_paths = [
        "C:/Windows/Fonts/arialbd.ttf",
        "C:/Windows/Fonts/seguisb.ttf",
        "C:/Windows/Fonts/arial.ttf",
    ]
    for path in font_paths:
        if os.path.exists(path):
            try:
                return ImageFont.truetype(path, size)
            except:
                continue
    return ImageFont.load_default()

def get_text_metrics(draw, text, font):
    bbox = draw.textbbox((0, 0), text, font=font)
    return bbox[2] - bbox[0], bbox[3] - bbox[1], -bbox[0], -bbox[1]

def create_launcher_icon(size):
    img = Image.new('RGBA', (size, size), TRANSPARENT)
    draw = ImageDraw.Draw(img)

    target_ratio = 0.80

    font_size = int(size * 1.2)
    font = create_font(font_size)

    w2, h2, ox2, oy2 = get_text_metrics(draw, "2", font)
    w4, h4, ox4, oy4 = get_text_metrics(draw, "4", font)

    overlap = int(max(w2, w4) * 0.12)
    total_width = w2 + w4 - overlap
    max_height = max(h2, h4)

    # 取宽高中较小的缩放比例，确保两个维度都不超过 80%
    scale = min((size * target_ratio) / total_width, (size * target_ratio) / max_height)
    font_size = int(font_size * scale)
    font = create_font(font_size)

    w2, h2, ox2, oy2 = get_text_metrics(draw, "2", font)
    w4, h4, ox4, oy4 = get_text_metrics(draw, "4", font)

    overlap = int(max(w2, w4) * 0.12)
    total_width = w2 + w4 - overlap

    center_x, center_y = size // 2, size // 2
    start_x = center_x - total_width // 2

    draw.text((start_x + ox2, center_y - h2 // 2 + oy2), "2", fill=BLUE, font=font)
    draw.text((start_x + w2 - overlap + ox4, center_y - h4 // 2 + oy4), "4", fill=BLUE, font=font)

    return img

def create_round_icon(size):
    img = create_launcher_icon(size)
    mask = Image.new('L', (size, size), 0)
    ImageDraw.Draw(mask).ellipse([0, 0, size - 1, size - 1], fill=255)
    result = Image.new('RGBA', (size, size), TRANSPARENT)
    result.paste(img, mask=mask)
    return result

def main():
    base_path = os.path.dirname(os.path.abspath(__file__))
    res_path = os.path.join(base_path, 'app', 'src', 'main', 'res')

    print("Generating icons: 2+4 overlapping, centered, ~77%x54%")

    for density, size in MIPMAP_SIZES.items():
        mipmap_dir = os.path.join(res_path, f'mipmap-{density}')
        os.makedirs(mipmap_dir, exist_ok=True)

        create_launcher_icon(size).save(os.path.join(mipmap_dir, 'ic_launcher.webp'), 'webp', quality=95)
        create_round_icon(size).save(os.path.join(mipmap_dir, 'ic_launcher_round.webp'), 'webp', quality=95)
        print(f'  {density} ({size}x{size})')

    print('Done!')

if __name__ == '__main__':
    main()
