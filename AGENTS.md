Global rules for YOU working in this repo. These are **hard rules, not suggestions** — they cannot be skipped, watered down, or reasoned around "just this once." If a rule seems to conflict with a specific task, stop and ask rather than silently ignoring it.

## 1. Simplicity over cleverness
- Write **one clear, correct implementation** per problem. Do not write multiple fallback paths "just in case."
- If a fallback exists that only fires when the primary path is broken, that fallback is dead code. Delete it. Fix the root cause instead.
- On failure: fail fast and loud (raise / log clearly). Never silently catch-and-continue to fake success.

## 2. File structure
- **No monofiles.** Code must be split into multiple files by logical concern (e.g. models, routes, utils, config kept separate).
- No fixed line-count limit — use judgment — but a single file holding "the whole app" is never acceptable.
- **No duplicated logic across files.** If the same logic appears more than once, extract it into a shared module and import it. Copy-pasted blocks are a bug, not a shortcut.

## 3. Libraries over hand-rolled code
- Do not hand-roll logic (parsing, HTTP, retries, scraping, auth, date handling, etc.) when a battle-tested, actively maintained library exists. A larger dependency list is fine and preferred over reinventing solved problems.
- **Never pick a library from memory/training data alone.** Actively web search for current options before choosing one — a library that was standard a year or two ago may now be outdated, unmaintained, or superseded. Verify the library is still actively maintained before adding it.

## 4. Never assume — verify first
- Before writing any code against an API, library, or web page, the agent MUST verify it first. This applies always, not only when something "seems" unfamiliar.
- Verification means, as applicable:
  - **Web search** for current docs, changelogs, and known issues.
  - **`git clone` and read the actual source** of the library/tool in question to confirm real function signatures, schemas, and behavior — do not trust remembered APIs.
  - **Real browser inspection with zendriver** for any web page/site being scraped or integrated with — inspect the live DOM/network requests, don't guess selectors or endpoints.
- If verification is skipped and code is written from assumption, that is a rule violation and the work must be redone.

## 5. Testing — real, not mocked
- Every change requires **unit tests**.
- In addition, you must **actually run the real program/service as a real running instance** and exercise it with **real web requests** — no mocking network calls, external services, or dependencies to fake a passing test.
- A change is not "done" until it has been verified running for real, not just passing mocked tests.

## 6. Commit often
- Commit frequently to avoid lost work. Granularity is the agent's judgment call, but uncommitted work should never pile up into one large, unreviewable change.

---

**Summary for the agent:** simple > clever, split files, don't repeat yourself, use maintained libraries (found via live search, not memory), verify everything against real sources/browsers before coding, test against real running instances with real requests, and commit often.
