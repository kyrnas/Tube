import { Box, CircularProgress, Typography } from "@mui/material";

const ProcessingProgressBar = ({ value, quality }) => {
  return (
    <Box sx={{ margin: "10px" }}>
      <Typography variant="h6">{quality}</Typography>
      <Box sx={{ position: "relative", display: "inline-flex" }}>
        <CircularProgress variant="determinate" value={parseInt(value)} />
        <Box
          sx={{
            top: 0,
            left: 0,
            bottom: 0,
            right: 0,
            position: "absolute",
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
          }}
        >
          <Typography variant="caption" component="div" color="text.secondary">
            {value}%
          </Typography>
        </Box>
      </Box>
    </Box>
  );
};

export default ProcessingProgressBar;
