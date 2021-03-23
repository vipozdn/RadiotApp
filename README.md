## Radiot app

Android приложение для подкаста Radio-t. На данный момент еще в разработке. Данный проект сделан автором с целю изучить новые технологии поэтому не следует ожидать от приложения быстрого развития.

### Технологии:

- UI: Jetpack compose
- UI pattern: MVVM.
- Threading: coroutine
- Linter: detekt
- Testing: Junit and mockk
- CI: Github actions

### RoadMap

[x] просмотр списка подкастов
[x] просмотр комментариев
[x] Голосование за комментарии
[x] Авторизация через github
[ ] Добавить картинки
[ ] Поддержка markdown
[ ] Поправить авторизцию через другие соц. сети
[ ] Добавление комментариев
[ ] Удаление комментариев
[ ] Редактирование комментариев
[ ] Обработка ошибок

### Структура проекта

Проект состоит из двух частей:

- app - приложение
- remark - внутренняя библиотека для системы комментариев [remark42](https://github.com/umputun/remark42)

## Beta

На данный момент приложения еще нет в релизе но вы можете его поставить как внутренний тестер
по [ссылке](https://play.google.com/apps/internaltest/4700474952294733221), плюс вы все можете
собрать проект сами.

### Помощь проекту

Я, как автор проекта, будет рад любым MR или найденным багам.