import type IssueDetails from "@/types/IssueDetails.ts";
import type IssueSummary from "@/types/IssueSummary.ts";
import type Page from "@/types/Page.ts";
import type { SortItem } from "vuetify/lib/components/VDataTable/composables/sort.js";

import { storeToRefs } from "pinia";
import { readonly, ref } from "vue";

import { getIssueList } from "@/api/issue/get-issueList.ts";
import { ROUTES_ISSUE_DETAIL } from "@/constants.ts";
import router from "@/plugins/router.ts";
import { useFilterStore } from "@/stores/filter.ts";
import { useSnackbarStore } from "@/stores/snackbar.ts";

export function useIssueList() {
  const snackbarStore = useSnackbarStore();
  const { getFilter } = storeToRefs(useFilterStore());

  const loading = ref(true);
  const issues = ref<IssueSummary[]>([]);
  const totalIssues = ref(0);
  const page = ref<number>(1);
  const itemsPerPage = ref<number>(10);
  const sortBy = ref<SortItem[]>([
    { key: "owner", order: "asc" },
    { key: "repository", order: "asc" },
    { key: "number", order: "asc" },
  ]);

  function fetchIssues() {
    loading.value = true;
    getIssueList(
      page.value - 1,
      itemsPerPage.value,
      sortBy.value,
      getFilter.value
    )
      .then((content: Page<IssueSummary>) => {
        issues.value = content.content;
        loading.value = false;
        totalIssues.value = content.page.totalElements;
      })
      .catch((error) => {
        snackbarStore.showMessage(error);
        loading.value = false;
      });
  }

  function updateOptions({
    page: newPage,
    itemsPerPage: newItemsPerPage,
    sortBy: newSortBy,
  }: {
    page: number;
    itemsPerPage: number;
    sortBy: SortItem[];
  }) {
    page.value = newPage;
    itemsPerPage.value = newItemsPerPage;
    sortBy.value = newSortBy;
    fetchIssues();
  }

  function goToIssue(
    _: MouseEvent,
    props: { item: IssueDetails; index: number }
  ) {
    router
      .push({
        name: ROUTES_ISSUE_DETAIL,
        params: {
          owner: props.item.owner,
          repository: props.item.repository,
          number: props.item.number,
        },
      })
      .then();
  }

  return {
    issues: readonly(issues),
    totalIssues: readonly(totalIssues),
    itemsPerPage: readonly(itemsPerPage),
    loading: readonly(loading),
    page: readonly(page),
    search: getFilter.value.search,
    sortBy: readonly(sortBy),
    fetchIssues,
    updateOptions,
    goToIssue,
  };
}
