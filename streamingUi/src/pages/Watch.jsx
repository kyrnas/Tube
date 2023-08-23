import { useParams } from "react-router-dom";
import VideoPlayer from "../components/Player/VideoPlayer";

const WatchPage = () => {
  const { videoId } = useParams();

  return <VideoPlayer videoId={videoId} />;
};

export default WatchPage;
