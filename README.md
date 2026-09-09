# Step Counter

A simple and modern Android step counter built with **Kotlin** and **Jetpack Compose**.

The app uses the Android `TYPE_STEP_COUNTER` sensor to track steps throughout the day and keeps tracking active in the background using a **Health Foreground Service**.

## Features

- Real-time step counting
- Daily step tracking
- Persistent step data using DataStore
- Daily step goal
- Circular progress indicator
- Estimated walking distance
- Estimated calories burned
- Background step tracking
- Health Foreground Service
- Activity Recognition permission handling
- Notification showing tracking status
- Automatic daily baseline reset
- Sensor availability detection
- Modern Jetpack Compose UI

## Tech Stack

- **Kotlin**
- **Jetpack Compose**
- **Android SDK**
- **Material Design**
- **ViewModel**
- **Kotlin Coroutines**
- **StateFlow**
- **DataStore Preferences**
- **Android Sensor Framework**
- **Foreground Service**

## Architecture

The project follows a simple layered architecture:

```text
MainActivity
     │
     ▼
StepsViewModel
     │
     ▼
StepRepository
     │
     ├──────────────► StepDataStore
     │
     ▼
StepTrackingService
     │
     ▼
StepSensorManager
     │
     ▼
TYPE_STEP_COUNTER
