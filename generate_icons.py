#!/usr/bin/env python3
"""
生成 24 Points 应用的 mipmap 图标
设计：2和4左右排列，部分重叠，4压着2，透明底色+蓝色字体
"""

from PIL import Image, ImageDraw, ImageFont
import os

# 颜色定义
BLUE = (33, 150, 243)       # #2196F3 Material Blue
TRANSPARENT = (0, 0, 0, 0)  # 透明

# mipmap 尺寸配置
MIPMAP_SIZES = {
    'mdpi': 48,
    'hdpi': 72,
    'xhdpi': 96,
    'xxhdpi': 144,
    'xxxhdpi': 192,
}

def create_font(size):
    """尝试加载系统字体"""
    font_paths = [
        "C:/Windows/Fonts/arialbd.ttf",
        "C:/Windows/Fonts/seguisb.ttf",
        "C:/Windows/Fonts/arial.ttf",
        "/System/Library/Fonts/Helvetica.ttc",
        "/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf",
    ]
    for path in font_paths:
        if os.path.exists(path):
            try:
                return ImageFont.truetype(path, size)
            except:
                continue
    return ImageFont.load_default()

def get_text_metrics(draw, text, font):
    """获取文字的精确尺寸"""
    bbox = draw.textbbox((0, 0), text, font=font)
    width = bbox[2] - bbox[0]
    height = bbox[3] - bbox[1]
    offset_x = -bbox[0]
    offset_y = -bbox[1]
    return width, height, offset_x, offset_y

def create_launcher_icon(size):
    """创建图标 - 2和4左右重叠，居中，高度占80%"""
    img = Image.new('RGBA', (size, size), TRANSPARENT)
    draw = ImageDraw.Draw(img)

    target_ratio = 0.80  # 目标覆盖率

    # 初始大字体
    font_size = int(size * 1.2)
    font = create_font(font_size)

    w2, h2, ox2, oy2 = get_text_metrics(draw, "2", font)
    w4, h4, ox4, oy4 = get_text_metrics(draw, "4", font)

    # 重叠量（较小）
    overlap = int(max(w2, w4) * 0.10)

    total_width = w2 + w4 - overlap
    max_height = max(h2, h4)

    # 以高度为基准调整（确保高度达到 80%）
    scale = (size * target_ratio) / max_height
    font_size = int(font_size * scale)
    font = create_font(font_size)

    # 重新获取尺寸
    w2, h2, ox2, oy2 = get_text_metrics(draw, "2", font)
    w4, h4, ox4, oy4 = get_text_metrics(draw, "4", font)

    overlap = int(max(w2, w4) * 0.10)
    total_width = w2 + w4 - overlap
    max_height = max(h2, h4)

    center_x = size // 2
    center_y = size // 2
    start_x = center_x - total_width // 2

    # 绘制 2（下层）
    x2 = start_x + ox2
    y2 = center_y - h2 // 2 + oy2
    draw.text((x2, y2), "2", fill=BLUE, font=font)

    # 绘制 4（上层，压着2）
    x4 = start_x + w2 - overlap + ox4
    y4 = center_y - h4 // 2 + oy4
    draw.text((x4, y4), "4", fill=BLUE, font=font)

    return img

def create_round_icon(size):
    """创建圆形图标"""
    img = create_launcher_icon(size)

    mask = Image.new('L', (size, size), 0)
    mask_draw = ImageDraw.Draw(mask)
    mask_draw.ellipse([0, 0, size - 1, size - 1], fill=255)

    result = Image.new('RGBA', (size, size), TRANSPARENT)
    result.paste(img, mask=mask)

    return result

def main():
    base_path = os.path.dirname(os.path.abspath(__file__))
    res_path = os.path.join(base_path, 'app', 'src', 'main', 'res')

    print("Generating icons: 2 and 4 overlapping, centered, 80% size")
    print(f"Color: Blue (#2196F3), Background: Transparent\n")

    for density, size in MIPMAP_SIZES.items():
        mipmap_dir = os.path.join(res_path, f'mipmap-{density}')
        os.makedirs(mipmap_dir, exist_ok=True)

        icon = create_launcher_icon(size)
        icon.save(os.path.join(mipmap_dir, 'ic_launcher.webp'), 'webp', quality=95)
        print(f'Generated {density}/ic_launcher.webp ({size}x{size})')

        round_icon = create_round_icon(size)
        round_icon.save(os.path.join(mipmap_dir, 'ic_launcher_round.webp'), 'webp', quality=95)
        print(f'Generated {density}/ic_launcher_round.webp ({size}x{size})')

    print('\nAll icons generated successfully!')

if __name__ == '__main__':
    main()
