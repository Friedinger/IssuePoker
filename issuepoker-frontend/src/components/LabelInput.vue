<template>
  <v-input v-bind="$attrs">
    <v-field class="ps-4 pt-2 pb-2">
      <v-chip-group>
        <v-chip
          v-for="(color, name) in modelValue"
          :key="name"
          :style="`color: ${color}`"
          closable
          @click:close="removeLabel(name)"
        >
          {{ name }}
        </v-chip>
      </v-chip-group>
      <v-btn
        class="align-self-center"
        density="compact"
        icon
        rounded="rounded"
        variant="tonal"
      >
        <v-icon>{{ mdiPlus }}</v-icon>
        <v-menu
          v-model="menuOpen"
          :close-on-content-click="false"
          activator="parent"
        >
          <v-card>
            <v-card-title>Label hinzufügen</v-card-title>
            <v-card-text>
              <v-form
                v-model="newLabelValid"
                class="d-flex flex-column ga-4"
                @submit.prevent
              >
                <v-text-field
                  v-model="newLabelName"
                  :rules="[validateLabelName]"
                  density="comfortable"
                  hide-details="auto"
                  label="Label"
                />
                <v-color-picker
                  v-model="newLabelColor"
                  mode="hexa"
                />
                <v-btn
                  :disabled="!newLabelValid"
                  type="submit"
                  @click="addLabel"
                  >Hinzufügen</v-btn
                >
              </v-form>
            </v-card-text>
          </v-card>
        </v-menu>
      </v-btn>
    </v-field>
  </v-input>
</template>

<script lang="ts" setup>
import type { VInput } from "vuetify/components";

import { mdiPlus } from "@mdi/js";
import { ref } from "vue";

const props = defineProps<{ modelValue: Record<string, string> }>();
const emit =
  defineEmits<
    (e: "update:modelValue", value: Record<string, string>) => void
  >();

const newLabelName = ref("");
const newLabelColor = ref("#000000");
const newLabelValid = ref();
const menuOpen = ref(false);

function addLabel() {
  const name = newLabelName.value.trim();
  const color = newLabelColor.value;
  if (!name || name in props.modelValue) return;
  emit("update:modelValue", { ...props.modelValue, [name]: color });
  newLabelName.value = "";
  newLabelColor.value = "";
  menuOpen.value = false;
}

function validateLabelName(value: string) {
  if (value.trim().length < 1) return "Bitte ein Label angeben.";
  if (value in props.modelValue) return "Label existiert bereits.";
  if (value.length > 255)
    return "Label darf nicht länger als 255 Zeichen sein.";
  return true;
}

function removeLabel(name: string) {
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  const { [name]: _, ...rest } = props.modelValue;
  emit("update:modelValue", rest);
}
</script>
