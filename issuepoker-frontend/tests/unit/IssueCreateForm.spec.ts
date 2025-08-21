import { mount } from "@vue/test-utils";
import { createPinia } from "pinia";
import { describe, expect, it, vi } from "vitest";
import { createVuetify } from "vuetify";
import * as components from "vuetify/components";
import * as directives from "vuetify/directives";

// @ts-expect-error: "TS2307 cannot find module" is a false positive here
import IssueCreateForm from "@/components/IssueCreateForm.vue";
import { ROUTES_ISSUE_DETAIL } from "../../src/constants";

vi.mock("@/api/create-issue.ts", () => ({
  createIssue: vi.fn().mockResolvedValue({ id: 42 }),
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

describe("IssueCreateForm", () => {
  it("showsInputFields", () => {
    const wrapper = factory();
    expect(
      wrapper
        .findComponent({ name: "VTextField", props: "label=Besitzer" })
        .exists()
    ).toBe(true);
    expect(
      wrapper
        .findComponent({ name: "VTextField", props: "label=Repository" })
        .exists()
    ).toBe(true);
    expect(
      wrapper
        .findComponent({ name: "VNumberInput", props: "label=Nummer" })
        .exists()
    ).toBe(true);
    expect(
      wrapper
        .findComponent({ name: "VTextField", props: "label=Titel" })
        .exists()
    ).toBe(true);
    expect(
      wrapper
        .findComponent({ name: "VTextarea", props: "label=Beschreibung" })
        .exists()
    ).toBe(true);
  });

  it("showsButtons", () => {
    const wrapper = factory();
    const saveBtn = wrapper.findComponent({
      name: "VBtn",
      props: "label=Speichern,type=submit",
    });
    expect(saveBtn.exists()).toBe(true);
    const cancelBtn = wrapper.findComponent({
      name: "VBtn",
      props: "label=Abbrechen",
    });
    expect(cancelBtn.exists()).toBe(true);
    expect(cancelBtn.props("to")).toEqual({ name: "home" });
  });

  it("deactivatesSubmitButton", async () => {
    const wrapper = factory();
    const saveBtn = wrapper.findComponent({ name: "VBtn" });
    const titleField = wrapper.findComponent({ name: "VTextField" });
    const descriptionField = wrapper.findComponent({ name: "VTextarea" });
    await wrapper.vm.$nextTick();
    expect(saveBtn.props("disabled")).toBe(true);

    await descriptionField.setValue("Test Description");
    await wrapper.vm.$nextTick();
    expect(saveBtn.props("disabled")).toBe(true);

    await titleField.setValue("Test Issue");
    await wrapper.vm.$nextTick();
    expect(saveBtn.props("disabled")).toBe(false);

    await descriptionField.setValue("");
    expect(saveBtn.props("disabled")).toBe(false);
  });

  it("submitCallsApiAndRedirectsDetails", async () => {
    const wrapper = factory();
    const titleField = wrapper.findComponent({ name: "VTextField" });
    const descriptionField = wrapper.findComponent({ name: "VTextarea" });
    const saveBtn = wrapper.findComponent({ name: "VBtn" });

    await titleField.setValue("TestTitle");
    await descriptionField.setValue("TestDescription");
    await wrapper.vm.$nextTick();

    await saveBtn.trigger("click");
    expect(createIssue).toHaveBeenCalledWith("TestTitle", "TestDescription");
    expect(router.push).toHaveBeenCalledWith({
      name: ROUTES_ISSUE_DETAIL,
      params: { id: 42 },
    });
  });
});
