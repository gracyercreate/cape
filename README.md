# Capes and Essence

Мод для Minecraft **1.21.11** (Fabric, Java 21).

- Отключает все плащи Mojang (и их элитры) у всех игроков на клиенте.
- Добавляет плащ **Dragon's Legacy** (Rarity: RARE). Текстура плаща применяется и к элитрам.
- Получение: 100% с Дракона Края, 0.25% в каждом сундуке структур Края (города и корабли Края).
- ПКМ с плащом в руке - надеть, Shift+ПКМ - снять.

## Сборка

GitHub: вкладка **Actions** -> workflow `build` (запускается на каждый push и вручную).
Готовый `.jar` лежит в артефактах запуска (`capesandessence`).

Локально:

```
./gradlew build
```

Результат: `build/libs/capesandessence-1.0.0.jar`. Нужен Fabric Loader и Fabric API.
