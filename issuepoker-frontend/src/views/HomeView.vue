<template>
  <v-container>
    <v-row class="text-center">
      <v-col cols="12">
        <v-img
          class="my-3"
          height="200"
          src="@/assets/IssuePoker Logo Light.svg"
        />
      </v-col>

      <v-col class="mb-4">
        <h1 class="text-h3 font-weight-bold mb-3">
          Willkommen beim Issue Poker
        </h1>
        <p>
          Das API-Gateway ist:
          <span :class="status">{{ status }}</span>
        </p>
        <p>{{ entity }}</p>
        <p v-if="user">
          User:
          {{ user.email }}
        </p>
      </v-col>
    </v-row>
  </v-container>
</template>

<script lang="ts" setup>
import type { TheEntity } from "@/api/fetch-theentity.ts";
import type User from "@/types/User.ts";

import { onMounted, ref } from "vue";

import { getEntity } from "@/api/fetch-theentity.ts";
import { checkHealth } from "@/api/health-client";
import { getUser } from "@/api/user-client.ts";
import { useSnackbarStore } from "@/stores/snackbar";
import HealthState from "@/types/HealthState";

const snackbarStore = useSnackbarStore();
const status = ref("DOWN");
const user = ref<User>();
const entity = ref<TheEntity>();

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
  getEntity()
    .then((content: TheEntity) => (entity.value = content))
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
