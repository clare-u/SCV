import { create } from "zustand";
import { UserResponse } from "@/types";

interface UserState {
  user: UserResponse | null;
  setUser: (user: UserResponse) => void;
  clearUser: () => void;
}

export const useUserStore = create<UserState>((set) => ({
  user: null,
  setUser: (user: UserResponse) => set({ user }),
  clearUser: () => set({ user: null }),
}));
