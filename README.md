# =collector=
Basic time-series operations with collections

[![Build Status](https://travis-ci.org/maslick/collector.svg?branch=master)](https://travis-ci.org/maslick/collector)
[ ![Download](https://api.bintray.com/packages/maslick/maven/collector/images/download.svg) ](https://bintray.com/maslick/maven/collector)


## Installation
```
repositories {
    maven {
        url  "https://dl.bintray.com/maslick/maven/"
    }
}

dependencies {    
    compile('io.maslick:collector:1.1')
}
```


## Usage
```kotlin
import io.maslick.collector.KotlinHelper.toListWhile

data class Data(var id: Long? = null, var name: String? = null)

fun main(args: Array<String>) {
    val list = listOf(
            Data(id = 1, name = "Michael Jordan"),
            Data(id = 2, name = "Lebron James"),
            Data(id = 3, name = "Dwight Howard"),
            Data(id = 4, name = "Nate Robinson"),
            Data(id = 5, name = "Donovan Mitchel")
    )

    val result = list.toListWhileLazy { bucket, item -> item.id!! - bucket.first().id!! < 2 }
    
    result.forEach {
        println(it.map { it.name })
    }
}
```

should print: 
```
[Michael Jordan, Lebron James]
[Dwight Howard, Nate Robinson]
[Donovan Mitchel]
```
