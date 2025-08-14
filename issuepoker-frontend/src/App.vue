<template>
  <v-app>
    <the-snackbar />
    <v-app-bar color="primary">
      <v-row align="center">
        <v-col
          class="d-flex align-center justify-start"
          cols="3"
        >
          <v-app-bar-nav-icon @click.stop="toggleDrawer()" />
          <router-link
            class="flex-fill"
            to="/"
          >
            <v-col class="d-flex align-center justify-start">
              <v-img
                contain
                height="auto"
                max-width="50"
                src="@/assets/IssuePoker_Icon.svg"
              />
              <v-toolbar-title class="font-weight-bold">
                <span class="text-white">Issue</span>
                <span class="text-secondary">Poker</span>
              </v-toolbar-title>
            </v-col>
          </router-link>
        </v-col>
        <v-col
          class="d-flex align-center justify-center"
          cols="6"
        >
          <v-text-field
            id="searchField"
            v-model="query"
            :prepend-inner-icon="mdiMagnify"
            clearable
            flat
            hide-details
            label="Suche"
            theme="dark"
            variant="solo-inverted"
            @keyup.enter="search"
            @click:clear="search"
          />
        </v-col>
        <v-col
          class="d-flex align-center justify-end"
          cols="3"
        >
          <app-switcher
            v-if="appswitcherBaseUrl"
            :base-url="appswitcherBaseUrl"
            :icon="mdiApps"
            :tags="['global']"
          />
          <v-btn
            icon
            variant="text"
          >
            <ad2-image-avatar
              v-if="userStore.getUser !== null"
              :username="userStore.getUser.preferred_username"
            />
          </v-btn>
        </v-col>
      </v-row>
    </v-app-bar>
    <v-navigation-drawer v-model="drawer">
      <v-list>
        <v-list-item :to="{ name: ROUTES_HOME }">
          <v-list-item-title>Home</v-list-item-title>
        </v-list-item>
      </v-list>
    </v-navigation-drawer>
    <v-main>
      <v-container fluid>
        <router-view v-slot="{ Component }">
          <v-fade-transition mode="out-in">
            <component :is="Component" />
          </v-fade-transition>
        </router-view>
      </v-container>
    </v-main>
  </v-app>
</template>

<script lang="ts" setup>
import type { VotingOptions } from "@/stores/votingOptions.ts";
import type User from "@/types/User";
import type { LocationQueryValue } from "vue-router";

import { mdiApps, mdiMagnify } from "@mdi/js";
import { AppSwitcher } from "@muenchen/appswitcher-vue";
import { useToggle } from "@vueuse/core";
import { onMounted, ref, watch } from "vue";
import { useRoute } from "vue-router";

import { getVotingOptions } from "@/api/fetch-votingOptions.ts";
import { getUser } from "@/api/user-client";
import Ad2ImageAvatar from "@/components/common/Ad2ImageAvatar.vue";
import TheSnackbar from "@/components/TheSnackbar.vue";
import { APPSWITCHER_URL, ROUTES_HOME } from "@/constants";
import router from "@/plugins/router.ts";
import { useSnackbarStore } from "@/stores/snackbar";
import { useUserStore } from "@/stores/user";
import { useVotingOptionsStore } from "@/stores/votingOptions.ts";

const query = ref("");
const appswitcherBaseUrl = APPSWITCHER_URL;

const snackbarStore = useSnackbarStore();
const userStore = useUserStore();
const votingOptionsStore = useVotingOptionsStore();
const [drawer, toggleDrawer] = useToggle();
const route = useRoute();

onMounted(() => {
  loadUser();
  getVotingOptions()
    .then((content: VotingOptions) =>
      votingOptionsStore.setVotingOptions(content)
    )
    .catch((error) => {
      votingOptionsStore.setVotingOptions([]);
      snackbarStore.showMessage(error);
    });
  parseSearch(route.query.search);
});

watch(
  () => route.query.search,
  (value) => parseSearch(value)
);

/**
 * Loads UserInfo from the backend and sets it in the store.
 */
function loadUser(): void {
  getUser()
    .then((user: User) => userStore.setUser(user))
    .catch(() => {
      // No user info received, so fallback
      userStore.setUser(null);
    });
}

/**
 * Navigates to the page with the search results and sends an event to trigger further searches.
 */

function search() {
  if (query.value !== "") {
    router.push({ name: ROUTES_HOME, query: { search: query.value } });
  } else {
    router.push({ name: ROUTES_HOME });
  }
}

function parseSearch(value: LocationQueryValue | LocationQueryValue[]) {
  if (Array.isArray(value)) {
    value = value[0];
  }
  query.value = value || "";
}
</script>
