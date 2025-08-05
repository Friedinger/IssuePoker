import { createRouter, createWebHashHistory } from "vue-router";

import IssuesListView from "@/components/IssueList.vue";
import {
  ROUTES_HOME,
  ROUTES_ISSUES_CREATE,
  ROUTES_ISSUES_DETAIL,
  ROUTES_ISSUES_LIST,
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
    path: "/issues",
    name: ROUTES_ISSUES_LIST,
    component: IssuesListView,
  },
  {
    path: "/issues/:id",
    name: ROUTES_ISSUES_DETAIL,
    component: IssueDetailsView,
  },
  {
    path: "/issues/new",
    name: ROUTES_ISSUES_CREATE,
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
