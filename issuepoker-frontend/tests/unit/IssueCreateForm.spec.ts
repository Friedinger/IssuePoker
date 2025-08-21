import { mount, VueWrapper } from "@vue/test-utils";
import { createPinia } from "pinia";
import { describe, expect, it, vi } from "vitest";
import { createVuetify } from "vuetify";
import * as components from "vuetify/components";
import * as directives from "vuetify/directives";

// @ts-expect-error: "TS2307 cannot find module" is a false positive here
import IssueCreateForm from "@/components/IssueCreateForm.vue";
import { ROUTES_HOME, ROUTES_ISSUE_DETAIL } from "../../src/constants";

vi.mock("@/api/create-issue.ts", () => ({
  createIssue: vi.fn().mockResolvedValue({
    owner: "Mock Owner",
    repository: "Mock Repository",
    number: 43,
  }),
}));
vi.mock("@/plugins/router.ts", () => ({
  default: { push: vi.fn() },
}));

// @ts-expect-error: "TS2307 cannot find module" is a false positive here
const { createIssue } = await import("@/api/create-issue.ts");
// @ts-expect-error: "TS2307 cannot find module" is a false positive here
const router = (await import("@/plugins/router.ts")).default;

const pinia = createPinia();
const vuetify = createVuetify({ components, directives });

function factory() {
  return mount(IssueCreateForm, {
    global: {
      plugins: [pinia, vuetify],
      stubs: ["router-link"],
    },
  });
}

function findField(
  wrapper: VueWrapper<IssueCreateForm>,
  componentName: string,
  label: string
) {
  if (componentName === "VBtn") {
    return wrapper
      .findAllComponents({ name: "VBtn" })
      .find((b) => b.text().trim() === label);
  }
  return wrapper
    .findAllComponents({ name: componentName })
    .find((c) => c.props("label") === label);
}

function getFormFields(wrapper: VueWrapper<IssueCreateForm>) {
  return {
    saveBtn: findField(wrapper, "VBtn", "Speichern"),
    cancelBtn: findField(wrapper, "VBtn", "Abbrechen"),
    ownerField: findField(wrapper, "VTextField", "Besitzer"),
    repoField: findField(wrapper, "VTextField", "Repository"),
    numberField: findField(wrapper, "VNumberInput", "Nummer"),
    titleField: findField(wrapper, "VTextField", "Titel"),
    descriptionField: findField(wrapper, "VTextarea", "Beschreibung"),
  };
}

describe("IssueCreateForm", () => {
  it("showsInputFields", () => {
    const wrapper = factory();
    const { ownerField, repoField, numberField, titleField, descriptionField } =
      getFormFields(wrapper);

    expect(ownerField).toBeTruthy();
    expect(repoField).toBeTruthy();
    expect(numberField).toBeTruthy();
    expect(titleField).toBeTruthy();
    expect(descriptionField).toBeTruthy();
  });

  it("showsButtons", () => {
    const wrapper = factory();
    const { saveBtn, cancelBtn } = getFormFields(wrapper);

    expect(saveBtn).toBeTruthy();
    expect(saveBtn?.attributes("type")).toEqual("submit");
    expect(cancelBtn).toBeTruthy();
    expect(cancelBtn?.props("to")).toEqual({ name: ROUTES_HOME });
  });

  it("deactivatesSubmitButton", async () => {
    const wrapper = factory();
    const {
      saveBtn,
      ownerField,
      repoField,
      numberField,
      titleField,
      descriptionField,
    } = getFormFields(wrapper);

    await wrapper.vm.$nextTick();
    expect(saveBtn.props("disabled")).toBe(true);

    await descriptionField.setValue("Test Description");
    await wrapper.vm.$nextTick();
    expect(saveBtn.props("disabled")).toBe(true);

    await ownerField.setValue("Test Owner");
    await wrapper.vm.$nextTick();
    expect(saveBtn.props("disabled")).toBe(true);

    await repoField.setValue("Test Repository");
    await wrapper.vm.$nextTick();
    expect(saveBtn.props("disabled")).toBe(true);

    await numberField.setValue(42);
    await wrapper.vm.$nextTick();
    expect(saveBtn.props("disabled")).toBe(true);

    await titleField.setValue("Test Title");
    await wrapper.vm.$nextTick();
    expect(saveBtn.props("disabled")).toBe(false);

    await descriptionField.setValue("");
    expect(saveBtn.props("disabled")).toBe(false);
  });

  it("submitCallsApiAndRedirectsDetails", async () => {
    const wrapper = factory();
    const {
      saveBtn,
      ownerField,
      repoField,
      numberField,
      titleField,
      descriptionField,
    } = getFormFields(wrapper);

    await ownerField.setValue("Test Owner");
    await repoField.setValue("Test Repository");
    await numberField.setValue(42);
    await titleField.setValue("Test Title");
    await descriptionField.setValue("Test Description");
    await wrapper.vm.$nextTick();

    await saveBtn.trigger("click");
    expect(createIssue).toHaveBeenCalledWith(
      "Test Owner",
      "Test Repository",
      42,
      "Test Title",
      "Test Description"
    );
    expect(router.push).toHaveBeenCalledWith({
      name: ROUTES_ISSUE_DETAIL,
      params: {
        owner: "Mock Owner",
        repository: "Mock Repository",
        number: 43,
      },
    });
  });
});
