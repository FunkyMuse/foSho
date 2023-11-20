<p align="center"> 
   <img src="https://raw.githubusercontent.com/FunkyMuse/foSho/main/foSho.png"/> 
</p>


[![](https://jitpack.io/v/FunkyMuse/foSho.svg)](https://jitpack.io/#FunkyMuse/foSho)

### "Opinionated" Android type safe navigation for Compose's Navigation Component

A KSP library that helps you generate type safe code with minimal effort and provides out of the box solutions to bundle everything together and scale your app in easy manner.

### Main features
- Typesafe navigation arguments (special thanks for parsing the arguments to [compose destinations](https://github.com/raamcosta/compose-destinations))
- Simple setup with minimal code
- ViewModel type safe arguments with injection support when using Hilt
- Screen "level" type safe arguments
- Callback arguments
- Bottom sheet support through [Accompanist Material](https://github.com/google/accompanist/tree/main/navigation-material)
- Nested navigation support out of the box
- Transitions enter/exit to and from screens
- Deep links
- Graph aggregator factory
- Custom navigator to open screens easily
- Global navigation
- Multi and single module support

Everything you can do with the Official Jetpack Compose Navigation but in a type safe way.

### Setup
The library is available through [JitPack](https://jitpack.io/).

1. Add JitPack to your project's settings.gradle
```kotlin
dependencyResolutionManagement {
    ...
    repositories {
        ...
        maven { setUrl("https://jitpack.io") }
    }
}
```

2. Add the dependency in the build.gradle
```toml
[versions]
foSho = <version>
ksp = <version>

[libraries]
foSho-android = { module = "com.github.FunkyMuse.foSho:navigator-android", version.ref = "foSho" }
foSho-codegen = { module = "com.github.FunkyMuse.foSho:navigator-codegen", version.ref = "foSho" }

[plugins]
ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }
```

Inside the project build.gradle make sure to add the [KSP](https://github.com/google/ksp) plugin
```kotlin
plugins {
    ...
    alias(libs.plugins.ksp).apply(false)
}
```
Inside the `:app` module
```kotlin
plugins {
    ....
    alias(libs.plugins.ksp)
}
```

```kotlin
dependencies {
    implementation(libs.foSho.android)
    ksp(libs.foSho.codegen)
}
```

If you intend to use `Hilt/Dagger/Anvil` with `foSho` and want your generated ViewModel arguments injectable,
inside `:app` module set `foSho.injectViewModelArguments` to "true", default is "false"
```kotlin
ksp {
    arg("foSho.injectViewModelArguments", "true")
}
```

<details open>
  <summary>Single module</summary>

```kotlin
ksp {
    arg("foSho.singleModule", "true")
}
```

</details>

3. Make magic happen

There are three things you need to write code for and it's pretty natural
1. Graph
2. Destination (Argument, Callback Argument)
3. Content

- `Graph`: Starting point is a `@Graph`, each `Graph` has a `startingDestination` and other `destinations`, also a `Graph` can be a `rootGraph` (only one throughout your app).
- `Destination`: every destination that's part of `startingDestination` and `destinations` is marked with `@Destination` and you implement one of the three UI types `Screen`, `Dialog` and `BottomSheet` for each one you can additionally control whether to generate view model arguments or nav stack entry arguments, `val generateViewModelArguments: Boolean = false`,
  `val generateScreenEntryArguments: Boolean = false`, you can annotate a destination with `@Argument` and `@CallbackArgument` in order to control to/from arguments.
- `Content`: everytime you click build a `Destination` implementation is generated for you, it's your responsibility to implement it and annotate that object with a `@Content`
In code this will look like this

```kotlin
@Graph(
    startingDestination = Home::class,
    destinations = [HomeDetails::class],
    rootGraph = true
)
internal object HomeGraph

@Destination
@Argument(
    name = "hideBottomNav",
    argumentType = ArgumentType.BOOLEAN,
    defaultValue = DefaultBooleanValueFalse::class
)
internal object Home : Screen

@Destination(generateViewModelArguments = true, generateScreenEntryArguments = false)
@Argument(name = "cardId", argumentType = ArgumentType.INT)
@CallbackArgument(name = "clickedShare", argumentType = ArgumentType.BOOLEAN)
internal object HomeDetails : Screen {
    override val deepLinksList: List<DeepLinkContract>
        get() = listOf(
            DeepLinkContract(
                action = Intent.ACTION_VIEW,
                uriPattern = "custom:url/{cardId}"
            )
        )
}

@Content
internal object HomeContent : HomeDestination {
  @Composable
  override fun AnimatedContentScope.Content() {
    val argumentsFromHomeDetailsDestination = HomeDetailsCallbackArguments.rememberHomeDetailsCallbackArguments()
    argumentsFromHomeDetailsDestination.OnSingleBooleanCallbackArgument(
      key = HomeDetailsCallbackArguments.CLICKED_SHARE,
      onValue = {
        if (it == true){
          //user has clicked share
        }
      }
    )
    HomeScreen(onClick = {
      Navigator.navigateSingleTop(HomeDetailsDestination.openHomeDetails(cardId = 42))
    })
  }
}

@Content
internal object HomeDetailsContent : HomeDetailsDestination {
  @Composable
  override fun AnimatedContentScope.Content() {

  }
}
```

A `GraphFactory` is generated for you which you can use with an extension function `addGraphs` to have ease of use like 
```kotlin
addGraphs(
  navigationGraphs = GraphFactory.graphs
)
```
you can checkout [this line](https://github.com/FunkyMuse/foSho/blob/365221e73e7aacdefd48b9fe84e9db0bec6c57c6/app/src/main/java/dev/funkymuse/fosho/sample/MainActivity.kt#L123).

A `Navigator` is there for you to collect the navigation events and also to send navigation events, for a single module setup, you can check out the [sample](https://github.com/FunkyMuse/foSho/tree/main/app).

<details open>
  <summary>Multi module</summary>


</details>

#### Important for Kotlin < 1.8.0

When using Kotlin version older than 1.8.0, you need to make sure the IDE looks at the generated folder.
See KSP related [issue](https://github.com/google/ksp/issues/37).

How to do it depends on the AGP version you are using in this case:

> **Warning**: In both cases, add this inside `android` block and replacing `applicationVariants` with `libraryVariants` if the module is not an application one (i.e, it uses `'com.android.library'` plugin).

<details><summary>Since AGP (Android Gradle Plugin) version 7.4.0</summary>  


* groovy - build.gradle(:module-name)

```gradle
applicationVariants.all { variant ->
    variant.addJavaSourceFoldersToModel(
            new File(buildDir, "generated/ksp/${variant.name}/kotlin")
    )
}
```


* kotlin - build.gradle.kts(:module-name)

```gradle
applicationVariants.all {
    addJavaSourceFoldersToModel(
        File(buildDir, "generated/ksp/$name/kotlin")
    )
}
```
</details>


<details><summary>For AGP (Android Gradle Plugin) version older than 7.4.0</summary>  

* groovy - build.gradle(:module-name)

```gradle
applicationVariants.all { variant ->
    kotlin.sourceSets {
        getByName(variant.name) {
            kotlin.srcDir("build/generated/ksp/${variant.name}/kotlin")
        }
    }
}
```

* kotlin - build.gradle.kts(:module-name)

```gradle
applicationVariants.all {
    kotlin.sourceSets {
        getByName(name) {
            kotlin.srcDir("build/generated/ksp/$name/kotlin")
        }
    }
}
```

</details>


## Found this repository useful? ❤️

Support it by joining [stargazers](https://github.com/FunkyMuse/foSho/stargazers) for this repository. 🌠

And [follow me](https://github.com/FunkyMuse) or check out my [blog](https://funkymuse.dev/) for my next creations! ⭐

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

## License
[Apache 2.0](https://choosealicense.com/licenses/apache-2.0/)


