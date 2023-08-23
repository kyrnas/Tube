import { Box, Button, Typography } from "@mui/material";
import { grey } from "@mui/material/colors";
import { Link } from "react-router-dom";

const PageNotFound = () => {
  return (
    <Box>
      <Typography variant="h1">Page does not exist</Typography>
      <Link to={"/"}>
        <Button variant="text">
          <strong>Back to Home Page</strong>
        </Button>
      </Link>
    </Box>
  );
};

export default PageNotFound;
