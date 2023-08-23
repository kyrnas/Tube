import ReactPlayer from "react-player";
import config from "../../configuration/config";
import { formatTime } from "./format";
import VideoPlayerControls from "./VideoPlayerControls";
import { Box, Container, Typography } from "@mui/material";
import { useState, useRef } from "react";
import screenfull from "screenfull";
import { useQuery } from "react-query";
import axios from "axios";
import "./VideoPlayer.css";

const VideoPlayer = ({ videoId }) => {
  const fetchVideoMetadata = () => {
    return axios.get(
      "http://" + config.apiBaseUrl + "/video/metadata/" + videoId
    );
  };
  const { isLoading, data, isError, error } = useQuery(
    "video",
    fetchVideoMetadata
  );

  const videoPlayerRef = useRef();
  const playerContainerRef = useRef();

  const currentTime = videoPlayerRef.current
    ? videoPlayerRef.current.getCurrentTime()
    : "00:00";

  const duration = videoPlayerRef.current
    ? videoPlayerRef.current.getDuration()
    : "00:00";

  const formatCurrentTime = formatTime(currentTime);

  const formatDuration = formatTime(duration);

  const [videoState, setVideoState] = useState({
    playing: false,
    muted: false,
    volume: 0.5,
    played: 0,
    seeking: false,
    buffer: true,
    quality: "1080p",
  });

  const {
    playing,
    muted,
    volume,
    playbackRate,
    played,
    seeking,
    buffer,
    quality,
  } = videoState;

  const playPauseHandler = () => {
    setVideoState((prevState) => {
      return { ...prevState, playing: !prevState.playing };
    });
  };

  const progressHandler = (state) => {
    if (!seeking) {
      setVideoState((prevState) => {
        return { ...prevState, ...state };
      });
    }
  };

  const seekHandler = (e, value) => {
    setVideoState((prevState) => {
      return { ...prevState, playing: false, played: parseFloat(value) / 100 };
    });
  };

  const seekMouseUpHandler = (e, value) => {
    setVideoState((prevState) => {
      return { ...prevState, playing: true, seeking: false };
    });
    videoPlayerRef.current.seekTo(value / 100);
  };

  const volumeChangeHandler = (e, value) => {
    const newVolume = parseFloat(value) / 100;
    setVideoState((prevState) => {
      return {
        ...prevState,
        volume: newVolume,
        muted: Number(newVolume) === 0 ? true : false,
      };
    });
  };

  const muteHandler = () => {
    setVideoState((prevState) => {
      return { ...prevState, muted: !videoState.muted };
    });
  };

  const bufferStartHandler = () => {
    setVideoState((prevState) => {
      return { ...prevState, buffer: true };
    });
  };

  const bufferEndHandler = () => {
    setVideoState((prevState) => {
      return { ...prevState, buffer: false };
    });
  };

  const changeQualityHandler = (newQuality) => {
    setVideoState((prevState) => {
      return { ...prevState, quality: newQuality };
    });
  };

  const fullscreenHandler = () => {
    screenfull.toggle(playerContainerRef.current);
  };

  if (!data || data.data == "" || isError) {
    return <Typography variant="h2">Video not found</Typography>;
  }

  if (isLoading) {
    return <Typography variant="h2">Preparing Video</Typography>;
  }

  return (
    <Box className="video_container">
      <Container maxWidth="lg" justify="center">
        <Box className="player__wrapper" ref={playerContainerRef}>
          <div onClick={playPauseHandler}>
            <ReactPlayer
              ref={videoPlayerRef}
              className="player"
              url={
                "http://" +
                config.apiBaseUrl +
                "/video/" +
                videoId +
                `?quality=${quality}`
              }
              width={"100%"}
              height={"100%"}
              playing={playing}
              muted={muted}
              volume={volume}
              onProgress={progressHandler}
              onEnded={playPauseHandler}
              onBuffer={bufferStartHandler}
              onBufferEnd={bufferEndHandler}
            />
          </div>
          <VideoPlayerControls
            onPlayPause={playPauseHandler}
            playing={playing}
            played={played}
            onSeek={seekHandler}
            onSeekMouseUp={seekMouseUpHandler}
            volume={volume}
            onVolumeChangeHandler={volumeChangeHandler}
            mute={muted}
            onMute={muteHandler}
            duration={formatDuration}
            currentTime={formatCurrentTime}
            buffer={buffer}
            quality={quality}
            onQualityChanged={changeQualityHandler}
            videoName={data?.data?.name}
            onFullScreenToggle={fullscreenHandler}
          />
        </Box>
      </Container>
    </Box>
  );
};

export default VideoPlayer;
