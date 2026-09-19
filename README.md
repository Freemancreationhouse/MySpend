# My Spend V1.0
Android offline-first personal spending dashboard.

## Included in this source build
- Unified local transaction database (SQLite)
- Dashboard: expense, income and net
- Manual expense entry and categories
- Recent transaction timeline
- Notification-listener based detection for recognizable payment notifications
- Basic merchant category rules
- Duplicate protection using unique transaction references
- No internet permission; data stays on-device

## Build
1. Open this folder in Android Studio (Ladybug or newer recommended).
2. Let Gradle sync.
3. Run on Android 8.0+ device.
4. In My Spend tap **Enable automatic UPI notification detection**, then explicitly enable My Spend in Android's Notification Access screen.

## Important V1 limitations
Notification text differs by bank/payment app and can change. V1 does not claim direct access to PhonePe, Paytm, BHIM, or MobiKwik accounts. It only parses recognizable notifications shown on the device. CSV/PDF statement import, budgets, charts, transaction editing and encrypted DB hardening are planned for the next implementation pass.
