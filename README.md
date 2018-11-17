# FiveThingsKotlin
[![codebeat badge](https://codebeat.co/badges/3beb40b4-4805-4753-bb9e-7c5991c2d730)](https://codebeat.co/projects/github-com-alisonthemonster-fivethingskotlin-master) [![CircleCI](https://circleci.com/gh/alisonthemonster/FiveThingsKotlin/tree/master.svg?style=svg)](https://circleci.com/gh/alisonthemonster/FiveThingsKotlin/tree/master)

An app to track your quickly track, review, and reflect on your days.
  - Write about your day in five quick notes
  - Search through your past notes
  - Notifications reminders
  - Light and dark modes
  - Chart your happiness over time [IN PROGRESS]
  - Find your happiest/angriest/saddest days [IN PROGRESS]
  - Turn your digital entries into physical books with a design you can fully customize [IN PROGRESS]

### Technology + Frameworks Used:
- MVVM pattern using Livedata + Databinding + RxJava
- Jetpack paging library
- Retrofit + OKHTTP
- KotlinTest for unit testing
- Espresso for instrumented tests
- MockWebServer for instrumented tests
- ~~Travis CI~~ Circle CI
- Fastlane + HockeyApp for CICD
- Crashlytics + Firebase for monitoring and analytics
- CompactCalendarView
- Custom animated vector drawables


### Screenshots


<img src="https://i.imgur.com/ZveoZNN.png" width="250"> <img src="https://i.imgur.com/p5suUk7.png" width="250"> <img src="https://i.imgur.com/YCPxu9J.png" width="250"> <img src="https://i.imgur.com/ODxc8v4.png" width="250"> <img src="https://i.imgur.com/ul9OHGg.png" width="250">



*If you clone this repo the app will not build for a few reasons: You'll need my `google-services.json` for firebase and you'll be missing some font assets I didn't want to upload to github.*
