import { create } from "zustand";
import { MemberResponse } from "@/types";

interface MemberState {
  member: MemberResponse | null;
  setMember: (member: MemberResponse) => void;
  clearMember: () => void;
}

export const useMemberStore = create<MemberState>((set) => ({
  member: null,
  setMember: (member: MemberResponse) => set({ member }),
  clearMember: () => set({ member: null }),
}));
