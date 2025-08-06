<template>
  <v-container>
    <v-row align="center">
      <v-col>
        <v-img
          class="my-3"
          height="200"
          src="@/assets/IssuePoker Logo Light.svg"
        />
      </v-col>
      <v-col>
        <h1 class="text-h3 font-weight-bold mb-3 text-center">
          Willkommen beim Issue Poker
        </h1>
        <p v-if="user">
          <strong>Angemeldet als:</strong> {{ user.name }} ({{ user.email }},
          {{ user.sub }})
        </p>
        <br />
        <p>
          Das API-Gateway ist:
          <span :class="status">{{ status }}</span>
        </p>
      </v-col>
    </v-row>
    <issue-list />
  </v-container>
</template>

<script lang="ts" setup>
import type User from "@/types/User.ts";

import { onMounted, ref } from "vue";

import { checkHealth } from "@/api/health-client";
import { getUser } from "@/api/user-client.ts";
import IssueList from "@/components/IssueList.vue";
import { useSnackbarStore } from "@/stores/snackbar";
import HealthState from "@/types/HealthState";

const snackbarStore = useSnackbarStore();
const status = ref("DOWN");
const user = ref<User>();

onMounted(() => {
  checkHealth()
    .then((content: HealthState) => (status.value = content.status))
    .catch((error) => {
      snackbarStore.showMessage(error);
    });
  getUser()
    .then((content: User) => (user.value = content))
    .catch((error) => {
      snackbarStore.showMessage(error);
    });
});
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
