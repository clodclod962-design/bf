# MyMod (Fabric 1.20.1, client-side)

Right Shift - меню. ЛКМ - вкл/выкл модуль, ПКМ - настройки, СКМ - назначить клавишу (ESC - снять), колесо - прокрутка.
Настройки и бинды: .minecraft/config/mymod.json

Модули:
- Combat: AutoTotem(Z), AutoPearl(X), KillAura(R, +TargetStrafe, Criticals), Velocity(V)
- Movement: Sprint, Flight(G), Speed, NoFall, Spider, AutoWalk, AutoJump, AirJump, Step, Sneak, Glide
- Player: FastPlace, AutoRespawn, NoHunger, AutoEat, AutoArmor, AutoLog
- Render: Fullbright(B), ESP(N), StorageESP(J), Tracers, Xray(H), BlockOutline
- Effects: ChinaHat(K, неоновая шляпа), Particles, HitEffects, Ambience (небо/время/погода)
- Misc: HUD

Сборка: `gradle build` или GitHub Actions (.github/workflows/build.yml) -> Artifacts -> mymod-jar. Нужен Fabric API 0.92.2+1.20.1.
Xray не работает вместе с Sodium/Embeddium/Iris.

## Новые модули
- AutoHeal - ест золотое яблоко из хотбара при низком HP.
- CommandBinds - команды на клавиши/по таймеру, файл `config/mymod-commands.txt` (`KEY_R:/home`, `EVERY 300:/spawn`).
- AutoLogin - /login (или /register) при входе, пароль в `config/mymod-login.txt` (только локально, не коммитить).

## Кастомные предметы (Inferno)
Inferno Blade + броня (шлем, нагрудник, поножи, ботинки), вкладка Combat в креативе. Выдача: `/give @s mymod:inferno_sword`.
Рецептов нет - выдаются командой/креативом. ВАЖНО: теперь мод должен стоять и на клиенте, и на сервере (Fabric), иначе предметов не будет.
