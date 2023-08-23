import { Outlet } from "react-router-dom";
import Header from "../components/Header";
import NavDrawer from "../components/NavDrawer";
import { Box } from "@mui/material";
import { useSelector } from "react-redux";

const RootLayout = () => {
  const expandNav = useSelector((state) => state.ui.expandNav);

  const drawerWidth = expandNav ? 240 : 72;

  return (
    <Box
      sx={{ display: "flex", flexDirection: "column", width: "100%" }}
      bgcolor={"background.default"}
    >
      <Header />
      <NavDrawer drawerWidth={drawerWidth} expandNav={expandNav} />
      <Box
        component="main"
        sx={{
          flexGrow: 0,
          marginLeft: `${drawerWidth}px`,
        }}
      >
        <Box sx={{ height: "80px" }} bgcolor={"background.default"} />
        <Outlet />
      </Box>
    </Box>
  );
};

export default RootLayout;
