<template>
  <v-container>
    <v-row align="center">
      <v-col>
        <v-img
          class="my-3"
          height="200"
          src="@/assets/IssuePoker_Logo_Light.svg"
        />
      </v-col>
      <v-col>
        <h1 class="text-h3 font-weight-bold mb-3 text-center">
          Willkommen beim Issue Poker
        </h1>
        <p v-if="getUser">
          <strong>Angemeldet als:</strong> {{ getUser.preferred_username }} ({{
            getUser.email
          }}, {{ getUser.sub }})
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
import { storeToRefs } from "pinia";
import { onMounted, ref } from "vue";

import { checkHealth } from "@/api/health-client";
import IssueList from "@/components/IssueList.vue";
import { useSnackbarStore } from "@/stores/snackbar";
import { useUserStore } from "@/stores/user.ts";
import HealthState from "@/types/HealthState";

const snackbarStore = useSnackbarStore();
const { getUser } = storeToRefs(useUserStore());
const status = ref("DOWN");

onMounted(() => {
  checkHealth()
    .then((content: HealthState) => (status.value = content.status))
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
