```yaml
menu:
  animation:
    enabled: true              # Включает или отключает анимацию при открытии меню (true - анимация включена, false - анимация отключена)
    type: SPIRAL             # Тип анимации: "fill_glass" (последовательное заполнение), "wave" (волновое заполнение), "random_fill" (случайное заполнение), spiral
    direction: FORWARD      # Направление анимации: "forward" (слева направо, сверху вниз), "backward" (справа налево, снизу вверх), "random" (случайный порядок)
    material: "LIGHT_BLUE_STAINED_GLASS_PANE"  # Материал для заполнения пустых слотов (например, "LIGHT_BLUE_STAINED_GLASS_PANE", "GRAY_STAINED_GLASS_PANE")
    speed: 3                # Задержка между кадрами анимации в тиках (20 тиков = 1 секунда, минимальное значение: 1)
    sound: BLOCK_NOTE_BLOCK_BANJO # Звук, который проигрывается при заполнении каждого слота (например, "BLOCK_NOTE_BLOCK_BANJO", "BLOCK_ANVIL_LAND")
    sound_volume: 1.0
    sound_pitch: 1.2
    batch_size: 3             # Количество слотов, обрабатываемых за один тик (чем больше, тем быстрее анимация, но выше нагрузка; рекомендуемое значение: 1-5)
    autoUpdate: false         # Включает или отключает автоматическое обновление меню каждые 60 тиков (true - включено, false - отключено)

```
