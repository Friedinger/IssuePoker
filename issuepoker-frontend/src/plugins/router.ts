import { createRouter, createWebHashHistory } from "vue-router";

import {
  ROLE_ADMIN,
  ROUTES_HOME,
  ROUTES_ISSUE_DETAIL,
  ROUTES_ISSUE_EDIT,
  ROUTES_ISSUE_NEW,
} from "@/constants";
import { useUserStore } from "@/stores/user.ts";
import { isAdmin } from "@/util/userUtils.ts";
import HomeView from "@/views/HomeView.vue";
import IssueCreateView from "@/views/IssueCreateView.vue";
import IssueDetailsView from "@/views/IssueDetailsView.vue";

const routes = [
  {
    path: "/",
    name: ROUTES_HOME,
    component: HomeView,
  },
  {
    path: "/:owner/:repository/issues/:number",
    name: ROUTES_ISSUE_DETAIL,
    component: IssueDetailsView,
  },
  {
    path: "/:owner/:repository/issues/:number/:action",
    name: ROUTES_ISSUE_EDIT,
    component: IssueCreateView,
    beforeEnter: () => {
      return useUserStore().getUser?.authorities.includes(ROLE_ADMIN);
    },
  },
  {
    path: "/new",
    name: ROUTES_ISSUE_NEW,
    component: IssueCreateView,
    beforeEnter: () => {
      return useUserStore().getUser?.authorities.includes(ROLE_ADMIN);
    },
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
