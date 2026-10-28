# SIMPUS Digital UMSIDA — Form Registrasi Anggota Perpustakaan

Project aplikasi Android menggunakan **Jetpack Compose (Material 3)** untuk memenuhi Tugas Individu **Pertemuan 6: State, Event & Form Validation**.

## 1. Fitur Utama & Modifikasi dari Contoh Praktikum

Sesuai ketentuan tugas individu, project ini telah dikembangkan agar berbeda dari contoh dasar `KampusKu`:

- **Tema & Palet Warna Baru**: Mengusung tema **SIMPUS Digital UMSIDA (Registrasi Anggota Perpustakaan)** dengan skema warna *Emerald Teal* (`#0F766E`), aksen *Amber* (`#D97706`), dan latar belakang *Soft Mint* (`#F0FDF4`).
- **State Hoisting (`FormInputField`)**: Memisahkan komponen `OutlinedTextField` ke dalam composable *reusable* `FormInputField` yang menerima *state* melalui parameter `value` dan mengirimkan *event* melalui *callback* `onValueChange`.
- **Pemisahan Function Validasi**: Menggunakan fungsi validator terpisah (`isNameValid`, `isNimValid`, `isPhoneValid`, `isEmailValid`, `isPasswordValid`, `isConfirmationValid`) agar kode UI bersih dan mudah diuji.
- **Penambahan Field & Aturan Validasi Baru**:
  1. **Nama Lengkap Mahasiswa**: Wajib diisi dan minimal 3 karakter.
  2. **Nomor Induk Mahasiswa (NIM)** *(Field Baru)*: Wajib berupa angka dengan panjang 8 hingga 12 digit (`KeyboardType.Number`).
  3. **Nomor WhatsApp Aktif** *(Field Baru)*: Wajib berupa angka dengan panjang 10 hingga 13 digit (`KeyboardType.Phone`).
  4. **Email Mahasiswa**: Wajib sesuai format email valid (`Patterns.EMAIL_ADDRESS`).
  5. **Kata Sandi Akun** *(Aturan Baru)*: Minimal 8 karakter serta **wajib mengandung kombinasi huruf dan angka**, dilengkapi tombol *toggle* lihat/sembunyikan kata sandi.
  6. **Konfirmasi Kata Sandi**: Wajib sama persis dengan kata sandi utama.
  7. **Checkbox Tata Tertib**: Wajib dicentang sebelum pendaftaran dapat diproses.

## 2. Penjelasan Singkat State, Event, dan Validasi

### A. State dan Recomposition
- **State** adalah data yang menentukan tampilan UI pada saat tertentu (seperti teks yang sedang diketik pada kolom input atau status centang pada *checkbox*).
- **Event** adalah aksi dari pengguna (seperti mengetik teks pada `onValueChange`, menekan *checkbox* pada `onCheckedChange`, atau menekan tombol pada `onClick`) yang memperbarui nilai *state*.
- Saat nilai *state* berubah, Jetpack Compose secara otomatis menjalankan **Recomposition** untuk memperbarui komponen UI yang membaca *state* tersebut.

### B. `remember` vs `rememberSaveable`
- **`rememberSaveable`** digunakan pada state `nama`, `nim`, `nomorWa`, `email`, `setuju`, dan `sudahSubmit` agar isian pengguna tidak hilang ketika layar mengalami perubahan konfigurasi (misalnya saat ponsel dirotasi).
- **`remember`** digunakan pada state `password` dan `konfirmasiPassword` sebagai state sementara (*transient*) demi alasan keamanan, sehingga kata sandi tidak disimpan ke dalam *saved instance state bundle* Android.

### C. Mekanisme Validasi Form
- Validasi dievaluasi secara deklaratif setiap kali *state* berubah, namun pesan kesalahan (`isError` dan `supportingText`) baru ditampilkan setelah pengguna menekan tombol **Aktifkan Keanggotaan** (`sudahSubmit = true`).
- Ketika pengguna memperbaiki isian pada kolom mana pun (`onValueChange`), status `berhasil` di-reset menjadi `false` dan indikator merah (`isError`) pada kolom yang sudah diperbaiki otomatis hilang secara *real-time*.
- Pesan sukses (`Registrasi Berhasil!`) hanya muncul apabila seluruh 7 aturan validasi bernilai `true` (`formValid == true`).

## 3. Cara Menjalankan Project di Android Studio

1. Buka **Android Studio** dan pilih **New Project** -> **Empty Activity** (pastikan template menggunakan Jetpack Compose dan bahasa Kotlin).
2. Isi konfigurasi project:
   - **Name**: `StateEventFormValidation`
   - **Package name**: `com.example.stateeventformvalidation`
   - **Minimum SDK**: `API 24` (atau lebih baru)
3. Buka file `app/build.gradle.kts` (Module `:app`), tambahkan dependency ikon Material di dalam blok `dependencies { ... }`:
   ```kotlin
   implementation("androidx.compose.material:material-icons-extended")
   ```
4. Klik **Sync Now** pada banner di bagian atas Android Studio dan tunggu hingga proses *Gradle Sync* selesai.
5. Buka file `app/src/main/java/com/example/stateeventformvalidation/MainActivity.kt`, lalu ganti seluruh isinya dengan kode dari `MainActivity.kt` pada project ini.
6. Jalankan aplikasi ke Emulator atau perangkat fisik Android dengan menekan tombol **Run 'app'** (`Shift + F10`).

## 4. Skenario Pengujian & Dokumentasi Screenshot

1. **Kondisi Awal (Running)**: Tampilan saat seluruh kolom formulir masih kosong.
2. **Kondisi Validasi Gagal**: Tampilan saat tombol **Aktifkan Keanggotaan** ditekan dengan input kosong atau tidak memenuhi aturan (border kolom berwarna merah beserta pesan error di bawah tiap kolom).
3. **Kondisi Validasi Berhasil**: Tampilan saat seluruh kolom diisi dengan data valid dan persetujuan dicentang hingga menampilkan status hijau **Registrasi Berhasil!**.
