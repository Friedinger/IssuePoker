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
const content = ref<string>(parseContent(props.content));

watch(
  () => props.content,
  (value) => (content.value = parseContent(value))
);

function parseContent(content: string): string {
  content = checkboxesToHtml(content);
  return content;
}

function checkboxesToHtml(content: string): string {
  return content.replace(
    /^\s*- \[([ x])]/gm,
    (_match, p1) =>
      `- <input type="checkbox"${p1 === "x" ? " checked" : ""} disabled>`
  );
}
</script>

<style scoped>
:deep(details details) {
  margin-left: 0.75rem;
}
:deep(ul),
:deep(ol) {
  margin-bottom: 0.5rem;
  margin-left: 1.5rem;
}
:deep(p),
:deep(details:not(:has(+ details)):not(:last-child)) {
  margin-bottom: 0.5rem;
}
:deep(li:has(input[type="checkbox"])) {
  margin-left: -1rem;
  list-style-type: none;
}
</style>
