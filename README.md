# Nojoom Mobile — Android Companion App

A standalone Android companion to Nojoom Manager (desktop). Kotlin + Jetpack Compose + Room (local SQLite). No sync yet — this app has its own local database on the phone, separate from the desktop app's `nojoom.db`.

## What's included

- **Dashboard** — KPI cards (clients, orders, pending orders, revenue collected, outstanding balance), recent orders, link to Reps/Rewards
- **Clients** — list, search-ready DAO, add/edit/delete, per-client order history
- **Orders** — create order (client + rep + today's 24K gold rate), add line items (Gold/Silver/Stone), live cost & profit calculation per item and per order, generate invoice from an order
- **Inventory** — SKU-based items (Gold/Silver/Stone), karat, weight, quantity, category
- **Invoices** — list, detail, record payments (auto-updates status: Unpaid → Partial → Paid), void/unvoid with reversible status (mirrors the desktop cascade fix)
- **Representatives & Rewards** — Mohamed, Archad, Sagaf, Hachim, Nady seeded on first launch; reward counter auto-accumulates 21K-equivalent grams per invoice and pays out €100 every 30g, exactly like the desktop rule
- **Pricing formula** — identical to desktop: `Pure Gold Weight = Weight × Karat/24`, `Gold Cost = Pure Gold × 24K Rate`, `Profit = Price − Total Cost`
- Deep navy + gold theme matching the brand. No "FZE"/"LLC" anywhere in the UI (public-facing rule respected).

## Important: this needs to be compiled on your machine

I built the complete, real source code — but I do **not** have access to Google's Maven repository or the Android SDK in this sandbox, so I cannot produce a `.apk` here. This is a genuine Android Studio project, not a mockup.

### To build it yourself (15 minutes, one-time setup)

1. Install **Android Studio** (free): https://developer.android.com/studio
2. Unzip `NojoomMobile.zip` anywhere on your PC
3. Open Android Studio → **Open** → select the unzipped `NojoomMobile` folder
4. Let Gradle sync (first time downloads dependencies — needs internet)
5. Plug in your Android phone (USB debugging on) or use an emulator
6. Click the green **Run ▶** button

To get an installable `.apk` file to send to your reps for testing:
`Build → Build Bundle(s) / APK(s) → Build APK(s)` — the file lands in `app/build/outputs/apk/debug/`.

## What's next (once you've tested this)

- Wire order status changes (chips are currently display-only, one line of code to activate)
- Photo capture for inventory items (camera + local file storage)
- When you're ready for sync: point it at your Supabase project (same one the Phone Sync PWA already uses) or read directly from the desktop app's `nojoom.db`
- PDF invoice export matching the desktop ReportLab template

## Project structure

```
app/src/main/java/com/nojoom/mobile/
├── data/           Room entities, DAOs, database
├── repository/     Single repository wrapping all DAOs
├── ui/
│   ├── screens/     One file per screen
│   ├── theme/        Navy/gold Compose theme
│   ├── nav/           Route constants
│   └── AppViewModel.kt   All business logic (order totals, reward calc, invoice generation)
└── util/Pricing.kt   Gold cost/profit formulas
```
