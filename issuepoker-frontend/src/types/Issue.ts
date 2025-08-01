import type Vote from "@/types/Vote.ts";

export default interface Issue {
  id: number;
  title: string;
  description: string;
  votes: Vote[];
}
