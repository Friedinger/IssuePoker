import type User from "@/types/User.ts";

export default interface Vote {
  id: string;
  user: User;
  vote: number;
}
