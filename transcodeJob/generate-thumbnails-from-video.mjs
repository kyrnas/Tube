import fs from "fs";
import { S3 } from "@aws-sdk/client-s3";
import { spawnSync } from "child_process";
import doesFileExist from "./does-file-exist.mjs";
import generateTmpFilePath from "./generate-tmp-file-path.mjs";

const ffprobePath = "/opt/bin/ffprobe";
const ffmpegPath = "/opt/bin/ffmpeg";

const THUMBNAIL_TARGET_BUCKET = "kyrstreams";

export default async (tmpVideoPath, numberOfThumbnails, videoFileName) => {
  const randomTimes = generateRandomTimes(tmpVideoPath, numberOfThumbnails);

  for (const [index, randomTime] of Object.entries(randomTimes)) {
    const tmpThumbnailPath = await createImageFromVideo(
      tmpVideoPath,
      randomTime
    );

    if (doesFileExist(tmpThumbnailPath)) {
      const nameOfImageToCreate = generateNameOfImageToUpload(videoFileName);
      await uploadFileToS3(tmpThumbnailPath, nameOfImageToCreate);
    }
  }
};

const generateRandomTimes = (tmpVideoPath, numberOfTimesToGenerate) => {
  const timesInSeconds = [];
  const videoDuration = getVideoDuration(tmpVideoPath);

  for (let x = 0; x < numberOfTimesToGenerate; x++) {
    const randomNum = getRandomNumberNotInExistingList(
      timesInSeconds,
      videoDuration
    );

    if (randomNum >= 0) {
      timesInSeconds.push(randomNum);
    }
  }

  return timesInSeconds;
};

const getRandomNumberNotInExistingList = (existingList, maxValueOfNumber) => {
  for (let attemptNumber = 0; attemptNumber < 3; attemptNumber++) {
    const randomNum = getRandomNumber(maxValueOfNumber);

    if (!existingList.includes(randomNum)) {
      return randomNum;
    }
  }

  return -1;
};

const getRandomNumber = (upperLimit) => {
  return Math.floor(Math.random() * upperLimit);
};

const getVideoDuration = (tmpVideoPath) => {
  const ffprobe = spawnSync(ffprobePath, [
    "-v",
    "error",
    "-show_entries",
    "format=duration",
    "-of",
    "default=nw=1:nk=1",
    tmpVideoPath,
  ]);

  return Math.floor(ffprobe.stdout.toString());
};

const createImageFromVideo = (tmpVideoPath, targetSecond) => {
  const tmpThumbnailPath = generateThumbnailPath(targetSecond);
  const ffmpegParams = createFfmpegParams(
    tmpVideoPath,
    tmpThumbnailPath,
    targetSecond
  );
  spawnSync(ffmpegPath, ffmpegParams);

  return tmpThumbnailPath;
};

const generateThumbnailPath = (targetSecond) => {
  const tmpThumbnailPathTemplate = "/tmp/thumbnail-{HASH}-{num}.jpg";
  const uniqueThumbnailPath = generateTmpFilePath(tmpThumbnailPathTemplate);
  const thumbnailPathWithNumber = uniqueThumbnailPath.replace(
    "{num}",
    targetSecond
  );

  return thumbnailPathWithNumber;
};

const createFfmpegParams = (tmpVideoPath, tmpThumbnailPath, targetSecond) => {
  return [
    "-ss",
    targetSecond,
    "-i",
    tmpVideoPath,
    "-vf",
    "thumbnail,scale=80:140",
    "-vframes",
    1,
    tmpThumbnailPath,
  ];
};

const generateNameOfImageToUpload = (videoFileName) => {
  const strippedExtension = videoFileName.replace(".mp4", "");
  return `${strippedExtension}.jpg`;
};

const uploadFileToS3 = async (tmpThumbnailPath, nameOfImageToCreate) => {
  const contents = fs.createReadStream(tmpThumbnailPath);
  const uploadParams = {
    Bucket: THUMBNAIL_TARGET_BUCKET,
    Key: nameOfImageToCreate,
    Body: contents,
    ContentType: "image/jpg",
  };

  const s3 = new S3();
  await s3.putObject(uploadParams)();
};
