import React, { useState, useRef } from "react";
import {
  Pause,
  PlayArrow,
  SkipNext,
  VolumeUp,
  VolumeOff,
  SettingsOutlined,
  FullscreenOutlined,
} from "@mui/icons-material";
import "./VideoPlayerControl.css";
import {
  Box,
  Slider,
  Typography,
  Menu,
  MenuItem,
  CircularProgress,
} from "@mui/material";
import { purple, red } from "@mui/material/colors";

const qualityList = ["1080p", "720p", "480p", "360p"];

const VideoPlayerControls = (props) => {
  const [anchorEl, setAnchorEl] = useState(null);
  const open = Boolean(anchorEl);

  const handleOpenMenu = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleCloseMenu = (newVal) => {
    if (newVal) {
      props.onQualityChanged(newVal);
    }
    setAnchorEl(null);
  };

  return (
    props.showControls && (
      <Box>
        <Box className="control_container">
          {/* Top container */}
          <Box className="top_container">
            <Typography variant="h2">{props.videoName}</Typography>
          </Box>
          {/* Middle Container */}
          {props.buffer && (
            <div className="mid__container">
              <CircularProgress sx={{ color: purple[700] }} />
            </div>
          )}
          {/* Bottom Container */}
          <Box className="bottom__container">
            <Box className="slider__container">
              <Slider
                sx={{
                  color: "red",
                  "& .MuiSlider-thumb": {
                    transition: "0.3s cubic-bezier(.47,1.64,.41,.8)",
                  },
                }}
                min={0}
                max={100}
                value={props.played * 100}
                onChange={props.onSeek}
                onChangeCommitted={props.onSeekMouseUp}
              />
            </Box>
            <Box className="control__box">
              <Box className="inner__controls">
                <div className="icon__btn" onClick={props.onPlayPause}>
                  {props.playing ? (
                    <Pause fontSize="medium" />
                  ) : (
                    <PlayArrow fontSize="medium" />
                  )}{" "}
                </div>
                <Box className="icon__btn">
                  <SkipNext fontSize="medium" />
                </Box>
                <div className="icon__btn" onClick={props.onMute}>
                  {props.mute ? (
                    <VolumeOff fontSize="medium" />
                  ) : (
                    <VolumeUp fontSize="medium" />
                  )}
                </div>

                <Slider
                  sx={{
                    width: "100px",
                    color: "red",
                    padding: "inherit",
                    marginLeft: "15px",
                    // [`.MuiSlider-thumb:not(.MuiSlider-active)`]: {
                    //   transition: "left 1s linear",
                    // },
                    // [`.MuiSlider-track`]: {
                    //   transition: "width 1s linear",
                    // },
                  }}
                  value={props.volume * 100}
                  onChange={props.onVolumeChangeHandler}
                  onChangeCommitted={props.onVolumeChangeHandler}
                />
                <span className="video_timer">
                  {props.currentTime} / {props.duration}
                </span>
              </Box>
              <Box className="left-side_controls">
                <Menu
                  id="basic-menu"
                  anchorEl={anchorEl}
                  open={open}
                  onClose={() => handleCloseMenu(null)}
                  MenuListProps={{
                    "aria-labelledby": "basic-button",
                  }}
                  anchorOrigin={{
                    vertical: "top",
                    horizontal: "center",
                  }}
                  transformOrigin={{ vertical: "bottom", horizontal: "center" }}
                  sx={{
                    [`.MuiList-root`]: {
                      display: "flex",
                      flexDirection: "column",
                    },
                  }}
                >
                  {qualityList.map((item) => {
                    return (
                      <MenuItem
                        key={item}
                        onClick={() => handleCloseMenu(item)}
                        selected={item === props.quality}
                        sx={{
                          "& MuiButtonBase-root-MuiMenuItem-root.Mui-selected":
                            {
                              backgroundColor: red[700],
                            },
                        }}
                      >
                        {item}
                      </MenuItem>
                    );
                  })}
                </Menu>
                <div
                  className="icon__btn"
                  onClick={handleOpenMenu}
                  aria-controls={open ? "basic-menu" : undefined}
                  aria-haspopup="menu"
                  aria-expanded={open}
                >
                  <SettingsOutlined />
                </div>
                <div className="icon__btn" onClick={props.onFullScreenToggle}>
                  <FullscreenOutlined />
                </div>
              </Box>
            </Box>
          </Box>
        </Box>
      </Box>
    )
  );
};

export default VideoPlayerControls;
