# Recipe Assistant

An Android app that pulls recipes from [TheMealDb](https://www.themealdb.com/), and can read out the ingredients and directions using Android TextToSpeech. The reading can be voice activated, but I am currently refactoring the app, and speech recognition is disabled. Speech recognition is implemented using [PocketSphinx](https://github.com/cmusphinx/pocketsphinx).

![Screenshot](./.github/screenshot1.png?raw=true)&nbsp;&nbsp;&nbsp;&nbsp;![Screenshot](./.github/screenshot2.png?raw=true)

## Built With

* [Kotlin](https://kotlinlang.org/) - Kotlin programing language
* [Android Architecture Components](https://developer.android.com/topic/libraries/architecture/index.html)
* [RxJava](https://github.com/ReactiveX/RxJava) - Reactive Extensions for the JVM
* [RxKotlin](https://github.com/ReactiveX/RxKotlin) - Kotlin Extensions for RxJava
* [Picasso](http://square.github.io/picasso/) - Image downloading and caching
* [Retrofit](http://square.github.io/retrofit/) - Type-safe HTTP client
