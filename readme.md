![Screenshot 2025-06-16 at 00 51 11](https://github.com/user-attachments/assets/0c4ef795-8283-4fd6-ac16-a1840d81ac4a)
# 🐶 DogBuddy – Dein zuverlässiger Hunde-Sitter-Manager

**„Never miss a walk or a meal – DogBuddy ist immer an deiner Seite.“**

DogBuddy hilft Hundebesitzer:innen und Hundesitter:innen, sich problemlos zu vernetzen, Termine für Fütterung und Spaziergänge zu planen und den Überblick über alle Pflegeaufgaben zu behalten. Ob du beruflich eingespannt bist oder im Urlaub: mit DogBuddy weißst du jederzeit, wer sich um deinen Liebling kümmert..

Für wen ist DogBuddy geeignet?
Hundebesitzer:innen, die flexible Betreuung für ihren Hund suchen, und Hundeliebhaber:innen, die in ihrer Nachbarschaft unterstützen möchten. Das Problem "vergessene Termine" oder "fehlende Kommunikation" gehört der Vergangenheit an.

### Was macht DogBuddy besonders?

- Zentrale Verwaltung aller Betreuungstermine und Pflegehinweise

- Automatische Push-Benachrichtigungen bei erledigten Aufgaben

- Integrierte Hunderassen-Infos für optimale Pflegeempfehlungen

- Einfaches Hochladen und Teilen von Fotos direkt in der App


## 🎨 Design
Füge hier am Ende die Screenshots deiner App ein.

![Mockup2](https://github.com/user-attachments/assets/1f2e355b-20c0-4b8a-905b-7576c969c3f5)
![Mockups1](https://github.com/user-attachments/assets/c7950474-51e5-480c-8959-96ad0b030f03)




## 📱 Features
- [ ] 1. **Authentication**  
   - User registration & login via Firebase Auth (email, name, address, …)
- [ ] 2. **UploadView**  
   - Photo upload (Firebase Storage)  
   - Fields: receipt date, desired pickup time, location
- [ ] 3. **MapView**  
   - Map integration with annotations for available items  
   - Distance calculation
- [ ] 4. **ProfileView**  
   - User details  
   - Dark/Light mode toggle
- [ ] 5. Dog breed-API
   - Detaillierte Infos zu Größe, Gewicht, Lebenserwartung und Pflegebedarf.
- [ ] 6. Verfügbarkeits-Kalender
   - Plane Termine für Fütterung und Spaziergänge mit anderen Sitter:innen.
- [ ] 7. Push-Benachrichtigungen
   - Erhalte Warnungen, sobald dein Hund gefüttert wurde oder ein Spaziergang abgeschlossen ist.


## 🧩 Technischer Aufbau

#### Projektaufbau
- MVVM-Architektur: Trennung von UI (Views), Business-Logik (ViewModels) und Datenzugriff (Repositories).

- Ordnerstruktur:

  - ui/ – Compose-Screens oder XML-Layouts

  - viewmodel/ – ViewModel-Klassen

  - data/ – Repositories, Firestore- und API-Clients

  - model/ – Datenklassen (User, Dog, Appointment, BreedInfo)

  - util/ – Helferklassen und Extensions

#### Datenspeicherung
- Firebase Authentication: Sichere Anmeldung per E-Mail/Passwort und Google Sign-In.

- Cloud Firestore: Speicherung von Nutzerdaten, Hundedaten und Terminen in Echtzeit.

- Firebase Storage: Speichern und Abrufen von Profil- und Hundebildern.

- Warum Firebase?

  - Schnelle Einrichtung und Skalierbarkeit

  - Echtzeit-Updates ohne eigenen Server

  - Out-of-the-box Push-Benachrichtigungen (FCM)

#### API Calls
- TheDogAPI: Abruf von Rasseinformationen wie Größe, Gewicht, Lebenserwartung und Pflegehinweisen.
  - Base URL: https://thedogapi.com
  - Client ID: `0eac2931bd2dc7e`

#### 3rd-Party Frameworks
- Hilt – Dependency Injection für übersichtliche Komponentenverwaltung

- Coil – Asynchrones Laden und Caching von Bildern

- Jetpack Navigation – Einfaches Routing zwischen Screens

- Jetpack Compose (optional) – Moderne deklarative UI

- Gson – JSON-Parsing für API-Antworten


## Ausblick
Jenachdem wie weit ich mit den Projeckt komme will, ich weitere tierarten API hinzufügen wie: 

- [ ] Schlangen oder Pferde



## 🙍🏾‍♂️ Developer / Author
[Jefferson Prensa](https://github.com/JPrensa)
