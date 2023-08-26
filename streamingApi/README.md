# Spring Boot 3 Backend

Use this REST API to fetch metadata about videos, stream the videos using byte stream, post new videos 
and asynchronously transcode and store them.

## How to run

* Install [FFMPEG](https://ffmpeg.org/download.html) locally. Make sure that the PATH env variable points 
to the installation directory. Check by running `ffmpeg -version`
* Make sure you have Java 17 installed locally
* Run the `./gradlew bootRun` command in the root of streamingApi folder