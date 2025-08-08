import { defineConfig } from "vitepress";
import { withMermaid } from "vitepress-plugin-mermaid";

// https://vitepress.dev/reference/site-config
const vitepressConfig = defineConfig({
  title: "IssuePoker Docs",
  description: "Documentation from IssuePoker",
  head: [
    [
      "link",
      {
        rel: "icon",
        href: `https://assets.muenchen.de/logos/lhm/icon-lhm-muenchen-32.png`,
      },
    ],
  ],
  lastUpdated: true,
  themeConfig: {
    // https://vitepress.dev/reference/default-theme-config
    nav: [
      { text: "Home", link: "/" },
      {
        text: "Docs",
        items: [
          { text: "Data model", link: "/datamodel" },
          {
            text: "Repository",
            link: "https://github.com/Friedinger/IssuePoker",
          },
        ],
      },
    ],
    sidebar: [
      { text: "Data model", link: "/datamodel" },
      { text: "Repository", link: "https://github.com/Friedinger/IssuePoker" },
    ],
    socialLinks: [
      { icon: "github", link: "https://github.com/Friedinger/IssuePoker" },
    ],
    editLink: {
      pattern: "https://github.com/Friedinger/IssuePoker/blob/main/docs/:path",
      text: "View this page on GitHub",
    },
    footer: {
      message: `<a href="https://opensource.muenchen.de/impress.html">Impress and Contact</a>`,
    },
    outline: {
      level: "deep",
    },
    search: {
      provider: "local",
    },
  },
  markdown: {
    image: {
      lazyLoading: true,
    },
  },
});

export default withMermaid(vitepressConfig);
