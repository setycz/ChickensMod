#!/usr/bin/env python3
"""Check custom model textures exist inside Minecraft's standard atlas directories."""
from pathlib import Path
import json

root = Path(__file__).resolve().parents[1] / 'src/main/resources/assets/roost'
errors = []
count = 0
for path in (root / 'models').rglob('*.json'):
    data = json.loads(path.read_text())
    for name, texture in data.get('textures', {}).items():
        if not texture.startswith('roost:'):
            continue
        relative = texture.split(':', 1)[1]
        if relative.split('/', 1)[0] not in ('item', 'block'):
            errors.append(f'{path.relative_to(root)}: {texture} is outside the item/block atlas directories')
        if not (root / 'textures' / (relative + '.png')).is_file():
            errors.append(f'{path.relative_to(root)}: missing {texture}')
        count += 1
    parent = data.get('parent', '')
    if parent.startswith('roost:') and not (root / 'models' / (parent.split(':', 1)[1] + '.json')).is_file():
        errors.append(f'{path.relative_to(root)}: missing parent {parent}')
for path in (root / 'items').glob('*.json'):
    model = json.loads(path.read_text()).get('model', {}).get('model', '')
    if model.startswith('roost:') and not (root / 'models' / (model.split(':', 1)[1] + '.json')).is_file():
        errors.append(f'{path.relative_to(root)}: missing item model {model}')
if errors:
    raise SystemExit('\n'.join(errors))
print(f'Checked {count} custom texture references: files exist and atlas paths are valid.')
