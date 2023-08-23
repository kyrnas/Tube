import React, { useState, useEffect } from "react";
import axios from "axios";
import ReactPlayer from "react-player";

import "./Demo.css";

const qualityOptions = ["1080p", "720p", "480p", "360p"];

const Demo = () => {
  const [videos, setVideos] = useState([]);
  const [selectedVideo, setSelectedVideo] = useState("");
  const [uploading, setUploading] = useState(false);
  const [uploadProgress, setUploadProgress] = useState(0);
  const [webSocket, setWebSocket] = useState(null);
  const [videoProgress, setVideoProgress] = useState();
  const [videoName, setVideoName] = useState("");
  const [uploadVideoFile, setUploadVideoFile] = useState();
  const [selectedQuality, setSelectedQuality] = useState(qualityOptions[0]);

  // Fetch the list of available videos when the component mounts
  useEffect(() => {
    fetchVideos();
  }, []);

  const fetchVideos = async () => {
    try {
      const response = await axios.get("http://192.168.50.182:8080/video/all");
      setVideos(response.data.content);
    } catch (error) {
      console.error("Error fetching videos:", error);
    }
  };

  const handleVideoSelect = (video) => {
    setSelectedVideo(video);
  };

  const handleVideoUpload = async (event) => {
    if (!uploadVideoFile) {
      window.alert("No video selected");
      return;
    }
    setUploading(true);
    setUploadProgress(0);

    const file = uploadVideoFile;
    const formData = new FormData();
    formData.append("file", file);
    formData.append("name", videoName); // Pass the video name as a request parameter

    try {
      const response = await axios.post(
        "http://192.168.50.182:8080/video",
        formData,
        {
          headers: { "Content-Type": "multipart/form-data" },
          onUploadProgress: (progressEvent) => {
            const progress = (progressEvent.loaded / progressEvent.total) * 100;
            setUploadProgress(progress);
          },
        }
      );
      setUploading(false);
      // Fetch the updated list of videos after upload
      fetchVideos();

      // Connect to WebSocket using the video ID from the response
      if (response.data) {
        const ws = new WebSocket(
          `ws://192.168.50.182:8080/progress/${response.data}`
        );
        setWebSocket(ws);
      }
    } catch (error) {
      console.error("Error uploading video:", error);
      setUploading(false);
    }
  };

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
        if (selectedVideo) {
          setVideoProgress(JSON.parse(event.data));
        }
      };
    }
  }, [webSocket, selectedVideo, videoProgress]);

  const handleQualityChange = (e) => {
    setSelectedQuality(e.target.value);
  };

  return (
    <div>
      <h1>Video Streaming App</h1>
      <div>
        <h2>Available Videos</h2>
        <ul>
          {videos.map((video) => (
            <li
              key={video.id}
              style={{ width: "33%", height: "auto", margin: "auto" }}
            >
              <button
                onClick={() => handleVideoSelect(video)}
                style={{ width: "100%" }}
              >
                <img
                  src={`http://192.168.50.182:8080/video/thumbnail/${video.id}`}
                  alt="video-thumbnail"
                  width="100%"
                />
                <h3>{video.name}</h3>
              </button>
            </li>
          ))}
        </ul>
      </div>
      <div>
        <h2>Upload Video</h2>
        <input
          type="file"
          accept="video/*"
          onChange={(event) => setUploadVideoFile(event.target.files[0])}
        />
        <input
          type="text"
          value={videoName}
          onChange={(e) => setVideoName(e.target.value)}
          placeholder="Video Name"
        />
        <button onClick={handleVideoUpload}>Save</button>
        {uploading && <p>Uploading... {uploadProgress.toFixed(2)}%</p>}
      </div>
      <div>
        <h2>Selected Video</h2>
        {selectedVideo && (
          <ReactPlayer
            className="react-player"
            url={`http://192.168.50.182:8080/video/${selectedVideo.id}?quality=${selectedQuality}`}
            controls
            width="100%"
            height="100%"
          />
        )}
        <div className="player-controls">
          {/* Quality selection dropdown */}
          <label htmlFor="qualitySelect">Select Quality: </label>
          <select
            id="qualitySelect"
            value={selectedQuality}
            onChange={handleQualityChange}
          >
            {qualityOptions.map((quality) => (
              <option key={quality} value={quality}>
                {quality}
              </option>
            ))}
          </select>
        </div>
      </div>
      {selectedVideo && videoProgress && (
        <div>
          <h2>Progress 1080p: {videoProgress["1080p"].toFixed(2)}%</h2>
          <h2>Progress 720p: {videoProgress["720p"].toFixed(2)}%</h2>
          <h2>Progress 480p: {videoProgress["480p"].toFixed(2)}%</h2>
          <h2>Progress 360p: {videoProgress["360p"].toFixed(2)}%</h2>
        </div>
      )}
    </div>
  );
};

export default Demo;
