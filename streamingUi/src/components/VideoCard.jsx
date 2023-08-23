import { Card, CardHeader, CardMedia } from "@mui/material";
import config from "../configuration/config";

const VideoCard = ({ id, title }) => {
  return (
    <Card elevation={1}>
      <CardMedia
        component="img"
        image={"http://" + config.apiBaseUrl + "/video/thumbnail/" + id}
        alt={title + " video thumbnail"}
      />
      <CardHeader title={title} titleTypographyProps={{ noWrap: true }} />
    </Card>
  );
};

export default VideoCard;
