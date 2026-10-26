# IDCloudHost VM Manager (Neobrutalism Edition)

Aplikasi Android native Jetpack Compose berkinerja tinggi untuk mengelola Virtual Machine (VM) IDCloudHost secara real-time—mulai dari melihat daftar VM, menyalakan (start), mematikan (stop), hingga mengubah spesifikasi (vCPU & RAM) serta nama VM—dilengkapi autentikasi API Key persisten, pemilihan region data center, dan Android Home Screen App Widget interaktif dengan gaya visual **Neobrutalism**.

---

### User Review & Critical Decisions

> [!IMPORTANT]
> Berdasarkan klarifikasi sebelumnya, keputusan utama telah dikonfirmasi dan disepakati:
> - **Region Selection**: Mendukung pemilihan lokasi/region otomatis dan dropdown dinamis (Jakarta `jkt01`, `jkt02`, `jkt03`, Singapore `sgp01`, dll.).
> - **VM Modification Scope**: Memprioritaskan pengubahan spesifikasi vCPU, RAM (MB), dan nama VM melalui modal editor neobrutalis.
> - **Initial Launch Flow**: Wajib memasukkan API Key IDCloudHost yang valid pada layar onboarding/setup awal sebelum masuk ke dashboard utama.
> - **Android Home Screen Widget**: AppWidget interaktif untuk memonitor status VM yang dipilih serta tombol aksi cepat *Start* & *Stop* langsung dari home screen ponsel.

---

### 1. Overview & Core Concept

IDCloudHost VM Manager dibangun untuk para *devops engineer*, *sysadmin*, dan *developer* yang membutuhkan akses cepat dan praktis untuk mengawasi dan mengontrol instance cloud mereka di mana saja tanpa harus membuka laptop atau web browser.

**Fitur Utama:**
1. **API Key Setup & Persistence**: Input API Key di awal dengan enkripsi lokal aman menggunakan Android Keystore / Encrypted Preferences & DataStore, serta dapat diganti kapan saja di menu Pengaturan.
2. **Multi-Region VM Discovery**: Mendeteksi dan memilih region IDCloudHost (`jkt01`, `jkt02`, `jkt03`, dll.) untuk menampilkan seluruh instance yang aktif maupun *stopped*.
3. **VM Control Center (Start, Stop, Force Stop)**: Tombol aksi instan dengan dialog konfirmasi neobrutal dan feedback visual status (*Running*, *Stopped*, *Rebooting*, *Error*).
4. **Modify VM (Resize & Rename)**: Modal interaktif untuk mengubah jumlah vCPU (1–16 core), alokasi RAM (1 GB–64 GB), dan label nama VM melalui endpoint `PATCH /v1/user-resource/vm`.
5. **Interactive Home Screen Widget**: Widget Android mandiri yang menampilkan status VM favorit pilihan pengguna (nama, IP publik, CPU, RAM, badge status) serta tombol *Start* dan *Stop* langsung dari Home Screen.
6. **Neobrutalism Aesthetics**: Desain berkarakter kuat dengan *bold black borders (2.5dp)*, *hard drop shadows*, palet warna vibrant kontras (*Electric Yellow*, *Neo Coral*, *Mint Cyan*, *Bright Cobalt*), tipografi punchy, dan animasi tombol taktil.

---

### 2. User Experience & Visual Design

#### Key User Flows
1. **First Launch / API Key Setup**: Pengguna disambut dengan form neobrutalis bergaya terminal/punchy untuk memasukkan IDCloudHost API Key dengan panduan link pembuatan token di portal IDCloudHost.
2. **Dashboard & Region Switcher**: Begitu tersambung, aplikasi menampilkan ringkasan jumlah VM (Total, Running, Stopped), dropdown region data center, search bar, dan daftar kartu VM.
3. **VM Detail & Quick Actions**: Mengetuk kartu VM akan menampilkan lembar detail (UUID, Public IP, Private IP, OS, disk, vCPU, RAM, creation date) beserta tombol *Start*, *Stop*, dan *Modify*.
4. **Modify Spec Sheet**: BottomSheet / Dialog dengan slider atau stepper neobrutal untuk menentukan jumlah vCPU dan RAM baru, serta textfield untuk nama baru dengan tombol "Apply Changes".
5. **Settings & Widget Configuration**: Menu pengaturan untuk mengganti API Key, menguji konektivitas API, memilih VM default untuk Home Screen Widget, dan mengatur interval auto-refresh.

#### Visual Identity & Neobrutalism Tokens
```
┌────────────────────────────────────────────────────────┐
│ NEOBRUTALISM DESIGN SYSTEM TOKENS                      │
├────────────────────────────────────────────────────────┤
│ Background     : #FFFDF5 (Warm Pale Cream)             │
│ Surface        : #FFFFFF (Crisp Card White)            │
│ Border Stroke  : #121212 (2.5dp Pitch Black Border)    │
│ Hard Shadow    : Offset 4dp x 4dp Solid #121212        │
│ Accent Primary : #FFE600 (Electric Yellow)             │
│ Accent Secondary: #FF5E7E (Neo Bubblegum Pink)         │
│ Status Running : #2CE59B (Mint Acid Green)             │
│ Status Stopped : #FF4A4A (Vibrant Brick Red)           │
│ Tag / Neutral  : #6C5CE7 (Retro Purple / Indigo)       │
│ Dark Mode Base : #18191E with #FFE600 & #2CE59B        │
└────────────────────────────────────────────────────────┘
```

#### Typography & Component Treatment
- **Headings & Badges**: Huruf tegas, bobot Bold/Black dengan border hitam tebal dan badge berkontras tinggi.
- **Data & Metrics (IP/UUID)**: Tipografi monospace untuk alamat IP publik/privat dan ID instansi guna meningkatkan keterbacaan teknis.
- **Buttons**: Tombol tebal dengan efek *physical push* (bergeser 2–3dp ke kanan-bawah saat ditekan, meniadakan bayangan untuk sensasi tombol fisik).

---

### 3. Key Product Decisions & Trade-Offs

1. **IDCloudHost API Protocol**:
   - *Pilihan*: Menggunakan Retrofit 2 + OkHttp dengan `HttpLoggingInterceptor` dan `apikey` HTTP header interceptor.
   - *Alasan*: Kompatibel penuh dengan dokumentasi resmi `https://api.idcloudhost.com/#vm`, mendukung request `GET /v1/{slug}/user-resource/vm/list`, `POST /v1/user-resource/vm/start`, `POST /v1/user-resource/vm/stop`, dan `PATCH /v1/user-resource/vm`.
2. **Local Credential Storage**:
   - *Pilihan*: `DataStore` & `SharedPreferences` terisolasi untuk API Key dan preferensi VM terpilih.
   - *Alasan*: Tidak membutuhkan backend eksternal tambahan; data credential tetap privat di perangkat pengguna.
3. **Android App Widget Architecture**:
   - *Pilihan*: `AppWidgetProvider` native dengan `RemoteViews` dan `BroadcastReceiver` / `CoroutineScope`.
   - *Alasan*: Menjamin kompabilitas tinggi pada seluruh versi Android (Android 7.0 hingga 15) tanpa overhead dependency eksternal, memberikan respon instan saat tombol Start/Stop diklik di home screen.
4. **Resilience & Error Handling**:
   - *Pilihan*: Penanganan error HTTP (401 Unauthorized jika API Key keliru, 404 jika VM tidak ditemukan, 400 jika quota limit) dengan pesan berbahasa Indonesia yang jelas dan instruktif.

---

### 4. Technical Architecture & Data Strategy

```
┌────────────────────────────────────────────────────────────────────────┐
│                        ANDROID ARCHITECTURE                            │
├────────────────────────────────────────────────────────────────────────┤
│                                                                        │
│   ┌─────────────────────┐             ┌───────────────────────────┐   │
│   │    Compose UI       │             │   Home Screen Widget      │   │
│   │  (Neobrutalism)     │             │  (AppWidgetProvider)      │   │
│   └──────────┬──────────┘             └─────────────┬─────────────┘   │
│              │                                      │                 │
│              ▼                                      ▼                 │
│   ┌─────────────────────┐             ┌───────────────────────────┐   │
│   │    VmViewModel      │             │   WidgetActionReceiver    │   │
│   │ (StateFlow/MVI UI)  │             │   (Background Coroutine)  │   │
│   └──────────┬──────────┘             └─────────────┬─────────────┘   │
│              │                                      │                 │
│              └──────────────────┬───────────────────┘                 │
│                                 │                                     │
│                                 ▼                                     │
│                     ┌────────────────────────┐                        │
│                     │      VmRepository      │                        │
│                     └───────────┬────────────┘                        │
│                                 │                                     │
│              ┌──────────────────┴──────────────────┐                  │
│              ▼                                     ▼                  │
│   ┌─────────────────────┐               ┌───────────────────────┐     │
│   │   IdCloudHostApi    │               │  ApiKeyPreferences    │     │
│   │ (Retrofit / OkHttp) │               │   (Encrypted / Store) │     │
│   └─────────────────────┘               └───────────────────────┘     │
│                                                                        │
└────────────────────────────────────────────────────────────────────────┘
```

#### Komponen Teknis:
- **`IdCloudHostApi`**: Antarmuka Retrofit untuk API IDCloudHost (`locations`, `vm/list`, `vm/start`, `vm/stop`, `vm/modify`).
- **`VmRepository`**: Pusat abstraksi data untuk memuat daftar VM, memperbarui status, mengeksekusi perintah start/stop, dan membaca konfigurasi API key.
- **`VmViewModel`**: Mengelola state UI (ApiKeyPresent, Loading, VmList, Error, SelectedVm, ModifyDialogState, NotificationToast).
- **`IdCloudHostWidgetProvider`**: Provider widget home screen dengan layout neobrutalis kompak (Status badge, Nama VM, Spek, IP, Tombol Start/Stop).
- **`Theme.kt & Components`**: Komponen Neobrutalism (`BrutalCard`, `BrutalButton`, `BrutalBadge`, `BrutalTextField`, `BrutalDialog`).
