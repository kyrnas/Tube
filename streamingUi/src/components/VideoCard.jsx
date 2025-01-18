import { Card, CardHeader, CardMedia } from "@mui/material";

const VideoCard = ({ id, title }) => {
  return (
    <Card elevation={1}>
      <CardMedia
        component="img"
        image={`${import.meta.env.VITE_CDN_URL}/${id}/thumbnail.jpg`}
        alt={title + " video thumbnail"}
      />
      <CardHeader title={title} titleTypographyProps={{ noWrap: true }} />
    </Card>
  );
};

export default VideoCard;
