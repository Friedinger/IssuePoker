import { createRouter, createWebHashHistory } from "vue-router";

import {
  ROUTES_HOME,
  ROUTES_ISSUE_CREATE,
  ROUTES_ISSUE_DETAIL,
} from "@/constants";
import HomeView from "@/views/HomeView.vue";
import IssuesCreateView from "@/views/IssueCreateView.vue";
import IssueDetailsView from "@/views/IssueDetailsView.vue";

const routes = [
  {
    path: "/",
    name: ROUTES_HOME,
    component: HomeView,
    meta: {},
  },
  {
    path: "/issues/:id",
    name: ROUTES_ISSUE_DETAIL,
    component: IssueDetailsView,
  },
  {
    path: "/issues/new",
    name: ROUTES_ISSUE_CREATE,
    component: IssuesCreateView,
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
