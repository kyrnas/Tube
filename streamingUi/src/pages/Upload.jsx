import {
  Alert,
  Box,
  Button,
  Input,
  LinearProgress,
  Snackbar,
  Typography,
} from "@mui/material";
import { useState } from "react";
import ReactPlayer from "react-player";
import axios from "axios";
import { useNavigate } from "react-router-dom";

const UploadPage = () => {
  const [videoUploadFile, setVideoUploadFile] = useState();
  const [videoFilePath, setVideoFilePath] = useState();
  const [videoName, setVideoName] = useState("");
  const [uploadProgress, setUploadProgress] = useState();
  const [showSucessMessage, setShowSuccessMessage] = useState(false);
  const [showErrorMessage, setShowErrorMessage] = useState(false);

  const navigate = useNavigate();

  const handleVideoUpload = async (event) => {
    if (!videoUploadFile) {
      window.alert("No video selected");
      return;
    }
    setUploadProgress(0);

    let newVideoId;

    const file = videoUploadFile;
    const formData = new FormData();
    formData.append("file", file);
    formData.append("name", videoName); // Pass the video name as a request parameter

    try {
      const response = await axios.post(`/api/video`, formData, {
        headers: { "Content-Type": "multipart/form-data" },
        onUploadProgress: (progressEvent) => {
          const progress = (progressEvent.loaded / progressEvent.total) * 100;
          setUploadProgress(progress);
        },
      });
      newVideoId = response.data;
    } catch (error) {
      console.error("Error uploading video:", error);
      setUploadProgress(null);
      setShowErrorMessage(true);
      return;
    }
    const forwardTimeout = setTimeout(() => {
      navigate(`/upload/${newVideoId}`);
    }, 3000);
    setShowSuccessMessage(true);
    setUploadProgress(null);
  };

  const handleSnackbarClose = (event, reason) => {
    if (reason === "clickaway") {
      return;
    }
    setShowSuccessMessage(false);
  };

  const handleErrorSnackbarClose = (event, reason) => {
    if (reason === "clickaway") {
      return;
    }
    setShowErrorMessage(false);
  };

  return (
    <Box>
      <Typography variant="h2">Upload New Video</Typography>
      {videoFilePath ? (
        <ReactPlayer url={videoFilePath} style={{ margin: "auto" }} />
      ) : null}
      <Box
        sx={{
          margin: "10px",
          display: "flex",
          justifyContent: "center",
        }}
      >
        <Button
          variant="contained"
          sx={{
            margin: "10px",
            width: "15%",
            height: "100%",
          }}
          component={"label"}
        >
          {videoUploadFile ? videoUploadFile.name : "Select File"}
          <input
            type="file"
            accept="video/*"
            hidden
            onChange={(event) => {
              setVideoUploadFile(event.target.files[0]);
              setVideoFilePath(URL.createObjectURL(event.target.files[0]));
            }}
          />
        </Button>
        <Input
          type="text"
          value={videoName}
          onChange={(e) => setVideoName(e.target.value)}
          placeholder="Video Name"
          sx={{ margin: "10px", width: "30%" }}
        />
        <Button
          variant="outlined"
          onClick={handleVideoUpload}
          sx={{ margin: "10px", width: "15%", whiteSpace: "nowrap" }}
          disabled={uploadProgress != null && uploadProgress > 0}
        >
          Upload
        </Button>
      </Box>
      {uploadProgress && (
        <Box
          sx={{
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
          }}
        >
          <Box sx={{ width: "60%", mr: 1 }}>
            <LinearProgress variant="determinate" value={uploadProgress} />
          </Box>
          <Box sx={{ minWidth: 35 }}>
            <Typography variant="body2" color="text.secondary">{`${Math.round(
              uploadProgress
            )}%`}</Typography>
          </Box>
        </Box>
      )}
      <Snackbar
        open={showSucessMessage}
        autoHideDuration={3000}
        onClose={handleSnackbarClose}
      >
        <Alert
          onClose={handleSnackbarClose}
          severity="success"
          sx={{ width: "100%" }}
        >
          Uploaded successfully! Forwarding...
        </Alert>
      </Snackbar>
      <Snackbar
        open={showErrorMessage}
        autoHideDuration={3000}
        onClose={handleErrorSnackbarClose}
      >
        <Alert
          onClose={handleErrorSnackbarClose}
          severity="error"
          sx={{ width: "100%" }}
        >
          An error occured while uploading.
        </Alert>
      </Snackbar>
    </Box>
  );
};

export default UploadPage;
