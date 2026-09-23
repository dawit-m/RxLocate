# RxLocate design notes

**Goal:** a calm, fast, trustworthy interface for two audiences: patients on a phone who need an answer quickly, and pharmacy staff who update stock all day.

## Principles
- **The search field is the product.** The landing page opens with it, and the finder keeps it on top.
- **Status is colour, but never only colour.** Stock signals borrow the Ethiopian flag (green, amber, red) and always carry a text label.
- **Structure only where content has structure.** Numbers appear only on the three-step sequence. Data lives in lists and tables, not in identical cards.
- **One flourish per page.** The landing hero rows rise in once; everything else responds only to user actions.

## Tokens (`static/css/style.css`)
| Token | Value | Use |
| --- | --- | --- |
| `--pine` | `#0A6B4A` | Primary actions, brand |
| `--pine-900` | `#0A2F23` | Dark sections, lead KPI |
| `--mint` | `#E4F3EB` | Tints, hover, focus ring |
| `--paper` / `--surface` | `#F6F8F6` / `#FFFFFF` | Page and panels |
| `--amber`, `--red` | `#8F5A00`, `#B0311D` | Low stock, out of stock / expired |

Type: **Bricolage Grotesque** (headings, big numbers), **Instrument Sans** (UI and body), **DM Mono** (USSD screens only). Noto Sans Ethiopic is in the stack so Amharic text renders correctly.

## Structure
- `templates/fragments/layout.html`: shared head, header, footer and SVG icon sprite.
- `css/style.css` (system + public pages), `css/auth.css`, `css/dashboard.css`.
- `js/search.js`, `js/auth.js`, `js/dashboard.js`: progressive enhancement only; every action still works as a plain form post.

## Accessibility
Skip link, visible focus rings, labelled controls, `aria-pressed` filters, `prefers-reduced-motion` respected, responsive down to 360px.
