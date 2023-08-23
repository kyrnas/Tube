import {
  AccessTimeOutlined,
  AddCircle,
  AddCircleRounded,
  AppShortcutOutlined,
  BookOnlineOutlined,
  BookOutlined,
  DownloadOutlined,
  HistoryOutlined,
  HomeOutlined,
  MusicNoteOutlined,
  SubscriptionsOutlined,
} from "@mui/icons-material";
import {
  Box,
  Divider,
  Drawer,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  Toolbar,
} from "@mui/material";
import { grey, red } from "@mui/material/colors";
import { useLocation, useNavigate } from "react-router-dom";

const NavDrawer = ({ drawerWidth, expandNav }) => {
  const navigate = useNavigate();
  const location = useLocation();

  const listItemStyle = expandNav
    ? { display: "flex", textAlign: "left" }
    : {
        display: "block",
        textAlign: "center",
        paddingLeft: "0",
        paddingRight: "0",
      };

  const topMenuItems = [
    {
      text: "Upload New Video",
      icon: <AddCircleRounded style={{ color: red[700] }} />,
      path: "/upload",
    },
  ];

  const menuItems = [
    {
      text: "Home",
      icon: <HomeOutlined />,
      path: "/",
    },
    {
      text: "Subscriptions",
      icon: <SubscriptionsOutlined />,
      path: "/subscriptions",
    },
    {
      text: "Shorts",
      icon: <AppShortcutOutlined />,
      path: "/shorts",
    },
    {
      text: "Music",
      icon: <MusicNoteOutlined />,
      path: "/music",
    },
  ];

  const menuTwoItems = [
    {
      text: "Library",
      icon: <BookOutlined />,
      path: "/library",
    },
    {
      text: "History",
      icon: <HistoryOutlined />,
      path: "/history",
    },
    {
      text: "Downloads",
      icon: <DownloadOutlined />,
      path: "/downloads",
    },
    {
      text: "Watch Later",
      icon: <AccessTimeOutlined />,
      path: "/watch_later",
    },
  ];

  return (
    <Drawer
      variant="permanent"
      sx={{
        width: drawerWidth,
        height: "100%",
        flexShrink: 0,
        paddingTop: 0,
        [`& .MuiDrawer-paper`]: {
          width: drawerWidth,
          boxSizing: "border-box",
          backgroundColor: grey[900],
          paddingTop: 0,
        },
      }}
    >
      <Toolbar />
      <Box sx={{ overflow: "auto" }}>
        <List>
          {topMenuItems.map((item) => (
            <ListItem
              key={item.text}
              button
              onClick={() => navigate(item.path)}
              sx={{
                ...listItemStyle,
              }}
            >
              <ListItemIcon
                sx={{
                  minWidth: "auto",
                  marginRight: "10px",
                  marginLeft: "10px",
                }}
              >
                {item.icon}
              </ListItemIcon>
              {expandNav ? <ListItemText primary={item.text} /> : null}
            </ListItem>
          ))}
        </List>
        <Divider />
        <List>
          {menuItems.map((item) => (
            <ListItem
              key={item.text}
              button
              onClick={() => navigate(item.path)}
              sx={{
                ...listItemStyle,
                backgroundColor:
                  location.pathname == item.path ? red[700] : null,
              }}
            >
              <ListItemIcon
                sx={{
                  minWidth: "auto",
                  marginRight: "10px",
                  marginLeft: "10px",
                }}
              >
                {item.icon}
              </ListItemIcon>
              {expandNav ? <ListItemText primary={item.text} /> : null}
            </ListItem>
          ))}
        </List>
        <Divider />
        <List>
          {menuTwoItems.map((item) => (
            <ListItem
              key={item.text}
              button
              onClick={() => navigate(item.path)}
              sx={{
                ...listItemStyle,
                backgroundColor:
                  location.pathname == item.path ? red[700] : null,
              }}
            >
              <ListItemIcon
                sx={{
                  minWidth: "auto",
                  marginRight: "10px",
                  marginLeft: "10px",
                }}
              >
                {item.icon}
              </ListItemIcon>
              {expandNav ? <ListItemText primary={item.text} /> : null}
            </ListItem>
          ))}
        </List>
        <Divider />
      </Box>
    </Drawer>
  );
};

export default NavDrawer;
