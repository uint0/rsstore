# RSStore

Continuously archive RSS feeds to various different backends. Aka I try to learn kotlin.

## Usage

```shell
$ ./gradlew run --args="-u https://nyaa.si/?page=rss -f NyaaSI -o output -F JSONLines"
```

## Supported Backends

| Name            | Description                                                          |
|-----------------|----------------------------------------------------------------------|
| JSON            | Outputs to a new JSON file in a directory of JSON files              |
| JSONLinesStream | Outputs to a single JSONLines file, appending new output             |

