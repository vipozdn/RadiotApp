## Radiot app with comments

Android app for Radio-T podcast with comments support. A project in development at the moment.
Author create the project for learn new technologies.

### App has following feature:

- Podcast and themes lists
- Comments list with reply
- Votes
- Post new comment
- Auth (github, twitter)

<a href='https://play.google.com/store/apps/details?id=com.stelmashchuk.radio_t&pcampaignid=pcampaignidMKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1'><img alt='Доступно в Google Play' src='https://play.google.com/intl/en_us/badges/static/images/badges/ru_badge_web_generic.png'/></a>

### Technologies:

- UI: Jetpack compose
- UI pattern: MVVM.
- Threading: coroutine
- Linter: [detekt](https://github.com/detekt/detekt)
- Testing: Junit and [mockk](https://github.com/mockk/mockk)
- CI: Github actions (run test, static code analysis, release app to google play)

### Project structure

#### radiot-app

Main product of this repo. App for radiot podcast. **Don't use this app for work with remark api.**

#### demo-app

App for develop and testing remark sdk.

#### remark

##### remark-api

Wrapper for remark api [remark42](https://github.com/umputun/remark42)
Provide useful interface with caching data from BE, and possibility for observable actual data.
Handle data change after new comment and vote. Also provide middleware for work with BE apply
query `site` for each request. Doesn't execute calls which required auth without user data.

Init remark api

````kotlin
val remarkSettings = RemarkSettings("remark", "https://demo.remark42.com/")
val api: RemarkApi by lazy { RemarkApi(context, remarkSettings.siteId, remarkSettings.baseUrl) }
````

RemarkApi:

- `val commentDataControllerProvider: CommentDataControllerProvider`
- `fun saveByCookies(cookies: String): Boolean`
- `fun addLoginStateListener(onLoginChange: (RemarkCredentials) -> Unit)`
- `suspend fun getConfig(): Config`

CommentDataControllerProvider:

- `fun getDataController(postUrl: String): CommentDataController`

CommentDataController:

- `suspend fun observeComments(commentRoot: CommentRoot): Flow<FullCommentInfo>`
- `suspend fun vote(commentId: String, vote: VoteType): RemarkError?`
- `suspend fun postComment(commentRoot: CommentRoot, text: String): RemarkError?`

##### Remark sdk

Software development kit for integrate comment widget into your app.

Init remark sdk

```kotlin
RemarkComponent.init(applicationContext, RemarkSettings("remark", "https://demo.remark42.com/"))
```

Add comment widget:

Just call RemarkView compose fun into your compose fun.

```kotlin
RemarkView(postUrl = "https://remark42.com/demo/")
```
