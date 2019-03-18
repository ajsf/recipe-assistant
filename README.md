# Recipe Assistant

An Android app that pulls recipes from [TheMealDb](https://www.themealdb.com/), and can read the recipes out loud, triggered by buttons or voice commands. The reading is implemented using Android [TextToSpeech](https://developer.android.com/reference/android/speech/tts/TextToSpeech) and voice recognition is implemented using [PocketSphinx](https://github.com/cmusphinx/pocketsphinx).

![Screenshot](./.github/screenshot1.png?raw=true)&nbsp;&nbsp;&nbsp;&nbsp;![Screenshot](./.github/screenshot2.png?raw=true)

## Instructions

Voice commands are available once a recipe has been selected. To activate voice commands, click the mic icon in the upper right corner. The following voice commands are currently understood:

* "Play Ingredient" - Plays the currently selected ingredient.
* "Next Ingredient" - Selects and plays the next ingredient. Stays on the same ingredient if it is the last.
* "Previous Ingredient" - Selects and plays the previous ingredient. Stays on the same ingredient if it is the first.
* "Play Direction"- Plays the currently selected direction.
* "Next Direction" - Selects and plays the next direction. Stays on the same direction if it is the last.
* "Previous Direction" - Selects and plays the previous direction. Stays on the same direction if it is the first.
* "Stop" - Stops whatever is currently being read.

The voice recognition is still a work in progress. It needs some fine tuning and does not always pick up commands.

## Built With

* [Kotlin](https://kotlinlang.org/) - Kotlin programing language
* [Android Architecture Components](https://developer.android.com/topic/libraries/architecture/index.html)
* [RxJava](https://github.com/ReactiveX/RxJava) - Reactive Extensions for the JVM
* [RxKotlin](https://github.com/ReactiveX/RxKotlin) - Kotlin Extensions for RxJava
* [Picasso](http://square.github.io/picasso/) - Image downloading and caching
* [Retrofit](http://square.github.io/retrofit/) - Type-safe HTTP client
* [PocketSphinx](https://github.com/cmusphinx/pocketsphinx) - Voice recognition