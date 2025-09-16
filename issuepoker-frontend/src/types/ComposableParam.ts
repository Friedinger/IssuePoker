import type { Ref } from "vue";

export type ComposableParam<T> = T | Ref<T> | (() => T);
