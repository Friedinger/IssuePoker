// @formatter:off
// Composables
import {createRouter, createWebHashHistory} from "vue-router";

import {ROUTES_HOME, ROUTES_ISSUES_LIST} from "@/constants";
import IssuesListView from "@/views/IssueListView.vue";
import HomeView from "@/views/HomeView.vue";

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
