## РадиоТ с комментариями

Android приложение для подкаста Radio-t с поддержкой комментариев. На данный момент еще в разработке. Данный проект сделан автором с целю изучить новые технологии поэтому не следует ожидать от приложения быстрого развития.

<a href='https://play.google.com/store/apps/details?id=com.stelmashchuk.radio_t&pcampaignid=pcampaignidMKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1'><img alt='Доступно в Google Play' src='https://play.google.com/intl/en_us/badges/static/images/badges/ru_badge_web_generic.png'/></a>

### Технологии:

- UI: Jetpack compose
- UI pattern: MVVM.
- Threading: coroutine
- Linter: [detekt](https://github.com/detekt/detekt)
- Testing: Junit and [mockk](https://github.com/mockk/mockk)
- CI: Github actions (run test, static code analysis, release app to google play)

### RoadMap

- [x] просмотр списка подкастов
- [x] просмотр комментариев
- [x] Голосование за комментарии
- [x] Авторизация через github
- [ ] Добавить картинки
- [x] Поддержка markdown
- [ ] Поправить авторизцию через Google
- [ ] Добавление комментариев
- [ ] Удаление комментариев
- [ ] Редактирование комментариев
- [x] Обработка ошибок

### Структура проекта

Проект состоит из двух частей:

- app - приложение
- remark - внутренняя библиотека для системы комментариев [remark42](https://github.com/umputun/remark42)

### Помощь проекту

Автор проекта будет рад любым MR или найденным багам.
