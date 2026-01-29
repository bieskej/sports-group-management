# Šta fali aplikaciji za Play Store objavljivanje

Evo kompletne liste šta treba dodati i uraditi da bi se aplikacija mogla objaviti na Google Play Store-u.

---

## 📋 1. TEHNIČKI ZAHTJEVI - Što fali aplikaciji

### ✅ Što već imate:
- ✓ Osnovnu Android aplikaciju
- ✓ Keystore fajl (`upload-keystore.jks`)
- ✓ ProGuard konfiguraciju
- ✓ Release build type konfiguraciju
- ✓ Custom Ikonu aplikacije (pronađena `logo3`)
- ✓ Privacy Policy URL (https://matijevicmila.github.io/SportGroup-policies/privacy-policy)
- ✓ Signing Config (Verifikovano ✅ - Build Prošao)
- ✓ App Bundle Config (Verifikovano ✅ - `app-release.aab` generisan)

### ❌ Što fali:







#### 1.4 Feature Graphic i Screenshots

**Problem**: Play Store zahtjeva vizuelne materijale.

**Obavezno**:
- **Feature Graphic**: 1024 x 500 px (JPG ili 24-bit PNG, bez transparencije)
- **Screenshots**: Minimum 2, maksimum 8
  - Phone: 320-3840 px na dužoj strani
  - PNG ili JPG
  - Različiti screenshot-i za Phone/Tablet/TV ako podržavate

**Dodatno** (Opciono ali preporučeno):
- Promo video (YouTube link)
- TV banner (ako podržavate Android TV)

---

#### 1.5 App Content Rating

**Problem**: Morate odgovoriti na upitnik o sadržaju aplikacije.

**Proces**:
- U Play Console → Content rating
- Popunite upitnik (IARC sistem)
- Dobijate rating (Everyone, Teen, Mature, itd.)
- Besplatno je

---

#### 1.6 Target Audience & Content

**Problem**: Nove Play Store politike zahtjevaju izjave o sadržaju.

**Potrebno deklarisati**:
- Da li aplikacija cilja djecu
- Koje podatke prikupljate (Data Safety section)
- Da li ima reklama
- Da li ima in-app purchases

---



## 🌐 2. EKSTERNI ZAHTJEVI - Što treba van aplikacije

### 2.1 Google Play Console Account

**Potrebno**:
- Google nalog
- **Jednokratna naknada**: $25 USD
- Registracija na: https://play.google.com/console/signup

**Proces**:
1. Platite naknadu
2. Prihvatite Developer Distribution Agreement
3. Podesite developer profil

---

### 2.2 Store Listing Informacije

**Šta vas Play Console traži**:

#### Tekstualni sadržaj:
- **App name**: Do 30 karaktera
- **Short description**: Do 80 karaktera
- **Full description**: Do 4000 karaktera
- **Kategorija**: npr. Sports, Health & Fitness
- **Email kontakt**: Mora biti vidljiv
- **Opciono**: Web stranica, telefon

#### Grafički sadržaj:
- App icon (512x512 PNG)
- Feature graphic (1024x500)
- Screenshots (2-8)
- Promo video (opciono)

---

### 2.3 Production Track Setup

**Interno testiranje** → **Zatvoreno testiranje** → **Otvoreno testiranje** → **Produkcija**

**Preporuka za prvi put**:
1. Kreirajte Internal Testing track
2. Testirajte sa prijateljima/kolegama
3. Riješite bug-ove
4. Pređite na Production

---

### 2.4 Compliance Documents

**Potrebno**:
- ✓ Privacy Policy URL
- ✓ Content rating certificate
- ✓ Target age declaration
- ✓ Data safety form

---

## 📝 3. PRIPREMNI KORACI - Action Items

### Korak 1: Dodati signing config

```bash
# 1. Kreirajte ili provjerite postojeći keystore
keytool -list -v -keystore upload-keystore.jks

# 2. Dodajte signing config u build.gradle.kts (kao gore)
# 3. Dodajte lozinke u gradle.properties
```

### Korak 2: Build signed release

```bash
# AAB (preporučeno)
./gradlew bundleRelease

# Ili APK
./gradlew assembleRelease
```

Output će biti:
- AAB: `app/build/outputs/bundle/release/app-release.aab`
- APK: `app/build/outputs/apk/release/app-release.apk`

### Korak 3: Kreirajte grafičke materijale

1. **App icon**: Koristite Image Asset Studio u Android Studio
2. **Feature graphic**: 1024x500 banner za Play Store
3. **Screenshots**: Minimum 2 screenshots iz aplikacije

### Korak 4: Privacy Policy

Kreiranje jednostavnog privacy policy-a:

```markdown
# Privacy Policy for SportskaGrupa

Last updated: [Date]

## Introduction
This privacy policy describes how SportskaGrupa ("we", "our", or "us") 
handles your information.

## Data Collection
We do not collect, store, or share any personal data from our users.

## Internet Permission
Our app uses the INTERNET permission to connect to our backend API 
for app functionality. We do not track user behavior or share data 
with third parties.

## Contact
For questions, contact: [your-email@example.com]
```

Hostujte na GitHub Pages ili Google Sites.

### Korak 5: Kreirajte Play Console nalog

1. Idite na: https://play.google.com/console/signup
2. Platite $25
3. Popunite developer profil

### Korak 6: Upload na Play Console

1. Create app → Nova aplikacija
2. Popunite Store Listing
3. Upload AAB/APK
4. Popunite Content rating
5. Popunite Data safety
6. Submit for review

---

## ⚠️ ČESTE GREŠKE

> [!WARNING]
> - **NE** stavljajte keystore lozinke u Git!
> - **NE** zaboravite keystore lozinku (nećete moći update-ovati app)
> - **NE** koristite `debuggable` build za production
> - **TESTIRAJTE** release build prije upload-a!

---

## 📱 TESTIRANJE PRIJE OBJAVE

```bash
# 1. Build release
./gradlew assembleRelease

# 2. Instalirajte na test uređaj
adb install app/build/outputs/apk/release/app-release.apk

# 3. Testirajte sve funkcionalnosti
# - Login
# - Register  
# - Dashboard
# - Trainings
# - Statistics
```

---

## 🎯 CHECKLIST PRIJE SUBMISSION

- [ ] Signing config dodan u build.gradle.kts
- [ ] Release build se uspješno kompajlira
- [ ] Custom app icon kreirana
- [ ] Privacy Policy URL dostupan
- [ ] Feature Graphic (1024x500) kreiran
- [ ] Minimum 2 screenshots spremna
- [ ] App testiran na release build-u
- [ ] Play Console nalog kreiran ($25 plaćeno)
- [ ] Store listing informacije pripremljene
- [ ] Content rating upitnik popunjen
- [ ] Data safety form popunjen
- [ ] AAB/APK build uspješan

---

## 🚀 PROCIJENJENO VRIJEME

- **Tehnička priprema**: 2-4 sata
- **Grafički sadržaj**: 2-6 sati (ovisno o dizajnu)
- **Play Console setup**: 1-2 sata
- **Google review process**: 1-7 dana

**UKUPNO**: ~1 sedmica od početka do objave

---

## 📞 DODATNI RESURSI

- [Google Play Console Help](https://support.google.com/googleplay/android-developer)
- [App Signing Guide](https://developer.android.com/studio/publish/app-signing)
- [Play Console Documentation](https://developer.android.com/distribute/console)
- [Content Rating Guide](https://support.google.com/googleplay/android-developer/answer/9859655)
