<template>
  <v-app>
    <the-snackbar />
    <v-app-bar color="primary">
      <v-row align="center">
        <v-col
          class="d-flex align-center justify-start"
          cols="3"
        >
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
            v-if="route.name !== ROUTES_HOME"
            id="searchField"
            v-model="filter.search"
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
          <v-btn icon>
            <v-icon
              :class="
                statusGateway === 'UP' && statusBackend === 'UP' ? 'UP' : 'DOWN'
              "
              :icon="mdiInformationOutline"
            />
            <v-menu activator="parent">
              <v-list>
                <v-list-item>
                  <strong>Gateway: </strong>
                  <span :class="statusGateway">{{ statusGateway }}</span>
                </v-list-item>
                <v-list-item>
                  <strong>Backend: </strong>
                  <span :class="statusBackend">{{ statusBackend }}</span>
                </v-list-item>
              </v-list>
            </v-menu>
          </v-btn>
          <v-btn
            icon
            variant="text"
          >
            <ad2-image-avatar
              v-if="user"
              :username="user.preferred_username"
            />
            <v-menu
              v-if="user"
              activator="parent"
            >
              <v-list>
                <v-list-item>
                  <strong>Nutzername:</strong>
                  {{ user.preferred_username }}
                </v-list-item>
                <v-list-item v-if="user.givenName">
                  <strong>Vorname:</strong>
                  {{ user.givenName }}
                </v-list-item>
                <v-list-item v-if="user.familyName">
                  <strong>Nachname:</strong>
                  {{ user.familyName }}
                </v-list-item>
                <v-list-item>
                  <strong>Email:</strong>
                  {{ user.email }}
                </v-list-item>
                <v-list-item>
                  <strong>ID:</strong>
                  {{ user.sub }}
                </v-list-item>
                <v-list-item>
                  <strong>Rollen:</strong>
                  {{
                    user.authorities
                      .map((authority) => authority.split("_")[1])
                      .join(", ")
                  }}
                </v-list-item>
              </v-list>
            </v-menu>
          </v-btn>
        </v-col>
      </v-row>
    </v-app-bar>
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

import { mdiApps, mdiInformationOutline, mdiMagnify } from "@mdi/js";
import { AppSwitcher } from "@muenchen/appswitcher-vue";
import { storeToRefs } from "pinia";
import { computed, onMounted, ref } from "vue";
import { useRoute } from "vue-router";

import { getVotingOptions } from "@/api/issue/get-votingOptions.ts";
import {
  checkBackendHealth,
  checkGatewayHealth,
} from "@/api/misc/health-client.ts";
import { getUser } from "@/api/misc/user-client.ts";
import Ad2ImageAvatar from "@/components/common/Ad2ImageAvatar.vue";
import TheSnackbar from "@/components/TheSnackbar.vue";
import { APPSWITCHER_URL, ROUTES_HOME } from "@/constants";
import router from "@/plugins/router.ts";
import { useFilterStore } from "@/stores/filter.ts";
import { useSnackbarStore } from "@/stores/snackbar";
import { useUserStore } from "@/stores/user";
import { useVotingOptionsStore } from "@/stores/votingOptions.ts";
import HealthState from "@/types/HealthState.ts";

const appswitcherBaseUrl = APPSWITCHER_URL;
const snackbarStore = useSnackbarStore();
const userStore = useUserStore();
const { getUser: user } = storeToRefs(userStore);
const votingOptionsStore = useVotingOptionsStore();
const filter = computed({
  get: () => useFilterStore().getFilter,
  set: (value) => {
    useFilterStore().setFilter(value);
  },
});
const route = useRoute();
const statusGateway = ref("DOWN");
const statusBackend = ref("DOWN");

onMounted(() => {
  getUser()
    .then((user: User) => userStore.setUser(user))
    .catch((error) => {
      userStore.setUser(null);
      snackbarStore.showMessage(error);
    });
  getVotingOptions()
    .then((content: VotingOptions) =>
      votingOptionsStore.setVotingOptions(content)
    )
    .catch((error) => {
      votingOptionsStore.setVotingOptions([]);
      snackbarStore.showMessage(error);
    });
  checkGatewayHealth()
    .then((content: HealthState) => (statusGateway.value = content.status))
    .catch((error) => {
      snackbarStore.showMessage(error);
    });
  checkBackendHealth()
    .then((content: HealthState) => (statusBackend.value = content.status))
    .catch((error) => {
      snackbarStore.showMessage(error);
    });
});

function search() {
  router.push({ name: ROUTES_HOME });
}
</script>

<style scoped>
/*noinspection CssUnusedSymbol*/
.UP {
  color: limegreen;
}

/*noinspection CssUnusedSymbol*/
.DOWN {
  color: lightcoral;
}
</style>
