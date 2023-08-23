import { createSlice } from "@reduxjs/toolkit";

const uiSlice = createSlice({
  name: "ui",
  initialState: { expandNav: false },
  reducers: {
    toggle(state) {
      state.expandNav = !state.expandNav;
    },
  },
});

export const uiActions = uiSlice.actions;

export default uiSlice;
