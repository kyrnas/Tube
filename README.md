# Tube
This application focuses on copying the core functionality of YouTube: uploading and watching videos. I was interested in exploring more functionality of Spring Boot other than RESTful APIs. I additionally added a React front-end for this application, to demo the functionality. This application is also deployed on AWS under the [kyryoutube.com](https://kyryoutube.com) domain.

## Functionality
* Upload videos to a back-end server
* Transcode videos into lower qualities in order to allow serving them in all qualities
* Live broadcast the progress of the video being transcoded after the upload
* Display a pageable list of available videos
* Stream videos of different qualities to the browser client

## Technology
* Back-end - Spring Boot 3
  * REST API for uploading and listing the videos
  * Bytestreams for streaming the videos to browsers
  * Web sockets for video transcoding live updates
  * [FFMPEG](https://ffmpeg.org/) for transcoding the videos into different qualities and formats
* Front-end - React + Vite
  * MUI
  * React Query
  * Redux
  * React Player
* Cloud - AWS
  * EC2 for running the Spring Boot Server
  * S3 for hosting a static website
  * PostgreSQL RDS for video metadata database
  * CloudFront for CDN + serving the back-end and front-end under the same hostname (allows relative pathing on the front-end as well as setting up CORS)
  * Route53 for domain
