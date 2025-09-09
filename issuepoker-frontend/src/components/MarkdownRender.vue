<template>
  <vue-markdown
    :options="markdownOptions"
    :source="content"
  />
</template>

<script lang="ts" setup>
import { ref, watch } from "vue";
import VueMarkdown from "vue-markdown-render";

const markdownOptions = {
  html: true,
};
const props = defineProps<{
  content: string;
}>();
const content = ref<string>(props.content);

watch(
  () => props.content,
  (value) => (content.value = value)
);
</script>

<style scoped></style>
