<h1 align="center"> 
   <a href="https://funkymuse.dev/foSho/">foSho</a>
</h1>

<p align="center"> 
   <img height="250" src="https://raw.githubusercontent.com/FunkyMuse/foSho/main/foSho.png"/> 
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


<details open>
  <summary>Single module</summary>

```gradle
implementation ':<version>'
ksp ':<version>'
```
</details>

<details open>
  <summary>Multi module</summary>

```gradle
implementation ':<version>'
ksp ':<version>'
```
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


