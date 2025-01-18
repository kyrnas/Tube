import { Box, Card, CardMedia, Typography } from "@mui/material";
import axios from "axios";
import { useParams } from "react-router-dom";
import { useMutation, useQuery } from "react-query";
import { useEffect, useState } from "react";
import ProcessingProgressBar from "../components/ProcessingProgressBar";
import Image from "react-graceful-image";

const qualityList = ["1080p", "720p", "480p", "360p"];

const ProcessingProgressPage = () => {
  const [webSocket, setWebSocket] = useState(null);
  const [videoProgress, setVideoProgress] = useState();
  const { videoId } = useParams();

  const fetchVideoMetadata = () => {
    return axios.get("/api/video/metadata/" + videoId);
  };

  const startPollingVideoProgress = () => {
    return axios.post("/api/video/start-polling/" + videoId);
  };

  const { isLoading, data } = useQuery(`video/${videoId}`, fetchVideoMetadata);

  const {
    mutate,
    isLoading: pollingLoading,
    data: pollingData,
  } = useMutation(`video-polling/${videoId}`, startPollingVideoProgress);

  useEffect(() => {
    if (data?.data?.processing) {
      mutate();
    }
  }, [data]);

  useEffect(() => {
    const protocol = window.location.protocol === "https:" ? "wss:" : "ws:";
    const host = window.location.host;
    const wsPath = `/api/progress/${videoId}`;
    const wsUrl = `${protocol}//${host}${wsPath}`;

    const ws = new WebSocket(wsUrl);
    setWebSocket(ws);
  }, [pollingData]);

  // Effect to clean up WebSocket connection when the component is unmounted
  useEffect(() => {
    return () => {
      if (webSocket) {
        webSocket.close();
      }
    };
  }, [webSocket]);

  // Effect to handle WebSocket messages (progress updates)
  useEffect(() => {
    if (webSocket) {
      webSocket.onmessage = (event) => {
        // Update the progress for the selected video (if available)
        setVideoProgress(JSON.parse(event.data));
      };
    }
  }, [webSocket, videoProgress]);

  if (isLoading) {
    return <Typography variant="h2">Loading...</Typography>;
  }

  const ProgressBars = !data?.data?.processing ? (
    <Box sx={{ display: "flex", alignItems: "center" }}>
      {qualityList.map((item) => (
        <ProcessingProgressBar key={item} value={100} quality={item} />
      ))}
    </Box>
  ) : (
    <Box sx={{ display: "flex", alignItems: "center" }}>
      {qualityList.map((item) => (
        <ProcessingProgressBar
          key={item}
          value={
            videoProgress && videoProgress[item] !== "null"
              ? videoProgress[item]
              : 0
          }
          quality={item}
        />
      ))}
    </Box>
  );

  return (
    <>
      <Typography variant="h2">{data?.data?.name}</Typography>
      <Box
        sx={{
          padding: "20px",
          display: "flex",
          flexDirection: "column",
          justifyContent: "center",
          alignItems: "center",
        }}
      >
        {/* <Card elevation={1} sx={{ width: "40%" }}>
          <CardMedia
            component="img"
            image={`${import.meta.env.VITE_CDN_URL}/${videoId}/thumbnail.jpg`}
            alt={data?.data.name + " video thumbnail"}
          />
        </Card> */}
        <Image
          style={{ width: "40%" }}
          src={`${import.meta.env.VITE_CDN_URL}/${videoId}/thumbnail.jpg`}
          alt={data?.data.name + " video thumbnail"}
        />
        {ProgressBars}
      </Box>
    </>
  );
};

export default ProcessingProgressPage;
