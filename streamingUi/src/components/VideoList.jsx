import { Box, Grid, Typography, Skeleton, Card } from "@mui/material";
import VideoCard from "./VideoCard";
import { useQuery } from "react-query";
import config from "../configuration/config";
import { Link } from "react-router-dom";
import { AddCircleOutlineOutlined } from "@mui/icons-material";
import axios from "axios";

const fetchVideos = () => {
  return axios.get("http://" + config.apiBaseUrl + "/video/all");
};

const VideoList = () => {
  const { isLoading, data, isError, error } = useQuery("videos", fetchVideos);

  if (isLoading) {
    return (
      <Grid container spacing={1} alignItems="center" justifyContent="center">
        {Array.from(new Array(20)).map((item, index) => (
          <Grid item key={index} xs={12} md={4} lg={3} xl={2.5}>
            <Skeleton variant="rectangular" width={210} height={118} />
            <Box sx={{ pt: 0.5 }}>
              <Skeleton />
              <Skeleton width="60%" />
            </Box>
          </Grid>
        ))}
      </Grid>
    );
  }

  if (isError) {
    return <Typography variant="h2">Could not load data</Typography>;
  }

  return (
    <Grid container spacing={1} alignItems="stretch" justifyContent="center">
      <Grid
        item
        key={"add"}
        xs={12}
        md={4}
        lg={3}
        xl={2.5}
        style={{ objectFit: "contain" }}
      >
        <Link to={"/upload"}>
          <Card
            sx={{
              height: "100%",
              display: "flex",
              justifyContent: "center",
              alignItems: "center",
            }}
          >
            <AddCircleOutlineOutlined fontSize="large" />
          </Card>
        </Link>
      </Grid>
      {data &&
        data?.data.content.map((video) => (
          <Grid item key={video.id} xs={12} md={4} lg={3} xl={2.5}>
            <Link
              to={`/watch/${video.id}`}
              style={{ color: "inherit", textDecoration: "inherit" }}
            >
              <VideoCard id={video.id} title={video.name} />
            </Link>
          </Grid>
        ))}
    </Grid>
  );
};

export default VideoList;
