# Project Plan

Chef AI Revan (KOST CHEF) - A Neo-Brutalist budget AI chef app tailored for Indonesian "anak kos". Changes include: Indonesian localized theme (Merah Putih accents), Rupiah (IDR) currency support, Indonesian recipe mocks, and feature renaming (Mode Tanggal Tua, Daftar Belanja Warung).

## Project Brief

# Project Brief: Chef AI Revan (KOST CHEF)

**Chef AI Revan** is a Neo-Brutalist AI-powered culinary assistant tailored specifically for Indonesian "anak kos" (students/individuals living in boarding houses). The app provides budget-friendly meal solutions using local ingredients, helping users manage their finances in Rupiah (IDR) while eating well despite limited equipment like rice cookers.

## Features
- **AI Menu Anak Kos**: Generates creative recipe ideas based on a specific Rupiah budget and available ingredients (e.g., tempeh, eggs, tofu), with a focus on rice-cooker-friendly meals.
- **Rupiah Budget Tracker**: A high-contrast, bold Neo-Brutalist tracker for daily and weekly food spending in IDR, helping users visualize their remaining "allowance" for the month.
- **Mode Tanggal Tua (End-of-Month Mode)**: A specialized emergency filter that suggests ultra-low-cost recipes (under Rp 10.000) for the period when funds are nearly exhausted.
- **Daftar Belanja Warung (Shopping List)**: Automatically generates a concise shopping list from selected recipes, optimized for quick trips to local *warungs* or traditional markets.

## High-Level Technical Stack
- Kotlin, Jetpack Compose.
- Architecture: MVVM with Navigation Compose.
- Design: Neo-Brutalist with Indonesian nuance (Merah Putih accents, bold yellows, black borders).
- Currency: Rupiah (IDR).

## Implementation Steps
**Total Duration:** 35m 50s

### Task_1_DesignSystem: Establish Neo-Brutalist design system (Colors, Type) and reusable components (NeoButton, NeoCard, NeoTextField). Setup Navigation framework.
- **Status:** COMPLETED
- **Updates:** Established the Neo-Brutalist design system including Theme.kt with specified colors, Inter-style typography, and custom NeoCard, NeoButton, and NeoTextField components with 3dp borders and 8dp hard shadows. Implemented Jetpack Navigation for Dashboard, Budget Tracker, and Grocery Checklist. Created an adaptive app icon.
- **Acceptance Criteria:**
  - Theme.kt has correct colors (#F0F4FF, #7C3AED, #ADFF2F, #FF1493)
  - NeoButton, NeoCard, NeoTextField match design (3dp border, 8dp shadow)
  - Navigation between Dashboard, Budget, and Checklist works
- **Duration:** 9m 54s

### Task_2_CoreFeatures_Budget: Implement Room database and the Weekly Budget Tracker feature including the 'Survive till Payday' logic and UI.
- **Status:** COMPLETED
- **Updates:** Implemented Room database with entities for Budget and GroceryItem. Developed BudgetViewModel to handle business logic, including the 'Survive till Payday' threshold detection. Built the Budget Tracker UI with expense tracking and emergency mode alerts, and fully functional Grocery Checklist screen. All UI elements adhere to the Neo-Brutalist design system.
- **Acceptance Criteria:**
  - Room DB stores budget and grocery items
  - Budget screen allows setting limit and shows balance
  - 'Survive till Payday' logic implemented
- **Duration:** 5m 6s

### Task_3_Dashboard_Grocery: Build Main Dashboard with mocked AI recommendations and the Smart Grocery Checklist integrated with the data layer.
- **Status:** COMPLETED
- **Updates:** Completed the Main Dashboard with budget-aware mock AI recommendations. Integrated the 'Add to Grocery' functionality, allowing users to add recipe ingredients directly to the Room-backed Grocery Checklist. Added a 'Budget Snapshot' on the dashboard and ensured consistent Neo-Brutalist styling throughout the app. Verified data synchronization between the Budget, Dashboard, and Grocery screens via a shared ViewModel.
- **Acceptance Criteria:**
  - Dashboard displays budget-aware recipe recommendations
  - Grocery list reflects items from recipes or manual entry
  - UI adheres to Neo-Brutalist style
- **Duration:** 1m 57s

### Task_4_AppIcon_Run_Verify: Generate adaptive app icon, enable edge-to-edge, and perform final run/verify for stability and UI fidelity.
- **Status:** COMPLETED
- **Updates:** Final QA completed by critic agent. The app is stable, functionally complete, and strictly follows the Neo-Brutalist design specifications (3dp borders, 8dp hard shadows, specified colors). Adaptive icon and edge-to-edge support verified. All core features (Budget Tracker, 'Survive till Payday' mode, Dashboard, and Grocery Checklist) are fully integrated and working.
- **Acceptance Criteria:**
  - Adaptive app icon created
  - Edge-to-edge display enabled
  - App builds and runs without crashes
  - All existing tests pass
- **Duration:** 8m 51s

### Task_5_Indonesian_Localization_and_Rebranding: Rebrand and localize the app for Indonesian 'anak kos'. Update the Neo-Brutalist theme with Merah Putih (Red/White) and bold yellow accents. Rename features to 'AI Menu Anak Kos', 'Rupiah Budget Tracker', 'Mode Tanggal Tua', and 'Daftar Belanja Warung'. Implement IDR (Rp) currency formatting and update mock recipes with local ingredients and rice-cooker friendly instructions. Update 'Mode Tanggal Tua' threshold to Rp 10.000.
- **Status:** COMPLETED
- **Updates:** Localized the app for Indonesian "anak kos". Updated the Neo-Brutalist theme with Merah Putih and bold yellow accents. Renamed features to "AI Menu Anak Kos", "Rupiah Budget Tracker", "Mode Tanggal Tua", and "Daftar Belanja Warung". Implemented IDR (Rp) currency formatting using Locale("in", "ID"). Updated mock recipes to Indonesian student favorites (Nasi Telur, Mie Instan Mewah, etc.) with rice-cooker focus. Set "Mode Tanggal Tua" threshold to Rp 10.000.
- **Acceptance Criteria:**
  - Theme.kt updated with Merah Putih and bold yellow accents
  - All UI strings and feature names localized to Indonesian
  - Currency display uses 'Rp' prefix and Indonesian formatting
  - Mock recipes reflect Indonesian ingredients (tempeh, tofu, eggs) and rice-cooker focus
  - 'Mode Tanggal Tua' logic updated to Rp 10.000 threshold
- **Duration:** 4m 24s

### Task_6_Final_Run_and_Verify_Localized: Perform a final run and verify for the localized application. Instruct critic_agent to verify stability, alignment with the Indonesian Neo-Brutalist aesthetic, and functionality of localized features.
- **Status:** COMPLETED
- **Updates:** Final QA for the localized version completed by critic agent. The app is stable and correctly rebranded with Indonesian nuance (Merah Putih theme). All localized features, including Rupiah (Rp) currency formatting and 'Mode Tanggal Tua' logic (Rp 10.000 threshold), are fully functional. UI adheres to Neo-Brutalist design specifications with 3dp borders and 8dp shadows. Adaptive icon and edge-to-edge support are verified.
- **Acceptance Criteria:**
  - App builds and runs without crashes
  - Localized features (IDR, Indonesian names) work correctly
  - UI matches Indonesian Neo-Brutalist aesthetic
  - Make sure all existing tests pass
  - build pass
  - app does not crash
- **Duration:** 5m 38s

