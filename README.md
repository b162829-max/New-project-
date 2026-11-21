# Driver Accounts — Bilal Hussain

English-only driver accounts Android project with a premium Black & Gold dashboard.
Dashboard identity: Bilal Hussain — Driver ID HL5772.

## Earnings rules
- Uber, Yango, Bolt: only Credit and For Us contribute to Daily Earnings. Cash and Card Collection are recorded separately and do not add to earnings.
- On Trip (currently labeled Own Trip in the app): the full entered Cash and Card Collection amounts contribute to Daily Earnings.
- Company Trip: the full Amount contributes to Daily Earnings with no deduction.
- Daily and monthly totals are recalculated using these rules, including previously saved records.
- Legacy OWN Trip and other legacy entries are retained during record conversion.

## Reports
Daily and monthly text/PDF sharing includes individual fields, daily grand totals and monthly grand total.

## Build APK on GitHub
1. Upload the extracted project files to the repository root (keep `.github`).
2. Open Actions and run **Build Driver Accounts APK**.
3. Download the `Driver-Accounts-APK` artifact.

The workflow uses Java 17, Android SDK 35, Android Gradle Plugin 8.7.3 and Gradle 8.9.
Default login: username `BILAL`, PIN `8484`. Change these credentials in the app after login.
Records and profile photo are stored locally on this phone. Back up regularly.

## Additional daily entries
- Salary, Salik Toll, Car Expenses, EV Charging and Uzair Collected are recorded separately for each date.
- WhatsApp text and PDF reports omit all zero-value and blank fields; only entered non-zero amounts are shared.
- Additional entries are tracked separately and do not change the Daily Earnings calculation.
