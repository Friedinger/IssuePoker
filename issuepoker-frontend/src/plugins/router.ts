import { createRouter, createWebHashHistory } from "vue-router";

import {
  ROUTES_HOME,
  ROUTES_ISSUES_DETAIL,
  ROUTES_ISSUES_LIST,
} from "@/constants";
import HomeView from "@/views/HomeView.vue";
import IssueDetailsView from "@/views/IssueDetailsView.vue";
import IssuesListView from "@/views/IssueListView.vue";

const routes = [
  {
    path: "/",
    name: ROUTES_HOME,
    component: HomeView,
    meta: {},
  },
  {
    path: "/issues",
    name: ROUTES_ISSUES_LIST,
    component: IssuesListView,
  },
  {
    path: "/issue/:id",
    name: ROUTES_ISSUES_DETAIL,
    component: IssueDetailsView,
  },
  { path: "/:catchAll(.*)*", redirect: "/" }, // CatchAll route
];

const router = createRouter({
  history: createWebHashHistory(),
  routes,
  scrollBehavior() {
    return {
      top: 0,
      left: 0,
    };
  },
});

export default router;
