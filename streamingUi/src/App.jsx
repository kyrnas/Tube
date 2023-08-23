import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import "./App.css";
import RootLayout from "./pages/Root";
import ErrorPage from "./pages/Error";
import Demo from "./pages/Demo";
import PageNotFound from "./pages/PageNotFound";
import { CssBaseline, ThemeProvider, createTheme } from "@mui/material";
import { QueryClient, QueryClientProvider } from "react-query";
import HomePage from "./pages/Home";
import WatchPage from "./pages/Watch";
import UploadPage from "./pages/Upload";
import ProcessingProgressPage from "./pages/ProcessingProgress";

const darkTheme = createTheme({
  palette: {
    mode: "dark",
  },
});

const queryClient = new QueryClient();

function App() {
  return (
    <>
      <ThemeProvider theme={darkTheme}>
        <CssBaseline />
        <QueryClientProvider client={queryClient}>
          <BrowserRouter>
            <Routes>
              <Route
                path="/"
                element={<RootLayout />}
                errorElement={<ErrorPage />}
                id="root"
              >
                <Route path="" element={<HomePage />} />
                <Route path="watch/:videoId" element={<WatchPage />} />
                <Route path="upload">
                  <Route path="" element={<UploadPage />} />
                  <Route path=":videoId" element={<ProcessingProgressPage />} />
                </Route>
                <Route path="demo" element={<Demo />} />
                <Route path="*" element={<PageNotFound />} />
              </Route>
            </Routes>
          </BrowserRouter>
        </QueryClientProvider>
      </ThemeProvider>
    </>
  );
}

export default App;
